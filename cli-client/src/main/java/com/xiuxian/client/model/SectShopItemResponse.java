package com.xiuxian.client.model;

/**
 * 宗门商店物品响应
 */
public class SectShopItemResponse {
    private Long shopItemId;
    private String itemName;
    private String itemType;
    private Integer itemTier;
    private Long price;
    private Integer stock;
    private String description;

    public Long getShopItemId() { return shopItemId; }
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public Integer getItemTier() { return itemTier; }
    public Long getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getDescription() { return description; }

    public void setShopItemId(Long shopItemId) { this.shopItemId = shopItemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setItemTier(Integer itemTier) { this.itemTier = itemTier; }
    public void setPrice(Long price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setDescription(String description) { this.description = description; }
}
