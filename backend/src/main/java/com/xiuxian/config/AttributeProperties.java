package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 属性计算配置类
 * 从application.yml中读取app.attributes配置
 */
@Component
@ConfigurationProperties(prefix = "app.attributes")
public class AttributeProperties {

    /**
     * 基础属性值配置
     */
    private BaseConfig base = new BaseConfig();

    /**
     * 属性系数配置
     */
    private CoefficientConfig coefficient = new CoefficientConfig();

    /**
     * 属性上限配置
     */
    private MaxConfig max = new MaxConfig();

    /**
     * 基础属性值
     */
    public static class BaseConfig {
        private int health = 100;           // 基础气血值
        private int stamina = 100;          // 基础体力值
        private int spiritualPower = 100;    // 基础灵力值
        private double critRate = 5;         // 基础暴击率（%）
        private double critDamage = 150;     // 基础暴击伤害（%）
        private int speed = 100;             // 基础速度值

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public int getStamina() {
            return stamina;
        }

        public void setStamina(int stamina) {
            this.stamina = stamina;
        }

        public int getSpiritualPower() {
            return spiritualPower;
        }

        public void setSpiritualPower(int spiritualPower) {
            this.spiritualPower = spiritualPower;
        }

        public double getCritRate() {
            return critRate;
        }

        public void setCritRate(double critRate) {
            this.critRate = critRate;
        }

        public double getCritDamage() {
            return critDamage;
        }

        public void setCritDamage(double critDamage) {
            this.critDamage = critDamage;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }

    /**
     * 属性系数配置
     */
    public static class CoefficientConfig {
        private int constitution = 10;           // 体质系数
        private int spirit = 10;                 // 精神系数
        private double fortuneCritRate = 0.5;    // 气运暴击率系数
        private double fortuneCritDamage = 2;    // 气运暴击伤害系数
        private double fortuneSpeed = 2;         // 气运速度系数

        public int getConstitution() {
            return constitution;
        }

        public void setConstitution(int constitution) {
            this.constitution = constitution;
        }

        public int getSpirit() {
            return spirit;
        }

        public void setSpirit(int spirit) {
            this.spirit = spirit;
        }

        public double getFortuneCritRate() {
            return fortuneCritRate;
        }

        public void setFortuneCritRate(double fortuneCritRate) {
            this.fortuneCritRate = fortuneCritRate;
        }

        public double getFortuneCritDamage() {
            return fortuneCritDamage;
        }

        public void setFortuneCritDamage(double fortuneCritDamage) {
            this.fortuneCritDamage = fortuneCritDamage;
        }

        public double getFortuneSpeed() {
            return fortuneSpeed;
        }

        public void setFortuneSpeed(double fortuneSpeed) {
            this.fortuneSpeed = fortuneSpeed;
        }
    }

    /**
     * 属性上限配置
     */
    public static class MaxConfig {
        private int attribute = 999;      // 单个属性最大值
        private int totalPoints = 9999;   // 总属性点数上限

        public int getAttribute() {
            return attribute;
        }

        public void setAttribute(int attribute) {
            this.attribute = attribute;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }
    }

    // Getters for outer class
    public BaseConfig getBase() {
        return base;
    }

    public void setBase(BaseConfig base) {
        this.base = base;
    }

    public CoefficientConfig getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(CoefficientConfig coefficient) {
        this.coefficient = coefficient;
    }

    public MaxConfig getMax() {
        return max;
    }

    public void setMax(MaxConfig max) {
        this.max = max;
    }
}
