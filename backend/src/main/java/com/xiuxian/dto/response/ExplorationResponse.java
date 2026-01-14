package com.xiuxian.dto.response;

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
    private String result;
    private String rewards;
    private Integer experienceGained;
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
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public Integer getExperienceGained() {
        return experienceGained;
    }

    public void setExperienceGained(Integer experienceGained) {
        this.experienceGained = experienceGained;
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
