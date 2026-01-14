package com.xiuxian.client.model;

import java.util.List;

/**
 * 战斗响应
 * 注意：移除了LocalDateTime字段以避免Gson解析问题
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
    private String message;
    private List<String> combatLog;

    // Getters
    public Long getCombatId() { return combatId; }
    public Long getCharacterId() { return characterId; }
    public String getPlayerName() { return playerName; }
    public Long getMonsterId() { return monsterId; }
    public String getMonsterName() { return monsterName; }
    public String getCombatMode() { return combatMode; }
    public Boolean getVictory() { return victory; }
    public Integer getTurns() { return turns; }
    public Integer getDamageDealt() { return damageDealt; }
    public Integer getDamageTaken() { return damageTaken; }
    public Integer getCriticalHits() { return criticalHits; }
    public Integer getStaminaConsumed() { return staminaConsumed; }
    public Integer getExpGained() { return expGained; }
    public Integer getSpiritStonesGained() { return spiritStonesGained; }
    public List<String> getItemsDropped() { return itemsDropped; }
    public Integer getCharacterHpRemaining() { return characterHpRemaining; }
    public String getMessage() { return message; }
    public List<String> getCombatLog() { return combatLog; }

    // Setters
    public void setCombatId(Long combatId) { this.combatId = combatId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setMonsterId(Long monsterId) { this.monsterId = monsterId; }
    public void setMonsterName(String monsterName) { this.monsterName = monsterName; }
    public void setCombatMode(String combatMode) { this.combatMode = combatMode; }
    public void setVictory(Boolean victory) { this.victory = victory; }
    public void setTurns(Integer turns) { this.turns = turns; }
    public void setDamageDealt(Integer damageDealt) { this.damageDealt = damageDealt; }
    public void setDamageTaken(Integer damageTaken) { this.damageTaken = damageTaken; }
    public void setCriticalHits(Integer criticalHits) { this.criticalHits = criticalHits; }
    public void setStaminaConsumed(Integer staminaConsumed) { this.staminaConsumed = staminaConsumed; }
    public void setExpGained(Integer expGained) { this.expGained = expGained; }
    public void setSpiritStonesGained(Integer spiritStonesGained) { this.spiritStonesGained = spiritStonesGained; }
    public void setItemsDropped(List<String> itemsDropped) { this.itemsDropped = itemsDropped; }
    public void setCharacterHpRemaining(Integer characterHpRemaining) { this.characterHpRemaining = characterHpRemaining; }
    public void setMessage(String message) { this.message = message; }
    public void setCombatLog(List<String> combatLog) { this.combatLog = combatLog; }

    // 兼容方法
    public boolean isVictory() { return victory != null && victory; }
    public Integer getPlayerHealthRemaining() { return characterHpRemaining; }
    public Integer getSpiritualPowerGained() { return spiritStonesGained; }
    public String getMaterialDropped() {
        return (itemsDropped != null && !itemsDropped.isEmpty()) ? itemsDropped.get(0) : null;
    }
    public Integer getMaterialQuantity() {
        return (itemsDropped != null) ? itemsDropped.size() : 0;
    }
}
