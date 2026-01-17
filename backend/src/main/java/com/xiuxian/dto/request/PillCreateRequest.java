package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建丹药请求 DTO
 */
public class PillCreateRequest {

    @NotBlank(message = "丹药名称不能为空")
    @Size(min = 2, max = 50, message = "丹药名称长度在2-50个字符之间")
    private String pillName;

    @NotNull(message = "丹药等级不能为空")
    @Min(value = 1, message = "丹药等级不能小于1")
    @Max(value = 99, message = "丹药等级不能大于99")
    private Integer pillTier;

    @NotBlank(message = "品质不能为空")
    private String quality;

    @NotBlank(message = "效果类型不能为空")
    private String effectType;

    @NotNull(message = "效果值不能为空")
    @Min(value = 0, message = "效果值不能小于0")
    @Max(value = 9999, message = "效果值不能大于9999")
    private Integer effectValue;

    @Min(value = 0, message = "持续时间不能小于0")
    @Max(value = 9999, message = "持续时间不能大于9999")
    private Integer duration;

    @NotNull(message = "堆叠上限不能为空")
    @Min(value = 1, message = "堆叠上限不能小于1")
    @Max(value = 999, message = "堆叠上限不能大于999")
    private Integer stackLimit;

    @NotNull(message = "灵石价格不能为空")
    @Min(value = 0, message = "灵石价格不能小于0")
    @Max(value = 999999, message = "灵石价格不能大于999999")
    private Integer spiritStones;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(Integer stackLimit) {
        this.stackLimit = stackLimit;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
