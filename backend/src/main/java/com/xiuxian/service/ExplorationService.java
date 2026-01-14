package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.entity.ExplorationRecord;

import java.util.List;

/**
 * 探索Service接口
 */
public interface ExplorationService extends IService<ExplorationRecord> {

    /**
     * 获取所有探索区域
     * @param characterId 角色ID
     * @return 区域列表
     */
    List<ExplorationAreaResponse> getAllAreas(Long characterId);

    /**
     * 开始探索
     * @param request 探索请求
     * @return 探索结果
     */
    ExplorationResponse startExploration(ExplorationRequest request);

    /**
     * 获取探索记录
     * @param characterId 角色ID
     * @return 探索记录列表
     */
    List<ExplorationResponse> getExplorationRecords(Long characterId);
}
