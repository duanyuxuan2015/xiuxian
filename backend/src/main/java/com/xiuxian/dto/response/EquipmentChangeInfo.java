package com.xiuxian.dto.response;

/**
 * 装备变更信息
 */
public class EquipmentChangeInfo {

    private String equipmentSlot;
    private EquipmentResponse oldEquipment;
    private EquipmentResponse newEquipment;
    private Integer oldScore;
    private Integer newScore;
    private String reason;

    public EquipmentChangeInfo() {}

    public EquipmentChangeInfo(String slot, EquipmentResponse oldEq,
                              EquipmentResponse newEq, String reason) {
        this.equipmentSlot = slot;
        this.oldEquipment = oldEq;
        this.newEquipment = newEq;
        this.oldScore = oldEq != null ? oldEq.getBaseScore() : 0;
        this.newScore = newEq != null ? newEq.getBaseScore() : 0;
        this.reason = reason;
    }

    public String getEquipmentSlot() {
        return equipmentSlot;
    }

    public void setEquipmentSlot(String equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    public EquipmentResponse getOldEquipment() {
        return oldEquipment;
    }

    public void setOldEquipment(EquipmentResponse oldEquipment) {
        this.oldEquipment = oldEquipment;
    }

    public EquipmentResponse getNewEquipment() {
        return newEquipment;
    }

    public void setNewEquipment(EquipmentResponse newEquipment) {
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
