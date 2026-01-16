package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 宗门职位升级配置类
 * 从application.yml中读取app.position-promotion配置
 */
@Component
@ConfigurationProperties(prefix = "app.position-promotion")
public class PositionPromotionProperties {

    private List<PositionRequirement> requirements;

    public List<PositionRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<PositionRequirement> requirements) {
        this.requirements = requirements;
    }

    /**
     * 根据职位等级获取升级要求
     * @param level 职位等级 (1=弟子, 2=内门弟子, 3=核心弟子, 4=长老)
     * @return 升级要求，未找到返回null
     */
    public PositionRequirement getRequirementByLevel(int level) {
        if (requirements == null) {
            return null;
        }
        return requirements.stream()
                .filter(r -> r.getPositionLevel() == level)
                .findFirst()
                .orElse(null);
    }

    /**
     * 职位升级要求
     */
    public static class PositionRequirement {
        /**
         * 职位等级
         * 1=弟子, 2=内门弟子, 3=核心弟子, 4=长老, 5=掌门
         */
        private int positionLevel;

        /**
         * 所需声望值
         */
        private int requiredReputation;

        /**
         * 贡献值消耗
         */
        private int contributionCost;

        /**
         * 灵石消耗
         */
        private long spiritStonesCost;

        /**
         * 是否启用该升级路径
         */
        private boolean enabled = true;

        public int getPositionLevel() {
            return positionLevel;
        }

        public void setPositionLevel(int positionLevel) {
            this.positionLevel = positionLevel;
        }

        public int getRequiredReputation() {
            return requiredReputation;
        }

        public void setRequiredReputation(int requiredReputation) {
            this.requiredReputation = requiredReputation;
        }

        public int getContributionCost() {
            return contributionCost;
        }

        public void setContributionCost(int contributionCost) {
            this.contributionCost = contributionCost;
        }

        public long getSpiritStonesCost() {
            return spiritStonesCost;
        }

        public void setSpiritStonesCost(long spiritStonesCost) {
            this.spiritStonesCost = spiritStonesCost;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
