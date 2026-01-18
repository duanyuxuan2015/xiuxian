package com.xiuxian.dto.response;

/**
 * 宗门商店物品详情响应DTO
 */
public class SectShopItemDetailResponse {

    private Long itemId;
    private Long sectId;
    private String sectName;
    private String itemType;
    private Long refItemId;
    private String itemName;
    private Integer itemTier;
    private String description;
    private Integer price;
    private Integer stockLimit;
    private Integer currentStock;
    private Integer requiredPosition;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
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

    public Long getRefItemId() {
        return refItemId;
    }

    public void setRefItemId(Long refItemId) {
        this.refItemId = refItemId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStockLimit() {
        return stockLimit;
    }

    public void setStockLimit(Integer stockLimit) {
        this.stockLimit = stockLimit;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getRequiredPosition() {
        return requiredPosition;
    }

    public void setRequiredPosition(Integer requiredPosition) {
        this.requiredPosition = requiredPosition;
    }
}
