package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.SectCreateRequest;
import com.xiuxian.dto.request.SectUpdateRequest;
import com.xiuxian.dto.response.SectDetailResponse;
import com.xiuxian.dto.response.SectListItemResponse;
import com.xiuxian.entity.Sect;
import com.xiuxian.mapper.SectMapper;
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
import java.util.stream.Collectors;

/**
 * 宗门配置管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/sect")
public class SectManagementController {

    private static final Logger logger = LoggerFactory.getLogger(SectManagementController.class);

    @Autowired
    private SectMapper sectMapper;

    /**
     * 分页查询宗门列表
     */
    @GetMapping("/list")
    public Result<PageResult<SectListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sectType) {

        logger.info("分页查询宗门列表: page={}, pageSize={}, keyword={}, sectType={}",
                page, pageSize, keyword, sectType);

        // 构建查询条件
        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Sect::getSectName, keyword);
        }
        if (sectType != null && !sectType.isEmpty()) {
            wrapper.eq(Sect::getSectType, sectType);
        }
        wrapper.orderByDesc(Sect::getCreatedAt);

        // 分页查询
        Page<Sect> pageParam = new Page<>(page, pageSize);
        Page<Sect> result = sectMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<SectListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<SectListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取宗门详情
     */
    @GetMapping("/{sectId}")
    public Result<SectDetailResponse> getDetail(@PathVariable Long sectId) {
        logger.info("获取宗门详情: sectId={}", sectId);

        Sect sect = sectMapper.selectById(sectId);
        if (sect == null) {
            return Result.error("宗门不存在");
        }

        SectDetailResponse response = convertToDetail(sect);
        return Result.success(response);
    }

    /**
     * 创建宗门
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SectCreateRequest request) {
        logger.info("创建宗门: sectName={}", request.getSectName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getSectName(), null)) {
            return Result.error("宗门名称已存在");
        }

        // 转换并保存
        Sect sect = new Sect();
        BeanUtils.copyProperties(request, sect);
        sectMapper.insert(sect);

        logger.info("宗门创建成功: sectId={}", sect.getSectId());
        return Result.success("创建成功", sect.getSectId());
    }

    /**
     * 更新宗门
     */
    @PutMapping("/{sectId}")
    public Result<Void> update(
            @PathVariable Long sectId,
            @Valid @RequestBody SectUpdateRequest request) {

        logger.info("更新宗门: sectId={}, sectName={}", sectId, request.getSectName());

        Sect sect = sectMapper.selectById(sectId);
        if (sect == null) {
            return Result.error("宗门不存在");
        }

        // 检查名称唯一性
        if (request.getSectName() != null &&
            !request.getSectName().isEmpty() &&
            !request.getSectName().equals(sect.getSectName()) &&
            !checkNameUnique(request.getSectName(), sectId)) {
            return Result.error("宗门名称已存在");
        }

        // 更新
        BeanUtils.copyProperties(request, sect);
        sectMapper.updateById(sect);

        logger.info("宗门更新成功: sectId={}", sectId);
        return Result.success("更新成功");
    }

    /**
     * 删除宗门
     */
    @DeleteMapping("/{sectId}")
    public Result<Void> delete(@PathVariable Long sectId) {
        logger.info("删除宗门: sectId={}", sectId);

        Sect sect = sectMapper.selectById(sectId);
        if (sect == null) {
            return Result.error("宗门不存在");
        }

        sectMapper.deleteById(sectId);

        logger.info("宗门删除成功: sectId={}", sectId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除宗门
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> sectIds) {
        logger.info("批量删除宗门: count={}", sectIds.size());

        if (sectIds == null || sectIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        sectMapper.deleteBatchIds(sectIds);

        logger.info("批量删除成功: count={}", sectIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 检查名称唯一性
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkName(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {

        logger.info("检查名称唯一性: name={}, excludeId={}", name, excludeId);

        boolean isUnique = checkNameUnique(name, excludeId);
        return Result.success(isUnique);
    }

    /**
     * 获取所有宗门类型列表
     */
    @GetMapping("/sect-types")
    public Result<List<String>> getSectTypes() {
        logger.info("获取所有宗门类型列表");

        // 查询所有不重复的宗门类型
        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Sect::getSectType);
        wrapper.isNotNull(Sect::getSectType);
        wrapper.groupBy(Sect::getSectType);
        wrapper.orderByAsc(Sect::getSectType);

        List<Sect> sects = sectMapper.selectList(wrapper);
        List<String> sectTypes = sects.stream()
                .map(Sect::getSectType)
                .collect(Collectors.toList());

        logger.info("获取宗门类型列表成功: count={}", sectTypes.size());
        return Result.success(sectTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String sectName, Long excludeId) {
        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sect::getSectName, sectName);
        if (excludeId != null) {
            wrapper.ne(Sect::getSectId, excludeId);
        }
        return sectMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为列表项响应DTO
     */
    private SectListItemResponse convertToListItem(Sect sect) {
        SectListItemResponse response = new SectListItemResponse();
        response.setSectId(sect.getSectId());
        response.setSectName(sect.getSectName());
        response.setSectType(sect.getSectType());
        response.setRequiredRealmLevel(sect.getRequiredRealmLevel());
        response.setSpecialty(sect.getSpecialty());
        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private SectDetailResponse convertToDetail(Sect sect) {
        SectDetailResponse response = new SectDetailResponse();
        BeanUtils.copyProperties(sect, response);
        return response;
    }
}
