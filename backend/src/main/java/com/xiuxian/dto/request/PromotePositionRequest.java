package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 职位升级请求DTO
 */
public class PromotePositionRequest {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    public PromotePositionRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }
}
