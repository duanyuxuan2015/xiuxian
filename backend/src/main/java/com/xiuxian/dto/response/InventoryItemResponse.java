package com.xiuxian.dto.response;

import java.time.LocalDateTime;

/**
 * 背包物品响应DTO
 */
public class InventoryItemResponse {

    private Long inventoryId;
    private String itemType; // material, pill, equipment
    private Long itemId;
    private String itemName;
    private String itemDetail; // 物品详细信息（品质、属性等）
    private Integer quantity;
    private LocalDateTime acquiredAt;

    // 装备详细属性（仅当 itemType=equipment 时有值）
    private Integer attackPower;
    private Integer defensePower;
    private Integer healthBonus;
    private Integer criticalRate;
    private Integer speedBonus;
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;
    private Integer baseScore;
    private Integer enhancementLevel;

    // 丹药效果（仅当 itemType=pill 时有值）
    private String effectType;
    private Integer effectValue;

    public InventoryItemResponse() {
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(LocalDateTime acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    // 装备属性的 getter 和 setter
    public Integer getAttackPower() { return attackPower; }
    public void setAttackPower(Integer attackPower) { this.attackPower = attackPower; }

    public Integer getDefensePower() { return defensePower; }
    public void setDefensePower(Integer defensePower) { this.defensePower = defensePower; }

    public Integer getHealthBonus() { return healthBonus; }
    public void setHealthBonus(Integer healthBonus) { this.healthBonus = healthBonus; }

    public Integer getCriticalRate() { return criticalRate; }
    public void setCriticalRate(Integer criticalRate) { this.criticalRate = criticalRate; }

    public Integer getSpeedBonus() { return speedBonus; }
    public void setSpeedBonus(Integer speedBonus) { this.speedBonus = speedBonus; }

    public Integer getPhysicalResist() { return physicalResist; }
    public void setPhysicalResist(Integer physicalResist) { this.physicalResist = physicalResist; }

    public Integer getIceResist() { return iceResist; }
    public void setIceResist(Integer iceResist) { this.iceResist = iceResist; }

    public Integer getFireResist() { return fireResist; }
    public void setFireResist(Integer fireResist) { this.fireResist = fireResist; }

    public Integer getLightningResist() { return lightningResist; }
    public void setLightningResist(Integer lightningResist) { this.lightningResist = lightningResist; }

    public Integer getBaseScore() { return baseScore; }
    public void setBaseScore(Integer baseScore) { this.baseScore = baseScore; }

    public Integer getEnhancementLevel() { return enhancementLevel; }
    public void setEnhancementLevel(Integer enhancementLevel) { this.enhancementLevel = enhancementLevel; }

    // 丹药效果的 getter 和 setter
    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }

    public Integer getEffectValue() { return effectValue; }
    public void setEffectValue(Integer effectValue) { this.effectValue = effectValue; }

    @Override
    public String toString() {
        return "InventoryItemResponse{" +
                "inventoryId=" + inventoryId +
                ", itemType='" + itemType + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemDetail='" + itemDetail + '\'' +
                ", quantity=" + quantity +
                ", acquiredAt=" + acquiredAt +
                '}';
    }
}
