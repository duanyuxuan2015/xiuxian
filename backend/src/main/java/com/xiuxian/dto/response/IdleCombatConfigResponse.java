package com.xiuxian.dto.response;

/**
 * 挂机战斗配置响应DTO
 */
public class IdleCombatConfigResponse {

    private Integer maxBattles;  // 最大战斗轮数

    public IdleCombatConfigResponse() {
    }

    public IdleCombatConfigResponse(Integer maxBattles) {
        this.maxBattles = maxBattles;
    }

    public Integer getMaxBattles() {
        return maxBattles;
    }

    public void setMaxBattles(Integer maxBattles) {
        this.maxBattles = maxBattles;
    }
}
