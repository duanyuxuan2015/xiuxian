package com.xiuxian.dto.response;

/**
 * 宗门列表项响应DTO
 */
public class SectListItemResponse {

    private Long sectId;
    private String sectName;
    private String sectType;
    private Integer requiredRealmLevel;
    private String specialty;

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

    public Integer getRequiredRealmLevel() {
        return requiredRealmLevel;
    }

    public void setRequiredRealmLevel(Integer requiredRealmLevel) {
        this.requiredRealmLevel = requiredRealmLevel;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
