package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.ExplorationEventCreateRequest;
import com.xiuxian.dto.request.ExplorationEventUpdateRequest;
import com.xiuxian.dto.response.ExplorationEventDetailResponse;
import com.xiuxian.service.ExplorationEventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 探索事件管理控制器
 */
@RestController
@RequestMapping("/admin/exploration-event")
public class ExplorationEventController {

    @Autowired
    private ExplorationEventService explorationEventService;

    /**
     * 创建探索事件
     */
    @PostMapping
    public Result<ExplorationEventDetailResponse> createEvent(@Valid @RequestBody ExplorationEventCreateRequest request) {
        ExplorationEventDetailResponse response = explorationEventService.createEvent(request);
        return Result.success("创建成功", response);
    }

    /**
     * 更新探索事件
     */
    @PutMapping
    public Result<ExplorationEventDetailResponse> updateEvent(@Valid @RequestBody ExplorationEventUpdateRequest request) {
        ExplorationEventDetailResponse response = explorationEventService.updateEvent(request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除探索事件
     */
    @DeleteMapping("/{eventId}")
    public Result<String> deleteEvent(@PathVariable Long eventId) {
        explorationEventService.deleteEvent(eventId);
        return Result.success("删除成功");
    }

    /**
     * 获取探索事件详情
     */
    @GetMapping("/{eventId}")
    public Result<ExplorationEventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        ExplorationEventDetailResponse response = explorationEventService.getEventDetail(eventId);
        return Result.success(response);
    }

    /**
     * 根据区域ID获取所有事件
     */
    @GetMapping("/area/{areaId}")
    public Result<List<ExplorationEventDetailResponse>> getEventsByAreaId(@PathVariable Long areaId) {
        List<ExplorationEventDetailResponse> events = explorationEventService.getEventsByAreaId(areaId);
        return Result.success(events);
    }

    /**
     * 获取所有探索事件列表
     */
    @GetMapping("/list")
    public Result<List<ExplorationEventDetailResponse>> getAllEvents() {
        List<ExplorationEventDetailResponse> events = explorationEventService.getAllEvents();
        return Result.success(events);
    }
}
