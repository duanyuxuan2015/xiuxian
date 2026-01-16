package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 挂机战斗配置类
 * 从application.yml中读取app.idle-combat配置
 */
@Component
@ConfigurationProperties(prefix = "app.idle-combat")
public class IdleCombatProperties {

    /**
     * 挂机最大战斗轮数
     */
    private int maxBattles = 30;

    public int getMaxBattles() {
        return maxBattles;
    }

    public void setMaxBattles(int maxBattles) {
        this.maxBattles = maxBattles;
    }
}
