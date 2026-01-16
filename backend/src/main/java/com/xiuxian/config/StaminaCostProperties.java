package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 体力消耗配置属性
 * 从 application.yml 中读取 app.stamina-costs 配置
 * 统一管理各个场景的体力消耗
 */
@Component
@ConfigurationProperties(prefix = "app.stamina-costs")
public class StaminaCostProperties {

    /**
     * 修炼消耗的体力
     */
    private int cultivation = 5;

    /**
     * 战斗基础消耗倍数（乘以妖兽的staminaCost）
     * 默认为 1.0，表示直接使用妖兽表中的体力消耗
     */
    private double combatMultiplier = 1.0;

    /**
     * 战斗失败时体力消耗比例
     * 0.5 表示失败时消耗 50% 的体力
     */
    private double combatDefeatRatio = 0.5;

    /**
     * 炼丹消耗的体力
     * 0 表示不消耗体力
     */
    private int alchemy = 0;

    /**
     * 炼器消耗的体力
     * 0 表示不消耗体力
     */
    private int forging = 0;

    /**
     * 探索消耗的体力
     * 0 表示不消耗体力
     */
    private int exploration = 0;

    /**
     * 打坐消耗的体力
     * 0 表示不消耗体力
     */
    private int meditation = 0;

    /**
     * 突破消耗的体力
     * 0 表示不消耗体力
     */
    private int breakthrough = 0;

    // Getter 和 Setter 方法

    public int getCultivation() {
        return cultivation;
    }

    public void setCultivation(int cultivation) {
        this.cultivation = cultivation;
    }

    public double getCombatMultiplier() {
        return combatMultiplier;
    }

    public void setCombatMultiplier(double combatMultiplier) {
        this.combatMultiplier = combatMultiplier;
    }

    public double getCombatDefeatRatio() {
        return combatDefeatRatio;
    }

    public void setCombatDefeatRatio(double combatDefeatRatio) {
        this.combatDefeatRatio = combatDefeatRatio;
    }

    public int getAlchemy() {
        return alchemy;
    }

    public void setAlchemy(int alchemy) {
        this.alchemy = alchemy;
    }

    public int getForging() {
        return forging;
    }

    public void setForging(int forging) {
        this.forging = forging;
    }

    public int getExploration() {
        return exploration;
    }

    public void setExploration(int exploration) {
        this.exploration = exploration;
    }

    public int getMeditation() {
        return meditation;
    }

    public void setMeditation(int meditation) {
        this.meditation = meditation;
    }

    public int getBreakthrough() {
        return breakthrough;
    }

    public void setBreakthrough(int breakthrough) {
        this.breakthrough = breakthrough;
    }
}
