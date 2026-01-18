package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.EquipmentCreateRequest;
import com.xiuxian.dto.request.EquipmentUpdateRequest;
import com.xiuxian.dto.response.EquipmentDetailResponse;
import com.xiuxian.dto.response.EquipmentListItemResponse;
import com.xiuxian.entity.Equipment;
import com.xiuxian.mapper.EquipmentMapper;
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
 * 装备管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/equipment")
public class EquipmentAdminController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentAdminController.class);

    @Autowired
    private EquipmentMapper equipmentMapper;

    /**
     * 分页查询装备列表
     * GET /api/v1/admin/equipment/list
     */
    @GetMapping("/list")
    public Result<PageResult<EquipmentListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String equipmentType,
            @RequestParam(required = false) String quality) {

        logger.info("分页查询装备列表: page={}, pageSize={}, keyword={}, type={}, quality={}",
                page, pageSize, keyword, equipmentType, quality);

        // 构建查询条件
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Equipment::getEquipmentName, keyword);
        }
        if (equipmentType != null && !equipmentType.isEmpty()) {
            wrapper.eq(Equipment::getEquipmentType, equipmentType);
        }
        if (quality != null && !quality.isEmpty()) {
            wrapper.eq(Equipment::getQuality, quality);
        }
        wrapper.orderByDesc(Equipment::getCreatedAt);

        // 分页查询
        Page<Equipment> pageParam = new Page<>(page, pageSize);
        Page<Equipment> result = equipmentMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<EquipmentListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<EquipmentListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取装备详情
     * GET /api/v1/admin/equipment/{equipmentId}
     */
    @GetMapping("/{equipmentId}")
    public Result<EquipmentDetailResponse> getDetail(@PathVariable Long equipmentId) {
        logger.info("获取装备详情: equipmentId={}", equipmentId);

        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return Result.error("装备不存在");
        }

        EquipmentDetailResponse response = convertToDetail(equipment);
        return Result.success(response);
    }

    /**
     * 创建装备
     * POST /api/v1/admin/equipment
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody EquipmentCreateRequest request) {
        logger.info("创建装备: equipmentName={}", request.getEquipmentName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getEquipmentName(), null)) {
            return Result.error("装备名称已存在");
        }

        // 转换并保存
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(request, equipment);
        equipmentMapper.insert(equipment);

        logger.info("装备创建成功: equipmentId={}", equipment.getEquipmentId());
        return Result.success("创建成功", equipment.getEquipmentId());
    }

    /**
     * 更新装备
     * PUT /api/v1/admin/equipment/{equipmentId}
     */
    @PutMapping("/{equipmentId}")
    public Result<Void> update(
            @PathVariable Long equipmentId,
            @Valid @RequestBody EquipmentUpdateRequest request) {

        logger.info("更新装备: equipmentId={}, equipmentName={}", equipmentId, request.getEquipmentName());

        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return Result.error("装备不存在");
        }

        logger.info("数据库中当前装备名称: {}", equipment.getEquipmentName());

        // 打印接收到的抗性属性
        logger.info("接收到的抗性属性: physicalResist={}, iceResist={}, fireResist={}, lightningResist={}",
                request.getPhysicalResist(), request.getIceResist(), request.getFireResist(), request.getLightningResist());

        // 检查名称唯一性（只有当名称发生变化时才检查）
        if (request.getEquipmentName() != null &&
            !request.getEquipmentName().isEmpty() &&
            !request.getEquipmentName().equals(equipment.getEquipmentName()) &&
            !checkNameUnique(request.getEquipmentName(), equipmentId)) {
            logger.error("装备名称已存在: name={}, equipmentId={}", request.getEquipmentName(), equipmentId);
            return Result.error("装备名称已存在");
        }

        // 更新前打印
        logger.info("更新前装备抗性: physicalResist={}, iceResist={}, fireResist={}, lightningResist={}",
                equipment.getPhysicalResist(), equipment.getIceResist(), equipment.getFireResist(), equipment.getLightningResist());

        // 更新
        BeanUtils.copyProperties(request, equipment);

        // 更新后打印
        logger.info("更新后装备抗性: physicalResist={}, iceResist={}, fireResist={}, lightningResist={}",
                equipment.getPhysicalResist(), equipment.getIceResist(), equipment.getFireResist(), equipment.getLightningResist());

        equipmentMapper.updateById(equipment);

        logger.info("装备更新成功: equipmentId={}", equipmentId);
        return Result.success("更新成功");
    }

    /**
     * 删除装备
     * DELETE /api/v1/admin/equipment/{equipmentId}
     */
    @DeleteMapping("/{equipmentId}")
    public Result<Void> delete(@PathVariable Long equipmentId) {
        logger.info("删除装备: equipmentId={}", equipmentId);

        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            return Result.error("装备不存在");
        }

        equipmentMapper.deleteById(equipmentId);

        logger.info("装备删除成功: equipmentId={}", equipmentId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除装备
     * DELETE /api/v1/admin/equipment/batch
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> equipmentIds) {
        logger.info("批量删除装备: count={}", equipmentIds.size());

        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        equipmentMapper.deleteBatchIds(equipmentIds);

        logger.info("批量删除成功: count={}", equipmentIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 检查名称唯一性
     * GET /api/v1/admin/equipment/check-name
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkName(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {

        logger.info("检查名称唯一性: name={}, excludeId={}", name, excludeId);

        boolean isUnique = checkNameUnique(name, excludeId);
        return Result.success(isUnique);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String equipmentName, Long excludeId) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getEquipmentName, equipmentName);
        if (excludeId != null) {
            wrapper.ne(Equipment::getEquipmentId, excludeId);
        }
        long count = equipmentMapper.selectCount(wrapper);
        logger.info("检查名称唯一性: name={}, excludeId={}, count={}", equipmentName, excludeId, count);
        return count == 0;
    }

    /**
     * 转换为列表项响应DTO
     */
    private EquipmentListItemResponse convertToListItem(Equipment equipment) {
        EquipmentListItemResponse response = new EquipmentListItemResponse();
        response.setEquipmentId(equipment.getEquipmentId());
        response.setEquipmentName(equipment.getEquipmentName());
        response.setEquipmentType(equipment.getEquipmentType());
        response.setQuality(equipment.getQuality());
        response.setBaseScore(equipment.getBaseScore());
        response.setAttackPower(equipment.getAttackPower());
        response.setDefensePower(equipment.getDefensePower());
        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private EquipmentDetailResponse convertToDetail(Equipment equipment) {
        EquipmentDetailResponse response = new EquipmentDetailResponse();
        BeanUtils.copyProperties(equipment, response);
        return response;
    }
}
