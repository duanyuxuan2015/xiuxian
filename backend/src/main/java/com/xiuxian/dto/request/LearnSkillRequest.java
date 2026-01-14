package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 学习技能请求DTO
 */
public class LearnSkillRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "技能ID不能为空")
    private Long skillId;

    public LearnSkillRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
