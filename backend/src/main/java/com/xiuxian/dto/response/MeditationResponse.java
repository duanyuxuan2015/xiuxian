package com.xiuxian.dto.response;

/**
 * 打坐响应DTO
 */
public class MeditationResponse {

    private Long characterId;
    private String playerName;
    private Integer staminaRecovered;
    private Integer currentStamina;
    private Integer maxStamina;
    private Integer spiritualPowerRecovered;
    private Integer currentSpiritualPower;
    private Integer maxSpiritualPower;
    private Integer healthRecovered;
    private Integer currentHealth;
    private Integer maxHealth;
    private String message;

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

    public Integer getStaminaRecovered() {
        return staminaRecovered;
    }

    public void setStaminaRecovered(Integer staminaRecovered) {
        this.staminaRecovered = staminaRecovered;
    }

    public Integer getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(Integer currentStamina) {
        this.currentStamina = currentStamina;
    }

    public Integer getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(Integer maxStamina) {
        this.maxStamina = maxStamina;
    }

    public Integer getSpiritualPowerRecovered() {
        return spiritualPowerRecovered;
    }

    public void setSpiritualPowerRecovered(Integer spiritualPowerRecovered) {
        this.spiritualPowerRecovered = spiritualPowerRecovered;
    }

    public Integer getCurrentSpiritualPower() {
        return currentSpiritualPower;
    }

    public void setCurrentSpiritualPower(Integer currentSpiritualPower) {
        this.currentSpiritualPower = currentSpiritualPower;
    }

    public Integer getMaxSpiritualPower() {
        return maxSpiritualPower;
    }

    public void setMaxSpiritualPower(Integer maxSpiritualPower) {
        this.maxSpiritualPower = maxSpiritualPower;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHealthRecovered() {
        return healthRecovered;
    }

    public void setHealthRecovered(Integer healthRecovered) {
        this.healthRecovered = healthRecovered;
    }

    public Integer getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(Integer currentHealth) {
        this.currentHealth = currentHealth;
    }

    public Integer getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }
}
