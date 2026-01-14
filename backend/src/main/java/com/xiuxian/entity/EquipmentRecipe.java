package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 装备配方实体
 */
@TableName("equipment_recipe")
public class EquipmentRecipe {

    @TableId(type = IdType.AUTO)
    private Long recipeId;

    private String recipeName;

    private Long equipmentId;

    private Integer baseSuccessRate;

    private Integer forgingLevelRequired;

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

    public EquipmentRecipe() {
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

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getBaseSuccessRate() {
        return baseSuccessRate;
    }

    public void setBaseSuccessRate(Integer baseSuccessRate) {
        this.baseSuccessRate = baseSuccessRate;
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
        return "EquipmentRecipe{" +
                "recipeId=" + recipeId +
                ", recipeName='" + recipeName + '\'' +
                ", equipmentId=" + equipmentId +
                ", recipeTier=" + recipeTier +
                '}';
    }
}
