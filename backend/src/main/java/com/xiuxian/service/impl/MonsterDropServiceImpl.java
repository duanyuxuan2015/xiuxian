package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.MonsterDrop;
import com.xiuxian.entity.Equipment;
import com.xiuxian.mapper.MonsterDropMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.MonsterDropService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 妖怪装备掉落配置Service实现类
 */
@Service
public class MonsterDropServiceImpl extends ServiceImpl<MonsterDropMapper, MonsterDrop> implements MonsterDropService {

    private static final Logger logger = LoggerFactory.getLogger(MonsterDropServiceImpl.class);
    private final Random random = new Random();

    private final EquipmentMapper equipmentMapper;

    public MonsterDropServiceImpl(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public List<MonsterDrop> getDropsByMonsterId(Long monsterId) {
        LambdaQueryWrapper<MonsterDrop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonsterDrop::getMonsterId, monsterId)
                .eq(MonsterDrop::getDeleted, 0);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public List<Long> rollEquipmentDrops(Long monsterId, Long characterId) {
        List<Long> droppedEquipmentIds = new ArrayList<>();

        // 获取该怪物的所有掉落配置
        List<MonsterDrop> dropConfigs = getDropsByMonsterId(monsterId);
        if (dropConfigs == null || dropConfigs.isEmpty()) {
            logger.debug("怪物ID={}没有装备掉落配置", monsterId);
            return droppedEquipmentIds;
        }

        // 对每个掉落配置进行掉落检测
        for (MonsterDrop dropConfig : dropConfigs) {
            boolean shouldDrop = false;

            // 如果是必掉，直接掉落
            if (dropConfig.getIsGuaranteed() != null && dropConfig.getIsGuaranteed() == 1) {
                shouldDrop = true;
            } else {
                // 否则根据掉落率进行随机
                double roll = random.nextDouble() * 100;
                double dropRate = dropConfig.getDropRate() != null ? dropConfig.getDropRate().doubleValue() : 0;
                if (roll < dropRate) {
                    shouldDrop = true;
                }
            }

            if (shouldDrop) {
                Long equipmentId = dropConfig.getEquipmentId();

                // 验证装备是否存在
                Equipment equipment = equipmentMapper.selectById(equipmentId);
                if (equipment != null) {
                    droppedEquipmentIds.add(equipmentId);
                    logger.info("装备掉落: monsterId={}, equipmentId={}, equipmentName={}",
                            monsterId, equipmentId, equipment.getEquipmentName());

                    // 将装备添加到角色背包（可选，根据游戏需求决定）
                    // 这里暂时只记录掉落，不自动添加到背包
                } else {
                    logger.warn("掉落的装备不存在: equipmentId={}", equipmentId);
                }
            }
        }

        if (!droppedEquipmentIds.isEmpty()) {
            logger.info("装备掉落完成: monsterId={}, characterId={}, droppedCount={}",
                    monsterId, characterId, droppedEquipmentIds.size());
        }

        return droppedEquipmentIds;
    }

    @Override
    @Transactional
    public void deleteDropsByMonsterId(Long monsterId) {
        LambdaQueryWrapper<MonsterDrop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonsterDrop::getMonsterId, monsterId);
        this.remove(wrapper);
        logger.info("删除怪物掉落配置: monsterId={}", monsterId);
    }
}
