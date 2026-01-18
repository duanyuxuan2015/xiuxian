package com.xiuxian.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 探索事件更新请求
 */
public class ExplorationEventUpdateRequest {

    @NotNull(message = "事件ID不能为空")
    private Long eventId;

    @NotNull(message = "区域ID不能为空")
    private Long areaId;

    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    @NotBlank(message = "事件名称不能为空")
    private String eventName;

    private String description;

    @NotNull(message = "事件级别不能为空")
    @Min(value = 1, message = "事件级别必须大于0")
    private Integer level;

    private String rewardType;

    private Long rewardId;

    @Min(value = 1, message = "奖励数量最小值必须大于0")
    private Integer rewardQuantityMin;

    @Min(value = 1, message = "奖励数量最大值必须大于0")
    private Integer rewardQuantityMax;

    private Long monsterId;

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
