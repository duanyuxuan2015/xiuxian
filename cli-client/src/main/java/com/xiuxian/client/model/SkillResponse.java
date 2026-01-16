package com.xiuxian.client.model;

/**
 * 技能响应
 */
public class SkillResponse {
    private Long characterSkillId;
    private Long skillId;
    private String skillName;
    private String functionType;
    private String elementType;
    private Integer tier;
    private Integer skillLevel;
    private Integer proficiency;
    private boolean isLearned;
    private boolean isEquipped;
    private Integer slotIndex;
    private String description;
    private Integer baseDamage;
    private Integer spiritualCost;

    public Long getCharacterSkillId() { return characterSkillId; }
    public Long getSkillId() { return skillId; }
    public String getSkillName() { return skillName; }
    public String getFunctionType() { return functionType; }
    public String getElementType() { return elementType; }
    public Integer getTier() { return tier; }
    public Integer getSkillLevel() { return skillLevel; }
    public Integer getProficiency() { return proficiency; }
    public boolean isLearned() { return isLearned; }
    public boolean isEquipped() { return isEquipped; }
    public Integer getSlotIndex() { return slotIndex; }
    public String getDescription() { return description; }
    public Integer getBaseDamage() { return baseDamage; }
    public Integer getSpiritualCost() { return spiritualCost; }

    public void setCharacterSkillId(Long characterSkillId) { this.characterSkillId = characterSkillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setFunctionType(String functionType) { this.functionType = functionType; }
    public void setElementType(String elementType) { this.elementType = elementType; }
    public void setTier(Integer tier) { this.tier = tier; }
    public void setSkillLevel(Integer skillLevel) { this.skillLevel = skillLevel; }
    public void setProficiency(Integer proficiency) { this.proficiency = proficiency; }
    public void setLearned(boolean learned) { isLearned = learned; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    public void setSlotIndex(Integer slotIndex) { this.slotIndex = slotIndex; }
    public void setDescription(String description) { this.description = description; }
    public void setBaseDamage(Integer baseDamage) { this.baseDamage = baseDamage; }
    public void setSpiritualCost(Integer spiritualCost) { this.spiritualCost = spiritualCost; }
}
