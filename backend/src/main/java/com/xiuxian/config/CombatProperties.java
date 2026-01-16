package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 战斗配置属性
 * 从application.yml中读取app.combat配置
 */
@Component
@ConfigurationProperties(prefix = "app.combat")
public class CombatProperties {

    /**
     * 最大战斗回合数
     */
    private int maxTurns = 50;

    /**
     * 基础暴击率（%）
     */
    private int criticalRateBase = 5;

    /**
     * 暴击伤害倍率
     */
    private double criticalDamageMultiplier = 1.5;

    /**
     * 伤害随机波动配置
     */
    private DamageFluctuationConfig damageFluctuation = new DamageFluctuationConfig();

    /**
     * 战斗失败时体力消耗比例
     */
    private double staminaCostDefeatRatio = 0.5;

    /**
     * 伤害随机波动配置类
     */
    public static class DamageFluctuationConfig {
        /**
         * 伤害波动下限（如0.9表示90%）
         */
        private double min = 0.9;

        /**
         * 伤害波动上限（如1.1表示110%）
         */
        private double max = 1.1;

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }
    }

    // Getter和Setter方法

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public int getCriticalRateBase() {
        return criticalRateBase;
    }

    public void setCriticalRateBase(int criticalRateBase) {
        this.criticalRateBase = criticalRateBase;
    }

    public double getCriticalDamageMultiplier() {
        return criticalDamageMultiplier;
    }

    public void setCriticalDamageMultiplier(double criticalDamageMultiplier) {
        this.criticalDamageMultiplier = criticalDamageMultiplier;
    }

    public DamageFluctuationConfig getDamageFluctuation() {
        return damageFluctuation;
    }

    public void setDamageFluctuation(DamageFluctuationConfig damageFluctuation) {
        this.damageFluctuation = damageFluctuation;
    }

    public double getStaminaCostDefeatRatio() {
        return staminaCostDefeatRatio;
    }

    public void setStaminaCostDefeatRatio(double staminaCostDefeatRatio) {
        this.staminaCostDefeatRatio = staminaCostDefeatRatio;
    }
}
