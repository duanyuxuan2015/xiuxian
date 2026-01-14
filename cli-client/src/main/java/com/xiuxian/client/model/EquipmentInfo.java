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
    private Integer attack;
    private Integer defense;
    private boolean isEquipped;

    public Long getCharacterEquipmentId() { return characterEquipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public String getEquipmentType() { return equipmentType; }
    public String getEquipmentSlot() { return equipmentSlot; }
    public Integer getTier() { return tier; }
    public String getQuality() { return quality; }
    public Integer getAttack() { return attack; }
    public Integer getDefense() { return defense; }
    public boolean isEquipped() { return isEquipped; }

    public void setCharacterEquipmentId(Long characterEquipmentId) { this.characterEquipmentId = characterEquipmentId; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
    public void setEquipmentSlot(String equipmentSlot) { this.equipmentSlot = equipmentSlot; }
    public void setTier(Integer tier) { this.tier = tier; }
    public void setQuality(String quality) { this.quality = quality; }
    public void setAttack(Integer attack) { this.attack = attack; }
    public void setDefense(Integer defense) { this.defense = defense; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
}
