package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 角色装备关联实体类
 */
@TableName("character_equipment")
public class CharacterEquipment {

    @TableId(value = "character_equipment_id", type = IdType.AUTO)
    private Long characterEquipmentId;

    private Long characterId;

    private Long equipmentId;

    private String equipmentSlot;

    private LocalDateTime equippedAt;

    public CharacterEquipment() {
    }

    public Long getCharacterEquipmentId() {
        return characterEquipmentId;
    }

    public void setCharacterEquipmentId(Long characterEquipmentId) {
        this.characterEquipmentId = characterEquipmentId;
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

    public LocalDateTime getEquippedAt() {
        return equippedAt;
    }

    public void setEquippedAt(LocalDateTime equippedAt) {
        this.equippedAt = equippedAt;
    }

    @Override
    public String toString() {
        return "CharacterEquipment{" +
                "characterEquipmentId=" + characterEquipmentId +
                ", characterId=" + characterId +
                ", equipmentId=" + equipmentId +
                ", equipmentSlot='" + equipmentSlot + '\'' +
                '}';
    }
}
