package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 更新装备请求 DTO
 */
public class EquipmentUpdateRequest {

    @Size(min = 2, max = 50, message = "装备名称长度在2-50个字符之间")
    private String equipmentName;

    private String equipmentType;

    private String quality;

    @Min(value = 0, message = "基础评分不能小于0")
    @Max(value = 99999, message = "基础评分不能大于99999")
    private Integer baseScore;

    @Min(value = 0, message = "攻击力不能小于0")
    @Max(value = 9999, message = "攻击力不能大于9999")
    private Integer attackPower;

    @Min(value = 0, message = "防御力不能小于0")
    @Max(value = 9999, message = "防御力不能大于9999")
    private Integer defensePower;

    @Min(value = 0, message = "生命加成不能小于0")
    @Max(value = 99999, message = "生命加成不能大于99999")
    private Integer healthBonus;

    @Min(value = 0, message = "暴击率不能小于0")
    @Max(value = 100, message = "暴击率不能大于100")
    private Integer criticalRate;

    @Min(value = 0, message = "速度加成不能小于0")
    @Max(value = 999, message = "速度加成不能大于999")
    private Integer speedBonus;

    @Min(value = 0, message = "物理抗性不能小于0")
    @Max(value = 100, message = "物理抗性不能大于100")
    private Integer physicalResist;

    @Min(value = 0, message = "冰抗性不能小于0")
    @Max(value = 100, message = "冰抗性不能大于100")
    private Integer iceResist;

    @Min(value = 0, message = "火抗性不能小于0")
    @Max(value = 100, message = "火抗性不能大于100")
    private Integer fireResist;

    @Min(value = 0, message = "雷抗性不能小于0")
    @Max(value = 100, message = "雷抗性不能大于100")
    private Integer lightningResist;

    @Min(value = 0, message = "强化等级不能小于0")
    @Max(value = 99, message = "强化等级不能大于99")
    private Integer enhancementLevel;

    @Min(value = 0, message = "宝石槽位数不能小于0")
    @Max(value = 10, message = "宝石槽位数不能大于10")
    private Integer gemSlotCount;

    @Size(max = 500, message = "特殊效果描述不能超过500个字符")
    private String specialEffects;

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(Integer baseScore) {
        this.baseScore = baseScore;
    }

    public Integer getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(Integer attackPower) {
        this.attackPower = attackPower;
    }

    public Integer getDefensePower() {
        return defensePower;
    }

    public void setDefensePower(Integer defensePower) {
        this.defensePower = defensePower;
    }

    public Integer getHealthBonus() {
        return healthBonus;
    }

    public void setHealthBonus(Integer healthBonus) {
        this.healthBonus = healthBonus;
    }

    public Integer getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(Integer criticalRate) {
        this.criticalRate = criticalRate;
    }

    public Integer getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(Integer speedBonus) {
        this.speedBonus = speedBonus;
    }

    public Integer getPhysicalResist() {
        return physicalResist;
    }

    public void setPhysicalResist(Integer physicalResist) {
        this.physicalResist = physicalResist;
    }

    public Integer getIceResist() {
        return iceResist;
    }

    public void setIceResist(Integer iceResist) {
        this.iceResist = iceResist;
    }

    public Integer getFireResist() {
        return fireResist;
    }

    public void setFireResist(Integer fireResist) {
        this.fireResist = fireResist;
    }

    public Integer getLightningResist() {
        return lightningResist;
    }

    public void setLightningResist(Integer lightningResist) {
        this.lightningResist = lightningResist;
    }

    public Integer getEnhancementLevel() {
        return enhancementLevel;
    }

    public void setEnhancementLevel(Integer enhancementLevel) {
        this.enhancementLevel = enhancementLevel;
    }

    public Integer getGemSlotCount() {
        return gemSlotCount;
    }

    public void setGemSlotCount(Integer gemSlotCount) {
        this.gemSlotCount = gemSlotCount;
    }

    public String getSpecialEffects() {
        return specialEffects;
    }

    public void setSpecialEffects(String specialEffects) {
        this.specialEffects = specialEffects;
    }
}
