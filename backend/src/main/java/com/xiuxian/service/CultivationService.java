package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.BreakthroughRequest;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.request.MeditationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.dto.response.MeditationResponse;
import com.xiuxian.dto.response.MeditationTimeResponse;
import com.xiuxian.entity.CultivationRecord;

/**
 * 修炼Service接口
 */
public interface CultivationService extends IService<CultivationRecord> {

    /**
     * 开始修炼
     * @param request 修炼请求
     * @return 修炼结果
     */
    CultivationResponse startCultivation(CultivationRequest request);

    /**
     * 检查并升级境界层次
     * @param characterId 角色ID
     * @return 是否升级
     */
    boolean checkAndUpgradeRealmLevel(Long characterId);

    /**
     * 获取角色修炼记录（分页）
     * @param characterId 角色ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<CultivationRecord> getCultivationRecords(Long characterId, int page, int pageSize);

    /**
     * 尝试境界突破
     * @param request 突破请求
     * @return 突破结果
     */
    BreakthroughResponse attemptBreakthrough(BreakthroughRequest request);

    /**
     * 计算突破成功率
     * @param characterId 角色ID
     * @return 成功率（0-100）
     */
    int calculateBreakthroughRate(Long characterId);

    /**
     * 打坐恢复体力和灵力
     * @param request 打坐请求
     * @return 打坐结果
     */
    MeditationResponse meditation(MeditationRequest request);

    /**
     * 获取打坐所需时间
     * @param characterId 角色ID
     * @return 打坐时间信息
     */
    MeditationTimeResponse getMeditationTime(Long characterId);
}
