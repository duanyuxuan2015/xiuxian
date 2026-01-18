package com.xiuxian.dto.response;

import com.xiuxian.entity.ExplorationArea;

/**
 * 探索区域响应DTO
 */
public class ExplorationAreaResponse {

    private Long areaId;
    private String areaName;
    private String description;
    private Integer requiredRealmLevel;
    private Integer dangerLevel;
    private Integer spiritCost;
    private Integer staminaCost;
    private Integer baseExploreTime;
    private Boolean canExplore;

    public ExplorationAreaResponse() {
    }

    public static ExplorationAreaResponse fromEntity(ExplorationArea area, int characterRealmLevel) {
        ExplorationAreaResponse response = new ExplorationAreaResponse();
        response.setAreaId(area.getAreaId());
        response.setAreaName(area.getAreaName());
        response.setDescription(area.getDescription());
        response.setRequiredRealmLevel(area.getRequiredRealmLevel());
        response.setDangerLevel(area.getDangerLevel());
        response.setSpiritCost(area.getSpiritCost());
        response.setStaminaCost(area.getStaminaCost());
        response.setBaseExploreTime(area.getBaseExploreTime());
        response.setCanExplore(characterRealmLevel >= area.getRequiredRealmLevel());
        return response;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

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

    public Boolean getCanExplore() {
        return canExplore;
    }

    public void setCanExplore(Boolean canExplore) {
        this.canExplore = canExplore;
    }

    // 向后兼容：客户端期望 minRealmLevel 和 maxRealmLevel
    public Integer getMinRealmLevel() {
        return requiredRealmLevel;
    }

    public Integer getMaxRealmLevel() {
        return requiredRealmLevel;
    }
}
