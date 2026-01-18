package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 更新宗门商店物品请求DTO
 */
public class SectShopItemUpdateRequest {

    private Long sectId;

    @Size(max = 50, message = "物品类型长度不能超过50个字符")
    private String itemType;

    private Long refItemId;

    @Size(max = 100, message = "物品名称长度不能超过100个字符")
    private String itemName;

    @Min(value = 1, message = "物品品阶最小为1")
    @Max(value = 999, message = "物品品阶最大为999")
    private Integer itemTier;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Min(value = 0, message = "价格不能为负数")
    private Integer price;

    @Min(value = 1, message = "库存上限最小为1")
    @Max(value = 99999, message = "库存上限最大为99999")
    private Integer stockLimit;

    @Min(value = 0, message = "当前库存不能为负数")
    private Integer currentStock;

    @Min(value = 1, message = "要求职位最小为1")
    @Max(value = 999, message = "要求职位最大为999")
    private Integer requiredPosition;

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
