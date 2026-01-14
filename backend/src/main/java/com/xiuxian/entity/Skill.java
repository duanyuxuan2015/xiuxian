package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技能实体
 */
@TableName("skill")
public class Skill {

    @TableId(type = IdType.AUTO)
    private Long skillId;

    private String skillName;

    private String functionType;

    private String elementType;

    private Integer baseDamage;

    private BigDecimal skillMultiplier;

    private Integer spiritualCost;

    private BigDecimal damageGrowthRate;

    private BigDecimal multiplierGrowth;

    private Integer spiritualCostGrowth;

    private String description;

    private Integer tier;

    private Long sectId;

    private String unlockMethod;

    private Integer cost;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Skill() {
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public Integer getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(Integer baseDamage) {
        this.baseDamage = baseDamage;
    }

    public BigDecimal getSkillMultiplier() {
        return skillMultiplier;
    }

    public void setSkillMultiplier(BigDecimal skillMultiplier) {
        this.skillMultiplier = skillMultiplier;
    }

    public Integer getSpiritualCost() {
        return spiritualCost;
    }

    public void setSpiritualCost(Integer spiritualCost) {
        this.spiritualCost = spiritualCost;
    }

    public BigDecimal getDamageGrowthRate() {
        return damageGrowthRate;
    }

    public void setDamageGrowthRate(BigDecimal damageGrowthRate) {
        this.damageGrowthRate = damageGrowthRate;
    }

    public BigDecimal getMultiplierGrowth() {
        return multiplierGrowth;
    }

    public void setMultiplierGrowth(BigDecimal multiplierGrowth) {
        this.multiplierGrowth = multiplierGrowth;
    }

    public Integer getSpiritualCostGrowth() {
        return spiritualCostGrowth;
    }

    public void setSpiritualCostGrowth(Integer spiritualCostGrowth) {
        this.spiritualCostGrowth = spiritualCostGrowth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getUnlockMethod() {
        return unlockMethod;
    }

    public void setUnlockMethod(String unlockMethod) {
        this.unlockMethod = unlockMethod;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
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
        return "Skill{" +
                "skillId=" + skillId +
                ", skillName='" + skillName + '\'' +
                ", functionType='" + functionType + '\'' +
                ", tier=" + tier +
                '}';
    }
}
