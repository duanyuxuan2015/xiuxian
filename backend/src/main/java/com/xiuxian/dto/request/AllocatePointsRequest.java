package com.xiuxian.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 属性点分配请求DTO
 */
public class AllocatePointsRequest {

    @NotNull(message = "角色ID不能为空")
    private Long characterId;

    @NotNull(message = "体质加点不能为空")
    @Min(value = 0, message = "体质加点不能为负数")
    private Integer constitutionPoints;

    @NotNull(message = "精神加点不能为空")
    @Min(value = 0, message = "精神加点不能为负数")
    private Integer spiritPoints;

    @NotNull(message = "悟性加点不能为空")
    @Min(value = 0, message = "悟性加点不能为负数")
    private Integer comprehensionPoints;

    @NotNull(message = "机缘加点不能为空")
    @Min(value = 0, message = "机缘加点不能为负数")
    private Integer luckPoints;

    @NotNull(message = "气运加点不能为空")
    @Min(value = 0, message = "气运加点不能为负数")
    private Integer fortunePoints;

    public AllocatePointsRequest() {
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public Integer getConstitutionPoints() {
        return constitutionPoints;
    }

    public void setConstitutionPoints(Integer constitutionPoints) {
        this.constitutionPoints = constitutionPoints;
    }

    public Integer getSpiritPoints() {
        return spiritPoints;
    }

    public void setSpiritPoints(Integer spiritPoints) {
        this.spiritPoints = spiritPoints;
    }

    public Integer getComprehensionPoints() {
        return comprehensionPoints;
    }

    public void setComprehensionPoints(Integer comprehensionPoints) {
        this.comprehensionPoints = comprehensionPoints;
    }

    public Integer getLuckPoints() {
        return luckPoints;
    }

    public void setLuckPoints(Integer luckPoints) {
        this.luckPoints = luckPoints;
    }

    public Integer getFortunePoints() {
        return fortunePoints;
    }

    public void setFortunePoints(Integer fortunePoints) {
        this.fortunePoints = fortunePoints;
    }

    /**
     * 获取总分配点数
     */
    public int getTotalPoints() {
        return constitutionPoints + spiritPoints + comprehensionPoints + luckPoints + fortunePoints;
    }
}
