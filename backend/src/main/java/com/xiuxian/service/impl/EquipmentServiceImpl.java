package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AutoEquipRequest;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.AutoEquipResponse;
import com.xiuxian.dto.response.EquipmentChangeInfo;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 装备Service实现类
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    // 装备槽位列表
    private static final String[] EQUIPMENT_SLOTS = {
        "武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链"
    };

    private final CharacterEquipmentMapper characterEquipmentMapper;
    private final CharacterService characterService;
    private final CharacterInventoryMapper characterInventoryMapper;

    public EquipmentServiceImpl(CharacterEquipmentMapper characterEquipmentMapper,
                                CharacterService characterService,
                                CharacterInventoryMapper characterInventoryMapper) {
        this.characterEquipmentMapper = characterEquipmentMapper;
        this.characterService = characterService;
        this.characterInventoryMapper = characterInventoryMapper;
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
        bonus.criticalRateBonus = 0;
        bonus.speedBonus = 0;
        bonus.physicalResistBonus = 0;
        bonus.iceResistBonus = 0;
        bonus.fireResistBonus = 0;
        bonus.lightningResistBonus = 0;

        // 累加所有装备的属性加成
        for (CharacterEquipment ce : characterEquipments) {
            Equipment equipment = baseMapper.selectById(ce.getEquipmentId());
            if (equipment != null) {
                bonus.attackBonus += safeAdd(equipment.getAttackPower());
                bonus.defenseBonus += safeAdd(equipment.getDefensePower());
                bonus.healthBonus += safeAdd(equipment.getHealthBonus());
                bonus.criticalRateBonus += safeAdd(equipment.getCriticalRate());
                bonus.speedBonus += safeAdd(equipment.getSpeedBonus());
                bonus.physicalResistBonus += safeAdd(equipment.getPhysicalResist());
                bonus.iceResistBonus += safeAdd(equipment.getIceResist());
                bonus.fireResistBonus += safeAdd(equipment.getFireResist());
                bonus.lightningResistBonus += safeAdd(equipment.getLightningResist());
            }
        }

        logger.debug("计算角色{}的装备加成: attack={}, defense={}, health={}, crit={}, speed={}",
                characterId, bonus.attackBonus, bonus.defenseBonus, bonus.healthBonus,
                bonus.criticalRateBonus, bonus.speedBonus);

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
        return "武器".equals(slot) || "头盔".equals(slot) || "铠甲".equals(slot)
                || "护手".equals(slot) || "护腿".equals(slot) || "靴子".equals(slot)
                || "戒指1".equals(slot) || "戒指2".equals(slot) || "项链".equals(slot);
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
            case "铠甲":
                return "铠甲".equals(equipmentType);
            case "护手":
                return "护手".equals(equipmentType);
            case "护腿":
                return "护腿".equals(equipmentType);
            case "靴子":
                return "靴子".equals(equipmentType);
            case "戒指1":
            case "戒指2":
                return "戒指".equals(equipmentType);
            case "项链":
                return "项链".equals(equipmentType);
            default:
                return false;
        }
    }

    @Override
    @Transactional
    public AutoEquipResponse autoEquip(AutoEquipRequest request) {
        Long characterId = request.getCharacterId();
        String priorityAttribute = request.getPriorityAttribute();

        // 1. 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 获取当前已装备的装备
        List<EquipmentResponse> currentEquipments = getCharacterEquipments(characterId);
        Map<String, EquipmentResponse> currentEquipMap = currentEquipments.stream()
            .collect(Collectors.toMap(EquipmentResponse::getEquipmentSlot, e -> e));

        // 3. 获取背包中的装备
        List<Equipment> inventoryEquipments = getInventoryEquipments(characterId);
        if (inventoryEquipments.isEmpty()) {
            throw new BusinessException(6001, "背包中没有可装备的物品");
        }

        // 4. 按装备类型分组
        Map<String, List<Equipment>> equipmentsByType = inventoryEquipments.stream()
            .collect(Collectors.groupingBy(Equipment::getEquipmentType));

        // 5. 计算最优装备方案并执行装备
        List<EquipmentChangeInfo> changes = new ArrayList<>();
        Set<Long> usedEquipmentIds = new HashSet<>(); // 跟踪已使用的装备ID

        for (String slot : EQUIPMENT_SLOTS) {
            // 获取该槽位当前装备
            EquipmentResponse currentEquipment = currentEquipMap.get(slot);

            // 根据槽位确定装备类型
            String equipmentType = getEquipmentTypeForSlot(slot);
            if (equipmentType == null) continue;

            // 获取该类型的所有候选装备（排除已使用的）
            List<Equipment> candidates = equipmentsByType.getOrDefault(equipmentType, new ArrayList<>())
                .stream()
                .filter(e -> !usedEquipmentIds.contains(e.getEquipmentId()))
                .collect(Collectors.toList());

            // 找出最优装备
            Equipment bestEquipment = findBestEquipment(candidates, priorityAttribute, currentEquipment);

            if (bestEquipment != null) {
                // 判断是否需要替换
                if (shouldReplace(currentEquipment, bestEquipment, priorityAttribute)) {
                    // 执行装备替换
                    EquipmentResponse oldEq = currentEquipment;

                    // 先卸下旧装备
                    if (oldEq != null) {
                        unequipItem(characterId, slot);
                    }

                    // 穿戴新装备
                    EquipRequest equipRequest = new EquipRequest();
                    equipRequest.setCharacterId(characterId);
                    equipRequest.setEquipmentId(bestEquipment.getEquipmentId());
                    equipRequest.setEquipmentSlot(slot);
                    equipItem(equipRequest);

                    // 标记为已使用
                    usedEquipmentIds.add(bestEquipment.getEquipmentId());

                    // 记录变更
                    String reason = buildChangeReason(oldEq,
                        EquipmentResponse.fromEntity(bestEquipment, slot, true),
                        priorityAttribute);
                    changes.add(new EquipmentChangeInfo(slot, oldEq,
                        EquipmentResponse.fromEntity(bestEquipment, slot, true), reason));
                }
            }
        }

        logger.info("角色{}完成一键装备，共更换{}件装备，优先属性：{}",
            characterId, changes.size(), priorityAttribute);

        return new AutoEquipResponse(changes, priorityAttribute);
    }

    @Override
    public AutoEquipResponse previewAutoEquip(AutoEquipRequest request) {
        Long characterId = request.getCharacterId();
        String priorityAttribute = request.getPriorityAttribute();

        // 验证角色是否存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 获取当前已装备的装备
        List<EquipmentResponse> currentEquipments = getCharacterEquipments(characterId);
        Map<String, EquipmentResponse> currentEquipMap = currentEquipments.stream()
            .collect(Collectors.toMap(EquipmentResponse::getEquipmentSlot, e -> e));

        // 获取背包中的装备
        List<Equipment> inventoryEquipments = getInventoryEquipments(characterId);

        // 按装备类型分组
        Map<String, List<Equipment>> equipmentsByType = inventoryEquipments.stream()
            .collect(Collectors.groupingBy(Equipment::getEquipmentType));

        // 计算预览方案
        List<EquipmentChangeInfo> changes = new ArrayList<>();
        Set<Long> usedEquipmentIds = new HashSet<>();

        for (String slot : EQUIPMENT_SLOTS) {
            EquipmentResponse currentEquipment = currentEquipMap.get(slot);

            String equipmentType = getEquipmentTypeForSlot(slot);
            if (equipmentType == null) continue;

            List<Equipment> candidates = equipmentsByType.getOrDefault(equipmentType, new ArrayList<>())
                .stream()
                .filter(e -> !usedEquipmentIds.contains(e.getEquipmentId()))
                .collect(Collectors.toList());

            Equipment bestEquipment = findBestEquipment(candidates, priorityAttribute, currentEquipment);

            if (bestEquipment != null && shouldReplace(currentEquipment, bestEquipment, priorityAttribute)) {
                EquipmentResponse newEq = EquipmentResponse.fromEntity(bestEquipment, slot, true);
                String reason = buildChangeReason(currentEquipment, newEq, priorityAttribute);
                changes.add(new EquipmentChangeInfo(slot, currentEquipment, newEq, reason));

                usedEquipmentIds.add(bestEquipment.getEquipmentId());
            }
        }

        return new AutoEquipResponse(changes, priorityAttribute);
    }

    /**
     * 获取背包中的装备列表
     */
    private List<Equipment> getInventoryEquipments(Long characterId) {
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
               .eq(CharacterInventory::getItemType, "equipment");

        List<CharacterInventory> inventoryItems = characterInventoryMapper.selectList(wrapper);
        List<Long> equipmentIds = inventoryItems.stream()
            .map(CharacterInventory::getItemId)
            .collect(Collectors.toList());

        if (equipmentIds.isEmpty()) {
            return new ArrayList<>();
        }

        return baseMapper.selectBatchIds(equipmentIds);
    }

    /**
     * 根据槽位获取装备类型
     */
    private String getEquipmentTypeForSlot(String slot) {
        switch (slot) {
            case "武器": return "武器";
            case "头盔": return "头盔";
            case "铠甲": return "铠甲";
            case "护手": return "护手";
            case "护腿": return "护腿";
            case "靴子": return "靴子";
            case "戒指1":
            case "戒指2": return "戒指";
            case "项链": return "项链";
            default: return null;
        }
    }

    /**
     * 找出最优装备
     */
    private Equipment findBestEquipment(List<Equipment> candidates, String priorityAttribute,
                                       EquipmentResponse currentEquipment) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        // 根据优先属性获取比较器
        Comparator<Equipment> comparator = getEquipmentComparator(priorityAttribute);

        // 找出评分/属性最高的装备
        return candidates.stream()
            .max(comparator)
            .orElse(null);
    }

    /**
     * 获取装备比较器
     */
    private Comparator<Equipment> getEquipmentComparator(String priorityAttribute) {
        if (priorityAttribute == null || priorityAttribute.isEmpty()) {
            // 默认按baseScore排序
            return Comparator.comparing((Equipment e) ->
                e.getBaseScore() != null ? e.getBaseScore() : 0);
        }

        switch (priorityAttribute.toLowerCase()) {
            case "physical":
                return Comparator.comparing((Equipment e) ->
                    e.getPhysicalResist() != null ? e.getPhysicalResist() : 0);
            case "ice":
                return Comparator.comparing((Equipment e) ->
                    e.getIceResist() != null ? e.getIceResist() : 0);
            case "fire":
                return Comparator.comparing((Equipment e) ->
                    e.getFireResist() != null ? e.getFireResist() : 0);
            case "lightning":
                return Comparator.comparing((Equipment e) ->
                    e.getLightningResist() != null ? e.getLightningResist() : 0);
            default:
                return Comparator.comparing((Equipment e) ->
                    e.getBaseScore() != null ? e.getBaseScore() : 0);
        }
    }

    /**
     * 判断是否需要替换装备
     */
    private boolean shouldReplace(EquipmentResponse current, Equipment candidate, String priorityAttribute) {
        if (current == null) {
            return true; // 槽位为空，需要装备
        }

        // 不装备同一件装备
        if (current.getEquipmentId().equals(candidate.getEquipmentId())) {
            return false;
        }

        if (priorityAttribute == null || priorityAttribute.isEmpty()) {
            // 按baseScore比较
            Integer candidateScore = candidate.getBaseScore() != null ? candidate.getBaseScore() : 0;
            Integer currentScore = current.getBaseScore() != null ? current.getBaseScore() : 0;
            return candidateScore > currentScore;
        }

        // 按指定属性抗性比较
        int candidateResist = getResistValue(candidate, priorityAttribute);
        int currentResist = getResistValueFromResponse(current, priorityAttribute);

        return candidateResist > currentResist;
    }

    /**
     * 获取装备的抗性值
     */
    private int getResistValue(Equipment equipment, String attribute) {
        if (equipment == null) return 0;

        switch (attribute.toLowerCase()) {
            case "physical":
                return equipment.getPhysicalResist() != null ? equipment.getPhysicalResist() : 0;
            case "ice":
                return equipment.getIceResist() != null ? equipment.getIceResist() : 0;
            case "fire":
                return equipment.getFireResist() != null ? equipment.getFireResist() : 0;
            case "lightning":
                return equipment.getLightningResist() != null ? equipment.getLightningResist() : 0;
            default:
                return equipment.getBaseScore() != null ? equipment.getBaseScore() : 0;
        }
    }

    /**
     * 从Response获取装备的抗性值
     */
    private int getResistValueFromResponse(EquipmentResponse response, String attribute) {
        if (response == null) return 0;

        switch (attribute.toLowerCase()) {
            case "physical":
                return response.getPhysicalResist() != null ? response.getPhysicalResist() : 0;
            case "ice":
                return response.getIceResist() != null ? response.getIceResist() : 0;
            case "fire":
                return response.getFireResist() != null ? response.getFireResist() : 0;
            case "lightning":
                return response.getLightningResist() != null ? response.getLightningResist() : 0;
            default:
                return response.getBaseScore() != null ? response.getBaseScore() : 0;
        }
    }

    /**
     * 构建变更原因
     */
    private String buildChangeReason(EquipmentResponse oldEq, EquipmentResponse newEq, String priorityAttribute) {
        if (oldEq == null) {
            return "装备新物品";
        }

        if (priorityAttribute == null || priorityAttribute.isEmpty()) {
            return String.format("评分提升：%d → %d",
                oldEq.getBaseScore(), newEq.getBaseScore());
        }

        int oldResist = getResistValueFromResponse(oldEq, priorityAttribute);
        int newResist = getResistValueFromResponse(newEq, priorityAttribute);
        String attrName = getAttributeName(priorityAttribute);

        return String.format("%s抗性提升：%d → %d", attrName, oldResist, newResist);
    }

    /**
     * 获取属性中文名
     */
    private String getAttributeName(String attribute) {
        switch (attribute.toLowerCase()) {
            case "physical": return "物理";
            case "ice": return "冰系";
            case "fire": return "火系";
            case "lightning": return "雷系";
            default: return "基础";
        }
    }
}
