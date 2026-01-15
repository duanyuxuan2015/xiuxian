package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 妖怪装备掉落配置实体
 */
@TableName("monster_drop")
public class MonsterDrop {

    @TableId(type = IdType.AUTO)
    private Long monsterDropId;

    @TableField("monster_id")
    private Long monsterId;

    @TableField("equipment_id")
    private Long equipmentId;

    @TableField("drop_rate")
    private BigDecimal dropRate;

    @TableField("drop_quantity")
    private Integer dropQuantity;

    @TableField("min_quality")
    private String minQuality;

    @TableField("max_quality")
    private String maxQuality;

    @TableField("is_guaranteed")
    private Integer isGuaranteed;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("deleted")
    private Integer deleted;

    // Getters and Setters
    public Long getMonsterDropId() {
        return monsterDropId;
    }

    public void setMonsterDropId(Long monsterDropId) {
        this.monsterDropId = monsterDropId;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public BigDecimal getDropRate() {
        return dropRate;
    }

    public void setDropRate(BigDecimal dropRate) {
        this.dropRate = dropRate;
    }

    public Integer getDropQuantity() {
        return dropQuantity;
    }

    public void setDropQuantity(Integer dropQuantity) {
        this.dropQuantity = dropQuantity;
    }

    public String getMinQuality() {
        return minQuality;
    }

    public void setMinQuality(String minQuality) {
        this.minQuality = minQuality;
    }

    public String getMaxQuality() {
        return maxQuality;
    }

    public void setMaxQuality(String maxQuality) {
        this.maxQuality = maxQuality;
    }

    public Integer getIsGuaranteed() {
        return isGuaranteed;
    }

    public void setIsGuaranteed(Integer isGuaranteed) {
        this.isGuaranteed = isGuaranteed;
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
}
