package com.xiuxian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 装备请求DTO
 */
public class EquipRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "装备ID不能为空")
    private Long equipmentId;

    @NotBlank(message = "装备槽位不能为空")
    private String equipmentSlot;

    public EquipRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentSlot() {
        return equipmentSlot;
    }

    public void setEquipmentSlot(String equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }
}
