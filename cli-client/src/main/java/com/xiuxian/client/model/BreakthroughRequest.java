package com.xiuxian.client.model;

/**
 * 境界突破请求
 */
public class BreakthroughRequest {
    private Long characterId;
    private Boolean usePill;
    private Long pillId;

    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }

    public Boolean getUsePill() { return usePill; }
    public void setUsePill(Boolean usePill) { this.usePill = usePill; }

    public Long getPillId() { return pillId; }
    public void setPillId(Long pillId) { this.pillId = pillId; }
}
