package com.xiuxian.dto.response;

import com.xiuxian.entity.Equipment;

/**
 * 装备响应DTO
 */
public class EquipmentResponse {

    private Long equipmentId;
    private String equipmentName;
    private String equipmentType;
    private String quality;
    private Integer baseScore;
    private Integer attackPower;
    private Integer defensePower;
    private Integer healthBonus;
    private Integer criticalRate;
    private Integer speedBonus;
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;
    private Integer enhancementLevel;
    private Integer gemSlotCount;
    private String equipmentSlot;
    private Boolean isEquipped;

    public EquipmentResponse() {
    }

    public static EquipmentResponse fromEntity(Equipment equipment, String slot, boolean isEquipped) {
        EquipmentResponse response = new EquipmentResponse();
        response.setEquipmentId(equipment.getEquipmentId());
        response.setEquipmentName(equipment.getEquipmentName());
        response.setEquipmentType(equipment.getEquipmentType());
        response.setQuality(equipment.getQuality());
        response.setBaseScore(equipment.getBaseScore());
        response.setAttackPower(equipment.getAttackPower());
        response.setDefensePower(equipment.getDefensePower());
        response.setHealthBonus(equipment.getHealthBonus());
        response.setCriticalRate(equipment.getCriticalRate());
        response.setSpeedBonus(equipment.getSpeedBonus());
        response.setPhysicalResist(equipment.getPhysicalResist());
        response.setIceResist(equipment.getIceResist());
        response.setFireResist(equipment.getFireResist());
        response.setLightningResist(equipment.getLightningResist());
        response.setEnhancementLevel(equipment.getEnhancementLevel());
        response.setGemSlotCount(equipment.getGemSlotCount());
        response.setEquipmentSlot(slot);
        response.setIsEquipped(isEquipped);
        return response;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

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

    public String getEquipmentSlot() {
        return equipmentSlot;
    }

    public void setEquipmentSlot(String equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    public Boolean getIsEquipped() {
        return isEquipped;
    }

    public void setIsEquipped(Boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
}
