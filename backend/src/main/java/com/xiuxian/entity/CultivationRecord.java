package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 修炼记录实体类
 */
@TableName("cultivation_record")
public class CultivationRecord {

    @TableId(value = "cultivation_id", type = IdType.AUTO)
    private Long cultivationId;

    private Long characterId;

    private String startRealm;

    private Integer startLevel;

    private String endRealm;

    private Integer endLevel;

    private Integer expGained;

    private Integer staminaConsumed;

    private Integer isBreakthrough;

    private Integer breakthroughSuccess;

    private LocalDateTime cultivationTime;

    public CultivationRecord() {
    }

    public Long getCultivationId() {
        return cultivationId;
    }

    public void setCultivationId(Long cultivationId) {
        this.cultivationId = cultivationId;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getStartRealm() {
        return startRealm;
    }

    public void setStartRealm(String startRealm) {
        this.startRealm = startRealm;
    }

    public Integer getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    public String getEndRealm() {
        return endRealm;
    }

    public void setEndRealm(String endRealm) {
        this.endRealm = endRealm;
    }

    public Integer getEndLevel() {
        return endLevel;
    }

    public void setEndLevel(Integer endLevel) {
        this.endLevel = endLevel;
    }

    public Integer getExpGained() {
        return expGained;
    }

    public void setExpGained(Integer expGained) {
        this.expGained = expGained;
    }

    public Integer getStaminaConsumed() {
        return staminaConsumed;
    }

    public void setStaminaConsumed(Integer staminaConsumed) {
        this.staminaConsumed = staminaConsumed;
    }

    public Integer getIsBreakthrough() {
        return isBreakthrough;
    }

    public void setIsBreakthrough(Integer isBreakthrough) {
        this.isBreakthrough = isBreakthrough;
    }

    public Integer getBreakthroughSuccess() {
        return breakthroughSuccess;
    }

    public void setBreakthroughSuccess(Integer breakthroughSuccess) {
        this.breakthroughSuccess = breakthroughSuccess;
    }

    public LocalDateTime getCultivationTime() {
        return cultivationTime;
    }

    public void setCultivationTime(LocalDateTime cultivationTime) {
        this.cultivationTime = cultivationTime;
    }

    @Override
    public String toString() {
        return "CultivationRecord{" +
                "cultivationId=" + cultivationId +
                ", characterId=" + characterId +
                ", expGained=" + expGained +
                '}';
    }
}
