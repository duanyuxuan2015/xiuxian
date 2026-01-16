package com.xiuxian.client.model;

import java.time.LocalDateTime;

/**
 * 炼丹响应
 */
public class AlchemyResponse {
    private Long recordId;
    private Long characterId;
    private Long recipeId;
    private String recipeName;
    private boolean success;
    private String pillName;
    private String resultQuality;
    private Integer quantity;
    private Integer experienceGained;
    private String message;
    private LocalDateTime createdAt;

    public Long getRecordId() { return recordId; }
    public Long getCharacterId() { return characterId; }
    public Long getRecipeId() { return recipeId; }
    public String getRecipeName() { return recipeName; }
    public boolean isSuccess() { return success; }
    public String getPillName() { return pillName; }
    public String getResultQuality() { return resultQuality; }
    public Integer getQuantity() { return quantity; }
    public Integer getExpGained() { return experienceGained; } // 兼容旧代码
    public Integer getExperienceGained() { return experienceGained; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setPillName(String pillName) { this.pillName = pillName; }
    public void setResultQuality(String resultQuality) { this.resultQuality = resultQuality; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setExpGained(Integer expGained) { this.experienceGained = expGained; } // 兼容旧代码
    public void setExperienceGained(Integer experienceGained) { this.experienceGained = experienceGained; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
