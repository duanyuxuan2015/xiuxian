package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 探索区域实体
 */
@TableName("exploration_area")
public class ExplorationArea {

    @TableId(type = IdType.AUTO)
    private Long areaId;

    private String areaName;

    private String description;

    private Integer requiredRealmLevel;

    private Integer dangerLevel;

    private Integer spiritCost;

    private Integer staminaCost;

    private Integer baseExploreTime;

    public ExplorationArea() {
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

    public Integer getBaseExploreTime() {
        return baseExploreTime;
    }

    public void setBaseExploreTime(Integer baseExploreTime) {
        this.baseExploreTime = baseExploreTime;
    }

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }
}
