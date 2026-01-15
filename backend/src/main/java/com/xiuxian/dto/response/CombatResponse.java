package com.xiuxian.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 战斗响应DTO
 */
public class CombatResponse {

    private Long combatId;
    private Long characterId;
    private String playerName;
    private Long monsterId;
    private String monsterName;
    private String combatMode;
    private Boolean victory;
    private Integer turns;
    private Integer damageDealt;
    private Integer damageTaken;
    private Integer criticalHits;
    private Integer staminaConsumed;
    private Integer expGained;
    private Integer spiritStonesGained;
    private List<String> itemsDropped;
    private Integer characterHpRemaining;
    private Integer characterStaminaRemaining;  // 剩余体力
    private Integer characterSpiritualPowerRemaining;  // 剩余灵力
    private String message;
    private List<String> combatLog;
    private LocalDateTime combatTime;

    public CombatResponse() {
    }

    public Long getCombatId() {
        return combatId;
    }

    public void setCombatId(Long combatId) {
        this.combatId = combatId;
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

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public String getCombatMode() {
        return combatMode;
    }

    public void setCombatMode(String combatMode) {
        this.combatMode = combatMode;
    }

    public Boolean getVictory() {
        return victory;
    }

    public void setVictory(Boolean victory) {
        this.victory = victory;
    }

    public Integer getTurns() {
        return turns;
    }

    public void setTurns(Integer turns) {
        this.turns = turns;
    }

    public Integer getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Integer damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Integer getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(Integer damageTaken) {
        this.damageTaken = damageTaken;
    }

    public Integer getCriticalHits() {
        return criticalHits;
    }

    public void setCriticalHits(Integer criticalHits) {
        this.criticalHits = criticalHits;
    }

    public Integer getStaminaConsumed() {
        return staminaConsumed;
    }

    public void setStaminaConsumed(Integer staminaConsumed) {
        this.staminaConsumed = staminaConsumed;
    }

    public Integer getExpGained() {
        return expGained;
    }

    public void setExpGained(Integer expGained) {
        this.expGained = expGained;
    }

    public Integer getSpiritStonesGained() {
        return spiritStonesGained;
    }

    public void setSpiritStonesGained(Integer spiritStonesGained) {
        this.spiritStonesGained = spiritStonesGained;
    }

    public List<String> getItemsDropped() {
        return itemsDropped;
    }

    public void setItemsDropped(List<String> itemsDropped) {
        this.itemsDropped = itemsDropped;
    }

    public Integer getCharacterHpRemaining() {
        return characterHpRemaining;
    }

    public void setCharacterHpRemaining(Integer characterHpRemaining) {
        this.characterHpRemaining = characterHpRemaining;
    }

    public Integer getCharacterStaminaRemaining() {
        return characterStaminaRemaining;
    }

    public void setCharacterStaminaRemaining(Integer characterStaminaRemaining) {
        this.characterStaminaRemaining = characterStaminaRemaining;
    }

    public Integer getCharacterSpiritualPowerRemaining() {
        return characterSpiritualPowerRemaining;
    }

    public void setCharacterSpiritualPowerRemaining(Integer characterSpiritualPowerRemaining) {
        this.characterSpiritualPowerRemaining = characterSpiritualPowerRemaining;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getCombatLog() {
        return combatLog;
    }

    public void setCombatLog(List<String> combatLog) {
        this.combatLog = combatLog;
    }

    public LocalDateTime getCombatTime() {
        return combatTime;
    }

    public void setCombatTime(LocalDateTime combatTime) {
        this.combatTime = combatTime;
    }
}
