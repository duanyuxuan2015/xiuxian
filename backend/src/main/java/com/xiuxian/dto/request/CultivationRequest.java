package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 修炼请求DTO
 */
public class CultivationRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    public CultivationRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }
}
