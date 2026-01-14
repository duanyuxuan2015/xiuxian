package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 丹药实体
 */
@TableName("pill")
public class Pill {

    @TableId(type = IdType.AUTO)
    private Long pillId;

    private String pillName;

    private Integer pillTier;

    private String quality;

    private String effectType;

    private Integer effectValue;

    private Integer duration;

    private Integer stackLimit;

    private Integer spiritStones;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Pill() {
    }

    public Long getPillId() {
        return pillId;
    }

    public void setPillId(Long pillId) {
        this.pillId = pillId;
    }

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public Integer getPillTier() {
        return pillTier;
    }

    public void setPillTier(Integer pillTier) {
        this.pillTier = pillTier;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public Integer getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(Integer effectValue) {
        this.effectValue = effectValue;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(Integer stackLimit) {
        this.stackLimit = stackLimit;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Pill{" +
                "pillId=" + pillId +
                ", pillName='" + pillName + '\'' +
                ", pillTier=" + pillTier +
                ", quality='" + quality + '\'' +
                ", effectType='" + effectType + '\'' +
                '}';
    }
}
