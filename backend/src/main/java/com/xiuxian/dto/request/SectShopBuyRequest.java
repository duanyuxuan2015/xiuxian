package com.xiuxian.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 宗门商店购买请求DTO
 */
public class SectShopBuyRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "商品ID不能为空")
    private Long itemId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    public SectShopBuyRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
