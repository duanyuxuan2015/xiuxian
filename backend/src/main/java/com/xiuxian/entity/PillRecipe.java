package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 丹方实体
 */
@TableName("pill_recipe")
public class PillRecipe {

    @TableId(type = IdType.AUTO)
    private Long recipeId;

    private String recipeName;

    private Long pillId;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public PillRecipe() {
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "PillRecipe{" +
                "recipeId=" + recipeId +
                ", recipeName='" + recipeName + '\'' +
                ", pillId=" + pillId +
                ", recipeTier=" + recipeTier +
                '}';
    }
}
