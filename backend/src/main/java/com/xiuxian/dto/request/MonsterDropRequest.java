package com.xiuxian.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 怪物掉落配置请求DTO
 */
public class MonsterDropRequest {

    @NotBlank(message = "物品类型不能为空")
    private String itemType; // "equipment" or "material"

    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    /**
     * @deprecated 使用 itemId 替代，保留用于向后兼容
     */
    @Deprecated
    private Long equipmentId;

    @NotNull(message = "掉落率不能为空")
    @DecimalMin(value = "0.0", message = "掉落率必须大于等于0")
    @DecimalMax(value = "100.0", message = "掉落率必须小于等于100")
    private BigDecimal dropRate;

    @NotNull(message = "掉落数量不能为空")
    @Min(value = 1, message = "掉落数量必须大于等于1")
    @Max(value = 99, message = "掉落数量必须小于等于99")
    private Integer dropQuantity;

    private String minQuality;

    private String maxQuality;

    private Boolean isGuaranteed = false;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * @deprecated 使用 getItemId() 替代，保留用于向后兼容
     */
    @Deprecated
    public Long getEquipmentId() {
        return equipmentId;
    }

    /**
     * @deprecated 使用 setItemId() 替代，保留用于向后兼容
     */
    @Deprecated
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public BigDecimal getDropRate() {
        return dropRate;
    }

    public void setDropRate(BigDecimal dropRate) {
        this.dropRate = dropRate;
    }

    public Integer getDropQuantity() {
        return dropQuantity;
    }

    public void setDropQuantity(Integer dropQuantity) {
        this.dropQuantity = dropQuantity;
    }

    public String getMinQuality() {
        return minQuality;
    }

    public void setMinQuality(String minQuality) {
        this.minQuality = minQuality;
    }

    public String getMaxQuality() {
        return maxQuality;
    }

    public void setMaxQuality(String maxQuality) {
        this.maxQuality = maxQuality;
    }

    public Boolean getIsGuaranteed() {
        return isGuaranteed;
    }

    public void setIsGuaranteed(Boolean isGuaranteed) {
        this.isGuaranteed = isGuaranteed;
    }
}
