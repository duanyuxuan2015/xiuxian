package com.xiuxian.client.model;

/**
 * 装备信息
 */
public class EquipmentInfo {
    private Long characterEquipmentId;
    private String equipmentName;
    private String equipmentType;
    private String equipmentSlot;
    private Integer tier;
    private String quality;
    private Integer baseScore;
    private Integer enhancementLevel;
    private Integer attackPower;
    private Integer defensePower;
    private Integer healthBonus;
    private Integer criticalRate;
    private Integer speedBonus;
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;
    private boolean isEquipped;

    public Long getCharacterEquipmentId() { return characterEquipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public String getEquipmentType() { return equipmentType; }
    public String getEquipmentSlot() { return equipmentSlot; }
    public Integer getTier() { return tier; }
    public String getQuality() { return quality; }

    // 基础评分
    public Integer getBaseScore() { return baseScore; }

    // 强化等级
    public Integer getEnhancementLevel() { return enhancementLevel; }

    // 攻击力
    public Integer getAttack() { return attackPower; }
    public Integer getAttackPower() { return attackPower; }

    // 防御力
    public Integer getDefense() { return defensePower; }
    public Integer getDefensePower() { return defensePower; }

    // 气血加成
    public Integer getHealthBonus() { return healthBonus; }

    // 暴击
    public Integer getCriticalRate() { return criticalRate; }

    // 速度
    public Integer getSpeedBonus() { return speedBonus; }

    // 物理抗性
    public Integer getPhysicalResist() { return physicalResist; }

    // 冰系抗性
    public Integer getIceResist() { return iceResist; }

    // 火系抗性
    public Integer getFireResist() { return fireResist; }

    // 电系抗性
    public Integer getLightningResist() { return lightningResist; }

    public boolean isEquipped() { return isEquipped; }

    public void setCharacterEquipmentId(Long characterEquipmentId) { this.characterEquipmentId = characterEquipmentId; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
    public void setEquipmentSlot(String equipmentSlot) { this.equipmentSlot = equipmentSlot; }
    public void setTier(Integer tier) { this.tier = tier; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setBaseScore(Integer baseScore) { this.baseScore = baseScore; }
    public void setEnhancementLevel(Integer enhancementLevel) { this.enhancementLevel = enhancementLevel; }

    public void setAttack(Integer attack) { this.attackPower = attack; }
    public void setAttackPower(Integer attackPower) { this.attackPower = attackPower; }

    public void setDefense(Integer defense) { this.defensePower = defense; }
    public void setDefensePower(Integer defensePower) { this.defensePower = defensePower; }

    public void setHealthBonus(Integer healthBonus) { this.healthBonus = healthBonus; }
    public void setCriticalRate(Integer criticalRate) { this.criticalRate = criticalRate; }
    public void setSpeedBonus(Integer speedBonus) { this.speedBonus = speedBonus; }
    public void setPhysicalResist(Integer physicalResist) { this.physicalResist = physicalResist; }
    public void setIceResist(Integer iceResist) { this.iceResist = iceResist; }
    public void setFireResist(Integer fireResist) { this.fireResist = fireResist; }
    public void setLightningResist(Integer lightningResist) { this.lightningResist = lightningResist; }

    public void setEquipped(boolean equipped) { isEquipped = equipped; }
}
