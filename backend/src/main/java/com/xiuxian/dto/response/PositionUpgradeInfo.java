package com.xiuxian.dto.response;

/**
 * 职位升级信息响应DTO
 */
public class PositionUpgradeInfo {

    /**
     * 当前职位
     */
    private String currentPosition;

    /**
     * 下一职位
     */
    private String nextPosition;

    /**
     * 当前声望值
     */
    private Integer currentReputation;

    /**
     * 所需声望值
     */
    private Integer requiredReputation;

    /**
     * 当前贡献值
     */
    private Integer currentContribution;

    /**
     * 所需贡献值
     */
    private Integer requiredContribution;

    /**
     * 当前灵石
     */
    private Long currentSpiritStones;

    /**
     * 所需灵石
     */
    private Long requiredSpiritStones;

    /**
     * 是否可以升级
     */
    private Boolean canUpgrade;

    /**
     * 是否可用（有更高职位可升级）
     */
    private Boolean available;

    /**
     * 不可用原因（当 available=false 时）
     */
    private String unavailableReason;

    public PositionUpgradeInfo() {
        this.available = true;
        this.canUpgrade = false;
    }

    /**
     * 创建不可用的升级信息（已是最高职位或下一职位未开放）
     */
    public static PositionUpgradeInfo notAvailable(String currentPosition, String reason) {
        PositionUpgradeInfo info = new PositionUpgradeInfo();
        info.setCurrentPosition(currentPosition);
        info.setAvailable(false);
        info.setUnavailableReason(reason);
        info.setCanUpgrade(false);
        return info;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(String nextPosition) {
        this.nextPosition = nextPosition;
    }

    public Integer getCurrentReputation() {
        return currentReputation;
    }

    public void setCurrentReputation(Integer currentReputation) {
        this.currentReputation = currentReputation;
    }

    public Integer getRequiredReputation() {
        return requiredReputation;
    }

    public void setRequiredReputation(Integer requiredReputation) {
        this.requiredReputation = requiredReputation;
    }

    public Integer getCurrentContribution() {
        return currentContribution;
    }

    public void setCurrentContribution(Integer currentContribution) {
        this.currentContribution = currentContribution;
    }

    public Integer getRequiredContribution() {
        return requiredContribution;
    }

    public void setRequiredContribution(Integer requiredContribution) {
        this.requiredContribution = requiredContribution;
    }

    public Long getCurrentSpiritStones() {
        return currentSpiritStones;
    }

    public void setCurrentSpiritStones(Long currentSpiritStones) {
        this.currentSpiritStones = currentSpiritStones;
    }

    public Long getRequiredSpiritStones() {
        return requiredSpiritStones;
    }

    public void setRequiredSpiritStones(Long requiredSpiritStones) {
        this.requiredSpiritStones = requiredSpiritStones;
    }

    public Boolean getCanUpgrade() {
        return canUpgrade;
    }

    public void setCanUpgrade(Boolean canUpgrade) {
        this.canUpgrade = canUpgrade;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }

    public void setUnavailableReason(String unavailableReason) {
        this.unavailableReason = unavailableReason;
    }
}
