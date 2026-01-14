package com.xiuxian.dto.response;

import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.ForgeRecord;

import java.time.LocalDateTime;

/**
 * 锻造结果响应DTO
 */
public class ForgeResponse {

    private Long recordId;
    private Long characterId;
    private Long recipeId;
    private String recipeName;
    private Boolean success;
    private String equipmentName;
    private String resultQuality;
    private Integer experienceGained;
    private String message;
    private LocalDateTime createdAt;

    public ForgeResponse() {
    }

    public static ForgeResponse fromEntity(ForgeRecord record, String recipeName, Equipment equipment) {
        ForgeResponse response = new ForgeResponse();
        response.setRecordId(record.getRecordId());
        response.setCharacterId(record.getCharacterId());
        response.setRecipeId(record.getRecipeId());
        response.setRecipeName(recipeName);
        response.setSuccess(record.getSuccess());
        response.setEquipmentName(equipment != null ? equipment.getEquipmentName() : null);
        response.setResultQuality(record.getResultQuality());
        response.setExperienceGained(record.getExperienceGained());
        response.setCreatedAt(record.getCreatedAt());

        if (record.getSuccess()) {
            response.setMessage(String.format("锻造成功！获得 %s(%s)，锻造经验 +%d",
                    equipment.getEquipmentName(), record.getResultQuality(), record.getExperienceGained()));
        } else {
            response.setMessage(String.format("锻造失败，材料损失，锻造经验 +%d", record.getExperienceGained()));
        }

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

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
