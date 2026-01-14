package com.xiuxian.dto.response;

import com.xiuxian.entity.Sect;

/**
 * 宗门响应DTO
 */
public class SectResponse {

    private Long sectId;
    private String sectName;
    private String description;
    private String specialty;
    private Integer memberCount;
    private Integer requiredRealmLevel;
    private Boolean isJoined;

    public SectResponse() {
    }

    public static SectResponse fromEntity(Sect sect, int memberCount, boolean isJoined) {
        SectResponse response = new SectResponse();
        response.setSectId(sect.getSectId());
        response.setSectName(sect.getSectName());
        response.setDescription(sect.getDescription());
        response.setSpecialty(sect.getSpecialty());
        response.setMemberCount(memberCount);
        response.setRequiredRealmLevel(sect.getRequiredRealmLevel());
        response.setIsJoined(isJoined);
        return response;
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

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getRequiredRealmLevel() {
        return requiredRealmLevel;
    }

    public void setRequiredRealmLevel(Integer requiredRealmLevel) {
        this.requiredRealmLevel = requiredRealmLevel;
    }

    public Boolean getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(Boolean isJoined) {
        this.isJoined = isJoined;
    }
}
