package com.xiuxian.dto.response;

/**
 * 任务进度响应DTO
 */
public class TaskProgressResponse {

    private Long progressId;
    private String taskName;
    private String taskType;
    private String taskTypeDisplay;
    private String status;
    private String statusDisplay;  // 显示用：进行中、已完成、已领奖
    private Integer currentProgress;
    private Integer targetCount;
    private String progressDisplay;  // 显示用：3/5
    private Integer contributionReward;
    private Integer reputationReward;

    public TaskProgressResponse() {
    }

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(Integer targetCount) {
        this.targetCount = targetCount;
    }

    public String getProgressDisplay() {
        return progressDisplay;
    }

    public void setProgressDisplay(String progressDisplay) {
        this.progressDisplay = progressDisplay;
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
}
