package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ExplorationEventCreateRequest;
import com.xiuxian.dto.request.ExplorationEventUpdateRequest;
import com.xiuxian.dto.response.ExplorationEventDetailResponse;
import com.xiuxian.entity.ExplorationArea;
import com.xiuxian.entity.ExplorationEvent;
import com.xiuxian.mapper.ExplorationEventMapper;
import com.xiuxian.service.ExplorationAreaService;
import com.xiuxian.service.ExplorationEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 探索事件服务实现类
 */
@Service
public class ExplorationEventServiceImpl extends ServiceImpl<ExplorationEventMapper, ExplorationEvent>
        implements ExplorationEventService {

    @Autowired
    private ExplorationAreaService explorationAreaService;

    @Override
    public ExplorationEventDetailResponse createEvent(ExplorationEventCreateRequest request) {
        // 验证区域是否存在
        ExplorationArea area = explorationAreaService.getById(request.getAreaId());
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }

        ExplorationEvent event = new ExplorationEvent();
        event.setAreaId(request.getAreaId());
        event.setEventType(request.getEventType());
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setLevel(request.getLevel());
        event.setRewardType(request.getRewardType());
        event.setRewardId(request.getRewardId());
        event.setRewardQuantityMin(request.getRewardQuantityMin());
        event.setRewardQuantityMax(request.getRewardQuantityMax());
        event.setMonsterId(request.getMonsterId());

        this.save(event);
        return ExplorationEventDetailResponse.fromEntity(event);
    }

    @Override
    public ExplorationEventDetailResponse updateEvent(ExplorationEventUpdateRequest request) {
        ExplorationEvent event = this.getById(request.getEventId());
        if (event == null) {
            throw new BusinessException(9002, "探索事件不存在");
        }

        // 验证区域是否存在
        ExplorationArea area = explorationAreaService.getById(request.getAreaId());
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }

        event.setAreaId(request.getAreaId());
        event.setEventType(request.getEventType());
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setLevel(request.getLevel());
        event.setRewardType(request.getRewardType());
        event.setRewardId(request.getRewardId());
        event.setRewardQuantityMin(request.getRewardQuantityMin());
        event.setRewardQuantityMax(request.getRewardQuantityMax());
        event.setMonsterId(request.getMonsterId());

        this.updateById(event);
        return ExplorationEventDetailResponse.fromEntity(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        ExplorationEvent event = this.getById(eventId);
        if (event == null) {
            throw new BusinessException(9002, "探索事件不存在");
        }

        this.removeById(eventId);
    }

    @Override
    public ExplorationEventDetailResponse getEventDetail(Long eventId) {
        ExplorationEvent event = this.getById(eventId);
        if (event == null) {
            throw new BusinessException(9002, "探索事件不存在");
        }
        return ExplorationEventDetailResponse.fromEntity(event);
    }

    @Override
    public List<ExplorationEventDetailResponse> getEventsByAreaId(Long areaId) {
        LambdaQueryWrapper<ExplorationEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExplorationEvent::getAreaId, areaId)
                .orderByAsc(ExplorationEvent::getLevel)
                .orderByAsc(ExplorationEvent::getEventId);

        List<ExplorationEvent> events = this.list(wrapper);
        return events.stream()
                .map(ExplorationEventDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExplorationEventDetailResponse> getAllEvents() {
        List<ExplorationEvent> events = this.list();
        return events.stream()
                .map(ExplorationEventDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
