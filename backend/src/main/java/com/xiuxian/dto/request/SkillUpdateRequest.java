package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 更新技能请求DTO
 */
public class SkillUpdateRequest {

    @Size(max = 100, message = "技能名称长度不能超过100个字符")
    private String skillName;

    @Size(max = 50, message = "功能类型长度不能超过50个字符")
    private String functionType;

    @Size(max = 50, message = "元素类型长度不能超过50个字符")
    private String elementType;

    @Min(value = 0, message = "基础伤害不能为负数")
    private Integer baseDamage;

    private BigDecimal skillMultiplier;

    @Min(value = 0, message = "灵力消耗不能为负数")
    private Integer spiritualCost;

    private BigDecimal damageGrowthRate;

    private BigDecimal multiplierGrowth;

    private Integer spiritualCostGrowth;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Min(value = 1, message = "品阶最小为1")
    @Max(value = 999, message = "品阶最大为999")
    private Integer tier;

    private Long sectId;

    @Size(max = 200, message = "解锁方式长度不能超过200个字符")
    private String unlockMethod;

    @Min(value = 0, message = "学习花费不能为负数")
    private Integer cost;

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
}
