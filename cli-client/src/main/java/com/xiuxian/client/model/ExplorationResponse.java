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

    // 资源消耗和剩余信息
    private Integer staminaCost;
    private Integer staminaRemaining;
    private Integer spiritCost;
    private Integer spiritRemaining;

    // 战斗相关字段
    private Boolean needCombat;
    private Long monsterId;
    private String monsterName;

    public boolean isSuccess() { return success; }
    public String getAreaName() { return areaName; }
    public String getEventType() { return eventType; }
    public String getEventDescription() { return eventDescription; }
    public Integer getExpGained() { return expGained; }
    public String getItemFound() { return itemFound; }
    public Integer getSpiritualPowerGained() { return spiritualPowerGained; }
    public Integer getHealthLost() { return healthLost; }

    // 资源消耗和剩余信息的getter和setter
    public Integer getStaminaCost() { return staminaCost; }
    public Integer getStaminaRemaining() { return staminaRemaining; }
    public Integer getSpiritCost() { return spiritCost; }
    public Integer getSpiritRemaining() { return spiritRemaining; }

    // 战斗相关字段的getter和setter
    public Boolean getNeedCombat() { return needCombat; }
    public Long getMonsterId() { return monsterId; }
    public String getMonsterName() { return monsterName; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public void setItemFound(String itemFound) { this.itemFound = itemFound; }
    public void setSpiritualPowerGained(Integer spiritualPowerGained) { this.spiritualPowerGained = spiritualPowerGained; }
    public void setHealthLost(Integer healthLost) { this.healthLost = healthLost; }
    public void setStaminaCost(Integer staminaCost) { this.staminaCost = staminaCost; }
    public void setStaminaRemaining(Integer staminaRemaining) { this.staminaRemaining = staminaRemaining; }
    public void setSpiritCost(Integer spiritCost) { this.spiritCost = spiritCost; }
    public void setSpiritRemaining(Integer spiritRemaining) { this.spiritRemaining = spiritRemaining; }
    public void setNeedCombat(Boolean needCombat) { this.needCombat = needCombat; }
    public void setMonsterId(Long monsterId) { this.monsterId = monsterId; }
    public void setMonsterName(String monsterName) { this.monsterName = monsterName; }
}
