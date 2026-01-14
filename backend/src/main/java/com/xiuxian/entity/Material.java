package com.xiuxian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 材料实体
 */
@TableName("material")
public class Material {

    @TableId(type = IdType.AUTO)
    private Long materialId;

    private String materialName;

    private String materialType;

    private Integer materialTier;

    private String quality;

    private Integer stackLimit;

    private Integer spiritStones;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    public Material() {
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

    public Integer getStackLimit() {
        return stackLimit;
    }

    public void setStackLimit(Integer stackLimit) {
        this.stackLimit = stackLimit;
    }

    public Integer getSpiritStones() {
        return spiritStones;
    }

    public void setSpiritStones(Integer spiritStones) {
        this.spiritStones = spiritStones;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Material{" +
                "materialId=" + materialId +
                ", materialName='" + materialName + '\'' +
                ", materialType='" + materialType + '\'' +
                ", materialTier=" + materialTier +
                ", quality='" + quality + '\'' +
                '}';
    }
}
