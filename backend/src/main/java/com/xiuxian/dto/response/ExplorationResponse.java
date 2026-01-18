package com.xiuxian.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiuxian.entity.ExplorationRecord;

import java.time.LocalDateTime;

/**
 * 探索结果响应DTO
 */
public class ExplorationResponse {

    private Long recordId;
    private Long characterId;
    private Long areaId;
    private String areaName;
    private String eventType;
    private String eventName;
    private String description;

    @JsonProperty("eventDescription")
    private String eventDescription;

    private String result;

    @JsonProperty("itemFound")
    private String itemFound;

    private String rewards;

    @JsonProperty("expGained")
    private Integer expGained;

    private Integer experienceGained;

    @JsonProperty("spiritualPowerGained")
    private Integer spiritualPowerGained;

    @JsonProperty("healthLost")
    private Integer healthLost;

    private Integer staminaCost;
    private Integer staminaRemaining;
    private Integer spiritCost;
    private Integer spiritRemaining;

    private Boolean needCombat;
    private Long monsterId;
    private String monsterName;
    private LocalDateTime createdAt;

    public ExplorationResponse() {
    }

    public static ExplorationResponse fromRecord(ExplorationRecord record, String areaName, String eventName) {
        ExplorationResponse response = new ExplorationResponse();
        response.setRecordId(record.getRecordId());
        response.setCharacterId(record.getCharacterId());
        response.setAreaId(record.getAreaId());
        response.setAreaName(areaName);
        response.setEventType(record.getEventType());
        response.setEventName(eventName);
        response.setResult(record.getResult());
        response.setRewards(record.getRewards());
        response.setExperienceGained(record.getExperienceGained());
        response.setCreatedAt(record.getCreatedAt());
        response.setNeedCombat(false);
        return response;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        // 同时设置eventDescription
        this.eventDescription = description;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        // 同时设置description
        this.description = eventDescription;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getItemFound() {
        return itemFound;
    }

    public void setItemFound(String itemFound) {
        this.itemFound = itemFound;
        // 同时设置rewards
        this.rewards = itemFound;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
        // 同时设置itemFound
        this.itemFound = rewards;
    }

    public Integer getExpGained() {
        return expGained;
    }

    public void setExpGained(Integer expGained) {
        this.expGained = expGained;
        // 同时设置experienceGained
        this.experienceGained = expGained;
    }

    public Integer getExperienceGained() {
        return experienceGained;
    }

    public void setExperienceGained(Integer experienceGained) {
        this.experienceGained = experienceGained;
        // 同时设置expGained
        this.expGained = experienceGained;
    }

    public Integer getSpiritualPowerGained() {
        return spiritualPowerGained;
    }

    public void setSpiritualPowerGained(Integer spiritualPowerGained) {
        this.spiritualPowerGained = spiritualPowerGained;
    }

    public Integer getHealthLost() {
        return healthLost;
    }

    public void setHealthLost(Integer healthLost) {
        this.healthLost = healthLost;
    }

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }

    public Integer getStaminaRemaining() {
        return staminaRemaining;
    }

    public void setStaminaRemaining(Integer staminaRemaining) {
        this.staminaRemaining = staminaRemaining;
    }

    public Integer getSpiritCost() {
        return spiritCost;
    }

    public void setSpiritCost(Integer spiritCost) {
        this.spiritCost = spiritCost;
    }

    public Integer getSpiritRemaining() {
        return spiritRemaining;
    }

    public void setSpiritRemaining(Integer spiritRemaining) {
        this.spiritRemaining = spiritRemaining;
    }

    public Boolean getNeedCombat() {
        return needCombat;
    }

    public void setNeedCombat(Boolean needCombat) {
        this.needCombat = needCombat;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
