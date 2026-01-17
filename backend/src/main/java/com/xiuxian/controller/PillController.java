package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.PillCreateRequest;
import com.xiuxian.dto.request.PillUpdateRequest;
import com.xiuxian.dto.response.PillDetailResponse;
import com.xiuxian.dto.response.PillListItemResponse;
import com.xiuxian.entity.Pill;
import com.xiuxian.mapper.PillMapper;
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
 * 丹药管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/pill")
public class PillController {

    private static final Logger logger = LoggerFactory.getLogger(PillController.class);

    @Autowired
    private PillMapper pillMapper;

    /**
     * 分页查询丹药列表
     * GET /api/v1/admin/pill/list
     */
    @GetMapping("/list")
    public Result<PageResult<PillListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String quality,
            @RequestParam(required = false) String effectType) {

        logger.info("分页查询丹药列表: page={}, pageSize={}, keyword={}, quality={}, effectType={}",
                page, pageSize, keyword, quality, effectType);

        // 构建查询条件
        LambdaQueryWrapper<Pill> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Pill::getPillName, keyword);
        }
        if (quality != null && !quality.isEmpty()) {
            wrapper.eq(Pill::getQuality, quality);
        }
        if (effectType != null && !effectType.isEmpty()) {
            wrapper.eq(Pill::getEffectType, effectType);
        }
        wrapper.orderByDesc(Pill::getCreatedAt);

        // 分页查询
        Page<Pill> pageParam = new Page<>(page, pageSize);
        Page<Pill> result = pillMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<PillListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<PillListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取丹药详情
     * GET /api/v1/admin/pill/{pillId}
     */
    @GetMapping("/{pillId}")
    public Result<PillDetailResponse> getDetail(@PathVariable Long pillId) {
        logger.info("获取丹药详情: pillId={}", pillId);

        Pill pill = pillMapper.selectById(pillId);
        if (pill == null) {
            return Result.error("丹药不存在");
        }

        PillDetailResponse response = convertToDetail(pill);
        return Result.success(response);
    }

    /**
     * 创建丹药
     * POST /api/v1/admin/pill
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PillCreateRequest request) {
        logger.info("创建丹药: pillName={}", request.getPillName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getPillName(), null)) {
            return Result.error("丹药名称已存在");
        }

        // 转换并保存
        Pill pill = new Pill();
        BeanUtils.copyProperties(request, pill);
        pillMapper.insert(pill);

        logger.info("丹药创建成功: pillId={}", pill.getPillId());
        return Result.success("创建成功", pill.getPillId());
    }

    /**
     * 更新丹药
     * PUT /api/v1/admin/pill/{pillId}
     */
    @PutMapping("/{pillId}")
    public Result<Void> update(
            @PathVariable Long pillId,
            @Valid @RequestBody PillUpdateRequest request) {

        logger.info("更新丹药: pillId={}, pillName={}", pillId, request.getPillName());

        Pill pill = pillMapper.selectById(pillId);
        if (pill == null) {
            return Result.error("丹药不存在");
        }

        // 检查名称唯一性（只有当名称发生变化时才检查）
        if (request.getPillName() != null &&
            !request.getPillName().isEmpty() &&
            !request.getPillName().equals(pill.getPillName()) &&
            !checkNameUnique(request.getPillName(), pillId)) {
            logger.error("丹药名称已存在: name={}, pillId={}", request.getPillName(), pillId);
            return Result.error("丹药名称已存在");
        }

        // 更新
        BeanUtils.copyProperties(request, pill);
        pillMapper.updateById(pill);

        logger.info("丹药更新成功: pillId={}", pillId);
        return Result.success("更新成功");
    }

    /**
     * 删除丹药
     * DELETE /api/v1/admin/pill/{pillId}
     */
    @DeleteMapping("/{pillId}")
    public Result<Void> delete(@PathVariable Long pillId) {
        logger.info("删除丹药: pillId={}", pillId);

        Pill pill = pillMapper.selectById(pillId);
        if (pill == null) {
            return Result.error("丹药不存在");
        }

        pillMapper.deleteById(pillId);

        logger.info("丹药删除成功: pillId={}", pillId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除丹药
     * DELETE /api/v1/admin/pill/batch
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> pillIds) {
        logger.info("批量删除丹药: count={}", pillIds.size());

        if (pillIds == null || pillIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        pillMapper.deleteBatchIds(pillIds);

        logger.info("批量删除成功: count={}", pillIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 检查名称唯一性
     * GET /api/v1/admin/pill/check-name
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
     * 获取所有效果类型列表
     * GET /api/v1/admin/pill/effect-types
     */
    @GetMapping("/effect-types")
    public Result<List<String>> getEffectTypes() {
        logger.info("获取所有效果类型列表");

        // 查询所有不重复的效果类型
        LambdaQueryWrapper<Pill> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Pill::getEffectType);
        wrapper.isNotNull(Pill::getEffectType);
        wrapper.groupBy(Pill::getEffectType);
        wrapper.orderByAsc(Pill::getEffectType);

        List<Pill> pills = pillMapper.selectList(wrapper);
        List<String> effectTypes = pills.stream()
                .map(Pill::getEffectType)
                .collect(Collectors.toList());

        logger.info("获取效果类型列表成功: count={}", effectTypes.size());
        return Result.success(effectTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String pillName, Long excludeId) {
        LambdaQueryWrapper<Pill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pill::getPillName, pillName);
        if (excludeId != null) {
            wrapper.ne(Pill::getPillId, excludeId);
        }
        return pillMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为列表项响应DTO
     */
    private PillListItemResponse convertToListItem(Pill pill) {
        PillListItemResponse response = new PillListItemResponse();
        response.setPillId(pill.getPillId());
        response.setPillName(pill.getPillName());
        response.setPillTier(pill.getPillTier());
        response.setQuality(pill.getQuality());
        response.setEffectType(pill.getEffectType());
        response.setEffectValue(pill.getEffectValue());
        response.setSpiritStones(pill.getSpiritStones());
        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private PillDetailResponse convertToDetail(Pill pill) {
        PillDetailResponse response = new PillDetailResponse();
        BeanUtils.copyProperties(pill, response);
        return response;
    }
}
