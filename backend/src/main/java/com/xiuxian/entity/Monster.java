package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 妖兽实体类
 */
@TableName("monster")
public class Monster {

    @TableId(value = "monster_id", type = IdType.AUTO)
    private Long monsterId;

    private String monsterName;

    private Integer realmId;

    private String monsterType;

    private Integer speed;

    private Integer hp;

    private Integer attackPower;

    private Integer defensePower;

    private String attackElement;

    private Integer physicalResist;

    private Integer iceResist;

    private Integer fireResist;

    private Integer lightningResist;

    private Integer staminaCost;

    private Integer expReward;

    private Integer spiritStonesReward;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Monster() {
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

    public Integer getRealmId() {
        return realmId;
    }

    public void setRealmId(Integer realmId) {
        this.realmId = realmId;
    }

    public String getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(String monsterType) {
        this.monsterType = monsterType;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(Integer attackPower) {
        this.attackPower = attackPower;
    }

    public Integer getDefensePower() {
        return defensePower;
    }

    public void setDefensePower(Integer defensePower) {
        this.defensePower = defensePower;
    }

    public String getAttackElement() {
        return attackElement;
    }

    public void setAttackElement(String attackElement) {
        this.attackElement = attackElement;
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

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }

    public Integer getExpReward() {
        return expReward;
    }

    public void setExpReward(Integer expReward) {
        this.expReward = expReward;
    }

    public Integer getSpiritStonesReward() {
        return spiritStonesReward;
    }

    public void setSpiritStonesReward(Integer spiritStonesReward) {
        this.spiritStonesReward = spiritStonesReward;
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
        return "Monster{" +
                "monsterId=" + monsterId +
                ", monsterName='" + monsterName + '\'' +
                ", monsterType='" + monsterType + '\'' +
                '}';
    }
}
