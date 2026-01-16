package com.xiuxian.dto.response;

/**
 * 宗门任务响应DTO
 */
public class SectTaskResponse {

    private Long templateId;
    private String taskName;
    private String taskType;
    private String taskTypeDisplay;  // 显示用：战斗任务、修炼任务等
    private String description;
    private String targetDisplay;    // 显示用：击杀5只筑基期妖兽
    private Integer targetCount;
    private Integer contributionReward;
    private Integer reputationReward;
    private Integer requiredPosition;
    private Boolean canAccept;       // 是否可以接取（职位、次数等）

    public SectTaskResponse() {
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeDisplay() {
        return taskTypeDisplay;
    }

    public void setTaskTypeDisplay(String taskTypeDisplay) {
        this.taskTypeDisplay = taskTypeDisplay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetDisplay() {
        return targetDisplay;
    }

    public void setTargetDisplay(String targetDisplay) {
        this.targetDisplay = targetDisplay;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
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

    public Integer getRequiredPosition() {
        return requiredPosition;
    }

    public void setRequiredPosition(Integer requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    public Boolean getCanAccept() {
        return canAccept;
    }

    public void setCanAccept(Boolean canAccept) {
        this.canAccept = canAccept;
    }
}
