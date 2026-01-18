package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.ExplorationEventCreateRequest;
import com.xiuxian.dto.request.ExplorationEventUpdateRequest;
import com.xiuxian.dto.response.ExplorationEventDetailResponse;
import com.xiuxian.entity.ExplorationEvent;

import java.util.List;

/**
 * 探索事件服务接口
 */
public interface ExplorationEventService extends IService<ExplorationEvent> {

    /**
     * 创建探索事件
     */
    ExplorationEventDetailResponse createEvent(ExplorationEventCreateRequest request);

    /**
     * 更新探索事件
     */
    ExplorationEventDetailResponse updateEvent(ExplorationEventUpdateRequest request);

    /**
     * 删除探索事件
     */
    void deleteEvent(Long eventId);

    /**
     * 获取探索事件详情
     */
    ExplorationEventDetailResponse getEventDetail(Long eventId);

    /**
     * 根据区域ID获取所有事件
     */
    List<ExplorationEventDetailResponse> getEventsByAreaId(Long areaId);

    /**
     * 获取所有探索事件列表
     */
    List<ExplorationEventDetailResponse> getAllEvents();
}
