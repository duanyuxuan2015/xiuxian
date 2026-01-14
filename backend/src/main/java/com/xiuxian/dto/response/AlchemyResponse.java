package com.xiuxian.dto.response;

import com.xiuxian.entity.AlchemyRecord;
import com.xiuxian.entity.Pill;

import java.time.LocalDateTime;

/**
 * 炼丹结果响应DTO
 */
public class AlchemyResponse {

    private Long recordId;
    private Long characterId;
    private Long recipeId;
    private String recipeName;
    private Boolean success;
    private String pillName;
    private String resultQuality;
    private Integer quantity;
    private Integer experienceGained;
    private String message;
    private LocalDateTime createdAt;

    public AlchemyResponse() {
    }

    public static AlchemyResponse fromEntity(AlchemyRecord record, String recipeName, Pill pill) {
        AlchemyResponse response = new AlchemyResponse();
        response.setRecordId(record.getRecordId());
        response.setCharacterId(record.getCharacterId());
        response.setRecipeId(record.getRecipeId());
        response.setRecipeName(recipeName);
        response.setSuccess(record.getSuccess());
        response.setPillName(pill != null ? pill.getPillName() : null);
        response.setResultQuality(record.getResultQuality());
        response.setQuantity(record.getQuantity());
        response.setExperienceGained(record.getExperienceGained());
        response.setCreatedAt(record.getCreatedAt());

        if (record.getSuccess()) {
            response.setMessage(String.format("炼丹成功！获得 %s(%s) x%d，炼丹经验 +%d",
                    pill.getPillName(), record.getResultQuality(), record.getQuantity(), record.getExperienceGained()));
        } else {
            response.setMessage(String.format("炼丹失败，材料损失，炼丹经验 +%d", record.getExperienceGained()));
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

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
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
