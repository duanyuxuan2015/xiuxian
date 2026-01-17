package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.PillRecipeCreateRequest;
import com.xiuxian.dto.request.PillRecipeUpdateRequest;
import com.xiuxian.dto.response.PillRecipeDetailResponse;
import com.xiuxian.dto.response.PillRecipeListItemResponse;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.PillRecipe;
import com.xiuxian.entity.PillRecipeMaterial;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.mapper.PillRecipeMapper;
import com.xiuxian.mapper.PillRecipeMaterialMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 丹方管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/pill-recipe")
public class PillRecipeController {

    private static final Logger logger = LoggerFactory.getLogger(PillRecipeController.class);

    @Autowired
    private PillRecipeMapper pillRecipeMapper;

    @Autowired
    private PillRecipeMaterialMapper pillRecipeMaterialMapper;

    @Autowired
    private PillMapper pillMapper;

    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 分页查询丹方列表
     */
    @GetMapping("/list")
    public Result<PageResult<PillRecipeListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        logger.info("分页查询丹方列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);

        // 构建查询条件
        LambdaQueryWrapper<PillRecipe> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PillRecipe::getRecipeName, keyword);
        }
        wrapper.orderByDesc(PillRecipe::getCreatedAt);

        // 分页查询
        Page<PillRecipe> pageParam = new Page<>(page, pageSize);
        Page<PillRecipe> result = pillRecipeMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<PillRecipeListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<PillRecipeListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取丹方详情
     */
    @GetMapping("/{recipeId}")
    public Result<PillRecipeDetailResponse> getDetail(@PathVariable Long recipeId) {
        logger.info("获取丹方详情: recipeId={}", recipeId);

        PillRecipe pillRecipe = pillRecipeMapper.selectById(recipeId);
        if (pillRecipe == null) {
            return Result.error("丹方不存在");
        }

        PillRecipeDetailResponse response = convertToDetail(pillRecipe);
        return Result.success(response);
    }

    /**
     * 创建丹方
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> create(@Valid @RequestBody PillRecipeCreateRequest request) {
        logger.info("创建丹方: recipeName={}", request.getRecipeName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getRecipeName(), null)) {
            return Result.error("丹方名称已存在");
        }

        // 验证pillId是否存在
        Pill pill = pillMapper.selectById(request.getPillId());
        if (pill == null) {
            return Result.error("产出的丹药不存在");
        }

        // 验证材料是否存在
        if (request.getMaterials() != null && !request.getMaterials().isEmpty()) {
            for (PillRecipeCreateRequest.PillRecipeMaterialRequest materialReq : request.getMaterials()) {
                Material material = materialMapper.selectById(materialReq.getMaterialId());
                if (material == null) {
                    return Result.error("材料ID不存在: " + materialReq.getMaterialId());
                }
            }
        }

        // 转换并保存丹方
        PillRecipe pillRecipe = new PillRecipe();
        BeanUtils.copyProperties(request, pillRecipe);
        pillRecipeMapper.insert(pillRecipe);

        // 保存丹方材料
        if (request.getMaterials() != null && !request.getMaterials().isEmpty()) {
            for (PillRecipeCreateRequest.PillRecipeMaterialRequest materialReq : request.getMaterials()) {
                PillRecipeMaterial recipeMaterial = new PillRecipeMaterial();
                recipeMaterial.setRecipeId(pillRecipe.getRecipeId());
                recipeMaterial.setMaterialId(materialReq.getMaterialId());
                recipeMaterial.setQuantityRequired(materialReq.getQuantityRequired());
                recipeMaterial.setIsMainMaterial(materialReq.getIsMainMaterial());
                pillRecipeMaterialMapper.insert(recipeMaterial);
            }
        }

        logger.info("丹方创建成功: recipeId={}", pillRecipe.getRecipeId());
        return Result.success("创建成功", pillRecipe.getRecipeId());
    }

    /**
     * 更新丹方
     */
    @PutMapping("/{recipeId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(
            @PathVariable Long recipeId,
            @Valid @RequestBody PillRecipeUpdateRequest request) {

        logger.info("更新丹方: recipeId={}, recipeName={}", recipeId, request.getRecipeName());

        PillRecipe pillRecipe = pillRecipeMapper.selectById(recipeId);
        if (pillRecipe == null) {
            return Result.error("丹方不存在");
        }

        // 检查名称唯一性
        if (request.getRecipeName() != null &&
            !request.getRecipeName().isEmpty() &&
            !request.getRecipeName().equals(pillRecipe.getRecipeName()) &&
            !checkNameUnique(request.getRecipeName(), recipeId)) {
            return Result.error("丹方名称已存在");
        }

        // 验证pillId是否存在
        if (request.getPillId() != null) {
            Pill pill = pillMapper.selectById(request.getPillId());
            if (pill == null) {
                return Result.error("产出的丹药不存在");
            }
        }

        // 验证材料是否存在
        if (request.getMaterials() != null && !request.getMaterials().isEmpty()) {
            for (PillRecipeUpdateRequest.PillRecipeMaterialRequest materialReq : request.getMaterials()) {
                if (materialReq.getMaterialId() != null) {
                    Material material = materialMapper.selectById(materialReq.getMaterialId());
                    if (material == null) {
                        return Result.error("材料ID不存在: " + materialReq.getMaterialId());
                    }
                }
            }
        }

        // 更新丹方
        BeanUtils.copyProperties(request, pillRecipe);
        pillRecipeMapper.updateById(pillRecipe);

        // 更新丹方材料：先删除原有的，再插入新的
        if (request.getMaterials() != null) {
            // 删除原有材料
            LambdaQueryWrapper<PillRecipeMaterial> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(PillRecipeMaterial::getRecipeId, recipeId);
            pillRecipeMaterialMapper.delete(deleteWrapper);

            // 插入新材料
            for (PillRecipeUpdateRequest.PillRecipeMaterialRequest materialReq : request.getMaterials()) {
                PillRecipeMaterial recipeMaterial = new PillRecipeMaterial();
                recipeMaterial.setRecipeId(recipeId);
                recipeMaterial.setMaterialId(materialReq.getMaterialId());
                recipeMaterial.setQuantityRequired(materialReq.getQuantityRequired());
                recipeMaterial.setIsMainMaterial(materialReq.getIsMainMaterial());
                pillRecipeMaterialMapper.insert(recipeMaterial);
            }
        }

        logger.info("丹方更新成功: recipeId={}", recipeId);
        return Result.success("更新成功");
    }

    /**
     * 删除丹方
     */
    @DeleteMapping("/{recipeId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long recipeId) {
        logger.info("删除丹方: recipeId={}", recipeId);

        PillRecipe pillRecipe = pillRecipeMapper.selectById(recipeId);
        if (pillRecipe == null) {
            return Result.error("丹方不存在");
        }

        // 删除丹方材料
        LambdaQueryWrapper<PillRecipeMaterial> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PillRecipeMaterial::getRecipeId, recipeId);
        pillRecipeMaterialMapper.delete(deleteWrapper);

        // 删除丹方
        pillRecipeMapper.deleteById(recipeId);

        logger.info("丹方删除成功: recipeId={}", recipeId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除丹方
     */
    @DeleteMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(@RequestBody List<Long> recipeIds) {
        logger.info("批量删除丹方: count={}", recipeIds.size());

        if (recipeIds == null || recipeIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        // 删除丹方材料
        for (Long recipeId : recipeIds) {
            LambdaQueryWrapper<PillRecipeMaterial> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(PillRecipeMaterial::getRecipeId, recipeId);
            pillRecipeMaterialMapper.delete(deleteWrapper);
        }

        // 删除丹方
        pillRecipeMapper.deleteBatchIds(recipeIds);

        logger.info("批量删除成功: count={}", recipeIds.size());
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
     * 获取所有丹药列表（用于下拉选择）
     */
    @GetMapping("/pills")
    public Result<List<Map<String, Object>>> getPills() {
        logger.info("获取所有丹药列表");

        LambdaQueryWrapper<Pill> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Pill::getPillName);

        List<Pill> pills = pillMapper.selectList(wrapper);
        List<Map<String, Object>> result = pills.stream()
                .map(pill -> {
                    Map<String, Object> map = Map.of(
                        "pillId", pill.getPillId(),
                        "pillName", pill.getPillName()
                    );
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 获取所有材料列表（用于下拉选择）
     */
    @GetMapping("/materials")
    public Result<List<Map<String, Object>>> getMaterials() {
        logger.info("获取所有材料列表");

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Material::getMaterialName);

        List<Material> materials = materialMapper.selectList(wrapper);
        List<Map<String, Object>> result = materials.stream()
                .map(material -> {
                    Map<String, Object> map = Map.of(
                        "materialId", material.getMaterialId(),
                        "materialName", material.getMaterialName()
                    );
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String recipeName, Long excludeId) {
        LambdaQueryWrapper<PillRecipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PillRecipe::getRecipeName, recipeName);
        if (excludeId != null) {
            wrapper.ne(PillRecipe::getRecipeId, excludeId);
        }
        return pillRecipeMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为列表项响应DTO
     */
    private PillRecipeListItemResponse convertToListItem(PillRecipe pillRecipe) {
        PillRecipeListItemResponse response = new PillRecipeListItemResponse();
        response.setRecipeId(pillRecipe.getRecipeId());
        response.setRecipeName(pillRecipe.getRecipeName());
        response.setRecipeTier(pillRecipe.getRecipeTier());
        response.setOutputQuantity(pillRecipe.getOutputQuantity());
        response.setBaseSuccessRate(pillRecipe.getBaseSuccessRate());

        // 获取丹药名称
        if (pillRecipe.getPillId() != null) {
            Pill pill = pillMapper.selectById(pillRecipe.getPillId());
            if (pill != null) {
                response.setPillName(pill.getPillName());
            }
        }

        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private PillRecipeDetailResponse convertToDetail(PillRecipe pillRecipe) {
        PillRecipeDetailResponse response = new PillRecipeDetailResponse();
        BeanUtils.copyProperties(pillRecipe, response);

        // 获取丹药名称
        if (pillRecipe.getPillId() != null) {
            Pill pill = pillMapper.selectById(pillRecipe.getPillId());
            if (pill != null) {
                response.setPillName(pill.getPillName());
            }
        }

        // 获取丹方材料列表
        LambdaQueryWrapper<PillRecipeMaterial> materialWrapper = new LambdaQueryWrapper<>();
        materialWrapper.eq(PillRecipeMaterial::getRecipeId, pillRecipe.getRecipeId());
        List<PillRecipeMaterial> materials = pillRecipeMaterialMapper.selectList(materialWrapper);

        List<PillRecipeDetailResponse.PillRecipeMaterialResponse> materialResponses =
            new ArrayList<>();
        for (PillRecipeMaterial material : materials) {
            PillRecipeDetailResponse.PillRecipeMaterialResponse materialResponse =
                new PillRecipeDetailResponse.PillRecipeMaterialResponse();
            materialResponse.setRecipeMaterialId(material.getRecipeMaterialId());
            materialResponse.setMaterialId(material.getMaterialId());
            materialResponse.setQuantityRequired(material.getQuantityRequired());
            materialResponse.setIsMainMaterial(material.getIsMainMaterial());

            // 获取材料名称
            Material mat = materialMapper.selectById(material.getMaterialId());
            if (mat != null) {
                materialResponse.setMaterialName(mat.getMaterialName());
            }

            materialResponses.add(materialResponse);
        }
        response.setMaterials(materialResponses);

        return response;
    }
}
