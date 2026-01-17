package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 更新丹方请求DTO
 */
public class PillRecipeUpdateRequest {

    @Size(max = 100, message = "丹方名称长度不能超过100个字符")
    private String recipeName;

    private Long pillId;

    @Min(value = 1, message = "产出数量最小为1")
    @Max(value = 99, message = "产出数量最大为99")
    private Integer outputQuantity;

    @Min(value = 1, message = "基础成功率最小为1")
    @Max(value = 100, message = "基础成功率最大为100")
    private Integer baseSuccessRate;

    @Min(value = 1, message = "炼丹等级要求最小为1")
    @Max(value = 999, message = "炼丹等级要求最大为999")
    private Integer alchemyLevelRequired;

    @Min(value = 0, message = "灵力消耗不能为负数")
    private Integer spiritualCost;

    @Min(value = 0, message = "体力消耗不能为负数")
    private Integer staminaCost;

    @Min(value = 1, message = "炼制时长最小为1")
    private Integer duration;

    @Min(value = 1, message = "丹方品阶最小为1")
    @Max(value = 999, message = "丹方品阶最大为999")
    private Integer recipeTier;

    @Size(max = 50, message = "解锁方式长度不能超过50个字符")
    private String unlockMethod;

    @Min(value = 0, message = "解锁花费不能为负数")
    private Integer unlockCost;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    private List<PillRecipeMaterialRequest> materials;

    public static class PillRecipeMaterialRequest {
        private Long materialId;

        @Min(value = 1, message = "需要数量最小为1")
        @Max(value = 999, message = "需要数量最大为999")
        private Integer quantityRequired;

        @Min(value = 0, message = "是否主材最小为0")
        @Max(value = 1, message = "是否主材最大为1")
        private Integer isMainMaterial;

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

    public List<PillRecipeMaterialRequest> getMaterials() {
        return materials;
    }

    public void setMaterials(List<PillRecipeMaterialRequest> materials) {
        this.materials = materials;
    }
}
