package com.xiuxian.client.model;

import java.util.List;

/**
 * 丹方响应
 */
public class PillRecipeResponse {
    private Long recipeId;
    private String recipeName;
    private String pillName;
    private String effectType;
    private String quality;
    private Integer recipeTier;
    private Integer alchemyLevelRequired;
    private Integer baseSuccessRate;
    private List<MaterialRequirement> materials;

    public static class MaterialRequirement {
        private Long materialId;
        private String materialName;
        private Integer requiredQuantity;
        private Integer ownedQuantity;
        private boolean sufficient;

        public Long getMaterialId() { return materialId; }
        public String getMaterialName() { return materialName; }
        public Integer getRequiredQuantity() { return requiredQuantity; }
        public Integer getOwnedQuantity() { return ownedQuantity; }
        public boolean isSufficient() { return sufficient; }

        public void setMaterialId(Long materialId) { this.materialId = materialId; }
        public void setMaterialName(String materialName) { this.materialName = materialName; }
        public void setRequiredQuantity(Integer requiredQuantity) { this.requiredQuantity = requiredQuantity; }
        public void setOwnedQuantity(Integer ownedQuantity) { this.ownedQuantity = ownedQuantity; }
        public void setSufficient(boolean sufficient) { this.sufficient = sufficient; }
    }

    public Long getRecipeId() { return recipeId; }
    public String getRecipeName() { return recipeName; }
    public String getPillName() { return pillName; }
    public String getEffectType() { return effectType; }
    public String getQuality() { return quality; }
    public Integer getRecipeTier() { return recipeTier; }
    public Integer getAlchemyLevelRequired() { return alchemyLevelRequired; }
    public Integer getBaseSuccessRate() { return baseSuccessRate; }
    public List<MaterialRequirement> getMaterials() { return materials; }

    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }
    public void setPillName(String pillName) { this.pillName = pillName; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setRecipeTier(Integer recipeTier) { this.recipeTier = recipeTier; }
    public void setAlchemyLevelRequired(Integer alchemyLevelRequired) { this.alchemyLevelRequired = alchemyLevelRequired; }
    public void setBaseSuccessRate(Integer baseSuccessRate) { this.baseSuccessRate = baseSuccessRate; }
    public void setMaterials(List<MaterialRequirement> materials) { this.materials = materials; }
}
