package com.xiuxian.dto.response;

import com.xiuxian.entity.CharacterSkill;
import com.xiuxian.entity.Skill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技能响应DTO
 */
public class SkillResponse {

    private Long skillId;
    private Long characterSkillId;
    private String skillName;
    private String functionType;
    private String elementType;
    private Integer tier;
    private String description;
    private Integer baseDamage;
    private BigDecimal skillMultiplier;
    private Integer currentDamage;
    private Integer spiritualCost;
    private Integer skillLevel;
    private Integer proficiency;
    private Integer proficiencyRequired;
    private Boolean isEquipped;
    private Integer slotIndex;
    private Boolean isLearned;
    private LocalDateTime learnedAt;

    public SkillResponse() {
    }

    public static SkillResponse fromEntity(Skill skill, CharacterSkill charSkill) {
        SkillResponse response = new SkillResponse();
        response.setSkillId(skill.getSkillId());
        response.setSkillName(skill.getSkillName());
        response.setFunctionType(skill.getFunctionType());
        response.setElementType(skill.getElementType());
        response.setTier(skill.getTier());
        response.setDescription(skill.getDescription());
        response.setBaseDamage(skill.getBaseDamage());
        response.setSkillMultiplier(skill.getSkillMultiplier());
        response.setSpiritualCost(skill.getSpiritualCost());
        response.setMaxLevel(99); // 固定最大等级99

        if (charSkill != null) {
            response.setCharacterSkillId(charSkill.getCharacterSkillId());
            response.setSkillLevel(charSkill.getSkillLevel());
            response.setProficiency(charSkill.getProficiency());
            response.setIsEquipped(charSkill.getIsEquipped());
            response.setSlotIndex(charSkill.getSlotIndex());
            response.setLearnedAt(charSkill.getLearnedAt());
            response.setIsLearned(true);

            // 计算当前伤害 (基础伤害 + 技能等级 * 伤害成长率 * 基础伤害)
            int currentDamage = skill.getBaseDamage() + charSkill.getSkillLevel() *
                    (skill.getDamageGrowthRate().multiply(new BigDecimal(skill.getBaseDamage()))).intValue();
            response.setCurrentDamage(currentDamage);

            // 升级所需熟练度
            response.setProficiencyRequired(charSkill.getSkillLevel() * 100);
        } else {
            response.setSkillLevel(0);
            response.setProficiency(0);
            response.setIsEquipped(false);
            response.setIsLearned(false);
            response.setCurrentDamage(skill.getBaseDamage());
            response.setProficiencyRequired(100);
        }

        return response;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getCharacterSkillId() {
        return characterSkillId;
    }

    public void setCharacterSkillId(Long characterSkillId) {
        this.characterSkillId = characterSkillId;
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

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getCurrentDamage() {
        return currentDamage;
    }

    public void setCurrentDamage(Integer currentDamage) {
        this.currentDamage = currentDamage;
    }

    public Integer getSpiritualCost() {
        return spiritualCost;
    }

    public void setSpiritualCost(Integer spiritualCost) {
        this.spiritualCost = spiritualCost;
    }

    public Integer getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }

    public Integer getProficiencyRequired() {
        return proficiencyRequired;
    }

    public void setProficiencyRequired(Integer proficiencyRequired) {
        this.proficiencyRequired = proficiencyRequired;
    }

    public Boolean getIsEquipped() {
        return isEquipped;
    }

    public void setIsEquipped(Boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }

    public Boolean getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(Boolean isLearned) {
        this.isLearned = isLearned;
    }

    public LocalDateTime getLearnedAt() {
        return learnedAt;
    }

    public void setLearnedAt(LocalDateTime learnedAt) {
        this.learnedAt = learnedAt;
    }

    // 添加maxLevel字段
    private Integer maxLevel;
}
