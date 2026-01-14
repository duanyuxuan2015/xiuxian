package com.xiuxian.dto.response;

import com.xiuxian.entity.Material;

/**
 * 材料响应DTO
 */
public class MaterialResponse {

    private Long materialId;
    private String materialName;
    private String materialType;
    private Integer materialTier;
    private String quality;
    private String description;
    private Integer spiritStones;
    private Integer ownedQuantity;

    public MaterialResponse() {
    }

    public static MaterialResponse fromEntity(Material material, Integer ownedQuantity) {
        MaterialResponse response = new MaterialResponse();
        response.setMaterialId(material.getMaterialId());
        response.setMaterialName(material.getMaterialName());
        response.setMaterialType(material.getMaterialType());
        response.setMaterialTier(material.getMaterialTier());
        response.setQuality(material.getQuality());
        response.setDescription(material.getDescription());
        response.setSpiritStones(material.getSpiritStones());
        response.setOwnedQuantity(ownedQuantity != null ? ownedQuantity : 0);
        return response;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }

    public Integer getOwnedQuantity() {
        return ownedQuantity;
    }

    public void setOwnedQuantity(Integer ownedQuantity) {
        this.ownedQuantity = ownedQuantity;
    }
}
