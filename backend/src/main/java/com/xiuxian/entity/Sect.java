package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 宗门实体类
 */
@TableName("sect")
public class Sect {

    @TableId(value = "sect_id", type = IdType.AUTO)
    private Long sectId;

    private String sectName;

    private String sectType;

    private String description;

    private String specialty;

    private Integer requiredRealmLevel;

    private String skillFocus;

    private String joinRequirement;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Sect() {
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getSectType() {
        return sectType;
    }

    public void setSectType(String sectType) {
        this.sectType = sectType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Integer getRequiredRealmLevel() {
        return requiredRealmLevel;
    }

    public void setRequiredRealmLevel(Integer requiredRealmLevel) {
        this.requiredRealmLevel = requiredRealmLevel;
    }

    public String getSkillFocus() {
        return skillFocus;
    }

    public void setSkillFocus(String skillFocus) {
        this.skillFocus = skillFocus;
    }

    public String getJoinRequirement() {
        return joinRequirement;
    }

    public void setJoinRequirement(String joinRequirement) {
        this.joinRequirement = joinRequirement;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Sect{" +
                "sectId=" + sectId +
                ", sectName='" + sectName + '\'' +
                ", sectType='" + sectType + '\'' +
                '}';
    }
}
