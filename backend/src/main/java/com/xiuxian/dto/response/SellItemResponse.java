package com.xiuxian.dto.response;

/**
 * 售出物品响应
 */
public class SellItemResponse {
    private String itemName;
    private Integer quantity;
    private Long totalSpiritStones;
    private Long remainingSpiritStones;
    private String message;

    public SellItemResponse() {
    }

    public SellItemResponse(String itemName, Integer quantity, Long totalSpiritStones, Long remainingSpiritStones, String message) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalSpiritStones = totalSpiritStones;
        this.remainingSpiritStones = remainingSpiritStones;
        this.message = message;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getTotalSpiritStones() {
        return totalSpiritStones;
    }

    public void setTotalSpiritStones(Long totalSpiritStones) {
        this.totalSpiritStones = totalSpiritStones;
    }

    public Long getRemainingSpiritStones() {
        return remainingSpiritStones;
    }

    public void setRemainingSpiritStones(Long remainingSpiritStones) {
        this.remainingSpiritStones = remainingSpiritStones;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
