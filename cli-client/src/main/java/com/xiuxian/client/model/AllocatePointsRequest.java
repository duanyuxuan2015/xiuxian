package com.xiuxian.client.model;

/**
 * 属性点分配请求
 */
public class AllocatePointsRequest {
    private Long characterId;
    private Integer constitutionPoints;
    private Integer spiritPoints;
    private Integer comprehensionPoints;
    private Integer luckPoints;
    private Integer fortunePoints;

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

    public int getTotalPoints() {
        return constitutionPoints + spiritPoints + comprehensionPoints + luckPoints + fortunePoints;
    }
}
