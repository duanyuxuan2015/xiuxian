package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 角色宗门声望实体
 */
@TableName("character_sect_reputation")
public class CharacterSectReputation {

    @TableId(type = IdType.AUTO)
    private Long reputationId;

    private Long characterId;

    private Long sectId;

    private Integer reputation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CharacterSectReputation() {
    }

    public Long getReputationId() {
        return reputationId;
    }

    public void setReputationId(Long reputationId) {
        this.reputationId = reputationId;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
