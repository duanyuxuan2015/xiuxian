package com.xiuxian.dto.response;

import com.xiuxian.entity.SectShopItem;

/**
 * 宗门商店物品响应DTO
 */
public class SectShopItemResponse {

    private Long itemId;
    private Long sectId;
    private String itemType;
    private Long refItemId;
    private String itemName;
    private Integer price;
    private Integer stockLimit;
    private Integer currentStock;
    private Integer requiredPosition;
    private Boolean canBuy;

    public SectShopItemResponse() {
    }

    public static SectShopItemResponse fromEntity(SectShopItem item, int memberPosition) {
        SectShopItemResponse response = new SectShopItemResponse();
        response.setItemId(item.getItemId());
        response.setSectId(item.getSectId());
        response.setItemType(item.getItemType());
        response.setRefItemId(item.getRefItemId());
        response.setItemName(item.getItemName());
        response.setPrice(item.getPrice());
        response.setStockLimit(item.getStockLimit());
        response.setCurrentStock(item.getCurrentStock());
        response.setRequiredPosition(item.getRequiredPosition());
        // 库存为null时视为0（无库存），不能购买
        Integer currentStock = item.getCurrentStock();
        response.setCanBuy(memberPosition >= item.getRequiredPosition() && currentStock != null && currentStock > 0);
        return response;
    }

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

    public Boolean getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(Boolean canBuy) {
        this.canBuy = canBuy;
    }
}
