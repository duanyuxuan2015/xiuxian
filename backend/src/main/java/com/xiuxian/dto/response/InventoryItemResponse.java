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
