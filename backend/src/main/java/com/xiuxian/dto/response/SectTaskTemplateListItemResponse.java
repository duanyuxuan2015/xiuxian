package com.xiuxian.dto.response;

/**
 * 宗门任务模板列表项响应DTO
 */
public class SectTaskTemplateListItemResponse {

    private Long templateId;
    private Long sectId;
    private String sectName;
    private String taskType;
    private String taskName;
    private String targetType;
    private Integer targetCount;
    private Integer requiredPosition;
    private Integer contributionReward;
    private Integer reputationReward;
    private Boolean isActive;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getSectName() {
        return sectName;
    }

    public void setSectName(String sectName) {
        this.sectName = sectName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
    }

    public Integer getRequiredPosition() {
        return requiredPosition;
    }

    public void setRequiredPosition(Integer requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    public Integer getContributionReward() {
        return contributionReward;
    }

    public void setContributionReward(Integer contributionReward) {
        this.contributionReward = contributionReward;
    }

    public Integer getReputationReward() {
        return reputationReward;
    }

    public void setReputationReward(Integer reputationReward) {
        this.reputationReward = reputationReward;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
