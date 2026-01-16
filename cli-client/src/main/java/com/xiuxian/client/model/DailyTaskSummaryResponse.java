package com.xiuxian.client.model;

import java.util.List;

/**
 * 每日任务汇总响应
 */
public class DailyTaskSummaryResponse {
    private Integer remainingAccepts;
    private Integer completedCount;
    private Integer totalDailyLimit;
    private List<SectTaskResponse> availableTasks;
    private List<TaskProgressResponse> inProgressTasks;

    // Getters and Setters
    public Integer getRemainingAccepts() { return remainingAccepts; }
    public Integer getCompletedCount() { return completedCount; }
    public Integer getTotalDailyLimit() { return totalDailyLimit; }
    public List<SectTaskResponse> getAvailableTasks() { return availableTasks; }
    public List<TaskProgressResponse> getInProgressTasks() { return inProgressTasks; }

    public void setRemainingAccepts(Integer remainingAccepts) { this.remainingAccepts = remainingAccepts; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    public void setTotalDailyLimit(Integer totalDailyLimit) { this.totalDailyLimit = totalDailyLimit; }
    public void setAvailableTasks(List<SectTaskResponse> availableTasks) { this.availableTasks = availableTasks; }
    public void setInProgressTasks(List<TaskProgressResponse> inProgressTasks) { this.inProgressTasks = inProgressTasks; }
}
