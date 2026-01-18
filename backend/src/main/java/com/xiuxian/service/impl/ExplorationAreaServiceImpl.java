package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ExplorationAreaCreateRequest;
import com.xiuxian.dto.request.ExplorationAreaUpdateRequest;
import com.xiuxian.dto.response.ExplorationAreaDetailResponse;
import com.xiuxian.entity.ExplorationArea;
import com.xiuxian.mapper.ExplorationAreaMapper;
import com.xiuxian.service.ExplorationAreaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 探索区域服务实现类
 */
@Service
public class ExplorationAreaServiceImpl extends ServiceImpl<ExplorationAreaMapper, ExplorationArea>
        implements ExplorationAreaService {

    @Override
    public ExplorationAreaDetailResponse createArea(ExplorationAreaCreateRequest request) {
        ExplorationArea area = new ExplorationArea();
        area.setAreaName(request.getAreaName());
        area.setDescription(request.getDescription());
        area.setRequiredRealmLevel(request.getRequiredRealmLevel());
        area.setDangerLevel(request.getDangerLevel());
        area.setSpiritCost(request.getSpiritCost());
        area.setStaminaCost(request.getStaminaCost());
        area.setBaseExploreTime(request.getBaseExploreTime());

        this.save(area);
        return ExplorationAreaDetailResponse.fromEntity(area);
    }

    @Override
    public ExplorationAreaDetailResponse updateArea(ExplorationAreaUpdateRequest request) {
        ExplorationArea area = this.getById(request.getAreaId());
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }

        area.setAreaName(request.getAreaName());
        area.setDescription(request.getDescription());
        area.setRequiredRealmLevel(request.getRequiredRealmLevel());
        area.setDangerLevel(request.getDangerLevel());
        area.setSpiritCost(request.getSpiritCost());
        area.setStaminaCost(request.getStaminaCost());
        area.setBaseExploreTime(request.getBaseExploreTime());

        this.updateById(area);
        return ExplorationAreaDetailResponse.fromEntity(area);
    }

    @Override
    public void deleteArea(Long areaId) {
        ExplorationArea area = this.getById(areaId);
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }

        // TODO: 检查是否有关联的探索事件，如果有则不允许删除或级联删除

        this.removeById(areaId);
    }

    @Override
    public ExplorationAreaDetailResponse getAreaDetail(Long areaId) {
        ExplorationArea area = this.getById(areaId);
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }
        return ExplorationAreaDetailResponse.fromEntity(area);
    }

    @Override
    public List<ExplorationAreaDetailResponse> getAllAreas() {
        List<ExplorationArea> areas = this.list();
        return areas.stream()
                .map(ExplorationAreaDetailResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
