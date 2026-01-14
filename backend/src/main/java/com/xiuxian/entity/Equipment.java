package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 装备实体类
 */
@TableName("equipment")
public class Equipment {

    @TableId(value = "equipment_id", type = IdType.AUTO)
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

    private String specialEffects;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Equipment() {
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

    public String getSpecialEffects() {
        return specialEffects;
    }

    public void setSpecialEffects(String specialEffects) {
        this.specialEffects = specialEffects;
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
        return "Equipment{" +
                "equipmentId=" + equipmentId +
                ", equipmentName='" + equipmentName + '\'' +
                ", equipmentType='" + equipmentType + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }
}
