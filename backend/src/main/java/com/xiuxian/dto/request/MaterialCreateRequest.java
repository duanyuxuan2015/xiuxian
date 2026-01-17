package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建材料请求DTO
 */
public class MaterialCreateRequest {

    @NotBlank(message = "材料名称不能为空")
    @Size(max = 100, message = "材料名称长度不能超过100个字符")
    private String materialName;

    @NotBlank(message = "材料类型不能为空")
    @Size(max = 50, message = "材料类型长度不能超过50个字符")
    private String materialType;

    @NotNull(message = "材料品阶不能为空")
    @Min(value = 1, message = "材料品阶最小为1")
    @Max(value = 999, message = "材料品阶最大为999")
    private Integer materialTier;

    @NotBlank(message = "品质不能为空")
    @Size(max = 20, message = "品质长度不能超过20个字符")
    private String quality;

    @NotNull(message = "堆叠上限不能为空")
    @Min(value = 1, message = "堆叠上限最小为1")
    @Max(value = 99999, message = "堆叠上限最大为99999")
    private Integer stackLimit;

    @NotNull(message = "灵石价格不能为空")
    @Min(value = 0, message = "灵石价格不能为负数")
    private Integer spiritStones;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

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
}
