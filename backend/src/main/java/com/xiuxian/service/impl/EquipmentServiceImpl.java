package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.Equipment;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 装备Service实现类
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<CharacterEquipmentMapper, CharacterEquipment>
        implements EquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private static final Set<String> VALID_SLOTS = Set.of(
            "武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链");

    private static final Map<String, List<String>> SLOT_TYPE_MAPPING = Map.of(
            "武器", List.of("武器"),
            "头盔", List.of("头盔"),
            "铠甲", List.of("铠甲"),
            "护手", List.of("护手"),
            "护腿", List.of("护腿"),
            "靴子", List.of("靴子"),
            "戒指1", List.of("戒指"),
            "戒指2", List.of("戒指"),
            "项链", List.of("项链"));

    private final CharacterService characterService;
    private final EquipmentMapper equipmentMapper;

    public EquipmentServiceImpl(@Lazy CharacterService characterService, EquipmentMapper equipmentMapper) {
        this.characterService = characterService;
        this.equipmentMapper = equipmentMapper;
    }

    @Override
    public List<EquipmentResponse> getCharacterEquipments(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId);
        List<CharacterEquipment> charEquipments = this.list(wrapper);

        List<EquipmentResponse> responses = new ArrayList<>();
        for (CharacterEquipment ce : charEquipments) {
            Equipment equipment = equipmentMapper.selectById(ce.getEquipmentId());
            if (equipment != null) {
                responses.add(EquipmentResponse.fromEntity(equipment, ce.getEquipmentSlot(), true));
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public EquipmentResponse equipItem(EquipRequest request) {
        Long characterId = request.getCharacterId();
        Long equipmentId = request.getEquipmentId();
        String slot = request.getEquipmentSlot();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证装备
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(4001, "装备不存在");
        }

        // 3. 验证槽位
        if (!VALID_SLOTS.contains(slot)) {
            throw new BusinessException(4002, "无效的装备槽位：" + slot);
        }

        // 4. 验证装备类型与槽位匹配
        List<String> allowedTypes = SLOT_TYPE_MAPPING.get(slot);
        if (allowedTypes == null || !allowedTypes.contains(equipment.getEquipmentType())) {
            throw new BusinessException(4003, "装备类型与槽位不匹配，槽位：" + slot + "，装备类型：" + equipment.getEquipmentType());
        }

        // 5. 检查槽位是否已有装备，如果有则先卸下
        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId)
                .eq(CharacterEquipment::getEquipmentSlot, slot);
        CharacterEquipment existing = this.getOne(wrapper);
        if (existing != null) {
            this.removeById(existing.getCharacterEquipmentId());
            logger.info("卸下旧装备: characterId={}, slot={}, oldEquipmentId={}",
                    characterId, slot, existing.getEquipmentId());
        }

        // 6. 装备新装备
        CharacterEquipment newEquip = new CharacterEquipment();
        newEquip.setCharacterId(characterId);
        newEquip.setEquipmentId(equipmentId);
        newEquip.setEquipmentSlot(slot);
        newEquip.setEquippedAt(LocalDateTime.now());
        this.save(newEquip);

        logger.info("装备成功: characterId={}, slot={}, equipmentId={}, equipmentName={}",
                characterId, slot, equipmentId, equipment.getEquipmentName());

        return EquipmentResponse.fromEntity(equipment, slot, true);
    }

    @Override
    @Transactional
    public boolean unequipItem(Long characterId, String equipmentSlot) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId)
                .eq(CharacterEquipment::getEquipmentSlot, equipmentSlot);
        CharacterEquipment existing = this.getOne(wrapper);

        if (existing == null) {
            throw new BusinessException(4004, "该槽位没有装备");
        }

        this.removeById(existing.getCharacterEquipmentId());
        logger.info("卸下装备: characterId={}, slot={}, equipmentId={}",
                characterId, equipmentSlot, existing.getEquipmentId());

        return true;
    }

    @Override
    public EquipmentBonus calculateEquipmentBonus(Long characterId) {
        EquipmentBonus bonus = new EquipmentBonus();

        LambdaQueryWrapper<CharacterEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterEquipment::getCharacterId, characterId);
        List<CharacterEquipment> charEquipments = this.list(wrapper);

        for (CharacterEquipment ce : charEquipments) {
            Equipment equipment = equipmentMapper.selectById(ce.getEquipmentId());
            if (equipment != null) {
                if (equipment.getAttackPower() != null) {
                    bonus.setAttackPower(bonus.getAttackPower() + equipment.getAttackPower());
                }
                if (equipment.getDefensePower() != null) {
                    bonus.setDefensePower(bonus.getDefensePower() + equipment.getDefensePower());
                }
                if (equipment.getHealthBonus() != null) {
                    bonus.setHealthBonus(bonus.getHealthBonus() + equipment.getHealthBonus());
                }
                if (equipment.getCriticalRate() != null) {
                    bonus.setCriticalRate(bonus.getCriticalRate() + equipment.getCriticalRate());
                }
                if (equipment.getSpeedBonus() != null) {
                    bonus.setSpeedBonus(bonus.getSpeedBonus() + equipment.getSpeedBonus());
                }
                if (equipment.getPhysicalResist() != null) {
                    bonus.setPhysicalResist(Math.min(75, bonus.getPhysicalResist() + equipment.getPhysicalResist()));
                }
                if (equipment.getIceResist() != null) {
                    bonus.setIceResist(Math.min(75, bonus.getIceResist() + equipment.getIceResist()));
                }
                if (equipment.getFireResist() != null) {
                    bonus.setFireResist(Math.min(75, bonus.getFireResist() + equipment.getFireResist()));
                }
                if (equipment.getLightningResist() != null) {
                    bonus.setLightningResist(Math.min(75, bonus.getLightningResist() + equipment.getLightningResist()));
                }
            }
        }

        return bonus;
    }
}
