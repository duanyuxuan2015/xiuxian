package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 探索事件概率配置
 */
@Component
@ConfigurationProperties(prefix = "exploration-probability")
public class ExplorationProbabilityConfig {

    /**
     * 最大调整系数 (0.0 - 1.0)
     */
    private double maxAdjustment = 0.3;

    /**
     * 机缘每增加多少点，fortuneFactor增加0.1
     * 默认20点，即机缘200点达到最大影响
     */
    private int fortunePointsPerFactor = 20;

    /**
     * 各级别的基础概率 [level1, level2, level3, level4]
     */
    private List<Double> baseProbabilities = List.of(0.55, 0.30, 0.10, 0.05);

    /**
     * 各级别的调整方向 [level1, level2, level3, level4]
     * 负数表示减少，正数表示增加，绝对值越大影响越明显
     */
    private List<Double> directions = List.of(-1.0, -0.2, 0.5, 1.0);

    /**
     * 概率范围限制
     */
    private double minProbability = 0.05;
    private double maxProbability = 0.80;

    public double getMaxAdjustment() {
        return maxAdjustment;
    }

    public void setMaxAdjustment(double maxAdjustment) {
        this.maxAdjustment = maxAdjustment;
    }

    public int getFortunePointsPerFactor() {
        return fortunePointsPerFactor;
    }

    public void setFortunePointsPerFactor(int fortunePointsPerFactor) {
        this.fortunePointsPerFactor = fortunePointsPerFactor;
    }

    public List<Double> getBaseProbabilities() {
        return baseProbabilities;
    }

    public void setBaseProbabilities(List<Double> baseProbabilities) {
        this.baseProbabilities = baseProbabilities;
    }

    public List<Double> getDirections() {
        return directions;
    }

    public void setDirections(List<Double> directions) {
        this.directions = directions;
    }

    public double getMinProbability() {
        return minProbability;
    }

    public void setMinProbability(double minProbability) {
        this.minProbability = minProbability;
    }

    public double getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(double maxProbability) {
        this.maxProbability = maxProbability;
    }
}
