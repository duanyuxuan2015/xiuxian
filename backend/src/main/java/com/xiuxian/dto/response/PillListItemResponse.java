package com.xiuxian.dto.response;

/**
 * 丹药列表项响应 DTO
 */
public class PillListItemResponse {

    private Long pillId;
    private String pillName;
    private Integer pillTier;
    private String quality;
    private String effectType;
    private Integer effectValue;
    private Integer spiritStones;

    public Long getPillId() {
        return pillId;
    }

    public void setPillId(Long pillId) {
        this.pillId = pillId;
    }

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public Integer getPillTier() {
        return pillTier;
    }

    public void setPillTier(Integer pillTier) {
        this.pillTier = pillTier;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public Integer getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(Integer effectValue) {
        this.effectValue = effectValue;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }
}
