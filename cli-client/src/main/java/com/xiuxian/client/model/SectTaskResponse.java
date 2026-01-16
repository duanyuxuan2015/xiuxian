package com.xiuxian.client.model;

/**
 * 宗门任务响应
 */
public class SectTaskResponse {
    private Long templateId;
    private String taskName;
    private String taskType;
    private String taskTypeDisplay;
    private String description;
    private String targetDisplay;
    private Integer targetCount;
    private Integer contributionReward;
    private Integer reputationReward;
    private Integer requiredPosition;
    private Boolean canAccept;

    // Getters and Setters
    public Long getTemplateId() { return templateId; }
    public String getTaskName() { return taskName; }
    public String getTaskType() { return taskType; }
    public String getTaskTypeDisplay() { return taskTypeDisplay; }
    public String getDescription() { return description; }
    public String getTargetDisplay() { return targetDisplay; }
    public Integer getTargetCount() { return targetCount; }
    public Integer getContributionReward() { return contributionReward; }
    public Integer getReputationReward() { return reputationReward; }
    public Integer getRequiredPosition() { return requiredPosition; }
    public Boolean getCanAccept() { return canAccept; }

    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setTaskTypeDisplay(String taskTypeDisplay) { this.taskTypeDisplay = taskTypeDisplay; }
    public void setDescription(String description) { this.description = description; }
    public void setTargetDisplay(String targetDisplay) { this.targetDisplay = targetDisplay; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }
    public void setContributionReward(Integer contributionReward) { this.contributionReward = contributionReward; }
    public void setReputationReward(Integer reputationReward) { this.reputationReward = reputationReward; }
    public void setRequiredPosition(Integer requiredPosition) { this.requiredPosition = requiredPosition; }
    public void setCanAccept(Boolean canAccept) { this.canAccept = canAccept; }
}
