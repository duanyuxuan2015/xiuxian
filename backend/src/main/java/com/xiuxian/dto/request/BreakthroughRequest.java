package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 境界突破请求DTO
 */
public class BreakthroughRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    /**
     * 是否使用突破丹药辅助
     */
    private Boolean usePill;

    /**
     * 使用的丹药ID（可选）
     */
    private Long pillId;

    public BreakthroughRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Boolean getUsePill() {
        return usePill;
    }

    public void setUsePill(Boolean usePill) {
        this.usePill = usePill;
    }

    public Long getPillId() {
        return pillId;
    }

    public void setPillId(Long pillId) {
        this.pillId = pillId;
    }
}
