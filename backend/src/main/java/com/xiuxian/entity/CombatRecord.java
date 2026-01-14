package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 战斗记录实体类
 */
@TableName("combat_record")
public class CombatRecord {

    @TableId(value = "combat_id", type = IdType.AUTO)
    private Long combatId;

    private Long characterId;

    private Long monsterId;

    private String combatMode;

    private Integer isVictory;

    private Integer turns;

    private Integer damageDealt;

    private Integer damageTaken;

    private Integer criticalHits;

    private Integer staminaConsumed;

    private Integer expGained;

    private String itemsDropped;

    private LocalDateTime combatTime;

    public CombatRecord() {
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

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getCombatMode() {
        return combatMode;
    }

    public void setCombatMode(String combatMode) {
        this.combatMode = combatMode;
    }

    public Integer getIsVictory() {
        return isVictory;
    }

    public void setIsVictory(Integer isVictory) {
        this.isVictory = isVictory;
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

    public String getItemsDropped() {
        return itemsDropped;
    }

    public void setItemsDropped(String itemsDropped) {
        this.itemsDropped = itemsDropped;
    }

    public LocalDateTime getCombatTime() {
        return combatTime;
    }

    public void setCombatTime(LocalDateTime combatTime) {
        this.combatTime = combatTime;
    }

    @Override
    public String toString() {
        return "CombatRecord{" +
                "combatId=" + combatId +
                ", characterId=" + characterId +
                ", monsterId=" + monsterId +
                ", isVictory=" + isVictory +
                '}';
    }
}
