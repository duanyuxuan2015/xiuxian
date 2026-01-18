package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.ExplorationArea;
import com.xiuxian.entity.ExplorationEvent;
import com.xiuxian.entity.ExplorationRecord;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.Pill;
import com.xiuxian.mapper.ExplorationAreaMapper;
import com.xiuxian.mapper.ExplorationEventMapper;
import com.xiuxian.mapper.ExplorationRecordMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.MonsterMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.ExplorationService;
import com.xiuxian.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 探索Service实现类
 */
@Service
public class ExplorationServiceImpl extends ServiceImpl<ExplorationRecordMapper, ExplorationRecord>
        implements ExplorationService {

    private static final Logger logger = LoggerFactory.getLogger(ExplorationServiceImpl.class);

    private final CharacterService characterService;
    private final InventoryService inventoryService;
    private final ExplorationAreaMapper areaMapper;
    private final ExplorationEventMapper eventMapper;
    private final MaterialMapper materialMapper;
    private final PillMapper pillMapper;
    private final MonsterMapper monsterMapper;
    private final Random random = new Random();

    public ExplorationServiceImpl(@Lazy CharacterService characterService,
            InventoryService inventoryService,
            ExplorationAreaMapper areaMapper,
            ExplorationEventMapper eventMapper,
            MaterialMapper materialMapper,
            PillMapper pillMapper,
            MonsterMapper monsterMapper) {
        this.characterService = characterService;
        this.inventoryService = inventoryService;
        this.areaMapper = areaMapper;
        this.eventMapper = eventMapper;
        this.materialMapper = materialMapper;
        this.pillMapper = pillMapper;
        this.monsterMapper = monsterMapper;
    }

    @Override
    public List<ExplorationAreaResponse> getAllAreas(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        List<ExplorationArea> areas = areaMapper.selectList(null);
        List<ExplorationAreaResponse> responses = new ArrayList<>();

        for (ExplorationArea area : areas) {
            responses.add(ExplorationAreaResponse.fromEntity(area, character.getRealmLevel()));
        }

        return responses;
    }

    @Override
    @Transactional
    public ExplorationResponse startExploration(ExplorationRequest request) {
        Long characterId = request.getCharacterId();
        Long areaId = request.getAreaId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证区域
        ExplorationArea area = areaMapper.selectById(areaId);
        if (area == null) {
            throw new BusinessException(9001, "探索区域不存在");
        }

        // 3. 验证境界
        if (character.getRealmLevel() < area.getRequiredRealmLevel()) {
            throw new BusinessException(9002, "境界不足，需要境界等级: " + area.getRequiredRealmLevel());
        }

        // 4. 验证灵力（使用getSpiritualPower并处理null）
        Integer currentSpirit = character.getSpiritualPower();
        Integer spiritCost = area.getSpiritCost();

        if (currentSpirit == null) {
            throw new BusinessException(9003, "角色灵力数据异常，请联系管理员");
        }

        if (spiritCost == null) {
            spiritCost = 0; // 默认不消耗灵力
        }

        if (currentSpirit < spiritCost) {
            throw new BusinessException(9003,
                    "灵力不足，需要: " + spiritCost + "，当前: " + currentSpirit);
        }

        // 5. 消耗灵力
        character.setSpiritualPower(currentSpirit - spiritCost);

        // 6. 验证体力
        Integer currentStamina = character.getStamina();
        Integer staminaCost = area.getStaminaCost();

        if (currentStamina == null) {
            throw new BusinessException(9004, "角色体力数据异常，请联系管理员");
        }

        if (staminaCost == null) {
            staminaCost = 10; // 默认消耗10体力
        }

        if (currentStamina < staminaCost) {
            throw new BusinessException(9004,
                    "体力不足，需要: " + staminaCost + "，当前: " + currentStamina);
        }

        // 7. 消耗体力
        character.setStamina(currentStamina - staminaCost);
        characterService.updateById(character);

        // 8. 随机触发事件
        ExplorationEvent event = selectRandomEvent(areaId);

        // 9. 处理事件结果
        ExplorationResponse response = processEvent(character, area, event);

        // 10. 设置资源消耗和剩余信息
        response.setStaminaCost(staminaCost);
        response.setStaminaRemaining(character.getStamina());
        response.setSpiritCost(spiritCost);
        response.setSpiritRemaining(character.getSpiritualPower());

        logger.info("探索完成: characterId={}, areaId={}, areaName={}, eventType={}, staminaCost={}, staminaRemaining={}, spiritCost={}, spiritRemaining={}",
                characterId, areaId, area.getAreaName(), event != null ? event.getEventType() : "无事件",
                staminaCost, character.getStamina(), spiritCost, character.getSpiritualPower());

        return response;
    }

    private ExplorationEvent selectRandomEvent(Long areaId) {
        LambdaQueryWrapper<ExplorationEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExplorationEvent::getAreaId, areaId);
        List<ExplorationEvent> events = eventMapper.selectList(wrapper);

        if (events.isEmpty()) {
            return null;
        }

        // 根据概率选择事件
        int totalProbability = events.stream().mapToInt(ExplorationEvent::getProbability).sum();
        int roll = random.nextInt(totalProbability);

        int cumulative = 0;
        for (ExplorationEvent event : events) {
            cumulative += event.getProbability();
            if (roll < cumulative) {
                return event;
            }
        }

        return events.get(events.size() - 1);
    }

    @Transactional
    protected ExplorationResponse processEvent(PlayerCharacter character, ExplorationArea area,
            ExplorationEvent event) {
        ExplorationRecord record = new ExplorationRecord();
        record.setCharacterId(character.getCharacterId());
        record.setAreaId(area.getAreaId());
        record.setCreatedAt(LocalDateTime.now());

        ExplorationResponse response = new ExplorationResponse();
        response.setCharacterId(character.getCharacterId());
        response.setAreaId(area.getAreaId());
        response.setAreaName(area.getAreaName());
        response.setCreatedAt(LocalDateTime.now());

        if (event == null) {
            // 无事件
            record.setEventType("无事");
            record.setResult("一路平安，未发现特别之处");
            record.setExperienceGained(area.getDangerLevel() * 5);
            this.save(record);

            response.setEventType("无事");
            response.setEventName("平静探索");
            response.setResult("一路平安，未发现特别之处");
            response.setExperienceGained(area.getDangerLevel() * 5);
            response.setSpiritualPowerGained(0);
            response.setHealthLost(0);
            response.setNeedCombat(false);
            return response;
        }

        record.setEventId(event.getEventId());
        record.setEventType(event.getEventType());
        response.setEventType(event.getEventType());
        response.setEventName(event.getEventName());
        response.setDescription(event.getDescription());

        switch (event.getEventType()) {
            case "采集":
                processGatherEvent(character, event, record, response);
                break;
            case "战斗":
                processCombatEvent(character, event, record, response);
                break;
            case "机缘":
                processFortuneEvent(character, event, record, response);
                break;
            case "陷阱":
                processTrapEvent(character, area, event, record, response);
                break;
            case "无事":
            default:
                record.setResult("一路平安，未发现特别之处");
                record.setExperienceGained(area.getDangerLevel() * 5);
                response.setResult("一路平安，未发现特别之处");
                response.setExperienceGained(area.getDangerLevel() * 5);
                response.setSpiritualPowerGained(0);
                response.setHealthLost(0);
                response.setNeedCombat(false);
                break;
        }

        this.save(record);
        response.setRecordId(record.getRecordId());

        return response;
    }

    private void processGatherEvent(PlayerCharacter character, ExplorationEvent event,
            ExplorationRecord record, ExplorationResponse response) {
        String rewardType = event.getRewardType();
        Long rewardId = event.getRewardId();
        int quantity = random.nextInt(event.getRewardQuantityMax() - event.getRewardQuantityMin() + 1)
                + event.getRewardQuantityMin();

        String itemName = getItemName(rewardType, rewardId);
        inventoryService.addItem(character.getCharacterId(), rewardType, rewardId, quantity);

        String rewardStr = itemName + " x" + quantity;
        int exp = 10 + quantity * 5;

        record.setResult("采集成功");
        record.setRewards(rewardStr);
        record.setExperienceGained(exp);

        response.setResult("采集成功！");
        response.setRewards(rewardStr);
        response.setExperienceGained(exp);
        response.setSpiritualPowerGained(0);
        response.setHealthLost(0);
        response.setNeedCombat(false);
    }

    private void processCombatEvent(PlayerCharacter character, ExplorationEvent event,
            ExplorationRecord record, ExplorationResponse response) {
        Long monsterId = event.getMonsterId();
        Monster monster = monsterMapper.selectById(monsterId);

        if (monster != null) {
            response.setNeedCombat(true);
            response.setMonsterId(monsterId);
            response.setMonsterName(monster.getMonsterName());
            response.setResult("遭遇怪物：" + monster.getMonsterName() + "，需要战斗！");

            // 初始化所有字段以防止客户端NPE
            response.setExperienceGained(0);
            response.setSpiritualPowerGained(0);
            response.setHealthLost(0);
            response.setItemFound(null);

            record.setResult("遭遇怪物：" + monster.getMonsterName());
            record.setExperienceGained(0);
        } else {
            response.setNeedCombat(false);
            response.setResult("怪物逃跑了");

            // 初始化所有字段以防止客户端NPE
            response.setExperienceGained(5);
            response.setSpiritualPowerGained(0);
            response.setHealthLost(0);
            response.setItemFound(null);

            record.setResult("怪物逃跑了");
            record.setExperienceGained(5);
        }
    }

    private void processFortuneEvent(PlayerCharacter character, ExplorationEvent event,
            ExplorationRecord record, ExplorationResponse response) {
        String rewardType = event.getRewardType();
        Long rewardId = event.getRewardId();
        int quantity = random.nextInt(event.getRewardQuantityMax() - event.getRewardQuantityMin() + 1)
                + event.getRewardQuantityMin();

        String itemName = getItemName(rewardType, rewardId);
        inventoryService.addItem(character.getCharacterId(), rewardType, rewardId, quantity);

        String rewardStr = itemName + " x" + quantity;
        int exp = 30 + quantity * 10;

        record.setResult("获得机缘");
        record.setRewards(rewardStr);
        record.setExperienceGained(exp);

        response.setResult("获得机缘！发现了珍贵的宝物！");
        response.setRewards(rewardStr);
        response.setExperienceGained(exp);
        response.setSpiritualPowerGained(0);
        response.setHealthLost(0);
        response.setNeedCombat(false);
    }

    private void processTrapEvent(PlayerCharacter character, ExplorationArea area, ExplorationEvent event,
            ExplorationRecord record, ExplorationResponse response) {
        // 陷阱事件：损失部分生命或灵力
        int damage = area.getDangerLevel() * 10;
        character.setCurrentHealth(Math.max(1, character.getCurrentHealth() - damage));
        characterService.updateById(character);

        record.setResult("触发陷阱，损失生命 " + damage);
        record.setExperienceGained(area.getDangerLevel() * 3);

        response.setResult("不幸触发陷阱！损失生命 " + damage);
        response.setExperienceGained(area.getDangerLevel() * 3);
        response.setSpiritualPowerGained(0);
        response.setHealthLost(damage);
        response.setNeedCombat(false);
    }

    private String getItemName(String itemType, Long itemId) {
        if (itemType == null || itemId == null) {
            return "未知物品";
        }

        switch (itemType) {
            case "material":
                Material material = materialMapper.selectById(itemId);
                return material != null ? material.getMaterialName() : "未知材料";
            case "pill":
                Pill pill = pillMapper.selectById(itemId);
                return pill != null ? pill.getPillName() : "未知丹药";
            case "equipment":
                return "装备";
            default:
                return "未知物品";
        }
    }

    @Override
    public List<ExplorationResponse> getExplorationRecords(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<ExplorationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExplorationRecord::getCharacterId, characterId)
                .orderByDesc(ExplorationRecord::getCreatedAt)
                .last("LIMIT 50");

        List<ExplorationRecord> records = this.list(wrapper);
        List<ExplorationResponse> responses = new ArrayList<>();

        for (ExplorationRecord record : records) {
            ExplorationArea area = areaMapper.selectById(record.getAreaId());
            String areaName = area != null ? area.getAreaName() : "未知区域";

            String eventName = "未知事件";
            if (record.getEventId() != null) {
                ExplorationEvent event = eventMapper.selectById(record.getEventId());
                if (event != null) {
                    eventName = event.getEventName();
                }
            }

            responses.add(ExplorationResponse.fromRecord(record, areaName, eventName));
        }

        return responses;
    }
}
