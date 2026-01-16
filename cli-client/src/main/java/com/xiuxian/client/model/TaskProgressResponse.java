package com.xiuxian.client.model;

/**
 * 任务进度响应
 */
public class TaskProgressResponse {
    private Long progressId;
    private String taskName;
    private String taskType;
    private String taskTypeDisplay;
    private String status;
    private String statusDisplay;
    private Integer currentProgress;
    private Integer targetCount;
    private String progressDisplay;
    private Integer contributionReward;
    private Integer reputationReward;

    // Getters and Setters
    public Long getProgressId() { return progressId; }
    public String getTaskName() { return taskName; }
    public String getTaskType() { return taskType; }
    public String getTaskTypeDisplay() { return taskTypeDisplay; }
    public String getStatus() { return status; }
    public String getStatusDisplay() { return statusDisplay; }
    public Integer getCurrentProgress() { return currentProgress; }
    public Integer getTargetCount() { return targetCount; }
    public String getProgressDisplay() { return progressDisplay; }
    public Integer getContributionReward() { return contributionReward; }
    public Integer getReputationReward() { return reputationReward; }

    public void setProgressId(Long progressId) { this.progressId = progressId; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public void setTaskTypeDisplay(String taskTypeDisplay) { this.taskTypeDisplay = taskTypeDisplay; }
    public void setStatus(String status) { this.status = status; }
    public void setStatusDisplay(String statusDisplay) { this.statusDisplay = statusDisplay; }
    public void setCurrentProgress(Integer currentProgress) { this.currentProgress = currentProgress; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }
    public void setProgressDisplay(String progressDisplay) { this.progressDisplay = progressDisplay; }
    public void setContributionReward(Integer contributionReward) { this.contributionReward = contributionReward; }
    public void setReputationReward(Integer reputationReward) { this.reputationReward = reputationReward; }
}
