package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 战斗开始请求DTO
 */
public class CombatStartRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "妖兽ID不能为空")
    private Long monsterId;

    private String combatMode = "手动战斗";

    public CombatStartRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getCombatMode() {
        return combatMode;
    }

    public void setCombatMode(String combatMode) {
        this.combatMode = combatMode;
    }
}
