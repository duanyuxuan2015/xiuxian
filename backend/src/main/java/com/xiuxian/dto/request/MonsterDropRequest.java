package com.xiuxian.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 怪物掉落配置请求DTO
 */
public class MonsterDropRequest {

    @NotNull(message = "装备ID不能为空")
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

    public Long getEquipmentId() {
        return equipmentId;
    }

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
