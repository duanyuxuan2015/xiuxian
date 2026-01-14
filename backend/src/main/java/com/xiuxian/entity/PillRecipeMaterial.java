package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 丹方材料关联实体
 */
@TableName("pill_recipe_material")
public class PillRecipeMaterial {

    @TableId(type = IdType.AUTO)
    private Long recipeMaterialId;

    private Long recipeId;

    private Long materialId;

    private Integer quantityRequired;

    private Integer isMainMaterial;

    private LocalDateTime createdAt;

    public PillRecipeMaterial() {
    }

    public Long getRecipeMaterialId() {
        return recipeMaterialId;
    }

    public void setRecipeMaterialId(Long recipeMaterialId) {
        this.recipeMaterialId = recipeMaterialId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Integer getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Integer quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public Integer getIsMainMaterial() {
        return isMainMaterial;
    }

    public void setIsMainMaterial(Integer isMainMaterial) {
        this.isMainMaterial = isMainMaterial;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
