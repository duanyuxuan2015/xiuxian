package com.xiuxian.dto.response;

/**
 * 探索事件详情响应
 */
public class ExplorationEventDetailResponse {

    private Long eventId;
    private Long areaId;
    private String eventType;
    private String eventName;
    private String description;
    private Integer level;
    private String rewardType;
    private Long rewardId;
    private Integer rewardQuantityMin;
    private Integer rewardQuantityMax;
    private Long monsterId;

    public static ExplorationEventDetailResponse fromEntity(com.xiuxian.entity.ExplorationEvent event) {
        ExplorationEventDetailResponse response = new ExplorationEventDetailResponse();
        response.setEventId(event.getEventId());
        response.setAreaId(event.getAreaId());
        response.setEventType(event.getEventType());
        response.setEventName(event.getEventName());
        response.setDescription(event.getDescription());
        response.setLevel(event.getLevel());
        response.setRewardType(event.getRewardType());
        response.setRewardId(event.getRewardId());
        response.setRewardQuantityMin(event.getRewardQuantityMin());
        response.setRewardQuantityMax(event.getRewardQuantityMax());
        response.setMonsterId(event.getMonsterId());
        return response;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getRewardQuantityMin() {
        return rewardQuantityMin;
    }

    public void setRewardQuantityMin(Integer rewardQuantityMin) {
        this.rewardQuantityMin = rewardQuantityMin;
    }

    public Integer getRewardQuantityMax() {
        return rewardQuantityMax;
    }

    public void setRewardQuantityMax(Integer rewardQuantityMax) {
        this.rewardQuantityMax = rewardQuantityMax;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }
}
