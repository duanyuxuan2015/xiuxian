package com.xiuxian.client.model;

/**
 * 探索区域响应
 */
public class ExplorationAreaResponse {
    private Long areaId;
    private String areaName;
    private Integer minRealmLevel;
    private Integer maxRealmLevel;
    private String description;
    private boolean canAccess;

    public Long getAreaId() { return areaId; }
    public String getAreaName() { return areaName; }
    public Integer getMinRealmLevel() { return minRealmLevel; }
    public Integer getMaxRealmLevel() { return maxRealmLevel; }
    public String getDescription() { return description; }
    public boolean isCanAccess() { return canAccess; }

    public void setAreaId(Long areaId) { this.areaId = areaId; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public void setMinRealmLevel(Integer minRealmLevel) { this.minRealmLevel = minRealmLevel; }
    public void setMaxRealmLevel(Integer maxRealmLevel) { this.maxRealmLevel = maxRealmLevel; }
    public void setDescription(String description) { this.description = description; }
    public void setCanAccess(boolean canAccess) { this.canAccess = canAccess; }
}
