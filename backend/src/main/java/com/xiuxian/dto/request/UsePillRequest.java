package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 使用丹药请求DTO
 */
public class UsePillRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "背包物品ID不能为空")
    private Long inventoryId;

    private Integer quantity = 1; // 默认使用1个

    public UsePillRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null ? quantity : 1;
    }
}
