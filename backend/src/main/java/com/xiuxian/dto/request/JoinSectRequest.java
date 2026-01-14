package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 加入宗门请求DTO
 */
public class JoinSectRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "宗门ID不能为空")
    private Long sectId;

    public JoinSectRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }
}
