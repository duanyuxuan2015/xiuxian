package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;
import com.xiuxian.mapper.CombatRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.CombatService;
import com.xiuxian.service.MonsterService;
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
 * 战斗Service实现类
 */
@Service
public class CombatServiceImpl extends ServiceImpl<CombatRecordMapper, CombatRecord> implements CombatService {

    private static final Logger logger = LoggerFactory.getLogger(CombatServiceImpl.class);
    private static final Logger operationLogger = LoggerFactory.getLogger("OPERATION");

    private static final int MAX_TURNS = 50;
    private static final int CRITICAL_RATE_BASE = 5;

    private final CharacterService characterService;
    private final MonsterService monsterService;
    private final Random random = new Random();

    public CombatServiceImpl(@Lazy CharacterService characterService, MonsterService monsterService) {
        this.characterService = characterService;
        this.monsterService = monsterService;
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
        int staminaCost = result.victory ? monster.getStaminaCost() : monster.getStaminaCost() / 2;
        character.setStamina(character.getStamina() - staminaCost);
        character.setHealth(result.characterHpRemaining);
        character.setCurrentState("闲置");

        // 8. 发放奖励（如果胜利）
        int expGained = 0;
        int spiritStonesGained = 0;
        if (result.victory) {
            expGained = monster.getExpReward();
            spiritStonesGained = monster.getSpiritStonesReward();
            character.setExperience(character.getExperience() + expGained);
            character.setSpiritStones(character.getSpiritStones() + spiritStonesGained);
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
        response.setCombatLog(result.combatLog);
        response.setCombatTime(record.getCombatTime());

        if (result.victory) {
            response.setMessage(String.format("战斗胜利！击败%s，获得%d经验，%d灵石！",
                    monster.getMonsterName(), expGained, spiritStonesGained));
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
        while (characterHp > 0 && monsterHp > 0 && turn < MAX_TURNS) {
            turn++;

            // 角色攻击
            boolean isCritical = random.nextInt(100) < CRITICAL_RATE_BASE;
            int damage = calculateDamage(characterAttack, monster.getDefensePower(), isCritical);
            monsterHp -= damage;
            result.damageDealt += damage;
            if (isCritical) {
                result.criticalHits++;
                result.combatLog.add(String.format("回合%d: %s暴击！对%s造成%d点伤害", turn, character.getPlayerName(),
                        monster.getMonsterName(), damage));
            } else {
                result.combatLog.add(String.format("回合%d: %s攻击%s，造成%d点伤害", turn, character.getPlayerName(),
                        monster.getMonsterName(), damage));
            }

            if (monsterHp <= 0) {
                break;
            }

            // 妖兽攻击
            int monsterDamage = calculateDamage(monster.getAttackPower(), characterDefense, false);
            characterHp -= monsterDamage;
            result.damageTaken += monsterDamage;
            result.combatLog.add(String.format("回合%d: %s攻击%s，造成%d点伤害", turn, monster.getMonsterName(),
                    character.getPlayerName(), monsterDamage));
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
     * 计算角色攻击力
     */
    private int calculateCharacterAttack(PlayerCharacter character) {
        // 基础攻击力 = 体质 × 2 + 精神 × 1
        return character.getConstitution() * 2 + character.getSpirit();
    }

    /**
     * 计算角色防御力
     */
    private int calculateCharacterDefense(PlayerCharacter character) {
        // 基础防御力 = 体质 × 1.5
        return (int) (character.getConstitution() * 1.5);
    }

    /**
     * 计算伤害
     */
    private int calculateDamage(int attack, int defense, boolean isCritical) {
        int baseDamage = Math.max(1, attack - defense);
        if (isCritical) {
            baseDamage = (int) (baseDamage * 1.5);
        }
        // 添加随机波动 ±10%
        double fluctuation = 0.9 + random.nextDouble() * 0.2;
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
        List<String> combatLog;
    }
}
