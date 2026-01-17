package com.xiuxian.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 丹方详情响应DTO
 */
public class PillRecipeDetailResponse {

    private Long recipeId;
    private String recipeName;
    private Long pillId;
    private String pillName;
    private Integer outputQuantity;
    private Integer baseSuccessRate;
    private Integer alchemyLevelRequired;
    private Integer spiritualCost;
    private Integer staminaCost;
    private Integer duration;
    private Integer recipeTier;
    private String unlockMethod;
    private Integer unlockCost;
    private String description;
    private List<PillRecipeMaterialResponse> materials;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class PillRecipeMaterialResponse {
        private Long recipeMaterialId;
        private Long materialId;
        private String materialName;
        private Integer quantityRequired;
        private Integer isMainMaterial;

        public Long getRecipeMaterialId() {
            return recipeMaterialId;
        }

        public void setRecipeMaterialId(Long recipeMaterialId) {
            this.recipeMaterialId = recipeMaterialId;
        }

        public Long getMaterialId() {
            return materialId;
        }

        public void setMaterialId(Long materialId) {
            this.materialId = materialId;
        }

        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
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
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Long getPillId() {
        return pillId;
    }

    public void setPillId(Long pillId) {
        this.pillId = pillId;
    }

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public Integer getOutputQuantity() {
        return outputQuantity;
    }

    public void setOutputQuantity(Integer outputQuantity) {
        this.outputQuantity = outputQuantity;
    }

    public Integer getBaseSuccessRate() {
        return baseSuccessRate;
    }

    public void setBaseSuccessRate(Integer baseSuccessRate) {
        this.baseSuccessRate = baseSuccessRate;
    }

    public Integer getAlchemyLevelRequired() {
        return alchemyLevelRequired;
    }

    public void setAlchemyLevelRequired(Integer alchemyLevelRequired) {
        this.alchemyLevelRequired = alchemyLevelRequired;
    }

    public Integer getSpiritualCost() {
        return spiritualCost;
    }

    public void setSpiritualCost(Integer spiritualCost) {
        this.spiritualCost = spiritualCost;
    }

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRecipeTier() {
        return recipeTier;
    }

    public void setRecipeTier(Integer recipeTier) {
        this.recipeTier = recipeTier;
    }

    public String getUnlockMethod() {
        return unlockMethod;
    }

    public void setUnlockMethod(String unlockMethod) {
        this.unlockMethod = unlockMethod;
    }

    public Integer getUnlockCost() {
        return unlockCost;
    }

    public void setUnlockCost(Integer unlockCost) {
        this.unlockCost = unlockCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PillRecipeMaterialResponse> getMaterials() {
        return materials;
    }

    public void setMaterials(List<PillRecipeMaterialResponse> materials) {
        this.materials = materials;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
