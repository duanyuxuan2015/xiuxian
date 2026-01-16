package com.xiuxian.client.model;

/**
 * 职位升级信息
 */
public class PositionUpgradeInfo {

    private String currentPosition;
    private String nextPosition;
    private Integer currentReputation;
    private Integer requiredReputation;
    private Integer currentContribution;
    private Integer requiredContribution;
    private Long currentSpiritStones;
    private Long requiredSpiritStones;
    private Boolean canUpgrade;
    private Boolean available;
    private String unavailableReason;

    public PositionUpgradeInfo() {
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
