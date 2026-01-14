package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 角色技能实体
 */
@TableName("character_skill")
public class CharacterSkill {

    @TableId(type = IdType.AUTO)
    private Long characterSkillId;

    private Long characterId;

    private Long skillId;

    private Integer skillLevel;

    private Integer proficiency;

    private Boolean isEquipped;

    private Integer slotIndex;

    private LocalDateTime learnedAt;

    public CharacterSkill() {
    }

    public Long getCharacterSkillId() {
        return characterSkillId;
    }

    public void setCharacterSkillId(Long characterSkillId) {
        this.characterSkillId = characterSkillId;
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

    public Integer getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Integer getProficiency() {
        return proficiency;
    }

    public void setProficiency(Integer proficiency) {
        this.proficiency = proficiency;
    }

    public Boolean getIsEquipped() {
        return isEquipped;
    }

    public void setIsEquipped(Boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public Integer getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(Integer slotIndex) {
        this.slotIndex = slotIndex;
    }

    public LocalDateTime getLearnedAt() {
        return learnedAt;
    }

    public void setLearnedAt(LocalDateTime learnedAt) {
        this.learnedAt = learnedAt;
    }
}
