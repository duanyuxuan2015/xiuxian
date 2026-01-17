package com.xiuxian.dto.response;

/**
 * 丹方列表项响应DTO
 */
public class PillRecipeListItemResponse {

    private Long recipeId;
    private String recipeName;
    private String pillName;
    private Integer recipeTier;
    private Integer outputQuantity;
    private Integer baseSuccessRate;

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

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public Integer getRecipeTier() {
        return recipeTier;
    }

    public void setRecipeTier(Integer recipeTier) {
        this.recipeTier = recipeTier;
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
}
