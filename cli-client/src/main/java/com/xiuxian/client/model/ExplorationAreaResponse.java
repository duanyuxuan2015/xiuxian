package com.xiuxian.client.model;

/**
 * 探索区域响应
 */
public class ExplorationAreaResponse {
    private Long areaId;
    private String areaName;
    private Integer minRealmLevel;
    private Integer maxRealmLevel;
    private Integer dangerLevel;
    private Integer spiritCost;
    private Integer staminaCost;
    private String description;
    private boolean canAccess;

    public Long getAreaId() { return areaId; }
    public String getAreaName() { return areaName; }
    public Integer getMinRealmLevel() { return minRealmLevel; }
    public Integer getMaxRealmLevel() { return maxRealmLevel; }
    public Integer getDangerLevel() { return dangerLevel; }
    public Integer getSpiritCost() { return spiritCost; }
    public Integer getStaminaCost() { return staminaCost; }
    public String getDescription() { return description; }
    public boolean isCanAccess() { return canAccess; }

    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public void setMinRealmLevel(Integer minRealmLevel) { this.minRealmLevel = minRealmLevel; }
    public void setMaxRealmLevel(Integer maxRealmLevel) { this.maxRealmLevel = maxRealmLevel; }
    public void setDangerLevel(Integer dangerLevel) { this.dangerLevel = dangerLevel; }
    public void setSpiritCost(Integer spiritCost) { this.spiritCost = spiritCost; }
    public void setStaminaCost(Integer staminaCost) { this.staminaCost = staminaCost; }
    public void setDescription(String description) { this.description = description; }
    public void setCanAccess(boolean canAccess) { this.canAccess = canAccess; }
}
