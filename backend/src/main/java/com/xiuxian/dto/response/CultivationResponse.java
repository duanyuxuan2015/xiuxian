package com.xiuxian.dto.response;

import java.time.LocalDateTime;

/**
 * 修炼响应DTO
 */
public class CultivationResponse {

    private Long cultivationId;
    private Long characterId;
    private String playerName;
    private String startRealm;
    private Integer startLevel;
    private String endRealm;
    private Integer endLevel;
    private Integer expGained;
    private Integer staminaConsumed;
    private Long currentExperience;
    private Integer currentStamina;
    private Boolean leveledUp;
    private Integer availablePointsGained;
    private Boolean needsBreakthrough; // 是否需要突破
    private String nextRealm; // 下一个境界名称
    private String message;
    private LocalDateTime cultivationTime;

    public CultivationResponse() {
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public Long getCurrentExperience() {
        return currentExperience;
    }

    public void setCurrentExperience(Long currentExperience) {
        this.currentExperience = currentExperience;
    }

    public Integer getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(Integer currentStamina) {
        this.currentStamina = currentStamina;
    }

    public Boolean getLeveledUp() {
        return leveledUp;
    }

    public void setLeveledUp(Boolean leveledUp) {
        this.leveledUp = leveledUp;
    }

    public Integer getAvailablePointsGained() {
        return availablePointsGained;
    }

    public void setAvailablePointsGained(Integer availablePointsGained) {
        this.availablePointsGained = availablePointsGained;
    }

    public Boolean getNeedsBreakthrough() {
        return needsBreakthrough;
    }

    public void setNeedsBreakthrough(Boolean needsBreakthrough) {
        this.needsBreakthrough = needsBreakthrough;
    }

    public String getNextRealm() {
        return nextRealm;
    }

    public void setNextRealm(String nextRealm) {
        this.nextRealm = nextRealm;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCultivationTime() {
        return cultivationTime;
    }

    public void setCultivationTime(LocalDateTime cultivationTime) {
        this.cultivationTime = cultivationTime;
    }
}
