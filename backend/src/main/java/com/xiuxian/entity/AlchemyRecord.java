package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 炼丹记录实体
 */
@TableName("alchemy_record")
public class AlchemyRecord {

    @TableId(value = "alchemy_id", type = IdType.AUTO)
    private Long recordId;

    private Long characterId;

    private Long recipeId;

    @TableField(exist = false)
    private Long pillId;

    @TableField("is_success")
    private Boolean success;

    @TableField("output_quality")
    private String resultQuality;

    @TableField("output_quantity")
    private Integer quantity;

    @TableField("exp_gained")
    private Integer experienceGained;

    @TableField("alchemy_time")
    private LocalDateTime createdAt;

    public AlchemyRecord() {
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

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Long getPillId() {
        return pillId;
    }

    public void setPillId(Long pillId) {
        this.pillId = pillId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResultQuality() {
        return resultQuality;
    }

    public void setResultQuality(String resultQuality) {
        this.resultQuality = resultQuality;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getExperienceGained() {
        return experienceGained;
    }

    public void setExperienceGained(Integer experienceGained) {
        this.experienceGained = experienceGained;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
