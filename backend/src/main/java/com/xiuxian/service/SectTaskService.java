package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.AcceptTaskRequest;
import com.xiuxian.dto.request.SubmitTaskRequest;
import com.xiuxian.dto.response.DailyTaskSummaryResponse;
import com.xiuxian.dto.response.SectTaskResponse;
import com.xiuxian.dto.response.TaskProgressResponse;
import com.xiuxian.entity.SectTaskProgress;

import java.util.List;

/**
 * 宗门任务Service接口
 */
public interface SectTaskService extends IService<SectTaskProgress> {

    /**
     * 获取可领取的任务列表
     * @param characterId 角色ID
     * @return 任务列表
     */
    List<SectTaskResponse> getAvailableTasks(Long characterId);

    /**
     * 获取我的任务汇总（包含可接任务和进行中任务）
     * @param characterId 角色ID
     * @return 任务汇总信息
     */
    DailyTaskSummaryResponse getMyTaskSummary(Long characterId);

    /**
     * 接取任务
     * @param request 接取任务请求
     * @return 任务进度信息
     */
    TaskProgressResponse acceptTask(AcceptTaskRequest request);

    /**
     * 提交任务（验证进度）
     * @param request 提交任务请求
     * @return 更新后的任务进度信息
     */
    TaskProgressResponse submitTask(SubmitTaskRequest request);

    /**
     * 领取奖励
     * @param progressId 进度记录ID
     * @return 奖励信息
     */
    String claimReward(Long progressId);

    /**
     * 增加任务进度（内部方法，供其他系统调用）
     * @param characterId 角色ID
     * @param taskType 任务类型（combat/meditation/collection/crafting）
     * @param targetValue 目标值（如：妖兽境界、物品名称）
     * @param count 增加的进度数量
     */
    void addTaskProgress(Long characterId, String taskType, String targetValue, Integer count);

    /**
     * 每日重置任务次数（定时任务调用）
     */
    void resetDailyTasks();
}
