package com.xiuxian.client.model;

/**
 * 探索响应
 */
public class ExplorationResponse {
    private boolean success;
    private String areaName;
    private String eventType;
    private String eventDescription;
    private Integer expGained;
    private String itemFound;
    private Integer spiritualPowerGained;
    private Integer healthLost;

    public boolean isSuccess() { return success; }
    public String getAreaName() { return areaName; }
    public String getEventType() { return eventType; }
    public String getEventDescription() { return eventDescription; }
    public Integer getExpGained() { return expGained; }
    public String getItemFound() { return itemFound; }
    public Integer getSpiritualPowerGained() { return spiritualPowerGained; }
    public Integer getHealthLost() { return healthLost; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public void setItemFound(String itemFound) { this.itemFound = itemFound; }
    public void setSpiritualPowerGained(Integer spiritualPowerGained) { this.spiritualPowerGained = spiritualPowerGained; }
    public void setHealthLost(Integer healthLost) { this.healthLost = healthLost; }
}
