package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 境界配置实体类
 */
@TableName("realm")
public class Realm {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String realmName;

    private Integer realmLevel;

    private Integer subLevels;

    private Long requiredExp;

    private Integer breakthroughRate;

    private Integer hpBonus;

    private Integer spBonus;

    private Integer staminaBonus;

    private Integer attackBonus;

    private Integer defenseBonus;

    private Integer lifespanBonus;

    private Integer levelUpPoints;

    private String realmStage;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Realm() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getSubLevels() {
        return subLevels;
    }

    public void setSubLevels(Integer subLevels) {
        this.subLevels = subLevels;
    }

    public Long getRequiredExp() {
        return requiredExp;
    }

    public void setRequiredExp(Long requiredExp) {
        this.requiredExp = requiredExp;
    }

    public Integer getBreakthroughRate() {
        return breakthroughRate;
    }

    public void setBreakthroughRate(Integer breakthroughRate) {
        this.breakthroughRate = breakthroughRate;
    }

    public Integer getHpBonus() {
        return hpBonus;
    }

    public void setHpBonus(Integer hpBonus) {
        this.hpBonus = hpBonus;
    }

    public Integer getSpBonus() {
        return spBonus;
    }

    public void setSpBonus(Integer spBonus) {
        this.spBonus = spBonus;
    }

    public Integer getStaminaBonus() {
        return staminaBonus;
    }

    public void setStaminaBonus(Integer staminaBonus) {
        this.staminaBonus = staminaBonus;
    }

    public Integer getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(Integer attackBonus) {
        this.attackBonus = attackBonus;
    }

    public Integer getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(Integer defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public Integer getLifespanBonus() {
        return lifespanBonus;
    }

    public void setLifespanBonus(Integer lifespanBonus) {
        this.lifespanBonus = lifespanBonus;
    }

    public Integer getLevelUpPoints() {
        return levelUpPoints;
    }

    public void setLevelUpPoints(Integer levelUpPoints) {
        this.levelUpPoints = levelUpPoints;
    }

    public String getRealmStage() {
        return realmStage;
    }

    public void setRealmStage(String realmStage) {
        this.realmStage = realmStage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "Realm{" +
                "id=" + id +
                ", realmName='" + realmName + '\'' +
                ", realmLevel=" + realmLevel +
                ", realmStage='" + realmStage + '\'' +
                '}';
    }
}
