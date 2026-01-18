package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.SectTaskTemplateCreateRequest;
import com.xiuxian.dto.request.SectTaskTemplateUpdateRequest;
import com.xiuxian.dto.response.SectTaskTemplateDetailResponse;
import com.xiuxian.dto.response.SectTaskTemplateListItemResponse;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectTaskTemplate;
import com.xiuxian.mapper.SectMapper;
import com.xiuxian.mapper.SectTaskTemplateMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宗门任务模板配置管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/sect-task")
public class SectTaskTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(SectTaskTemplateController.class);

    @Autowired
    private SectTaskTemplateMapper sectTaskTemplateMapper;

    @Autowired
    private SectMapper sectMapper;

    /**
     * 分页查询任务模板列表
     */
    @GetMapping("/list")
    public Result<PageResult<SectTaskTemplateListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long sectId,
            @RequestParam(required = false) String taskType) {

        logger.info("分页查询任务模板列表: page={}, pageSize={}, keyword={}, sectId={}, taskType={}",
                page, pageSize, keyword, sectId, taskType);

        // 构建查询条件
        LambdaQueryWrapper<SectTaskTemplate> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SectTaskTemplate::getTaskName, keyword);
        }
        if (sectId != null) {
            wrapper.eq(SectTaskTemplate::getSectId, sectId);
        }
        if (taskType != null && !taskType.isEmpty()) {
            wrapper.eq(SectTaskTemplate::getTaskType, taskType);
        }
        wrapper.orderByDesc(SectTaskTemplate::getTemplateId);

        // 分页查询
        Page<SectTaskTemplate> pageParam = new Page<>(page, pageSize);
        Page<SectTaskTemplate> result = sectTaskTemplateMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<SectTaskTemplateListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<SectTaskTemplateListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取任务模板详情
     */
    @GetMapping("/{templateId}")
    public Result<SectTaskTemplateDetailResponse> getDetail(@PathVariable Long templateId) {
        logger.info("获取任务模板详情: templateId={}", templateId);

        SectTaskTemplate taskTemplate = sectTaskTemplateMapper.selectById(templateId);
        if (taskTemplate == null) {
            return Result.error("任务模板不存在");
        }

        SectTaskTemplateDetailResponse response = convertToDetail(taskTemplate);
        return Result.success(response);
    }

    /**
     * 创建任务模板
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SectTaskTemplateCreateRequest request) {
        logger.info("创建任务模板: taskName={}", request.getTaskName());

        // 验证宗门是否存在
        Sect sect = sectMapper.selectById(request.getSectId());
        if (sect == null) {
            return Result.error("宗门不存在");
        }

        // 转换并保存
        SectTaskTemplate taskTemplate = new SectTaskTemplate();
        BeanUtils.copyProperties(request, taskTemplate);
        sectTaskTemplateMapper.insert(taskTemplate);

        logger.info("任务模板创建成功: templateId={}", taskTemplate.getTemplateId());
        return Result.success("创建成功", taskTemplate.getTemplateId());
    }

    /**
     * 更新任务模板
     */
    @PutMapping("/{templateId}")
    public Result<Void> update(
            @PathVariable Long templateId,
            @Valid @RequestBody SectTaskTemplateUpdateRequest request) {

        logger.info("更新任务模板: templateId={}, taskName={}", templateId, request.getTaskName());

        SectTaskTemplate taskTemplate = sectTaskTemplateMapper.selectById(templateId);
        if (taskTemplate == null) {
            return Result.error("任务模板不存在");
        }

        // 验证宗门是否存在
        if (request.getSectId() != null) {
            Sect sect = sectMapper.selectById(request.getSectId());
            if (sect == null) {
                return Result.error("宗门不存在");
            }
        }

        // 更新
        BeanUtils.copyProperties(request, taskTemplate);
        sectTaskTemplateMapper.updateById(taskTemplate);

        logger.info("任务模板更新成功: templateId={}", templateId);
        return Result.success("更新成功");
    }

    /**
     * 删除任务模板
     */
    @DeleteMapping("/{templateId}")
    public Result<Void> delete(@PathVariable Long templateId) {
        logger.info("删除任务模板: templateId={}", templateId);

        SectTaskTemplate taskTemplate = sectTaskTemplateMapper.selectById(templateId);
        if (taskTemplate == null) {
            return Result.error("任务模板不存在");
        }

        sectTaskTemplateMapper.deleteById(templateId);

        logger.info("任务模板删除成功: templateId={}", templateId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除任务模板
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> templateIds) {
        logger.info("批量删除任务模板: count={}", templateIds.size());

        if (templateIds == null || templateIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        sectTaskTemplateMapper.deleteBatchIds(templateIds);

        logger.info("批量删除成功: count={}", templateIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 获取所有宗门列表（用于下拉选择）
     */
    @GetMapping("/sects")
    public Result<List<Map<String, Object>>> getSects() {
        logger.info("获取所有宗门列表");

        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Sect::getSectName);

        List<Sect> sects = sectMapper.selectList(wrapper);
        List<Map<String, Object>> result = sects.stream()
                .map(sect -> {
                    Map<String, Object> map = Map.of(
                        "sectId", sect.getSectId(),
                        "sectName", sect.getSectName()
                    );
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取所有任务类型（用于下拉选择）
     */
    @GetMapping("/task-types")
    public Result<List<String>> getTaskTypes() {
        logger.info("获取所有任务类型");

        LambdaQueryWrapper<SectTaskTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SectTaskTemplate::getTaskType);
        wrapper.isNotNull(SectTaskTemplate::getTaskType);
        wrapper.groupBy(SectTaskTemplate::getTaskType);
        wrapper.orderByAsc(SectTaskTemplate::getTaskType);

        List<String> taskTypes = sectTaskTemplateMapper.selectList(wrapper).stream()
                .map(SectTaskTemplate::getTaskType)
                .distinct()
                .collect(Collectors.toList());

        return Result.success(taskTypes);
    }

    /**
     * 获取所有目标类型（用于下拉选择）
     */
    @GetMapping("/target-types")
    public Result<List<String>> getTargetTypes() {
        logger.info("获取所有目标类型");

        LambdaQueryWrapper<SectTaskTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SectTaskTemplate::getTargetType);
        wrapper.isNotNull(SectTaskTemplate::getTargetType);
        wrapper.groupBy(SectTaskTemplate::getTargetType);
        wrapper.orderByAsc(SectTaskTemplate::getTargetType);

        List<String> targetTypes = sectTaskTemplateMapper.selectList(wrapper).stream()
                .map(SectTaskTemplate::getTargetType)
                .distinct()
                .collect(Collectors.toList());

        return Result.success(targetTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换为列表项响应DTO
     */
    private SectTaskTemplateListItemResponse convertToListItem(SectTaskTemplate taskTemplate) {
        SectTaskTemplateListItemResponse response = new SectTaskTemplateListItemResponse();
        response.setTemplateId(taskTemplate.getTemplateId());
        response.setSectId(taskTemplate.getSectId());
        response.setTaskType(taskTemplate.getTaskType());
        response.setTaskName(taskTemplate.getTaskName());
        response.setTargetType(taskTemplate.getTargetType());
        response.setTargetCount(taskTemplate.getTargetCount());
        response.setRequiredPosition(taskTemplate.getRequiredPosition());
        response.setContributionReward(taskTemplate.getContributionReward());
        response.setReputationReward(taskTemplate.getReputationReward());
        response.setIsActive(taskTemplate.getIsActive());

        // 获取宗门名称
        if (taskTemplate.getSectId() != null) {
            Sect sect = sectMapper.selectById(taskTemplate.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private SectTaskTemplateDetailResponse convertToDetail(SectTaskTemplate taskTemplate) {
        SectTaskTemplateDetailResponse response = new SectTaskTemplateDetailResponse();
        BeanUtils.copyProperties(taskTemplate, response);

        // 获取宗门名称
        if (taskTemplate.getSectId() != null) {
            Sect sect = sectMapper.selectById(taskTemplate.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }
}
