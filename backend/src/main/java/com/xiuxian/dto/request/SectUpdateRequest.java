package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 更新宗门请求DTO
 */
public class SectUpdateRequest {

    @Size(max = 100, message = "宗门名称长度不能超过100个字符")
    private String sectName;

    @Size(max = 50, message = "宗门类型长度不能超过50个字符")
    private String sectType;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Size(max = 100, message = "专长长度不能超过100个字符")
    private String specialty;

    @Min(value = 1, message = "要求境界等级最小为1")
    @Max(value = 999, message = "要求境界等级最大为999")
    private Integer requiredRealmLevel;

    @Size(max = 100, message = "技能侧重长度不能超过100个字符")
    private String skillFocus;

    @Size(max = 500, message = "加入要求长度不能超过500个字符")
    private String joinRequirement;

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
}
