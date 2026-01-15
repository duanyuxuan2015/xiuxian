package com.xiuxian.service;

import com.xiuxian.entity.MonsterDrop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 妖怪装备掉落配置Service
 */
public interface MonsterDropService extends IService<MonsterDrop> {

    /**
     * 根据怪物ID获取装备掉落配置列表
     * @param monsterId 怪物ID
     * @return 掉落配置列表
     */
    List<MonsterDrop> getDropsByMonsterId(Long monsterId);

    /**
     * 执行装备掉落检测
     * @param monsterId 怪物ID
     * @param characterId 角色ID
     * @return 掉落的装备ID列表
     */
    List<Long> rollEquipmentDrops(Long monsterId, Long characterId);
}
