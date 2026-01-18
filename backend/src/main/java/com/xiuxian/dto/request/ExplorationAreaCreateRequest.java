package com.xiuxian.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 探索区域创建请求
 */
public class ExplorationAreaCreateRequest {

    @NotBlank(message = "区域名称不能为空")
    private String areaName;

    private String description;

    @NotNull(message = "所需境界等级不能为空")
    @Min(value = 1, message = "境界等级必须大于0")
    private Integer requiredRealmLevel;

    @NotNull(message = "危险等级不能为空")
    @Min(value = 1, message = "危险等级必须大于0")
    private Integer dangerLevel;

    @NotNull(message = "灵力消耗不能为空")
    @Min(value = 0, message = "灵力消耗不能为负数")
    private Integer spiritCost;

    @NotNull(message = "体力消耗不能为空")
    @Min(value = 0, message = "体力消耗不能为负数")
    private Integer staminaCost;

    @Min(value = 1, message = "探索时间必须大于0")
    private Integer baseExploreTime;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRequiredRealmLevel() {
        return requiredRealmLevel;
    }

    public void setRequiredRealmLevel(Integer requiredRealmLevel) {
        this.requiredRealmLevel = requiredRealmLevel;
    }

    public Integer getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(Integer dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public Integer getSpiritCost() {
        return spiritCost;
    }

    public void setSpiritCost(Integer spiritCost) {
        this.spiritCost = spiritCost;
    }

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }

    public Integer getBaseExploreTime() {
        return baseExploreTime;
    }

    public void setBaseExploreTime(Integer baseExploreTime) {
        this.baseExploreTime = baseExploreTime;
    }
}
