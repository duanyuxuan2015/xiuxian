package com.xiuxian.dto.response;

import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.EquipmentRecipe;

import java.util.List;

/**
 * 装备配方响应DTO
 */
public class EquipmentRecipeResponse {

    private Long recipeId;
    private String recipeName;
    private String equipmentName;
    private String equipmentType;
    private String quality;
    private Integer baseSuccessRate;
    private Integer recipeTier;
    private Integer forgingLevelRequired;
    private Integer spiritualCost;
    private Integer staminaCost;
    private String description;
    private List<MaterialRequirement> materials;

    public EquipmentRecipeResponse() {
    }

    public static EquipmentRecipeResponse fromEntity(EquipmentRecipe recipe, Equipment equipment, List<MaterialRequirement> materials) {
        EquipmentRecipeResponse response = new EquipmentRecipeResponse();
        response.setRecipeId(recipe.getRecipeId());
        response.setRecipeName(recipe.getRecipeName());
        response.setEquipmentName(equipment.getEquipmentName());
        response.setEquipmentType(equipment.getEquipmentType());
        response.setQuality(equipment.getQuality());
        response.setBaseSuccessRate(recipe.getBaseSuccessRate());
        response.setRecipeTier(recipe.getRecipeTier());
        response.setForgingLevelRequired(recipe.getForgingLevelRequired());
        response.setSpiritualCost(recipe.getSpiritualCost());
        response.setStaminaCost(recipe.getStaminaCost());
        response.setDescription(recipe.getDescription());
        response.setMaterials(materials);
        return response;
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

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getBaseSuccessRate() {
        return baseSuccessRate;
    }

    public void setBaseSuccessRate(Integer baseSuccessRate) {
        this.baseSuccessRate = baseSuccessRate;
    }

    public Integer getRecipeTier() {
        return recipeTier;
    }

    public void setRecipeTier(Integer recipeTier) {
        this.recipeTier = recipeTier;
    }

    public Integer getForgingLevelRequired() {
        return forgingLevelRequired;
    }

    public void setForgingLevelRequired(Integer forgingLevelRequired) {
        this.forgingLevelRequired = forgingLevelRequired;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MaterialRequirement> getMaterials() {
        return materials;
    }

    public void setMaterials(List<MaterialRequirement> materials) {
        this.materials = materials;
    }

    /**
     * 材料需求内部类
     */
    public static class MaterialRequirement {
        private Long materialId;
        private String materialName;
        private Integer requiredQuantity;
        private Integer ownedQuantity;
        private Boolean sufficient;

        public MaterialRequirement() {
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

        public Integer getRequiredQuantity() {
            return requiredQuantity;
        }

        public void setRequiredQuantity(Integer requiredQuantity) {
            this.requiredQuantity = requiredQuantity;
        }

        public Integer getOwnedQuantity() {
            return ownedQuantity;
        }

        public void setOwnedQuantity(Integer ownedQuantity) {
            this.ownedQuantity = ownedQuantity;
        }

        public Boolean getSufficient() {
            return sufficient;
        }

        public void setSufficient(Boolean sufficient) {
            this.sufficient = sufficient;
        }
    }
}
