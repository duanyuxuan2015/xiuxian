package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;

import java.util.List;

/**
 * 战斗Service接口
 */
public interface CombatService extends IService<CombatRecord> {

    /**
     * 开始战斗（自动战斗模式）
     * @param request 战斗请求
     * @return 战斗结果
     */
    CombatResponse startCombat(CombatStartRequest request);

    /**
     * 获取可挑战的妖兽列表
     * @param characterId 角色ID
     * @return 妖兽列表
     */
    List<Monster> getAvailableMonsters(Long characterId);

    /**
     * 获取战斗记录（分页）
     * @param characterId 角色ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<CombatRecord> getCombatRecords(Long characterId, int page, int pageSize);
}
