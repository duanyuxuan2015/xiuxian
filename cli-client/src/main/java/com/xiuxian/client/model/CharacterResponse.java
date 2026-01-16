package com.xiuxian.client.model;

/**
 * 角色信息
 */
public class CharacterResponse {
    private Long characterId;
    private String playerName;
    private Integer realmLevel;
    private String realmName;
    private Long experience;  // 修为经验
    private Integer spiritualPower;
    private Integer spiritualPowerMax;
    private Integer health;
    private Integer healthMax;
    private Integer stamina;
    private Integer staminaMax;
    private Integer alchemyLevel;
    private Integer forgingLevel;  // 后端字段名
    private String sectName;
    private String sectPosition;
    private Integer contribution;
    private Integer reputation;
    private Long spiritStones;
    private String currentState;
    private Integer constitution;
    private Integer spirit;
    private Integer comprehension;
    private Integer luck;
    private Integer fortune;
    private Integer mindset;
    private Integer availablePoints;
    private Integer attack;  // 攻击力（包含境界加成）
    private Integer defense; // 防御力（包含境界加成）
    private Double critRate; // 暴击率（%）
    private Double critDamage; // 暴击伤害（%）
    private Double speed;    // 速度

    // 抗性属性
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;

    // Getters
    public Long getCharacterId() { return characterId; }
    public String getPlayerName() { return playerName; }
    public Integer getRealmLevel() { return realmLevel; }
    public String getRealmName() { return realmName; }
    public Long getExperience() { return experience; }
    public Long getCultivation() { return experience; }  // 兼容旧代码
    public Integer getSpiritualPower() { return spiritualPower; }
    public Integer getSpiritualPowerMax() { return spiritualPowerMax; }
    public Integer getHealth() { return health; }
    public Integer getMaxHealth() { return healthMax; }  // 兼容旧代码
    public Integer getHealthMax() { return healthMax; }
    public Integer getStamina() { return stamina; }
    public Integer getMaxStamina() { return staminaMax; }  // 兼容旧代码
    public Integer getStaminaMax() { return staminaMax; }
    public Integer getAlchemyLevel() { return alchemyLevel; }
    public Integer getForgeLevel() { return forgingLevel; }  // 兼容旧代码
    public Integer getForgingLevel() { return forgingLevel; }
    public String getSectName() { return sectName; }
    public String getSectPosition() { return sectPosition; }
    public Integer getContribution() { return contribution; }
    public Integer getReputation() { return reputation; }
    public Long getSpiritStones() { return spiritStones; }
    public String getCurrentState() { return currentState; }
    public Integer getConstitution() { return constitution; }
    public Integer getSpirit() { return spirit; }
    public Integer getComprehension() { return comprehension; }
    public Integer getLuck() { return luck; }
    public Integer getFortune() { return fortune; }
    public Integer getMindset() { return mindset; }
    public Integer getAvailablePoints() { return availablePoints; }

    public Integer getAttack() {
        return attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public Double getCritRate() {
        return critRate;
    }

    public Double getCritDamage() {
        return critDamage;
    }

    public Double getSpeed() {
        return speed;
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

    public String getGender() {
        return "男";  // 默认性别，后端暂无此字段
    }

    // Setters
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setRealmLevel(Integer realmLevel) { this.realmLevel = realmLevel; }
    public void setRealmName(String realmName) { this.realmName = realmName; }
    public void setExperience(Long experience) { this.experience = experience; }
    public void setCultivation(Long cultivation) { this.experience = cultivation; }  // 兼容旧代码
    public void setSpiritualPower(Integer spiritualPower) { this.spiritualPower = spiritualPower; }
    public void setSpiritualPowerMax(Integer spiritualPowerMax) { this.spiritualPowerMax = spiritualPowerMax; }
    public void setHealth(Integer health) { this.health = health; }
    public void setMaxHealth(Integer maxHealth) { this.healthMax = maxHealth; }  // 兼容旧代码
    public void setHealthMax(Integer healthMax) { this.healthMax = healthMax; }
    public void setStamina(Integer stamina) { this.stamina = stamina; }
    public void setMaxStamina(Integer maxStamina) { this.staminaMax = maxStamina; }  // 兼容旧代码
    public void setStaminaMax(Integer staminaMax) { this.staminaMax = staminaMax; }
    public void setAlchemyLevel(Integer alchemyLevel) { this.alchemyLevel = alchemyLevel; }
    public void setForgeLevel(Integer forgeLevel) { this.forgingLevel = forgeLevel; }  // 兼容旧代码
    public void setForgingLevel(Integer forgingLevel) { this.forgingLevel = forgingLevel; }
    public void setSectName(String sectName) { this.sectName = sectName; }
    public void setSectPosition(String sectPosition) { this.sectPosition = sectPosition; }
    public void setContribution(Integer contribution) { this.contribution = contribution; }
    public void setReputation(Integer reputation) { this.reputation = reputation; }
    public void setSpiritStones(Long spiritStones) { this.spiritStones = spiritStones; }
    public void setCurrentState(String currentState) { this.currentState = currentState; }
    public void setConstitution(Integer constitution) { this.constitution = constitution; }
    public void setSpirit(Integer spirit) { this.spirit = spirit; }
    public void setComprehension(Integer comprehension) { this.comprehension = comprehension; }
    public void setLuck(Integer luck) { this.luck = luck; }
    public void setFortune(Integer fortune) { this.fortune = fortune; }
    public void setMindset(Integer mindset) { this.mindset = mindset; }
    public void setAvailablePoints(Integer availablePoints) { this.availablePoints = availablePoints; }
    public void setAttack(Integer attack) { this.attack = attack; }
    public void setDefense(Integer defense) { this.defense = defense; }
    public void setCritRate(Double critRate) { this.critRate = critRate; }
    public void setCritDamage(Double critDamage) { this.critDamage = critDamage; }
    public void setSpeed(Double speed) { this.speed = speed; }
}
