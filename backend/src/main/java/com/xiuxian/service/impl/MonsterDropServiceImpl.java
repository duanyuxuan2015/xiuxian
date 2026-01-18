package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.MonsterDrop;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.mapper.MonsterDropMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
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
    private final MaterialMapper materialMapper;

    public MonsterDropServiceImpl(EquipmentMapper equipmentMapper, MaterialMapper materialMapper) {
        this.equipmentMapper = equipmentMapper;
        this.materialMapper = materialMapper;
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

        logger.debug("开始检测装备掉落: monsterId={}, characterId={}", monsterId, characterId);

        // 获取该怪物的所有装备掉落配置
        List<MonsterDrop> dropConfigs = getDropsByMonsterIdAndItemType(monsterId, "equipment");
        if (dropConfigs == null || dropConfigs.isEmpty()) {
            logger.debug("怪物ID={}没有装备掉落配置", monsterId);
            return droppedEquipmentIds;
        }

        logger.debug("找到 {} 条装备掉落配置", dropConfigs.size());

        // 对每个掉落配置进行掉落检测
        for (MonsterDrop dropConfig : dropConfigs) {
            boolean shouldDrop = false;
            Long equipmentId = dropConfig.getItemId();

            logger.debug("检测掉落: itemId={}, dropRate={}, isGuaranteed={}",
                equipmentId, dropConfig.getDropRate(), dropConfig.getIsGuaranteed());

            // 如果是必掉，直接掉落
            if (dropConfig.getIsGuaranteed() != null && dropConfig.getIsGuaranteed() == 1) {
                shouldDrop = true;
                logger.debug("  → 必掉，跳过随机");
            } else {
                // 否则根据掉落率进行随机
                double roll = random.nextDouble() * 100;
                double dropRate = dropConfig.getDropRate() != null ? dropConfig.getDropRate().doubleValue() : 0;
                logger.debug("  → 随机值: {:.2f}, 掉落率: {:.2f}%", roll, dropRate);

                if (roll < dropRate) {
                    shouldDrop = true;
                    logger.debug("  → 掉落成功");
                } else {
                    logger.debug("  → 未触发掉落");
                }
            }

            if (shouldDrop) {
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
        } else {
            logger.debug("装备掉落检测完成，未掉落任何装备");
        }

        return droppedEquipmentIds;
    }

    @Override
    @Transactional
    public List<Long> rollMaterialDrops(Long monsterId, Long characterId) {
        List<Long> droppedMaterialIds = new ArrayList<>();

        logger.debug("开始检测材料掉落: monsterId={}, characterId={}", monsterId, characterId);

        // 获取该怪物的所有材料掉落配置
        List<MonsterDrop> dropConfigs = getDropsByMonsterIdAndItemType(monsterId, "material");
        if (dropConfigs == null || dropConfigs.isEmpty()) {
            logger.debug("怪物ID={}没有材料掉落配置", monsterId);
            return droppedMaterialIds;
        }

        logger.debug("找到 {} 条材料掉落配置", dropConfigs.size());

        // 对每个掉落配置进行掉落检测
        for (MonsterDrop dropConfig : dropConfigs) {
            boolean shouldDrop = false;
            Long materialId = dropConfig.getItemId();

            logger.debug("检测掉落: itemId={}, dropRate={}, isGuaranteed={}",
                materialId, dropConfig.getDropRate(), dropConfig.getIsGuaranteed());

            // 如果是必掉，直接掉落
            if (dropConfig.getIsGuaranteed() != null && dropConfig.getIsGuaranteed() == 1) {
                shouldDrop = true;
                logger.debug("  → 必掉，跳过随机");
            } else {
                // 否则根据掉落率进行随机
                double roll = random.nextDouble() * 100;
                double dropRate = dropConfig.getDropRate() != null ? dropConfig.getDropRate().doubleValue() : 0;
                logger.debug("  → 随机值: {:.2f}, 掉落率: {:.2f}%", roll, dropRate);

                if (roll < dropRate) {
                    shouldDrop = true;
                    logger.debug("  → 掉落成功");
                } else {
                    logger.debug("  → 未触发掉落");
                }
            }

            if (shouldDrop) {
                // 验证材料是否存在
                Material material = materialMapper.selectById(materialId);
                if (material != null) {
                    droppedMaterialIds.add(materialId);
                    logger.info("材料掉落: monsterId={}, materialId={}, materialName={}",
                            monsterId, materialId, material.getMaterialName());
                } else {
                    logger.warn("掉落的材料不存在: materialId={}", materialId);
                }
            }
        }

        if (!droppedMaterialIds.isEmpty()) {
            logger.info("材料掉落完成: monsterId={}, characterId={}, droppedCount={}",
                    monsterId, characterId, droppedMaterialIds.size());
        } else {
            logger.debug("材料掉落检测完成，未掉落任何材料");
        }

        return droppedMaterialIds;
    }

    /**
     * 根据怪物ID和物品类型获取掉落配置
     * @param monsterId 怪物ID
     * @param itemType 物品类型（equipment/material）
     * @return 掉落配置列表
     */
    private List<MonsterDrop> getDropsByMonsterIdAndItemType(Long monsterId, String itemType) {
        LambdaQueryWrapper<MonsterDrop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MonsterDrop::getMonsterId, monsterId)
               .eq(MonsterDrop::getItemType, itemType)
               .eq(MonsterDrop::getDeleted, 0);
        return this.list(wrapper);
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
