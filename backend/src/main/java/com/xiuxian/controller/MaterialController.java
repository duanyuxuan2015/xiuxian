package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.MaterialCreateRequest;
import com.xiuxian.dto.request.MaterialUpdateRequest;
import com.xiuxian.dto.response.MaterialDetailResponse;
import com.xiuxian.dto.response.MaterialListItemResponse;
import com.xiuxian.entity.Material;
import com.xiuxian.mapper.MaterialMapper;
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
 * 材料管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/material")
public class MaterialController {

    private static final Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 分页查询材料列表
     * GET /api/v1/admin/material/list
     */
    @GetMapping("/list")
    public Result<PageResult<MaterialListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String quality,
            @RequestParam(required = false) String materialType) {

        logger.info("分页查询材料列表: page={}, pageSize={}, keyword={}, quality={}, materialType={}",
                page, pageSize, keyword, quality, materialType);

        // 构建查询条件
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Material::getMaterialName, keyword);
        }
        if (quality != null && !quality.isEmpty()) {
            wrapper.eq(Material::getQuality, quality);
        }
        if (materialType != null && !materialType.isEmpty()) {
            wrapper.eq(Material::getMaterialType, materialType);
        }
        wrapper.orderByDesc(Material::getCreatedAt);

        // 分页查询
        Page<Material> pageParam = new Page<>(page, pageSize);
        Page<Material> result = materialMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<MaterialListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<MaterialListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取材料详情
     * GET /api/v1/admin/material/{materialId}
     */
    @GetMapping("/{materialId}")
    public Result<MaterialDetailResponse> getDetail(@PathVariable Long materialId) {
        logger.info("获取材料详情: materialId={}", materialId);

        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            return Result.error("材料不存在");
        }

        MaterialDetailResponse response = convertToDetail(material);
        return Result.success(response);
    }

    /**
     * 创建材料
     * POST /api/v1/admin/material
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MaterialCreateRequest request) {
        logger.info("创建材料: materialName={}", request.getMaterialName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getMaterialName(), null)) {
            return Result.error("材料名称已存在");
        }

        // 转换并保存
        Material material = new Material();
        BeanUtils.copyProperties(request, material);
        materialMapper.insert(material);

        logger.info("材料创建成功: materialId={}", material.getMaterialId());
        return Result.success("创建成功", material.getMaterialId());
    }

    /**
     * 更新材料
     * PUT /api/v1/admin/material/{materialId}
     */
    @PutMapping("/{materialId}")
    public Result<Void> update(
            @PathVariable Long materialId,
            @Valid @RequestBody MaterialUpdateRequest request) {

        logger.info("更新材料: materialId={}, materialName={}", materialId, request.getMaterialName());

        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            return Result.error("材料不存在");
        }

        // 检查名称唯一性（只有当名称发生变化时才检查）
        if (request.getMaterialName() != null &&
            !request.getMaterialName().isEmpty() &&
            !request.getMaterialName().equals(material.getMaterialName()) &&
            !checkNameUnique(request.getMaterialName(), materialId)) {
            logger.error("材料名称已存在: name={}, materialId={}", request.getMaterialName(), materialId);
            return Result.error("材料名称已存在");
        }

        // 更新
        BeanUtils.copyProperties(request, material);
        materialMapper.updateById(material);

        logger.info("材料更新成功: materialId={}", materialId);
        return Result.success("更新成功");
    }

    /**
     * 删除材料
     * DELETE /api/v1/admin/material/{materialId}
     */
    @DeleteMapping("/{materialId}")
    public Result<Void> delete(@PathVariable Long materialId) {
        logger.info("删除材料: materialId={}", materialId);

        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            return Result.error("材料不存在");
        }

        materialMapper.deleteById(materialId);

        logger.info("材料删除成功: materialId={}", materialId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除材料
     * DELETE /api/v1/admin/material/batch
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> materialIds) {
        logger.info("批量删除材料: count={}", materialIds.size());

        if (materialIds == null || materialIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        materialMapper.deleteBatchIds(materialIds);

        logger.info("批量删除成功: count={}", materialIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 检查名称唯一性
     * GET /api/v1/admin/material/check-name
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
     * 获取所有材料类型列表
     * GET /api/v1/admin/material/material-types
     */
    @GetMapping("/material-types")
    public Result<List<String>> getMaterialTypes() {
        logger.info("获取所有材料类型列表");

        // 查询所有不重复的材料类型
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Material::getMaterialType);
        wrapper.isNotNull(Material::getMaterialType);
        wrapper.groupBy(Material::getMaterialType);
        wrapper.orderByAsc(Material::getMaterialType);

        List<Material> materials = materialMapper.selectList(wrapper);
        List<String> materialTypes = materials.stream()
                .map(Material::getMaterialType)
                .collect(Collectors.toList());

        logger.info("获取材料类型列表成功: count={}", materialTypes.size());
        return Result.success(materialTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String materialName, Long excludeId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getMaterialName, materialName);
        if (excludeId != null) {
            wrapper.ne(Material::getMaterialId, excludeId);
        }
        return materialMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为列表项响应DTO
     */
    private MaterialListItemResponse convertToListItem(Material material) {
        MaterialListItemResponse response = new MaterialListItemResponse();
        response.setMaterialId(material.getMaterialId());
        response.setMaterialName(material.getMaterialName());
        response.setMaterialType(material.getMaterialType());
        response.setMaterialTier(material.getMaterialTier());
        response.setQuality(material.getQuality());
        response.setSpiritStones(material.getSpiritStones());
        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private MaterialDetailResponse convertToDetail(Material material) {
        MaterialDetailResponse response = new MaterialDetailResponse();
        BeanUtils.copyProperties(material, response);
        return response;
    }
}
