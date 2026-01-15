package com.xiuxian.dto.response;

/**
 * 属性点分配响应DTO
 */
public class AllocatePointsResponse {

    private Long characterId;
    private String playerName;
    private Integer newConstitution;
    private Integer newSpirit;
    private Integer newComprehension;
    private Integer newLuck;
    private Integer newFortune;
    private Integer remainingPoints;
    private String message;

    // 衍生属性
    private Integer newAttack;
    private Integer newDefense;
    private Integer newHealthMax;
    private Integer newStaminaMax;
    private Integer newSpiritualPowerMax;
    private Double newCritRate;
    private Double newCritDamage;
    private Double newSpeed;

    public AllocatePointsResponse() {
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

    public Integer getNewConstitution() {
        return newConstitution;
    }

    public void setNewConstitution(Integer newConstitution) {
        this.newConstitution = newConstitution;
    }

    public Integer getNewSpirit() {
        return newSpirit;
    }

    public void setNewSpirit(Integer newSpirit) {
        this.newSpirit = newSpirit;
    }

    public Integer getNewComprehension() {
        return newComprehension;
    }

    public void setNewComprehension(Integer newComprehension) {
        this.newComprehension = newComprehension;
    }

    public Integer getNewLuck() {
        return newLuck;
    }

    public void setNewLuck(Integer newLuck) {
        this.newLuck = newLuck;
    }

    public Integer getNewFortune() {
        return newFortune;
    }

    public void setNewFortune(Integer newFortune) {
        this.newFortune = newFortune;
    }

    public Integer getRemainingPoints() {
        return remainingPoints;
    }

    public void setRemainingPoints(Integer remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNewAttack() {
        return newAttack;
    }

    public void setNewAttack(Integer newAttack) {
        this.newAttack = newAttack;
    }

    public Integer getNewDefense() {
        return newDefense;
    }

    public void setNewDefense(Integer newDefense) {
        this.newDefense = newDefense;
    }

    public Integer getNewHealthMax() {
        return newHealthMax;
    }

    public void setNewHealthMax(Integer newHealthMax) {
        this.newHealthMax = newHealthMax;
    }

    public Integer getNewStaminaMax() {
        return newStaminaMax;
    }

    public void setNewStaminaMax(Integer newStaminaMax) {
        this.newStaminaMax = newStaminaMax;
    }

    public Integer getNewSpiritualPowerMax() {
        return newSpiritualPowerMax;
    }

    public void setNewSpiritualPowerMax(Integer newSpiritualPowerMax) {
        this.newSpiritualPowerMax = newSpiritualPowerMax;
    }

    public Double getNewCritRate() {
        return newCritRate;
    }

    public void setNewCritRate(Double newCritRate) {
        this.newCritRate = newCritRate;
    }

    public Double getNewCritDamage() {
        return newCritDamage;
    }

    public void setNewCritDamage(Double newCritDamage) {
        this.newCritDamage = newCritDamage;
    }

    public Double getNewSpeed() {
        return newSpeed;
    }

    public void setNewSpeed(Double newSpeed) {
        this.newSpeed = newSpeed;
    }
}
