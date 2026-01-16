package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 一键装备请求DTO
 */
public class AutoEquipRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    /**
     * 优先属性（可选）
     * 可选值：physical（物理）、ice（冰系）、fire（火系）、lightning（雷系）
     * 为null时按baseScore装备
     */
    private String priorityAttribute;

    public AutoEquipRequest() {}

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getPriorityAttribute() {
        return priorityAttribute;
    }

    public void setPriorityAttribute(String priorityAttribute) {
        this.priorityAttribute = priorityAttribute;
    }
}
