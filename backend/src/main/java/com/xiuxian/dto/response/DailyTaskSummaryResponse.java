package com.xiuxian.dto.response;

import java.util.List;

/**
 * 每日任务汇总响应DTO
 */
public class DailyTaskSummaryResponse {

    private Integer remainingAccepts;  // 今日剩余接取次数
    private Integer completedCount;     // 今日已完成数
    private Integer totalDailyLimit;    // 每日总限制
    private List<SectTaskResponse> availableTasks;  // 可接取任务列表
    private List<TaskProgressResponse> inProgressTasks;  // 进行中任务列表

    public DailyTaskSummaryResponse() {
    }

    public Integer getRemainingAccepts() {
        return remainingAccepts;
    }

    public void setRemainingAccepts(Integer remainingAccepts) {
        this.remainingAccepts = remainingAccepts;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Integer getTotalDailyLimit() {
        return totalDailyLimit;
    }

    public void setTotalDailyLimit(Integer totalDailyLimit) {
        this.totalDailyLimit = totalDailyLimit;
    }

    public List<SectTaskResponse> getAvailableTasks() {
        return availableTasks;
    }

    public void setAvailableTasks(List<SectTaskResponse> availableTasks) {
        this.availableTasks = availableTasks;
    }

    public List<TaskProgressResponse> getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(List<TaskProgressResponse> inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }
}
