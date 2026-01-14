package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.BreakthroughRequest;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.request.MeditationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.dto.response.MeditationResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.CultivationRecord;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CultivationRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.CultivationService;
import com.xiuxian.service.RealmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 修炼Service实现类
 */
@Service
public class CultivationServiceImpl extends ServiceImpl<CultivationRecordMapper, CultivationRecord>
        implements CultivationService {

    private static final Logger logger = LoggerFactory.getLogger(CultivationServiceImpl.class);
    private static final Logger operationLogger = LoggerFactory.getLogger("OPERATION");

    /**
     * 每次修炼消耗的体力
     */
    private static final int STAMINA_COST = 5;

    /**
     * 基础经验值范围
     */
    private static final int BASE_EXP_MIN = 50;
    private static final int BASE_EXP_MAX = 200;

    private final CharacterService characterService;
    private final RealmService realmService;
    private final Random random = new Random();

    public CultivationServiceImpl(@Lazy CharacterService characterService, RealmService realmService) {
        this.characterService = characterService;
        this.realmService = realmService;
    }

    @Override
    @Transactional
    public CultivationResponse startCultivation(CultivationRequest request) {
        Long characterId = request.getCharacterId();

        // 1. 获取角色信息
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 检查角色状态
        if (!"闲置".equals(character.getCurrentState())) {
            throw new BusinessException(2002, "角色当前状态不允许修炼，当前状态：" + character.getCurrentState());
        }

        // 3. 检查体力是否足够
        if (character.getStamina() < STAMINA_COST) {
            throw new BusinessException(2003, "体力不足，需要" + STAMINA_COST + "点体力，当前：" + character.getStamina());
        }

        // 4. 获取当前境界信息
        Realm currentRealm = realmService.getById(character.getRealmId());
        if (currentRealm == null) {
            throw new BusinessException(2001, "境界数据异常");
        }

        // 5. 记录修炼前状态
        String startRealmName = currentRealm.getRealmName();
        int startLevel = character.getRealmLevel();

        // 6. 计算获得的经验值
        int expGained = calculateExpGained(character, currentRealm);

        // 7. 更新角色状态
        character.setStamina(character.getStamina() - STAMINA_COST);
        character.setExperience(character.getExperience() + expGained);
        character.setCurrentState("修炼中");

        // 8. 检查是否升级（直接使用内存中的角色对象，避免重新从数据库读取）
        boolean leveledUp = checkAndUpgradeRealmLevelInternal(character, currentRealm);

        // 9. 保存角色状态（包含经验值和可能的升级）
        characterService.updateCharacter(character);

        // 10. 获取境界信息用于显示
        Realm endRealm = realmService.getById(character.getRealmId());
        String endRealmName = endRealm != null ? endRealm.getRealmName() : startRealmName;
        int endLevel = character.getRealmLevel();

        // 11. 恢复闲置状态
        character.setCurrentState("闲置");
        characterService.updateCharacter(character);

        // 11. 创建修炼记录
        CultivationRecord record = new CultivationRecord();
        record.setCharacterId(characterId);
        record.setStartRealm(startRealmName);
        record.setStartLevel(startLevel);
        record.setEndRealm(endRealmName);
        record.setEndLevel(endLevel);
        record.setExpGained(expGained);
        record.setStaminaConsumed(STAMINA_COST);
        record.setIsBreakthrough(0);
        record.setCultivationTime(LocalDateTime.now());
        this.save(record);

        // 12. 记录操作日志
        operationLogger.info("修炼完成: characterId={}, expGained={}, leveledUp={}",
                characterId, expGained, leveledUp);

        // 13. 构建响应
        CultivationResponse response = new CultivationResponse();
        response.setCultivationId(record.getCultivationId());
        response.setCharacterId(characterId);
        response.setPlayerName(character.getPlayerName());
        response.setStartRealm(startRealmName);
        response.setStartLevel(startLevel);
        response.setEndRealm(endRealmName);
        response.setEndLevel(endLevel);
        response.setExpGained(expGained);
        response.setStaminaConsumed(STAMINA_COST);
        response.setCurrentExperience(character.getExperience());
        response.setCurrentStamina(character.getStamina());
        response.setLeveledUp(leveledUp);
        response.setCultivationTime(record.getCultivationTime());

        // 检查是否需要突破
        boolean needsBreakthrough = (endLevel >= currentRealm.getSubLevels());
        response.setNeedsBreakthrough(needsBreakthrough);

        if (needsBreakthrough) {
            // 获取下一个境界
            Realm nextRealmObj = realmService.getNextRealm(currentRealm.getRealmLevel());
            if (nextRealmObj != null) {
                response.setNextRealm(nextRealmObj.getRealmName());
            }
        }

        if (leveledUp) {
            int pointsGained = endRealm != null ? endRealm.getLevelUpPoints() : 5;
            response.setAvailablePointsGained(pointsGained);
            response.setMessage(String.format("修炼成功！获得%d经验，境界提升至%s%d层，获得%d属性点！",
                    expGained, endRealmName, endLevel, pointsGained));
        } else {
            response.setAvailablePointsGained(0);
            String baseMsg = String.format("修炼成功！获得%d经验。", expGained);
            if (needsBreakthrough) {
                baseMsg += String.format(" 已达%s%d层，需突破至%s才能继续提升境界！",
                        endRealmName, endLevel, response.getNextRealm() != null ? response.getNextRealm() : "下一境界");
            }
            response.setMessage(baseMsg);
        }

        return response;
    }

    @Override
    @Transactional
    public boolean checkAndUpgradeRealmLevel(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            return false;
        }

        Realm currentRealm = realmService.getById(character.getRealmId());
        if (currentRealm == null) {
            return false;
        }

        boolean upgraded = false;

        // 检查是否可以升级到下一层
        while (canLevelUp(character, currentRealm)) {
            // 当前层次 < 9时，层次+1
            if (character.getRealmLevel() < currentRealm.getSubLevels()) {
                character.setRealmLevel(character.getRealmLevel() + 1);
                character.setAvailablePoints(character.getAvailablePoints() + currentRealm.getLevelUpPoints());
                upgraded = true;

                logger.info("角色层次提升: characterId={}, realmLevel={}",
                        characterId, character.getRealmLevel());
            } else {
                // 已达最高层次，需要突破到下一个境界（由突破接口处理）
                break;
            }
        }

        if (upgraded) {
            characterService.updateCharacter(character);
        }

        return upgraded;
    }

    @Override
    public Page<CultivationRecord> getCultivationRecords(Long characterId, int page, int pageSize) {
        Page<CultivationRecord> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CultivationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CultivationRecord::getCharacterId, characterId)
                .orderByDesc(CultivationRecord::getCultivationTime);
        return this.page(pageParam, wrapper);
    }

    /**
     * 计算获得的经验值
     */
    private int calculateExpGained(PlayerCharacter character, Realm realm) {
        // 基础经验值 = 随机值(50-200)
        int baseExp = BASE_EXP_MIN + random.nextInt(BASE_EXP_MAX - BASE_EXP_MIN + 1);

        // 悟性加成：每10点悟性增加1%经验
        double comprehensionBonus = 1.0 + (character.getComprehension() / 1000.0);

        // 境界加成：境界越高，基础经验越多
        double realmBonus = 1.0 + (realm.getRealmLevel() - 1) * 0.1;

        return (int) (baseExp * comprehensionBonus * realmBonus);
    }

    /**
     * 检查是否可以升级到下一层
     */
    private boolean canLevelUp(PlayerCharacter character, Realm realm) {
        // 如果已经是最高层，不能再升级（需要突破）
        if (character.getRealmLevel() >= realm.getSubLevels()) {
            return false;
        }
        // 计算下一层所需经验
        long requiredExp = calculateRequiredExpForLevel(realm, character.getRealmLevel() + 1);
        return character.getExperience() >= requiredExp;
    }

    /**
     * 计算指定层次所需的经验值
     */
    private long calculateRequiredExpForLevel(Realm realm, int level) {
        // 每层所需经验 = 境界基础经验 × 层次系数
        // 层次系数：1层=1.0, 2层=1.5, 3层=2.0, ..., 9层=5.0
        double levelMultiplier = 1.0 + (level - 1) * 0.5;
        return (long) (realm.getRequiredExp() * levelMultiplier);
    }

    @Override
    @Transactional
    public BreakthroughResponse attemptBreakthrough(BreakthroughRequest request) {
        Long characterId = request.getCharacterId();

        // 1. 获取角色信息
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 检查角色状态
        if (!"闲置".equals(character.getCurrentState())) {
            throw new BusinessException(2002, "角色当前状态不允许突破，当前状态：" + character.getCurrentState());
        }

        // 3. 获取当前境界信息
        Realm currentRealm = realmService.getById(character.getRealmId());
        if (currentRealm == null) {
            throw new BusinessException(2001, "境界数据异常");
        }

        // 4. 检查是否达到当前境界最高层次
        if (character.getRealmLevel() < currentRealm.getSubLevels()) {
            throw new BusinessException(2004, "尚未达到当前境界最高层次，无法突破。当前：" +
                    currentRealm.getRealmName() + character.getRealmLevel() + "层，需达到" +
                    currentRealm.getSubLevels() + "层");
        }

        // 5. 获取下一个境界
        Realm nextRealm = realmService.getNextRealm(currentRealm.getRealmLevel());
        if (nextRealm == null) {
            throw new BusinessException(2005, "已达最高境界，无法继续突破");
        }

        // 6. 检查经验值是否足够
        if (character.getExperience() < nextRealm.getRequiredExp()) {
            throw new BusinessException(2006, "经验值不足，突破需要" + nextRealm.getRequiredExp() +
                    "经验，当前：" + character.getExperience());
        }

        // 7. 记录突破前状态
        String previousRealmName = currentRealm.getRealmName();
        int previousLevel = character.getRealmLevel();

        // 8. 计算突破成功率
        int breakthroughRate = calculateBreakthroughRate(characterId);

        // 9. 进行突破判定
        int roll = random.nextInt(100) + 1;
        boolean success = roll <= breakthroughRate;

        // 10. 构建响应
        BreakthroughResponse response = new BreakthroughResponse();
        response.setCharacterId(characterId);
        response.setPlayerName(character.getPlayerName());
        response.setSuccess(success);
        response.setBreakthroughRate(breakthroughRate);
        response.setPreviousRealm(previousRealmName);
        response.setPreviousLevel(previousLevel);
        response.setBreakthroughTime(LocalDateTime.now());

        if (success) {
            // 突破成功
            grantBreakthroughRewards(character, nextRealm);

            response.setCurrentRealm(nextRealm.getRealmName());
            response.setCurrentLevel(1);
            response.setAttributePointsGained(nextRealm.getLevelUpPoints());
            response.setHpBonusGained(nextRealm.getHpBonus() - currentRealm.getHpBonus());
            response.setSpBonusGained(nextRealm.getSpBonus() - currentRealm.getSpBonus());
            response.setAttackBonusGained(nextRealm.getAttackBonus() - currentRealm.getAttackBonus());
            response.setDefenseBonusGained(nextRealm.getDefenseBonus() - currentRealm.getDefenseBonus());
            response.setMessage(String.format("恭喜！突破成功！境界提升至%s1层！获得%d属性点！",
                    nextRealm.getRealmName(), nextRealm.getLevelUpPoints()));

            operationLogger.info("境界突破成功: characterId={}, from={}{}层, to={}1层",
                    characterId, previousRealmName, previousLevel, nextRealm.getRealmName());
        } else {
            // 突破失败
            response.setCurrentRealm(previousRealmName);
            response.setCurrentLevel(previousLevel);
            response.setAttributePointsGained(0);
            response.setHpBonusGained(0);
            response.setSpBonusGained(0);
            response.setAttackBonusGained(0);
            response.setDefenseBonusGained(0);
            response.setMessage(String.format("突破失败！成功率%d%%，本次未能突破%s。",
                    breakthroughRate, nextRealm.getRealmName()));

            operationLogger.info("境界突破失败: characterId={}, realm={}, rate={}%",
                    characterId, previousRealmName, breakthroughRate);
        }

        // 11. 创建突破记录
        CultivationRecord record = new CultivationRecord();
        record.setCharacterId(characterId);
        record.setStartRealm(previousRealmName);
        record.setStartLevel(previousLevel);
        record.setEndRealm(success ? nextRealm.getRealmName() : previousRealmName);
        record.setEndLevel(success ? 1 : previousLevel);
        record.setExpGained(0);
        record.setStaminaConsumed(0);
        record.setIsBreakthrough(1);
        record.setBreakthroughSuccess(success ? 1 : 0);
        record.setCultivationTime(LocalDateTime.now());
        this.save(record);

        return response;
    }

    @Override
    public int calculateBreakthroughRate(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            return 0;
        }

        Realm currentRealm = realmService.getById(character.getRealmId());
        if (currentRealm == null) {
            return 0;
        }

        // 基础成功率来自境界配置
        int baseRate = currentRealm.getBreakthroughRate();

        // 悟性加成：每100点悟性增加1%成功率
        int comprehensionBonus = character.getComprehension() / 100;

        // 机缘加成：每100点机缘增加0.5%成功率
        int luckBonus = character.getLuck() / 200;

        // 计算总成功率，上限95%
        int totalRate = baseRate + comprehensionBonus + luckBonus;
        return Math.min(totalRate, 95);
    }

    /**
     * 发放突破成功奖励
     */
    private void grantBreakthroughRewards(PlayerCharacter character, Realm newRealm) {
        // 更新境界
        character.setRealmId(newRealm.getId());
        character.setRealmLevel(1);

        // 增加属性点
        character.setAvailablePoints(character.getAvailablePoints() + newRealm.getLevelUpPoints());

        // 增加血量上限
        character.setHealthMax(character.getHealthMax() + newRealm.getHpBonus());
        character.setHealth(character.getHealthMax());

        // 增加灵力上限
        character.setSpiritualPowerMax(character.getSpiritualPowerMax() + newRealm.getSpBonus());
        character.setSpiritualPower(character.getSpiritualPowerMax());

        // 增加体力上限
        character.setStaminaMax(character.getStaminaMax() + newRealm.getStaminaBonus());
        character.setStamina(character.getStaminaMax());

        // 保存角色
        characterService.updateCharacter(character);

        logger.info("突破奖励发放: characterId={}, newRealm={}, pointsGained={}",
                character.getCharacterId(), newRealm.getRealmName(), newRealm.getLevelUpPoints());
    }

    @Override
    @Transactional
    public MeditationResponse meditation(MeditationRequest request) {
        Long characterId = request.getCharacterId();

        // 1. 获取角色信息
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 检查角色状态
        if (!"闲置".equals(character.getCurrentState())) {
            throw new BusinessException(2002, "角色当前状态不允许打坐，当前状态：" + character.getCurrentState());
        }

        // 3. 计算恢复量
        // 体力恢复：恢复最大体力的30%
        int staminaToRecover = (int) (character.getStaminaMax() * 0.3);
        int actualStaminaRecovered = Math.min(staminaToRecover, character.getStaminaMax() - character.getStamina());

        // 灵力恢复：恢复最大灵力的30%
        int spiritualPowerToRecover = (int) (character.getSpiritualPowerMax() * 0.3);
        int actualSpiritualPowerRecovered = Math.min(spiritualPowerToRecover,
                character.getSpiritualPowerMax() - character.getSpiritualPower());

        // 4. 更新角色状态
        character.setStamina(character.getStamina() + actualStaminaRecovered);
        character.setSpiritualPower(character.getSpiritualPower() + actualSpiritualPowerRecovered);
        characterService.updateCharacter(character);

        // 5. 记录操作日志
        operationLogger.info("打坐完成: characterId={}, staminaRecovered={}, spiritualPowerRecovered={}",
                characterId, actualStaminaRecovered, actualSpiritualPowerRecovered);

        // 6. 构建响应
        MeditationResponse response = new MeditationResponse();
        response.setCharacterId(characterId);
        response.setPlayerName(character.getPlayerName());
        response.setStaminaRecovered(actualStaminaRecovered);
        response.setCurrentStamina(character.getStamina());
        response.setMaxStamina(character.getStaminaMax());
        response.setSpiritualPowerRecovered(actualSpiritualPowerRecovered);
        response.setCurrentSpiritualPower(character.getSpiritualPower());
        response.setMaxSpiritualPower(character.getSpiritualPowerMax());
        response.setMessage(String.format("打坐完成！恢复%d点体力，%d点灵力。",
                actualStaminaRecovered, actualSpiritualPowerRecovered));

        return response;
    }

    /**
     * 检查并升级境界层次（内部方法，直接使用角色对象）
     * @param character 角色对象
     * @param realm 境界对象
     * @return 是否升级
     */
    private boolean checkAndUpgradeRealmLevelInternal(PlayerCharacter character, Realm realm) {
        if (character == null || realm == null) {
            return false;
        }

        boolean upgraded = false;

        // 检查是否可以升级到下一层（使用内存中的数据，避免重复读取数据库）
        while (canLevelUp(character, realm)) {
            // 当前层次 < 9时，层次+1
            if (character.getRealmLevel() < realm.getSubLevels()) {
                character.setRealmLevel(character.getRealmLevel() + 1);
                character.setAvailablePoints(character.getAvailablePoints() + realm.getLevelUpPoints());
                upgraded = true;

                logger.info("角色层次提升: characterId={}, realmLevel={}",
                        character.getCharacterId(), character.getRealmLevel());
            } else {
                // 已达最高层次，需要突破到下一个境界（由突破接口处理）
                break;
            }
        }

        // 只在所有升级完成后保存一次
        // 注意：不在这里保存，因为调用者会在后续保存
        return upgraded;
    }
}
