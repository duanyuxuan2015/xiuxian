package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 修炼配置属性
 * 从application.yml中读取app.cultivation配置
 */
@Component
@ConfigurationProperties(prefix = "app.cultivation")
public class CultivationProperties {

    /**
     * 基础经验值配置
     */
    private BaseExpConfig baseExp = new BaseExpConfig();

    /**
     * 属性加成系数配置
     */
    private BonusConfig bonus = new BonusConfig();

    /**
     * 层次升级系数（每层升级所需经验系数）
     * 1层=1.0倍, 2层=1.5倍, 3层=2.0倍...
     */
    private double levelMultiplier = 0.5;

    /**
     * 每次修炼消耗的体力
     */
    private int staminaCost = 5;

    /**
     * 基础经验值配置类
     */
    public static class BaseExpConfig {
        /**
         * 基础经验值下限
         */
        private int min = 50;

        /**
         * 基础经验值上限
         */
        private int max = 200;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

    /**
     * 属性加成系数配置类
     */
    public static class BonusConfig {
        /**
         * 悟性加成系数（每点悟性增加的经验比例）
         * 默认0.001表示每点悟性增加0.1%
         */
        private double comprehension = 0.001;

        /**
         * 境界加成系数（每境界等级增加的经验比例）
         * 默认0.1表示每境界等级增加10%
         */
        private double realmPerLevel = 0.1;

        public double getComprehension() {
            return comprehension;
        }

        public void setComprehension(double comprehension) {
            this.comprehension = comprehension;
        }

        public double getRealmPerLevel() {
            return realmPerLevel;
        }

        public void setRealmPerLevel(double realmPerLevel) {
            this.realmPerLevel = realmPerLevel;
        }
    }

    // Getter和Setter方法

    public BaseExpConfig getBaseExp() {
        return baseExp;
    }

    public void setBaseExp(BaseExpConfig baseExp) {
        this.baseExp = baseExp;
    }

    public BonusConfig getBonus() {
        return bonus;
    }

    public void setBonus(BonusConfig bonus) {
        this.bonus = bonus;
    }

    public double getLevelMultiplier() {
        return levelMultiplier;
    }

    public void setLevelMultiplier(double levelMultiplier) {
        this.levelMultiplier = levelMultiplier;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(int staminaCost) {
        this.staminaCost = staminaCost;
    }
}
