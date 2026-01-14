package com.xiuxian.dto.response;

import java.time.LocalDateTime;

/**
 * 境界突破响应DTO
 */
public class BreakthroughResponse {

    private Long characterId;
    private String playerName;
    private Boolean success;
    private Integer breakthroughRate;
    private String previousRealm;
    private Integer previousLevel;
    private String currentRealm;
    private Integer currentLevel;
    private Integer attributePointsGained;
    private Integer hpBonusGained;
    private Integer spBonusGained;
    private Integer attackBonusGained;
    private Integer defenseBonusGained;
    private String message;
    private LocalDateTime breakthroughTime;

    public BreakthroughResponse() {
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getBreakthroughRate() {
        return breakthroughRate;
    }

    public void setBreakthroughRate(Integer breakthroughRate) {
        this.breakthroughRate = breakthroughRate;
    }

    public String getPreviousRealm() {
        return previousRealm;
    }

    public void setPreviousRealm(String previousRealm) {
        this.previousRealm = previousRealm;
    }

    public Integer getPreviousLevel() {
        return previousLevel;
    }

    public void setPreviousLevel(Integer previousLevel) {
        this.previousLevel = previousLevel;
    }

    public String getCurrentRealm() {
        return currentRealm;
    }

    public void setCurrentRealm(String currentRealm) {
        this.currentRealm = currentRealm;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getAttributePointsGained() {
        return attributePointsGained;
    }

    public void setAttributePointsGained(Integer attributePointsGained) {
        this.attributePointsGained = attributePointsGained;
    }

    public Integer getHpBonusGained() {
        return hpBonusGained;
    }

    public void setHpBonusGained(Integer hpBonusGained) {
        this.hpBonusGained = hpBonusGained;
    }

    public Integer getSpBonusGained() {
        return spBonusGained;
    }

    public void setSpBonusGained(Integer spBonusGained) {
        this.spBonusGained = spBonusGained;
    }

    public Integer getAttackBonusGained() {
        return attackBonusGained;
    }

    public void setAttackBonusGained(Integer attackBonusGained) {
        this.attackBonusGained = attackBonusGained;
    }

    public Integer getDefenseBonusGained() {
        return defenseBonusGained;
    }

    public void setDefenseBonusGained(Integer defenseBonusGained) {
        this.defenseBonusGained = defenseBonusGained;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getBreakthroughTime() {
        return breakthroughTime;
    }

    public void setBreakthroughTime(LocalDateTime breakthroughTime) {
        this.breakthroughTime = breakthroughTime;
    }
}
