package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.config.CombatConstants;
import com.xiuxian.config.CombatProperties;
import com.xiuxian.config.StaminaCostProperties;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.entity.CharacterSkill;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Realm;
import com.xiuxian.entity.Skill;
import com.xiuxian.mapper.CharacterSkillMapper;
import com.xiuxian.mapper.CombatRecordMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.SkillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.CombatService;
import com.xiuxian.service.MonsterService;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.MonsterDropService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.service.SectTaskService;
import com.xiuxian.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 战斗Service实现类
 */
@Service
public class CombatServiceImpl extends ServiceImpl<CombatRecordMapper, CombatRecord> implements CombatService {

    private static final Logger logger = LoggerFactory.getLogger(CombatServiceImpl.class);
    private static final Logger operationLogger = LoggerFactory.getLogger("OPERATION");

    private final CharacterService characterService;
    private final MonsterService monsterService;
    private final RealmService realmService;
    private final MonsterDropService monsterDropService;
    private final InventoryService inventoryService;
    private final EquipmentMapper equipmentMapper;
    private final MaterialMapper materialMapper;
    private final SectTaskService sectTaskService;
    private final SkillService skillService;
    private final CharacterSkillMapper characterSkillMapper;
    private final SkillMapper skillMapper;
    private final CombatProperties combatProperties;
    private final StaminaCostProperties staminaCostProperties;
    private final Random random = new Random();

    public CombatServiceImpl(@Lazy CharacterService characterService, MonsterService monsterService,
                             RealmService realmService, MonsterDropService monsterDropService,
                             InventoryService inventoryService, EquipmentMapper equipmentMapper,
                             MaterialMapper materialMapper,
                             @Lazy SectTaskService sectTaskService, @Lazy SkillService skillService,
                             CharacterSkillMapper characterSkillMapper, SkillMapper skillMapper,
                             CombatProperties combatProperties, StaminaCostProperties staminaCostProperties) {
        this.characterService = characterService;
        this.monsterService = monsterService;
        this.realmService = realmService;
        this.monsterDropService = monsterDropService;
        this.inventoryService = inventoryService;
        this.equipmentMapper = equipmentMapper;
        this.materialMapper = materialMapper;
        this.sectTaskService = sectTaskService;
        this.skillService = skillService;
        this.characterSkillMapper = characterSkillMapper;
        this.skillMapper = skillMapper;
        this.combatProperties = combatProperties;
        this.staminaCostProperties = staminaCostProperties;
    }

    @Override
    @Transactional
    public CombatResponse startCombat(CombatStartRequest request) {
        Long characterId = request.getCharacterId();
        Long monsterId = request.getMonsterId();

        // 1. 获取角色信息
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 检查角色状态
        if (!"闲置".equals(character.getCurrentState())) {
            throw new BusinessException(2002, "角色当前状态不允许战斗，当前状态：" + character.getCurrentState());
        }

        // 3. 获取妖兽信息
        Monster monster = monsterService.getById(monsterId);
        if (monster == null) {
            throw new BusinessException(3001, "妖兽不存在");
        }

        // 4. 检查体力是否足够
        if (character.getStamina() < monster.getStaminaCost()) {
            throw new BusinessException(2003,
                    "体力不足，需要" + monster.getStaminaCost() + "点体力，当前：" + character.getStamina());
        }

        // 5. 设置战斗状态
        character.setCurrentState("战斗中");
        characterService.updateCharacter(character);

        // 6. 执行战斗逻辑
        CombatResult result = executeCombat(character, monster);

        // 7. 更新角色状态
        character = characterService.getById(characterId);
        double combatMultiplier = staminaCostProperties.getCombatMultiplier();
        double defeatRatio = staminaCostProperties.getCombatDefeatRatio();
        int baseStaminaCost = (int) (monster.getStaminaCost() * combatMultiplier);
        int staminaCost = result.victory ? baseStaminaCost : (int) (baseStaminaCost * defeatRatio);
        character.setStamina(character.getStamina() - staminaCost);
        character.setHealth(result.characterHpRemaining);
        character.setSpiritualPower(character.getSpiritualPower() - result.spiritualPowerConsumed);  // 扣除战斗中消耗的灵力
        character.setCurrentState("闲置");

        // 8. 发放奖励（如果胜利）
        int expGained = 0;
        int spiritStonesGained = 0;
        List<String> itemsDropped = new ArrayList<>();
        int equipmentDropCount = 0;  // 装备掉落数
        int materialDropCount = 0;    // 材料掉落数

        if (result.victory) {
            expGained = monster.getExpReward();
            spiritStonesGained = monster.getSpiritStonesReward();
            character.setExperience(character.getExperience() + expGained);
            character.setSpiritStones(character.getSpiritStones() + spiritStonesGained);

            // 执行装备掉落检测
            List<Long> droppedEquipmentIds = monsterDropService.rollEquipmentDrops(monsterId, characterId);
            if (droppedEquipmentIds != null && !droppedEquipmentIds.isEmpty()) {
                for (Long equipmentId : droppedEquipmentIds) {
                    Equipment equipment = equipmentMapper.selectById(equipmentId);
                    if (equipment != null) {
                        // 将装备添加到背包
                        inventoryService.addItem(characterId, "equipment", equipmentId, 1);
                        itemsDropped.add("🗡️" + equipment.getEquipmentName());
                        equipmentDropCount++;
                        logger.info("玩家{}击败{}，获得装备: {}（已添加到背包）", character.getPlayerName(),
                                monster.getMonsterName(), equipment.getEquipmentName());
                    }
                }
            }

            // 执行材料掉落检测
            List<Long> droppedMaterialIds = monsterDropService.rollMaterialDrops(monsterId, characterId);
            if (droppedMaterialIds != null && !droppedMaterialIds.isEmpty()) {
                for (Long materialId : droppedMaterialIds) {
                    Material material = materialMapper.selectById(materialId);
                    if (material != null) {
                        // 获取掉落数量（添加 itemType 条件以确保准确查询）
                        LambdaQueryWrapper<com.xiuxian.entity.MonsterDrop> dropWrapper = new LambdaQueryWrapper<>();
                        dropWrapper.eq(com.xiuxian.entity.MonsterDrop::getMonsterId, monsterId)
                                   .eq(com.xiuxian.entity.MonsterDrop::getItemType, "material")
                                   .eq(com.xiuxian.entity.MonsterDrop::getItemId, materialId)
                                   .eq(com.xiuxian.entity.MonsterDrop::getDeleted, 0);
                        com.xiuxian.entity.MonsterDrop dropConfig = monsterDropService.getOne(dropWrapper);
                        int quantity = (dropConfig != null && dropConfig.getDropQuantity() != null)
                                      ? dropConfig.getDropQuantity()
                                      : 1;

                        // 将材料添加到背包
                        inventoryService.addItem(characterId, "material", materialId, quantity);
                        itemsDropped.add("📦" + material.getMaterialName() + " x" + quantity);
                        materialDropCount++;
                        logger.info("玩家{}击败{}，获得材料: {} x{}（已添加到背包）", character.getPlayerName(),
                                monster.getMonsterName(), material.getMaterialName(), quantity);
                    }
                }
            }
        }

        // 更新宗门任务进度（战斗任务）
        if (result.victory) {
            try {
                sectTaskService.addTaskProgress(characterId, "combat",
                        String.valueOf(monster.getRealmId()), 1);
            } catch (Exception e) {
                logger.warn("更新宗门战斗任务进度失败: {}", e.getMessage());
            }
        }

        characterService.updateCharacter(character);

        // 9. 创建战斗记录
        CombatRecord record = new CombatRecord();
        record.setCharacterId(characterId);
        record.setMonsterId(monsterId);
        record.setCombatMode(request.getCombatMode());
        record.setIsVictory(result.victory ? 1 : 0);
        record.setTurns(result.turns);
        record.setDamageDealt(result.damageDealt);
        record.setDamageTaken(result.damageTaken);
        record.setCriticalHits(result.criticalHits);
        record.setStaminaConsumed(staminaCost);
        record.setExpGained(expGained);
        record.setCombatTime(LocalDateTime.now());
        this.save(record);

        // 10. 记录操作日志
        operationLogger.info("战斗结束: characterId={}, monsterId={}, victory={}, turns={}, expGained={}",
                characterId, monsterId, result.victory, result.turns, expGained);

        // 11. 构建响应
        CombatResponse response = new CombatResponse();
        response.setCombatId(record.getCombatId());
        response.setCharacterId(characterId);
        response.setPlayerName(character.getPlayerName());
        response.setMonsterId(monsterId);
        response.setMonsterName(monster.getMonsterName());
        response.setCombatMode(request.getCombatMode());
        response.setVictory(result.victory);
        response.setTurns(result.turns);
        response.setDamageDealt(result.damageDealt);
        response.setDamageTaken(result.damageTaken);
        response.setCriticalHits(result.criticalHits);
        response.setStaminaConsumed(staminaCost);
        response.setExpGained(expGained);
        response.setSpiritStonesGained(spiritStonesGained);
        response.setCharacterHpRemaining(result.characterHpRemaining);
        response.setCharacterStaminaRemaining(character.getStamina());  // 设置剩余体力
        response.setCharacterSpiritualPowerRemaining(character.getSpiritualPower());  // 设置剩余灵力
        response.setItemsDropped(itemsDropped);  // 设置掉落物品
        response.setCombatLog(result.combatLog);
        response.setCombatTime(record.getCombatTime());

        if (result.victory) {
            StringBuilder message = new StringBuilder(String.format("战斗胜利！击败%s，获得%d经验，%d灵石！",
                    monster.getMonsterName(), expGained, spiritStonesGained));

            // 分别显示装备和材料掉落
            if (!itemsDropped.isEmpty()) {
                message.append("\n📦掉落: ").append(String.join(" | ", itemsDropped));
            }
            response.setMessage(message.toString());
        } else {
            response.setMessage(String.format("战斗失败！被%s击败，消耗%d体力。",
                    monster.getMonsterName(), staminaCost));
        }

        return response;
    }

    @Override
    public List<Monster> getAvailableMonsters(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 返回当前境界及以下的所有妖兽
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Monster::getRealmId, character.getRealmId())
                .orderByAsc(Monster::getRealmId)
                .orderByAsc(Monster::getMonsterType);
        return monsterService.list(wrapper);
    }

    @Override
    public Page<CombatRecord> getCombatRecords(Long characterId, int page, int pageSize) {
        Page<CombatRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CombatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CombatRecord::getCharacterId, characterId)
                .orderByDesc(CombatRecord::getCombatTime);
        return this.page(pageParam, wrapper);
    }

    /**
     * 执行战斗逻辑
     */
    private CombatResult executeCombat(PlayerCharacter character, Monster monster) {
        CombatResult result = new CombatResult();
        result.combatLog = new ArrayList<>();
        result.skillsUsed = new ArrayList<>();
        result.slotCooldowns = new HashMap<>();
        result.spiritualPowerConsumed = 0;  // 初始化灵力消耗

        // 初始化：所有槽位冷却为0
        for (int i = 1; i <= 8; i++) {
            result.slotCooldowns.put(i, 0);
        }

        // 获取已装备的技能
        List<SkillResponse> equippedSkills = skillService.getEquippedSkills(character.getCharacterId());
        Map<Integer, SkillResponse> skillSlots = new HashMap<>();
        for (SkillResponse skill : equippedSkills) {
            if (skill.getSlotIndex() != null) {
                skillSlots.put(skill.getSlotIndex(), skill);
            }
        }

        int characterHp = character.getHealth();
        int monsterHp = monster.getHp();
        int characterAttack = calculateCharacterAttack(character);
        int characterDefense = calculateCharacterDefense(character);

        result.combatLog.add(String.format("战斗开始！%s VS %s", character.getPlayerName(), monster.getMonsterName()));
        result.combatLog.add(String.format("%s: 生命%d 攻击%d 防御%d", character.getPlayerName(), characterHp,
                characterAttack, characterDefense));
        result.combatLog.add(String.format("%s: 生命%d 攻击%d 防御%d", monster.getMonsterName(), monsterHp,
                monster.getAttackPower(), monster.getDefensePower()));

        int turn = 0;
        int maxTurns = combatProperties.getMaxTurns();
        while (characterHp > 0 && monsterHp > 0 && turn < maxTurns) {
            turn++;

            // 每回合开始，更新冷却状态
            updateCooldowns(result.slotCooldowns);

            // 角色攻击（优先使用技能）
            SkillUsageResult skillResult = tryUseSkill(character, skillSlots, result.slotCooldowns);
            int damage;

            if (skillResult.usedSkill) {
                // 使用技能攻击
                damage = calculateSkillDamage(character, skillResult.skill, skillResult.charSkill);
                result.skillsUsed.add(skillResult.skill.getSkillName());
                result.spiritualPowerConsumed += skillResult.spiritualPowerCost;  // 累加灵力消耗

                // 暴击判断
                int criticalRateBase = combatProperties.getCriticalRateBase();
                boolean isCritical = random.nextInt(100) < criticalRateBase;
                if (isCritical) {
                    double criticalMultiplier = combatProperties.getCriticalDamageMultiplier();
                    damage = (int) (damage * criticalMultiplier);
                    result.criticalHits++;
                }

                monsterHp -= damage;
                result.damageDealt += damage;

                if (isCritical) {
                    result.combatLog.add(String.format("回合%d: %s使用[%s](Lv.%d)，对%s造成%d点暴击伤害！(%s剩余生命: %d)",
                            turn, character.getPlayerName(), skillResult.skill.getSkillName(),
                            skillResult.charSkill.getSkillLevel(),
                            monster.getMonsterName(), damage,
                            monster.getMonsterName(), Math.max(0, monsterHp)));
                } else {
                    result.combatLog.add(String.format("回合%d: %s使用[%s](Lv.%d)，对%s造成%d点伤害 (%s剩余生命: %d)",
                            turn, character.getPlayerName(), skillResult.skill.getSkillName(),
                            skillResult.charSkill.getSkillLevel(),
                            monster.getMonsterName(), damage,
                            monster.getMonsterName(), Math.max(0, monsterHp)));
                }
            } else {
                // 普通攻击
                int criticalRateBase = combatProperties.getCriticalRateBase();
                boolean isCritical = random.nextInt(100) < criticalRateBase;
                damage = calculateDamage(characterAttack, monster.getDefensePower(), isCritical);
                monsterHp -= damage;
                result.damageDealt += damage;

                if (isCritical) {
                    result.criticalHits++;
                    result.combatLog.add(String.format("回合%d: %s对%s造成%d点暴击伤害 (%s剩余生命: %d)",
                            turn, character.getPlayerName(), monster.getMonsterName(), damage,
                            monster.getMonsterName(), Math.max(0, monsterHp)));
                } else {
                    result.combatLog.add(String.format("回合%d: %s攻击%s，造成%d点伤害 (%s剩余生命: %d)",
                            turn, character.getPlayerName(), monster.getMonsterName(), damage,
                            monster.getMonsterName(), Math.max(0, monsterHp)));
                }
            }

            if (monsterHp <= 0) {
                break;
            }

            // 妖兽攻击
            int monsterDamage = calculateDamage(monster.getAttackPower(), characterDefense, false);
            characterHp -= monsterDamage;
            result.damageTaken += monsterDamage;
            result.combatLog.add(String.format("回合%d: %s攻击%s，造成%d点伤害 (%s剩余生命: %d)",
                    turn, monster.getMonsterName(), character.getPlayerName(), monsterDamage,
                    character.getPlayerName(), Math.max(0, characterHp)));
        }

        result.turns = turn;
        result.victory = monsterHp <= 0;
        result.characterHpRemaining = Math.max(0, characterHp);

        if (result.victory) {
            result.combatLog.add(String.format("战斗结束！%s获得胜利！", character.getPlayerName()));
        } else {
            result.combatLog.add(String.format("战斗结束！%s被击败！", character.getPlayerName()));
        }

        return result;
    }

    /**
     * 更新技能冷却状态
     */
    private void updateCooldowns(Map<Integer, Integer> slotCooldowns) {
        for (Map.Entry<Integer, Integer> entry : slotCooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                entry.setValue(entry.getValue() - 1);
            }
        }
    }

    /**
     * 尝试使用技能
     */
    private SkillUsageResult tryUseSkill(PlayerCharacter character,
                                         Map<Integer, SkillResponse> skillSlots,
                                         Map<Integer, Integer> slotCooldowns) {
        // 按槽位顺序（1-8）查找可用的攻击技能
        for (int slot = 1; slot <= 8; slot++) {
            // 检查该槽位是否有技能
            if (!skillSlots.containsKey(slot)) {
                continue;
            }

            // 检查冷却
            if (slotCooldowns.get(slot) > 0) {
                continue;
            }

            SkillResponse skillResponse = skillSlots.get(slot);

            // 检查是否为攻击类技能
            if (!CombatConstants.isAttackSkill(skillResponse.getFunctionType())) {
                continue;
            }

            // 查询CharacterSkill获取技能等级
            CharacterSkill charSkill = characterSkillMapper.selectById(skillResponse.getCharacterSkillId());
            if (charSkill == null) {
                continue;
            }

            // 检查灵力是否足够
            Skill skill = skillMapper.selectById(charSkill.getSkillId());
            if (skill == null || character.getSpiritualPower() < skill.getSpiritualCost()) {
                continue;
            }

            // 设置冷却
            slotCooldowns.put(slot, CombatConstants.SKILL_COOLDOWN_TURNS);

            // 返回使用的技能（包含灵力消耗信息，但不扣除）
            SkillUsageResult result = new SkillUsageResult();
            result.usedSkill = true;
            result.skill = skillResponse;
            result.charSkill = charSkill;
            result.spiritualPowerCost = skill.getSpiritualCost();
            return result;
        }

        // 没有可用技能
        return new SkillUsageResult();
    }

    /**
     * 计算技能伤害
     */
    private int calculateSkillDamage(PlayerCharacter character, SkillResponse skillResponse, CharacterSkill charSkill) {
        Skill skill = skillMapper.selectById(charSkill.getSkillId());
        if (skill == null) {
            return calculateCharacterAttack(character);
        }

        // 基础伤害 + 等级加成
        int baseDamage = skill.getBaseDamage() != null ? skill.getBaseDamage() : 0;
        if (skill.getDamageGrowthRate() != null && charSkill.getSkillLevel() > 1 && baseDamage > 0) {
            int levelBonus = (int) (baseDamage * skill.getDamageGrowthRate().doubleValue() * (charSkill.getSkillLevel() - 1));
            baseDamage += levelBonus;
        }

        // 应用技能倍率
        int skillDamage = baseDamage;
        if (skill.getSkillMultiplier() != null) {
            skillDamage = (int) (baseDamage * skill.getSkillMultiplier().doubleValue());
        }

        // 叠加角色攻击力加成
        int characterAttack = calculateCharacterAttack(character);
        skillDamage += characterAttack;

        // 添加随机波动
        double fluctuationMin = combatProperties.getDamageFluctuation().getMin();
        double fluctuationMax = combatProperties.getDamageFluctuation().getMax();
        double fluctuation = fluctuationMin + random.nextDouble() * (fluctuationMax - fluctuationMin);
        return Math.max(1, (int) (skillDamage * fluctuation));
    }

    /**
     * 技能使用结果内部类
     */
    private static class SkillUsageResult {
        boolean usedSkill = false;
        SkillResponse skill;
        CharacterSkill charSkill;
        int spiritualPowerCost;  // 技能消耗的灵力
    }

    /**
     * 计算角色攻击力
     * 攻击力 = 体质 × 2 + 精神 × 1 + 境界攻击加成
     */
    private int calculateCharacterAttack(PlayerCharacter character) {
        // 基础攻击力 = 体质 × 2 + 精神 × 1
        int baseAttack = character.getConstitution() * 2 + character.getSpirit();

        // 获取境界攻击加成
        Realm realm = realmService.getById(character.getRealmId());
        int realmAttackBonus = (realm != null && realm.getAttackBonus() != null) ? realm.getAttackBonus() : 0;

        return baseAttack + realmAttackBonus;
    }

    /**
     * 计算角色防御力
     * 防御力 = 体质 × 1.5 + 境界防御加成
     */
    private int calculateCharacterDefense(PlayerCharacter character) {
        // 基础防御力 = 体质 × 1.5
        int baseDefense = (int) (character.getConstitution() * 1.5);

        // 获取境界防御加成
        Realm realm = realmService.getById(character.getRealmId());
        int realmDefenseBonus = (realm != null && realm.getDefenseBonus() != null) ? realm.getDefenseBonus() : 0;

        return baseDefense + realmDefenseBonus;
    }

    /**
     * 计算伤害
     */
    private int calculateDamage(int attack, int defense, boolean isCritical) {
        int baseDamage = Math.max(1, attack - defense);
        if (isCritical) {
            double criticalMultiplier = combatProperties.getCriticalDamageMultiplier();
            baseDamage = (int) (baseDamage * criticalMultiplier);
        }
        // 添加随机波动
        double fluctuationMin = combatProperties.getDamageFluctuation().getMin();
        double fluctuationMax = combatProperties.getDamageFluctuation().getMax();
        double fluctuation = fluctuationMin + random.nextDouble() * (fluctuationMax - fluctuationMin);
        return Math.max(1, (int) (baseDamage * fluctuation));
    }

    /**
     * 战斗结果内部类
     */
    private static class CombatResult {
        boolean victory;
        int turns;
        int damageDealt;
        int damageTaken;
        int criticalHits;
        int characterHpRemaining;
        int spiritualPowerConsumed;  // 战斗中消耗的灵力
        List<String> combatLog;
        List<String> skillsUsed;
        Map<Integer, Integer> slotCooldowns;
    }
}
