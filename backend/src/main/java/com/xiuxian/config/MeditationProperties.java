package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 打坐配置属性
 */
@Component
@ConfigurationProperties(prefix = "app.meditation")
public class MeditationProperties {

    /**
     * 基础打坐时间（秒）
     */
    private int baseTime = 30;

    /**
     * 精神属性的时间减免系数（每点精神减少的秒数）
     */
    private double mindsetReductionCoefficient = 0.05;

    /**
     * 悟性属性的时间减免系数（每点悟性减少的秒数）
     */
    private double comprehensionReductionCoefficient = 0.05;

    /**
     * 最短打坐时间（秒）
     */
    private int minTime = 5;

    /**
     * 打坐恢复比例（0.0-1.0）
     */
    private double recoveryRatio = 0.3;

    // Getter和Setter方法
    public int getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(int baseTime) {
        this.baseTime = baseTime;
    }

    public double getMindsetReductionCoefficient() {
        return mindsetReductionCoefficient;
    }

    public void setMindsetReductionCoefficient(double mindsetReductionCoefficient) {
        this.mindsetReductionCoefficient = mindsetReductionCoefficient;
    }

    public double getComprehensionReductionCoefficient() {
        return comprehensionReductionCoefficient;
    }

    public void setComprehensionReductionCoefficient(double comprehensionReductionCoefficient) {
        this.comprehensionReductionCoefficient = comprehensionReductionCoefficient;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public double getRecoveryRatio() {
        return recoveryRatio;
    }

    public void setRecoveryRatio(double recoveryRatio) {
        this.recoveryRatio = recoveryRatio;
    }
}
