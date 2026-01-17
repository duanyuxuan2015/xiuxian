package com.xiuxian.dto.response;

/**
 * 材料列表项响应DTO
 */
public class MaterialListItemResponse {

    private Long materialId;
    private String materialName;
    private String materialType;
    private Integer materialTier;
    private String quality;
    private Integer spiritStones;

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Integer getMaterialTier() {
        return materialTier;
    }

    public void setMaterialTier(Integer materialTier) {
        this.materialTier = materialTier;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }
}
