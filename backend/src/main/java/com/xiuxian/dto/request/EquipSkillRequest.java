package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 装配技能请求DTO
 */
public class EquipSkillRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "角色技能ID不能为空")
    private Long characterSkillId;

    @NotNull(message = "技能槽位不能为空")
    @Min(value = 1, message = "技能槽位最小为1")
    @Max(value = 8, message = "技能槽位最大为8")
    private Integer slotIndex;

    public EquipSkillRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getCharacterSkillId() {
        return characterSkillId;
    }

    public void setCharacterSkillId(Long characterSkillId) {
        this.characterSkillId = characterSkillId;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }
}
