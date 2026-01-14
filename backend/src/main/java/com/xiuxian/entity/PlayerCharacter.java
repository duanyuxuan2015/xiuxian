package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@TableName("`character`")
public class PlayerCharacter {

    @TableId(value = "character_id", type = IdType.AUTO)
    private Long characterId;

    private String playerName;

    private String characterName;

    private Integer realmId;

    private Integer realmLevel;

    private Long experience;

    private Integer availablePoints;

    private Integer spiritualPower;

    private Integer spiritualPowerMax;

    private Integer stamina;

    private Integer staminaMax;

    private Integer health;

    private Integer healthMax;

    private Integer currentHealth;

    private Integer currentSpirit;

    private Integer mindset;

    private Integer constitution;

    private Integer spirit;

    private Integer comprehension;

    private Integer luck;

    private Integer fortune;

    private Long spiritStones;

    private Long sectId;

    private String sectPosition;

    private Integer contribution;

    private Integer reputation;

    private Integer alchemyLevel;

    private Integer alchemyExp;

    private Integer forgingLevel;

    private Integer forgingExp;

    private Integer forgeLevel;

    private String currentState;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public PlayerCharacter() {
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

    public Integer getRealmId() {
        return realmId;
    }

    public void setRealmId(Integer realmId) {
        this.realmId = realmId;
    }

    public Integer getRealmLevel() {
        return realmLevel;
    }

    public void setRealmLevel(Integer realmLevel) {
        this.realmLevel = realmLevel;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Integer getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Integer getSpiritualPower() {
        return spiritualPower;
    }

    public void setSpiritualPower(Integer spiritualPower) {
        this.spiritualPower = spiritualPower;
    }

    public Integer getSpiritualPowerMax() {
        return spiritualPowerMax;
    }

    public void setSpiritualPowerMax(Integer spiritualPowerMax) {
        this.spiritualPowerMax = spiritualPowerMax;
    }

    public Integer getStamina() {
        return stamina;
    }

    public void setStamina(Integer stamina) {
        this.stamina = stamina;
    }

    public Integer getStaminaMax() {
        return staminaMax;
    }

    public void setStaminaMax(Integer staminaMax) {
        this.staminaMax = staminaMax;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getHealthMax() {
        return healthMax;
    }

    public void setHealthMax(Integer healthMax) {
        this.healthMax = healthMax;
    }

    public Integer getMindset() {
        return mindset;
    }

    public void setMindset(Integer mindset) {
        this.mindset = mindset;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
    }

    public Integer getSpirit() {
        return spirit;
    }

    public void setSpirit(Integer spirit) {
        this.spirit = spirit;
    }

    public Integer getComprehension() {
        return comprehension;
    }

    public void setComprehension(Integer comprehension) {
        this.comprehension = comprehension;
    }

    public Integer getLuck() {
        return luck;
    }

    public void setLuck(Integer luck) {
        this.luck = luck;
    }

    public Integer getFortune() {
        return fortune;
    }

    public void setFortune(Integer fortune) {
        this.fortune = fortune;
    }

    public Long getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Long spiritStones) {
        this.spiritStones = spiritStones;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getSectPosition() {
        return sectPosition;
    }

    public void setSectPosition(String sectPosition) {
        this.sectPosition = sectPosition;
    }

    public Integer getContribution() {
        return contribution;
    }

    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public Integer getAlchemyLevel() {
        return alchemyLevel;
    }

    public void setAlchemyLevel(Integer alchemyLevel) {
        this.alchemyLevel = alchemyLevel;
    }

    public Integer getAlchemyExp() {
        return alchemyExp;
    }

    public void setAlchemyExp(Integer alchemyExp) {
        this.alchemyExp = alchemyExp;
    }

    public Integer getForgingLevel() {
        return forgingLevel;
    }

    public void setForgingLevel(Integer forgingLevel) {
        this.forgingLevel = forgingLevel;
    }

    public Integer getForgingExp() {
        return forgingExp;
    }

    public void setForgingExp(Integer forgingExp) {
        this.forgingExp = forgingExp;
    }

    public Integer getForgeLevel() {
        return forgeLevel;
    }

    public void setForgeLevel(Integer forgeLevel) {
        this.forgeLevel = forgeLevel;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Integer getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(Integer currentHealth) {
        this.currentHealth = currentHealth;
    }

    public Integer getCurrentSpirit() {
        return currentSpirit;
    }

    public void setCurrentSpirit(Integer currentSpirit) {
        this.currentSpirit = currentSpirit;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Character{" +
                "characterId=" + characterId +
                ", playerName='" + playerName + '\'' +
                ", realmId=" + realmId +
                ", realmLevel=" + realmLevel +
                '}';
    }
}
