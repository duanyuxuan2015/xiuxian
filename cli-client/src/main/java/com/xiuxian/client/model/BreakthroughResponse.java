package com.xiuxian.client.model;

/**
 * 境界突破响应
 * 注意：移除了LocalDateTime字段以避免Gson解析问题
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

    // Getters
    public Long getCharacterId() { return characterId; }
    public String getPlayerName() { return playerName; }
    public Boolean getSuccess() { return success; }
    public Integer getBreakthroughRate() { return breakthroughRate; }
    public String getPreviousRealm() { return previousRealm; }
    public Integer getPreviousLevel() { return previousLevel; }
    public String getCurrentRealm() { return currentRealm; }
    public Integer getCurrentLevel() { return currentLevel; }
    public Integer getAttributePointsGained() { return attributePointsGained; }
    public Integer getHpBonusGained() { return hpBonusGained; }
    public Integer getSpBonusGained() { return spBonusGained; }
    public Integer getAttackBonusGained() { return attackBonusGained; }
    public Integer getDefenseBonusGained() { return defenseBonusGained; }
    public String getMessage() { return message; }

    // Setters
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setSuccess(Boolean success) { this.success = success; }
    public void setBreakthroughRate(Integer breakthroughRate) { this.breakthroughRate = breakthroughRate; }
    public void setPreviousRealm(String previousRealm) { this.previousRealm = previousRealm; }
    public void setPreviousLevel(Integer previousLevel) { this.previousLevel = previousLevel; }
    public void setCurrentRealm(String currentRealm) { this.currentRealm = currentRealm; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }
    public void setAttributePointsGained(Integer attributePointsGained) { this.attributePointsGained = attributePointsGained; }
    public void setHpBonusGained(Integer hpBonusGained) { this.hpBonusGained = hpBonusGained; }
    public void setSpBonusGained(Integer spBonusGained) { this.spBonusGained = spBonusGained; }
    public void setAttackBonusGained(Integer attackBonusGained) { this.attackBonusGained = attackBonusGained; }
    public void setDefenseBonusGained(Integer defenseBonusGained) { this.defenseBonusGained = defenseBonusGained; }
    public void setMessage(String message) { this.message = message; }
}
