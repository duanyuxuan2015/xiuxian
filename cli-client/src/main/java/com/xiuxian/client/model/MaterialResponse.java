package com.xiuxian.client.model;

/**
 * 材料响应
 */
public class MaterialResponse {
    private Long materialId;
    private String materialName;
    private Integer materialTier;
    private String description;
    private Integer quantity;

    public Long getMaterialId() { return materialId; }
    public String getMaterialName() { return materialName; }
    public Integer getMaterialTier() { return materialTier; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }

    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public void setMaterialTier(Integer materialTier) { this.materialTier = materialTier; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
