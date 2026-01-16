package com.xiuxian.dto.response;

import com.xiuxian.entity.PlayerCharacter;
import java.time.LocalDateTime;

/**
 * 角色响应DTO
 */
public class CharacterResponse {

    private Long characterId;
    private String playerName;
    private String realmName;
    private Integer realmLevel;
    private Long experience;
    private Integer availablePoints;
    private Integer spiritualPower;
    private Integer spiritualPowerMax;
    private Integer stamina;
    private Integer staminaMax;
    private Integer health;
    private Integer healthMax;
    private Integer mindset;
    private Integer constitution;
    private Integer spirit;
    private Integer comprehension;
    private Integer luck;
    private Integer fortune;
    private Double critRate;
    private Double critDamage;
    private Double speed;
    private Integer attack;
    private Integer defense;
    private Long spiritStones;
    private String sectName;
    private String sectPosition;
    private Integer contribution;
    private Integer reputation;
    private Integer alchemyLevel;
    private Integer forgingLevel;
    private String currentState;
    private LocalDateTime createdAt;

    // 抗性属性
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;

    public CharacterResponse() {
    }

    public static CharacterResponse fromEntity(PlayerCharacter character, String realmName, String sectName) {
        CharacterResponse response = new CharacterResponse();
        response.setCharacterId(character.getCharacterId());
        response.setPlayerName(character.getPlayerName());
        response.setRealmName(realmName);
        response.setRealmLevel(character.getRealmLevel());
        response.setExperience(character.getExperience());
        response.setAvailablePoints(character.getAvailablePoints());
        response.setSpiritualPower(character.getSpiritualPower());
        response.setSpiritualPowerMax(character.getSpiritualPowerMax());
        response.setStamina(character.getStamina());
        response.setStaminaMax(character.getStaminaMax());
        response.setHealth(character.getHealth());
        response.setHealthMax(character.getHealthMax());
        response.setMindset(character.getMindset());
        response.setConstitution(character.getConstitution());
        response.setSpirit(character.getSpirit());
        response.setComprehension(character.getComprehension());
        response.setLuck(character.getLuck());
        response.setFortune(character.getFortune());
        response.setCritRate(character.getCritRate());
        response.setCritDamage(character.getCritDamage());
        response.setSpeed(character.getSpeed());
        response.setSpiritStones(character.getSpiritStones());
        response.setSectName(sectName);
        response.setSectPosition(character.getSectPosition());
        response.setContribution(character.getContribution());
        response.setReputation(character.getReputation());
        response.setAlchemyLevel(character.getAlchemyLevel());
        response.setForgingLevel(character.getForgingLevel());
        response.setCurrentState(character.getCurrentState());
        response.setCreatedAt(character.getCreatedAt());
        return response;
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

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
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

    public Double getCritRate() {
        return critRate;
    }

    public void setCritRate(Double critRate) {
        this.critRate = critRate;
    }

    public Double getCritDamage() {
        return critDamage;
    }

    public void setCritDamage(Double critDamage) {
        this.critDamage = critDamage;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Long getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Long spiritStones) {
        this.spiritStones = spiritStones;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
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

    public Integer getForgingLevel() {
        return forgingLevel;
    }

    public void setForgingLevel(Integer forgingLevel) {
        this.forgingLevel = forgingLevel;
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

    public Integer getPhysicalResist() {
        return physicalResist;
    }

    public void setPhysicalResist(Integer physicalResist) {
        this.physicalResist = physicalResist;
    }

    public Integer getIceResist() {
        return iceResist;
    }

    public void setIceResist(Integer iceResist) {
        this.iceResist = iceResist;
    }

    public Integer getFireResist() {
        return fireResist;
    }

    public void setFireResist(Integer fireResist) {
        this.fireResist = fireResist;
    }

    public Integer getLightningResist() {
        return lightningResist;
    }

    public void setLightningResist(Integer lightningResist) {
        this.lightningResist = lightningResist;
    }
}
