package com.xiuxian.client.model;

/**
 * 打坐响应
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

    // Getters
    public Long getCharacterId() { return characterId; }
    public String getPlayerName() { return playerName; }
    public Integer getStaminaRecovered() { return staminaRecovered; }
    public Integer getCurrentStamina() { return currentStamina; }
    public Integer getMaxStamina() { return maxStamina; }
    public Integer getSpiritualPowerRecovered() { return spiritualPowerRecovered; }
    public Integer getCurrentSpiritualPower() { return currentSpiritualPower; }
    public Integer getMaxSpiritualPower() { return maxSpiritualPower; }
    public Integer getHealthRecovered() { return healthRecovered; }
    public Integer getCurrentHealth() { return currentHealth; }
    public Integer getMaxHealth() { return maxHealth; }
    public String getMessage() { return message; }

    // Setters
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setStaminaRecovered(Integer staminaRecovered) { this.staminaRecovered = staminaRecovered; }
    public void setCurrentStamina(Integer currentStamina) { this.currentStamina = currentStamina; }
    public void setMaxStamina(Integer maxStamina) { this.maxStamina = maxStamina; }
    public void setSpiritualPowerRecovered(Integer spiritualPowerRecovered) { this.spiritualPowerRecovered = spiritualPowerRecovered; }
    public void setCurrentSpiritualPower(Integer currentSpiritualPower) { this.currentSpiritualPower = currentSpiritualPower; }
    public void setMaxSpiritualPower(Integer maxSpiritualPower) { this.maxSpiritualPower = maxSpiritualPower; }
    public void setHealthRecovered(Integer healthRecovered) { this.healthRecovered = healthRecovered; }
    public void setCurrentHealth(Integer currentHealth) { this.currentHealth = currentHealth; }
    public void setMaxHealth(Integer maxHealth) { this.maxHealth = maxHealth; }
    public void setMessage(String message) { this.message = message; }
}
