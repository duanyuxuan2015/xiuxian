package com.xiuxian.client.model;

/**
 * 装备变更信息
 */
public class EquipmentChangeInfo {
    private String equipmentSlot;
    private EquipmentInfo oldEquipment;
    private EquipmentInfo newEquipment;
    private Integer oldScore;
    private Integer newScore;
    private String reason;

    public String getEquipmentSlot() {
        return equipmentSlot;
    }

    public void setEquipmentSlot(String equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    public EquipmentInfo getOldEquipment() {
        return oldEquipment;
    }

    public void setOldEquipment(EquipmentInfo oldEquipment) {
        this.oldEquipment = oldEquipment;
    }

    public EquipmentInfo getNewEquipment() {
        return newEquipment;
    }

    public void setNewEquipment(EquipmentInfo newEquipment) {
        this.newEquipment = newEquipment;
    }

    public Integer getOldScore() {
        return oldScore;
    }

    public void setOldScore(Integer oldScore) {
        this.oldScore = oldScore;
    }

    public Integer getNewScore() {
        return newScore;
    }

    public void setNewScore(Integer newScore) {
        this.newScore = newScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
