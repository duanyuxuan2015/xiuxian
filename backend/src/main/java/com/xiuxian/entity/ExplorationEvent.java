package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 探索事件实体
 */
@TableName("exploration_event")
public class ExplorationEvent {

    @TableId(type = IdType.AUTO)
    private Long eventId;

    private Long areaId;

    private String eventType;

    private String eventName;

    private String description;

    private Integer probability;

    private String rewardType;

    private Long rewardId;

    private Integer rewardQuantityMin;

    private Integer rewardQuantityMax;

    private Long monsterId;

    public ExplorationEvent() {
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

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
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
