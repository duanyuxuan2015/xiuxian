package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.SkillCreateRequest;
import com.xiuxian.dto.request.SkillUpdateRequest;
import com.xiuxian.dto.response.SkillDetailResponse;
import com.xiuxian.dto.response.SkillListItemResponse;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.Skill;
import com.xiuxian.mapper.SectMapper;
import com.xiuxian.mapper.SkillMapper;
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
 * 技能配置管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/skill")
public class SkillManagementController {

    private static final Logger logger = LoggerFactory.getLogger(SkillManagementController.class);

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private SectMapper sectMapper;

    /**
     * 分页查询技能列表
     */
    @GetMapping("/list")
    public Result<PageResult<SkillListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long sectId,
            @RequestParam(required = false) String functionType) {

        logger.info("分页查询技能列表: page={}, pageSize={}, keyword={}, sectId={}, functionType={}",
                page, pageSize, keyword, sectId, functionType);

        // 构建查询条件
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Skill::getSkillName, keyword);
        }
        if (sectId != null) {
            wrapper.eq(Skill::getSectId, sectId);
        }
        if (functionType != null && !functionType.isEmpty()) {
            wrapper.eq(Skill::getFunctionType, functionType);
        }
        wrapper.orderByDesc(Skill::getSkillId);

        // 分页查询
        Page<Skill> pageParam = new Page<>(page, pageSize);
        Page<Skill> result = skillMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<SkillListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<SkillListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取技能详情
     */
    @GetMapping("/{skillId}")
    public Result<SkillDetailResponse> getDetail(@PathVariable Long skillId) {
        logger.info("获取技能详情: skillId={}", skillId);

        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            return Result.error("技能不存在");
        }

        SkillDetailResponse response = convertToDetail(skill);
        return Result.success(response);
    }

    /**
     * 创建技能
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SkillCreateRequest request) {
        logger.info("创建技能: skillName={}", request.getSkillName());

        // 验证宗门是否存在（如果指定了宗门）
        if (request.getSectId() != null) {
            Sect sect = sectMapper.selectById(request.getSectId());
            if (sect == null) {
                return Result.error("宗门不存在");
            }
        }

        // 转换并保存
        Skill skill = new Skill();
        BeanUtils.copyProperties(request, skill);
        skillMapper.insert(skill);

        logger.info("技能创建成功: skillId={}", skill.getSkillId());
        return Result.success("创建成功", skill.getSkillId());
    }

    /**
     * 更新技能
     */
    @PutMapping("/{skillId}")
    public Result<Void> update(
            @PathVariable Long skillId,
            @Valid @RequestBody SkillUpdateRequest request) {

        logger.info("更新技能: skillId={}, skillName={}", skillId, request.getSkillName());

        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            return Result.error("技能不存在");
        }

        // 验证宗门是否存在（如果指定了宗门）
        if (request.getSectId() != null) {
            Sect sect = sectMapper.selectById(request.getSectId());
            if (sect == null) {
                return Result.error("宗门不存在");
            }
        }

        // 更新
        BeanUtils.copyProperties(request, skill);
        skillMapper.updateById(skill);

        logger.info("技能更新成功: skillId={}", skillId);
        return Result.success("更新成功");
    }

    /**
     * 删除技能
     */
    @DeleteMapping("/{skillId}")
    public Result<Void> delete(@PathVariable Long skillId) {
        logger.info("删除技能: skillId={}", skillId);

        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            return Result.error("技能不存在");
        }

        skillMapper.deleteById(skillId);

        logger.info("技能删除成功: skillId={}", skillId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除技能
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> skillIds) {
        logger.info("批量删除技能: count={}", skillIds.size());

        if (skillIds == null || skillIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        skillMapper.deleteBatchIds(skillIds);

        logger.info("批量删除成功: count={}", skillIds.size());
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
     * 获取所有功能类型（用于下拉选择）
     */
    @GetMapping("/function-types")
    public Result<List<String>> getFunctionTypes() {
        logger.info("获取所有功能类型");

        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Skill::getFunctionType);
        wrapper.isNotNull(Skill::getFunctionType);
        wrapper.groupBy(Skill::getFunctionType);
        wrapper.orderByAsc(Skill::getFunctionType);

        List<String> functionTypes = skillMapper.selectList(wrapper).stream()
                .map(Skill::getFunctionType)
                .distinct()
                .collect(Collectors.toList());

        return Result.success(functionTypes);
    }

    /**
     * 获取所有元素类型（用于下拉选择）
     */
    @GetMapping("/element-types")
    public Result<List<String>> getElementTypes() {
        logger.info("获取所有元素类型");

        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Skill::getElementType);
        wrapper.isNotNull(Skill::getElementType);
        wrapper.groupBy(Skill::getElementType);
        wrapper.orderByAsc(Skill::getElementType);

        List<String> elementTypes = skillMapper.selectList(wrapper).stream()
                .map(Skill::getElementType)
                .distinct()
                .collect(Collectors.toList());

        return Result.success(elementTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换为列表项响应DTO
     */
    private SkillListItemResponse convertToListItem(Skill skill) {
        SkillListItemResponse response = new SkillListItemResponse();
        response.setSkillId(skill.getSkillId());
        response.setSkillName(skill.getSkillName());
        response.setFunctionType(skill.getFunctionType());
        response.setElementType(skill.getElementType());
        response.setBaseDamage(skill.getBaseDamage());
        response.setSkillMultiplier(skill.getSkillMultiplier());
        response.setTier(skill.getTier());
        response.setSectId(skill.getSectId());
        response.setCost(skill.getCost());

        // 获取宗门名称
        if (skill.getSectId() != null) {
            Sect sect = sectMapper.selectById(skill.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private SkillDetailResponse convertToDetail(Skill skill) {
        SkillDetailResponse response = new SkillDetailResponse();
        BeanUtils.copyProperties(skill, response);

        // 获取宗门名称
        if (skill.getSectId() != null) {
            Sect sect = sectMapper.selectById(skill.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }
}
