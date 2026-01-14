package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 探索请求DTO
 */
public class ExplorationRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "区域ID不能为空")
    private Long areaId;

    public ExplorationRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
}
