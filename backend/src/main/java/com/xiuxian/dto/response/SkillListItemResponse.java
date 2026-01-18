package com.xiuxian.dto.response;

import java.math.BigDecimal;

/**
 * 技能列表项响应DTO
 */
public class SkillListItemResponse {

    private Long skillId;
    private String skillName;
    private String functionType;
    private String elementType;
    private Integer baseDamage;
    private BigDecimal skillMultiplier;
    private Integer tier;
    private Long sectId;
    private String sectName;
    private Integer cost;

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

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
