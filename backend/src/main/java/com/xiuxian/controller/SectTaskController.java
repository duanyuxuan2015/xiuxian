package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.AcceptTaskRequest;
import com.xiuxian.dto.request.SubmitTaskRequest;
import com.xiuxian.dto.response.DailyTaskSummaryResponse;
import com.xiuxian.dto.response.SectTaskResponse;
import com.xiuxian.dto.response.TaskProgressResponse;
import com.xiuxian.service.SectTaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 宗门任务控制器
 */
@RestController
@RequestMapping("/sect/tasks")
public class SectTaskController {

    private final SectTaskService sectTaskService;

    public SectTaskController(SectTaskService sectTaskService) {
        this.sectTaskService = sectTaskService;
    }

    /**
     * 获取可领取的任务列表
     * GET /api/v1/sect/tasks/available/{characterId}
     */
    @GetMapping("/available/{characterId}")
    public Result<List<SectTaskResponse>> getAvailableTasks(@PathVariable Long characterId) {
        List<SectTaskResponse> tasks = sectTaskService.getAvailableTasks(characterId);
        return Result.success(tasks);
    }

    /**
     * 获取我的任务汇总（包含可接任务和进行中任务）
     * GET /api/v1/sect/tasks/my/{characterId}
     */
    @GetMapping("/my/{characterId}")
    public Result<DailyTaskSummaryResponse> getMyTaskSummary(@PathVariable Long characterId) {
        DailyTaskSummaryResponse summary = sectTaskService.getMyTaskSummary(characterId);
        return Result.success(summary);
    }

    /**
     * 接取任务
     * POST /api/v1/sect/tasks/accept
     */
    @PostMapping("/accept")
    public Result<TaskProgressResponse> acceptTask(@Valid @RequestBody AcceptTaskRequest request) {
        TaskProgressResponse response = sectTaskService.acceptTask(request);
        return Result.success(response);
    }

    /**
     * 提交任务
     * POST /api/v1/sect/tasks/submit
     */
    @PostMapping("/submit")
    public Result<TaskProgressResponse> submitTask(@Valid @RequestBody SubmitTaskRequest request) {
        TaskProgressResponse response = sectTaskService.submitTask(request);
        return Result.success(response);
    }

    /**
     * 领取任务奖励
     * POST /api/v1/sect/tasks/claim/{progressId}
     */
    @PostMapping("/claim/{progressId}")
    public Result<String> claimReward(@PathVariable Long progressId) {
        String message = sectTaskService.claimReward(progressId);
        return Result.success(message);
    }
}
