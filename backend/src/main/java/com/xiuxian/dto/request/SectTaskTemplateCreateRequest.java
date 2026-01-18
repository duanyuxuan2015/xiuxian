package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建宗门任务模板请求DTO
 */
public class SectTaskTemplateCreateRequest {

    @NotNull(message = "所属宗门不能为空")
    private Long sectId;

    @NotBlank(message = "任务类型不能为空")
    @Size(max = 50, message = "任务类型长度不能超过50个字符")
    private String taskType;

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100个字符")
    private String taskName;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @NotBlank(message = "目标类型不能为空")
    @Size(max = 50, message = "目标类型长度不能超过50个字符")
    private String targetType;

    @NotBlank(message = "目标值不能为空")
    @Size(max = 100, message = "目标值长度不能超过100个字符")
    private String targetValue;

    @NotNull(message = "目标数量不能为空")
    @Min(value = 1, message = "目标数量最小为1")
    @Max(value = 99999, message = "目标数量最大为99999")
    private Integer targetCount;

    @NotNull(message = "要求职位不能为空")
    @Min(value = 1, message = "要求职位最小为1")
    @Max(value = 999, message = "要求职位最大为999")
    private Integer requiredPosition;

    @NotNull(message = "贡献奖励不能为空")
    @Min(value = 0, message = "贡献奖励不能为负数")
    private Integer contributionReward;

    @NotNull(message = "声望奖励不能为空")
    @Min(value = 0, message = "声望奖励不能为负数")
    private Integer reputationReward;

    @NotNull(message = "每日上限不能为空")
    @Min(value = 0, message = "每日上限不能为负数")
    @Max(value = 999, message = "每日上限最大为999")
    private Integer dailyLimit;

    @NotNull(message = "是否启用不能为空")
    private Boolean isActive;

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
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

    public Integer getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
