package com.xiuxian.dto.response;

/**
 * 宗门商店物品列表项响应DTO
 */
public class SectShopItemListItemResponse {

    private Long itemId;
    private String sectName;
    private String itemType;
    private String itemName;
    private Integer itemTier;
    private Integer price;
    private Integer currentStock;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemTier() {
        return itemTier;
    }

    public void setItemTier(Integer itemTier) {
        this.itemTier = itemTier;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
}
