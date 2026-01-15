package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 装备Service实现类
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final CharacterEquipmentMapper characterEquipmentMapper;
    private final CharacterService characterService;

    public EquipmentServiceImpl(CharacterEquipmentMapper characterEquipmentMapper,
                                CharacterService characterService) {
        this.characterEquipmentMapper = characterEquipmentMapper;
        this.characterService = characterService;
    }

    @Override
    public List<EquipmentResponse> getCharacterEquipments(Long characterId) {
        // 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 查询角色已装备的装备
        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId);
        List<CharacterEquipment> characterEquipments = characterEquipmentMapper.selectList(wrapper);

        List<EquipmentResponse> responses = new ArrayList<>();
        for (CharacterEquipment ce : characterEquipments) {
            Equipment equipment = baseMapper.selectById(ce.getEquipmentId());
            if (equipment != null) {
                EquipmentResponse response = EquipmentResponse.fromEntity(
                        equipment,
                        ce.getEquipmentSlot(),
                        true
                );
                responses.add(response);
            }
        }

        logger.debug("获取角色{}的装备列表，共{}件", characterId, responses.size());
        return responses;
    }

    @Override
    @Transactional
    public EquipmentResponse equipItem(EquipRequest request) {
        Long characterId = request.getCharacterId();
        Long equipmentId = request.getEquipmentId();
        String equipmentSlot = request.getEquipmentSlot();

        // 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 验证槽位是否有效
        if (!isValidSlot(equipmentSlot)) {
            throw new BusinessException(4002, "无效的装备槽位: " + equipmentSlot);
        }

        // 验证装备是否存在
        Equipment equipment = baseMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(4001, "装备不存在");
        }

        // 验证装备类型与槽位是否匹配
        if (!isSlotMatchEquipmentType(equipmentSlot, equipment.getEquipmentType())) {
            throw new BusinessException(4003,
                    String.format("装备类型不匹配: %s不能装备到%s槽位", equipment.getEquipmentType(), equipmentSlot));
        }

        // 检查该槽位是否已有装备，如果有则先卸下
        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId)
                .eq(CharacterEquipment::getEquipmentSlot, equipmentSlot);
        CharacterEquipment existingEquipped = characterEquipmentMapper.selectOne(wrapper);

        if (existingEquipped != null) {
            // 卸下旧装备
            characterEquipmentMapper.deleteById(existingEquipped.getCharacterEquipmentId());
            logger.info("角色{}卸下槽位{}的旧装备ID:{}", characterId, equipmentSlot, existingEquipped.getEquipmentId());
        }

        // 穿戴新装备
        CharacterEquipment characterEquipment = new CharacterEquipment();
        characterEquipment.setCharacterId(characterId);
        characterEquipment.setEquipmentId(equipmentId);
        characterEquipment.setEquipmentSlot(equipmentSlot);
        characterEquipment.setEquippedAt(LocalDateTime.now());

        characterEquipmentMapper.insert(characterEquipment);

        logger.info("角色{}在槽位{}穿戴装备:{}", characterId, equipmentSlot, equipment.getEquipmentName());

        return EquipmentResponse.fromEntity(equipment, equipmentSlot, true);
    }

    @Override
    @Transactional
    public boolean unequipItem(Long characterId, String equipmentSlot) {
        // 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 查找该槽位的装备
        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId)
                .eq(CharacterEquipment::getEquipmentSlot, equipmentSlot);
        CharacterEquipment characterEquipment = characterEquipmentMapper.selectOne(wrapper);

        if (characterEquipment == null) {
            logger.warn("角色{}在槽位{}没有装备", characterId, equipmentSlot);
            return false;
        }

        // 卸下装备
        int deleted = characterEquipmentMapper.deleteById(characterEquipment.getCharacterEquipmentId());

        Equipment equipment = baseMapper.selectById(characterEquipment.getEquipmentId());
        String equipmentName = equipment != null ? equipment.getEquipmentName() : "未知";

        if (deleted > 0) {
            logger.info("角色{}从槽位{}卸下装备:{}", characterId, equipmentSlot, equipmentName);
            return true;
        } else {
            logger.error("角色{}卸下槽位{}的装备失败", characterId, equipmentSlot);
            return false;
        }
    }

    @Override
    public EquipmentBonus calculateEquipmentBonus(Long characterId) {
        // 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 查询角色已装备的装备
        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId);
        List<CharacterEquipment> characterEquipments = characterEquipmentMapper.selectList(wrapper);

        EquipmentBonus bonus = new EquipmentBonus();
        bonus.attackBonus = 0;
        bonus.defenseBonus = 0;
        bonus.healthBonus = 0;
        bonus.staminaBonus = 0;
        bonus.spiritualPowerBonus = 0;

        // 累加所有装备的属性加成
        for (CharacterEquipment ce : characterEquipments) {
            Equipment equipment = baseMapper.selectById(ce.getEquipmentId());
            if (equipment != null) {
                bonus.attackBonus += safeAdd(equipment.getAttackPower());
                bonus.defenseBonus += safeAdd(equipment.getDefensePower());
                bonus.healthBonus += safeAdd(equipment.getHealthBonus());
                // 装备可能还有其他属性加成，这里暂时只处理基础属性
            }
        }

        logger.debug("计算角色{}的装备加成: attack={}, defense={}, health={}",
                characterId, bonus.attackBonus, bonus.defenseBonus, bonus.healthBonus);

        return bonus;
    }

    /**
     * 安全的整数相加，处理null值
     */
    private int safeAdd(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * 验证槽位是否有效
     */
    private boolean isValidSlot(String slot) {
        return "武器".equals(slot) || "头盔".equals(slot) || "护甲".equals(slot)
                || "腰带".equals(slot) || "鞋子".equals(slot);
    }

    /**
     * 验证装备类型与槽位是否匹配
     */
    private boolean isSlotMatchEquipmentType(String slot, String equipmentType) {
        if (equipmentType == null) {
            return false;
        }

        switch (slot) {
            case "武器":
                return "武器".equals(equipmentType);
            case "头盔":
                return "头盔".equals(equipmentType);
            case "护甲":
                return "护甲".equals(equipmentType);
            case "腰带":
                return "腰带".equals(equipmentType);
            case "鞋子":
                return "鞋子".equals(equipmentType);
            default:
                return false;
        }
    }
}
