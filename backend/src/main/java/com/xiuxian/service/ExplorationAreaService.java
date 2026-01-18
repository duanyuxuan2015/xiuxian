package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.ExplorationAreaCreateRequest;
import com.xiuxian.dto.request.ExplorationAreaUpdateRequest;
import com.xiuxian.dto.response.ExplorationAreaDetailResponse;
import com.xiuxian.entity.ExplorationArea;

import java.util.List;

/**
 * 探索区域服务接口
 */
public interface ExplorationAreaService extends IService<ExplorationArea> {

    /**
     * 创建探索区域
     */
    ExplorationAreaDetailResponse createArea(ExplorationAreaCreateRequest request);

    /**
     * 更新探索区域
     */
    ExplorationAreaDetailResponse updateArea(ExplorationAreaUpdateRequest request);

    /**
     * 删除探索区域
     */
    void deleteArea(Long areaId);

    /**
     * 获取探索区域详情
     */
    ExplorationAreaDetailResponse getAreaDetail(Long areaId);

    /**
     * 获取所有探索区域列表
     */
    List<ExplorationAreaDetailResponse> getAllAreas();
}
