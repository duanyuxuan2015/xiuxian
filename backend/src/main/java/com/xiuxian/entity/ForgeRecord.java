package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 锻造记录实体
 */
@TableName("forge_record")
public class ForgeRecord {

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long characterId;

    private Long recipeId;

    private Long equipmentId;

    private Boolean success;

    private String resultQuality;

    private Integer experienceGained;

    private LocalDateTime createdAt;

    public ForgeRecord() {
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

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
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
