package com.xiuxian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 宗门任务配置类
 * 从application.yml中读取app.sect-task配置
 */
@Component
@ConfigurationProperties(prefix = "app.sect-task")
public class SectTaskProperties {

    /**
     * 每日任务接取上限
     */
    private int dailyAcceptLimit = 3;

    public int getDailyAcceptLimit() {
        return dailyAcceptLimit;
    }

    public void setDailyAcceptLimit(int dailyAcceptLimit) {
        this.dailyAcceptLimit = dailyAcceptLimit;
    }
}
