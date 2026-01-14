package com.xiuxian.client.model;

/**
 * 宗门响应
 */
public class SectResponse {
    private Long sectId;
    private String sectName;
    private String description;
    private String specialty;
    private Integer memberCount;
    private Integer requiredRealmLevel;
    private Boolean isJoined;

    public Long getSectId() { return sectId; }
    public String getSectName() { return sectName; }
    public String getDescription() { return description; }
    public String getSpecialty() { return specialty; }
    public Integer getMemberCount() { return memberCount; }
    public Integer getRequiredRealmLevel() { return requiredRealmLevel; }
    public Boolean getIsJoined() { return isJoined; }

    public void setSectId(Long sectId) { this.sectId = sectId; }
    public void setSectName(String sectName) { this.sectName = sectName; }
    public void setDescription(String description) { this.description = description; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public void setRequiredRealmLevel(Integer requiredRealmLevel) { this.requiredRealmLevel = requiredRealmLevel; }
    public void setIsJoined(Boolean isJoined) { this.isJoined = isJoined; }

    // 兼容旧方法名
    public Integer getCurrentMembers() { return memberCount; }
    public Integer getMaxMembers() { return null; } // 后端不提供此字段
    public void setCurrentMembers(Integer currentMembers) { this.memberCount = currentMembers; }
    public void setMaxMembers(Integer maxMembers) { /* 忽略 */ }
}
