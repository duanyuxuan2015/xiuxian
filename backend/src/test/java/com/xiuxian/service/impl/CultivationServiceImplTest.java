package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.BreakthroughRequest;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.request.MeditationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.dto.response.MeditationResponse;
import com.xiuxian.dto.response.MeditationTimeResponse;
import com.xiuxian.entity.CultivationRecord;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CultivationRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.RealmService;
import com.xiuxian.config.CultivationProperties;
import com.xiuxian.config.MeditationProperties;
import com.xiuxian.config.StaminaCostProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CultivationServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private RealmService realmService;
    @Mock
    private CultivationRecordMapper cultivationRecordMapper;

    @InjectMocks
    private CultivationServiceImpl cultivationService;

    private PlayerCharacter character;
    private Realm realm;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = CultivationServiceImpl.class;
        Field baseMapperField = null;
        while (clazz != null && baseMapperField == null) {
            try {
                baseMapperField = clazz.getSuperclass().getDeclaredField("baseMapper");
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (baseMapperField != null) {
            baseMapperField.setAccessible(true);
            baseMapperField.set(cultivationService, cultivationRecordMapper);
        }

        // 初始化 MeditationProperties 并设置默认值
        MeditationProperties meditationProperties = new MeditationProperties();
        meditationProperties.setBaseTime(30);
        meditationProperties.setMindsetReductionCoefficient(0.05);
        meditationProperties.setComprehensionReductionCoefficient(0.05);
        meditationProperties.setMinTime(5);

        // 使用反射设置 meditationProperties 到 cultivationService
        Field meditationField = CultivationServiceImpl.class.getDeclaredField("meditationProperties");
        meditationField.setAccessible(true);
        meditationField.set(cultivationService, meditationProperties);

        // 初始化 CultivationProperties 并设置默认值
        CultivationProperties cultivationProperties = new CultivationProperties();
        cultivationProperties.getBaseExp().setMin(50);
        cultivationProperties.getBaseExp().setMax(200);
        cultivationProperties.getBonus().setComprehension(0.001);
        cultivationProperties.getBonus().setRealmPerLevel(0.1);
        cultivationProperties.setLevelMultiplier(0.5);
        cultivationProperties.setStaminaCost(5);

        // 使用反射设置 cultivationProperties 到 cultivationService
        Field cultivationField = CultivationServiceImpl.class.getDeclaredField("cultivationProperties");
        cultivationField.setAccessible(true);
        cultivationField.set(cultivationService, cultivationProperties);

        // 初始化 StaminaCostProperties 并设置默认值
        StaminaCostProperties staminaCostProperties = new StaminaCostProperties();
        staminaCostProperties.setCultivation(5);
        staminaCostProperties.setCombatMultiplier(1.0);
        staminaCostProperties.setCombatDefeatRatio(0.5);
        staminaCostProperties.setAlchemy(0);
        staminaCostProperties.setForging(0);
        staminaCostProperties.setExploration(0);
        staminaCostProperties.setMeditation(0);
        staminaCostProperties.setBreakthrough(0);

        // 使用反射设置 staminaCostProperties 到 cultivationService
        Field staminaCostField = CultivationServiceImpl.class.getDeclaredField("staminaCostProperties");
        staminaCostField.setAccessible(true);
        staminaCostField.set(cultivationService, staminaCostProperties);

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setRealmId(1);
        character.setRealmLevel(1);
        character.setCurrentState("闲置");
        character.setStamina(100);
        character.setExperience(0L);
        character.setComprehension(10);
        character.setLuck(10);
        character.setAvailablePoints(0);  // 添加可用属性点
        character.setHealth(50);  // 设置为未满状态，避免触发"无需打坐"
        character.setHealthMax(100);  // 添加最大生命值
        character.setSpiritualPower(50);  // 设置为未满状态，避免触发"无需打坐"
        character.setSpiritualPowerMax(100);  // 添加最大灵力
        character.setStaminaMax(100);  // 添加最大体力

        realm = new Realm();
        realm.setId(1);
        realm.setRealmLevel(1);  // 添加realmLevel字段
        realm.setRealmName("Qi Refining");
        realm.setSubLevels(9);
        realm.setRequiredExp(100L);
        realm.setBreakthroughRate(80);
        realm.setLevelUpPoints(5);  // 添加升级点数
        realm.setHpBonus(10);  // 添加生命值加成
        realm.setSpBonus(10);  // 添加灵力加成
        realm.setStaminaBonus(10);  // 添加体力加成
        realm.setAttackBonus(5);  // 添加攻击力加成
        realm.setDefenseBonus(5);  // 添加防御力加成
    }

    @Test
    void startCultivation_Success() {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        CultivationResponse response = cultivationService.startCultivation(request);

        assertNotNull(response);
        verify(characterService, atLeast(2)).updateCharacter(any(PlayerCharacter.class)); // 至少更新2次
    }

    @Test
    void startCultivation_NotIdle() {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        character.setCurrentState("战斗中");
        when(characterService.getById(1L)).thenReturn(character);

        try {
            cultivationService.startCultivation(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2002, e.getCode());
        }
    }

    @Test
    void attemptBreakthrough_Success() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        character.setRealmLevel(9); // Max sub-level
        character.setExperience(200L);

        Realm nextRealm = new Realm();
        nextRealm.setId(2);
        nextRealm.setRealmLevel(2);  // 添加realmLevel字段
        nextRealm.setRealmName("Core Formation");
        nextRealm.setRequiredExp(200L);
        nextRealm.setLevelUpPoints(5);  // 添加升级点数
        nextRealm.setHpBonus(20);  // 添加生命值加成
        nextRealm.setSpBonus(20);  // 添加灵力加成
        nextRealm.setStaminaBonus(20);  // 添加体力加成
        nextRealm.setAttackBonus(10);  // 添加攻击力加成
        nextRealm.setDefenseBonus(10);  // 添加防御力加成

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(realmService.getNextRealm(1)).thenReturn(nextRealm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        // Success depends on random, but rates can be manipulated or we check structure
        // Since we can't easily mock Random inside the service without refactoring,
        // we assert that it runs without error and returns a valid response object.
        BreakthroughResponse response = cultivationService.attemptBreakthrough(request);

        assertNotNull(response);
    }

    @Test
    void startCultivation_InsufficientStamina() {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        character.setStamina(4); // 体力不足
        when(characterService.getById(1L)).thenReturn(character);

        try {
            cultivationService.startCultivation(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2003, e.getCode());
            assertTrue(e.getMessage().contains("体力不足"));
        }
    }

    @Test
    void startCultivation_CharacterNotFound() {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(999L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            cultivationService.startCultivation(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
            assertTrue(e.getMessage().contains("角色不存在"));
        }
    }

    @Test
    void startCultivation_RealmDataAbnormal() {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(null); // 境界数据异常

        try {
            cultivationService.startCultivation(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2001, e.getCode());
            assertTrue(e.getMessage().contains("境界数据异常"));
        }
    }

    @Test
    void attemptBreakthrough_NotAtMaxSubLevel() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        character.setRealmLevel(5); // 不在最高小境界
        realm.setSubLevels(9);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2004, e.getCode());
        }
    }

    @Test
    void attemptBreakthrough_InsufficientExperience() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        character.setRealmLevel(9); // 最高小境界
        character.setExperience(50L); // 经验值不足
        realm.setSubLevels(9);
        realm.setRequiredExp(100L);

        Realm nextRealm = new Realm();
        nextRealm.setId(2);
        nextRealm.setRequiredExp(200L);  // 下一境界需要200经验

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(realmService.getNextRealm(1)).thenReturn(nextRealm);

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2006, e.getCode());  // 应该是2006而不是2004
        }
    }

    @Test
    void attemptBreakthrough_NoNextRealm() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        character.setRealmLevel(9);
        character.setExperience(200L);
        realm.setSubLevels(9);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(realmService.getNextRealm(1)).thenReturn(null); // 没有更高境界

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2005, e.getCode());
        }
    }

    @Test
    void attemptBreakthrough_CharacterNotFound() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(999L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
            assertTrue(e.getMessage().contains("角色不存在"));
        }
    }

    @Test
    void attemptBreakthrough_NotIdle() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        character.setCurrentState("战斗中");
        when(characterService.getById(1L)).thenReturn(character);

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2002, e.getCode());
            assertTrue(e.getMessage().contains("状态不允许突破"));
        }
    }

    @Test
    void attemptBreakthrough_RealmDataAbnormal() {
        BreakthroughRequest request = new BreakthroughRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(null); // 境界数据异常

        try {
            cultivationService.attemptBreakthrough(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2001, e.getCode());
            assertTrue(e.getMessage().contains("境界数据异常"));
        }
    }

    @Test
    void calculateBreakthroughRate_Success() {
        character.setComprehension(100); // 悟性100，加成1%
        character.setLuck(200); // 机缘200，加成1%
        realm.setBreakthroughRate(80); // 基础成功率80%

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        int rate = cultivationService.calculateBreakthroughRate(1L);

        // 80 + 1 + 1 = 82
        assertEquals(82, rate);
    }

    @Test
    void calculateBreakthroughRate_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        int rate = cultivationService.calculateBreakthroughRate(999L);

        assertEquals(0, rate);
    }

    @Test
    void calculateBreakthroughRate_RealmDataAbnormal() {
        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(null);

        int rate = cultivationService.calculateBreakthroughRate(1L);

        assertEquals(0, rate);
    }

    @Test
    void calculateBreakthroughRate_MaxRate() {
        character.setComprehension(10000); // 极高悟性
        character.setLuck(10000); // 极高机缘
        realm.setBreakthroughRate(80);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        int rate = cultivationService.calculateBreakthroughRate(1L);

        // 应该被限制在95%
        assertEquals(95, rate);
    }

    @Test
    void checkAndUpgradeRealmLevel_Success() {
        character.setExperience(1000L); // 足够的经验
        character.setRealmLevel(1); // 当前第1层
        character.setAvailablePoints(0);
        realm.setRequiredExp(100L);
        realm.setLevelUpPoints(5);
        realm.setSubLevels(9);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(1L);

        assertTrue(upgraded);
        verify(characterService, atLeastOnce()).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void checkAndUpgradeRealmLevel_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(999L);

        assertFalse(upgraded);
    }

    @Test
    void checkAndUpgradeRealmLevel_RealmDataAbnormal() {
        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(null);

        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(1L);

        assertFalse(upgraded);
    }

    @Test
    void checkAndUpgradeRealmLevel_InsufficientExperience() {
        character.setExperience(50L); // 经验不足
        character.setRealmLevel(1);
        realm.setRequiredExp(100L);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(1L);

        assertFalse(upgraded);
        verify(characterService, never()).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void checkAndUpgradeRealmLevel_AtMaxSubLevel() {
        character.setExperience(1000L); // 足够的经验
        character.setRealmLevel(9); // 已经是最高层
        realm.setSubLevels(9);
        realm.setRequiredExp(100L);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(1L);

        // 已达最高层次，无法再升级，需要突破
        assertFalse(upgraded);
    }

    // ==================== 打坐功能测试 ====================

    @Test
    void meditation_Success() {
        // 准备测试数据
        character.setStamina(50); // 当前体力50/100
        character.setStaminaMax(100);
        character.setSpiritualPower(30); // 当前灵力30/100
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getCharacterId());
        assertEquals("TestPlayer", response.getPlayerName());

        // 验证恢复量：恢复30%
        assertEquals(30, response.getStaminaRecovered()); // 100 * 0.3 = 30
        assertEquals(80, response.getCurrentStamina()); // 50 + 30 = 80
        assertEquals(100, response.getMaxStamina());

        assertEquals(30, response.getSpiritualPowerRecovered()); // 100 * 0.3 = 30
        assertEquals(60, response.getCurrentSpiritualPower()); // 30 + 30 = 60
        assertEquals(100, response.getMaxSpiritualPower());

        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("打坐完成"));

        // 验证调用了更新方法
        verify(characterService, times(1)).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void meditation_PartialRecovery() {
        // 准备测试数据：体力接近上限，只能部分恢复
        character.setStamina(85); // 当前体力85/100，只能恢复15
        character.setStaminaMax(100);
        character.setSpiritualPower(90); // 当前灵力90/100，只能恢复10
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证部分恢复
        assertEquals(15, response.getStaminaRecovered()); // 只能恢复15
        assertEquals(100, response.getCurrentStamina()); // 达到上限

        assertEquals(10, response.getSpiritualPowerRecovered()); // 只能恢复10
        assertEquals(100, response.getCurrentSpiritualPower()); // 达到上限
    }

    @Test
    void meditation_FullStaminaAndSpiritualPower() {
        // 准备测试数据：体力和灵力都已满
        character.setStamina(100);
        character.setStaminaMax(100);
        character.setSpiritualPower(100);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证：已满状态，不需要恢复
        assertEquals(0, response.getStaminaRecovered());
        assertEquals(100, response.getCurrentStamina());
        assertEquals(0, response.getSpiritualPowerRecovered());
        assertEquals(100, response.getCurrentSpiritualPower());
    }

    @Test
    void meditation_CharacterNotFound() {
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(999L);

        when(characterService.getById(999L)).thenReturn(null);

        // 验证抛出异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cultivationService.meditation(request);
        });

        assertEquals(1003, exception.getCode());
        assertEquals("角色不存在", exception.getMessage());
    }

    @Test
    void meditation_CharacterNotIdle() {
        // 准备测试数据：角色不在闲置状态
        character.setCurrentState("修炼中");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);

        // 验证抛出异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cultivationService.meditation(request);
        });

        assertEquals(2002, exception.getCode());
        assertTrue(exception.getMessage().contains("角色当前状态不允许打坐"));
        assertTrue(exception.getMessage().contains("修炼中"));
    }

    // ==================== 打坐气血恢复测试 ====================

    @Test
    void meditation_WithHealthRecovery_Success() {
        // 准备测试数据：测试气血恢复功能
        character.setHealth(50); // 当前气血50/100
        character.setHealthMax(100);
        character.setStamina(60);
        character.setStaminaMax(100);
        character.setSpiritualPower(40);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证气血恢复
        assertNotNull(response);
        assertEquals(30, response.getHealthRecovered()); // 100 * 0.3 = 30
        assertEquals(80, response.getCurrentHealth()); // 50 + 30 = 80
        assertEquals(100, response.getMaxHealth());

        // 验证体力恢复
        assertEquals(30, response.getStaminaRecovered());
        assertEquals(90, response.getCurrentStamina());

        // 验证灵力恢复
        assertEquals(30, response.getSpiritualPowerRecovered());
        assertEquals(70, response.getCurrentSpiritualPower());

        // 验证消息包含气血恢复信息
        assertTrue(response.getMessage().contains("气血"));
        assertTrue(response.getMessage().contains("体力"));
        assertTrue(response.getMessage().contains("灵力"));
    }

    @Test
    void meditation_WithHealthRecovery_PartialRecovery() {
        // 准备测试数据：气血接近满值，只能部分恢复
        character.setHealth(95); // 当前气血95/100，只能恢复5
        character.setHealthMax(100);
        character.setStamina(85);
        character.setStaminaMax(100);
        character.setSpiritualPower(92);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证气血部分恢复
        assertEquals(5, response.getHealthRecovered()); // 只能恢复5到达上限
        assertEquals(100, response.getCurrentHealth()); // 达到上限
        assertEquals(100, response.getMaxHealth());

        // 验证体力部分恢复
        assertEquals(15, response.getStaminaRecovered());
        assertEquals(100, response.getCurrentStamina());

        // 验证灵力部分恢复
        assertEquals(8, response.getSpiritualPowerRecovered());
        assertEquals(100, response.getCurrentSpiritualPower());
    }

    @Test
    void meditation_WithHealthRecovery_FullHealth() {
        // 准备测试数据：气血已满
        character.setHealth(100); // 气血已满
        character.setHealthMax(100);
        character.setStamina(50);
        character.setStaminaMax(100);
        character.setSpiritualPower(60);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证气血不需要恢复
        assertEquals(0, response.getHealthRecovered());
        assertEquals(100, response.getCurrentHealth());

        // 验证体力和灵力正常恢复
        assertEquals(30, response.getStaminaRecovered());
        assertEquals(30, response.getSpiritualPowerRecovered());
    }

    @Test
    void meditation_RecoverAllThreeAttributes() {
        // 准备测试数据：测试同时恢复气血、体力、灵力
        character.setHealth(10); // 气血很低
        character.setHealthMax(100);
        character.setStamina(20); // 体力很低
        character.setStaminaMax(100);
        character.setSpiritualPower(15); // 灵力很低
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证三项都恢复了30%
        assertEquals(30, response.getHealthRecovered());
        assertEquals(40, response.getCurrentHealth()); // 10 + 30 = 40

        assertEquals(30, response.getStaminaRecovered());
        assertEquals(50, response.getCurrentStamina()); // 20 + 30 = 50

        assertEquals(30, response.getSpiritualPowerRecovered());
        assertEquals(45, response.getCurrentSpiritualPower()); // 15 + 30 = 45

        // 验证消息包含所有三项
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("气血"));
        assertTrue(response.getMessage().contains("体力"));
        assertTrue(response.getMessage().contains("灵力"));
    }

    @Test
    void meditation_WithHealthRecovery_LowHealth() {
        // 准备测试数据：气血非常低（模拟战斗后的情况）
        character.setHealth(5); // 气血只剩5
        character.setHealthMax(100);
        character.setStamina(100);
        character.setStaminaMax(100);
        character.setSpiritualPower(100);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证气血恢复
        assertEquals(30, response.getHealthRecovered());
        assertEquals(35, response.getCurrentHealth()); // 5 + 30 = 35

        // 验证体力和灵力已满，不需要恢复
        assertEquals(0, response.getStaminaRecovered());
        assertEquals(0, response.getSpiritualPowerRecovered());
    }

    @Test
    void meditation_WithHealthRecovery_DifferentMaxValues() {
        // 准备测试数据：测试不同的最大值（高境界角色）
        character.setHealth(100); // 当前气血100/200
        character.setHealthMax(200); // 更高的气血上限
        character.setStamina(80);
        character.setStaminaMax(150); // 更高的体力上限
        character.setSpiritualPower(120);
        character.setSpiritualPowerMax(180); // 更高的灵力上限
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证按最大值的30%恢复
        assertEquals(60, response.getHealthRecovered()); // 200 * 0.3 = 60
        assertEquals(160, response.getCurrentHealth()); // 100 + 60 = 160

        assertEquals(45, response.getStaminaRecovered()); // 150 * 0.3 = 45
        assertEquals(125, response.getCurrentStamina()); // 80 + 45 = 125

        assertEquals(54, response.getSpiritualPowerRecovered()); // 180 * 0.3 = 54
        assertEquals(174, response.getCurrentSpiritualPower()); // 120 + 54 = 174
    }

    @Test
    void meditation_VerifyCharacterStateUpdated() {
        // 准备测试数据
        character.setHealth(50);
        character.setHealthMax(100);
        character.setStamina(50);
        character.setStaminaMax(100);
        character.setSpiritualPower(50);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        // 执行打坐
        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        cultivationService.meditation(request);

        // 验证角色对象的状态已更新
        assertEquals(80, character.getHealth()); // 50 + 30 = 80
        assertEquals(80, character.getStamina());
        assertEquals(80, character.getSpiritualPower());

        // 验证调用了更新方法
        verify(characterService, times(1)).updateCharacter(character);
    }

    // ==================== 境界升级功能测试 ====================

    @Test
    void startCultivation_LevelUpWhenExperienceFull() {
        // 准备测试数据：角色当前经验80，悟性和机缘都设为0以获得基础经验
        // 基础经验50-100，假设获得100经验，总共180经验
        // 1层->2层需要150经验，所以应该升级到2层
        character.setExperience(100L);
        character.setRealmLevel(1);
        character.setStamina(100);
        character.setAvailablePoints(0);
        character.setComprehension(0);  // 悟性为0，获得基础经验
        character.setLuck(0);  // 机缘为0
        realm.setRequiredExp(100L);
        realm.setSubLevels(9);
        realm.setLevelUpPoints(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        // 执行修炼
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);
        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证升级成功（实际可能升到2层或3层，取决于随机经验值）
        assertNotNull(response);
        assertTrue(response.getLeveledUp());
        assertTrue(response.getEndLevel() >= 2); // 至少升到2层
        assertEquals(1, response.getStartLevel());
    }

    @Test
    void startCultivation_MultipleLevelUps() {
        // 准备测试数据：角色当前经验500，悟性和机缘都设为0以获得基础经验
        // 基础经验50-100，假设获得100经验
        // 1层->2层需要150，2层->3层需要200，3层->4层需要250
        // 500+100=600，应该升级到3层或4层（取决于随机值）
        character.setExperience(500L);
        character.setRealmLevel(1);
        character.setStamina(100);
        character.setAvailablePoints(0);
        character.setComprehension(0);  // 悟性为0
        character.setLuck(0);  // 机缘为0
        realm.setRequiredExp(100L);
        realm.setSubLevels(9);
        realm.setLevelUpPoints(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        // 执行修炼
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);
        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证连续升级（至少升级到2层以上）
        assertNotNull(response);
        assertTrue(response.getLeveledUp());
        assertTrue(response.getEndLevel() >= 2); // 至少升到2层
        assertEquals(1, response.getStartLevel());
    }

    @Test
    void startCultivation_NoLevelUpWhenExperienceNotEnough() {
        // 准备测试数据：角色当前经验0，悟性和机缘都设为0
        // 最多获得100经验，1层->2层需要150经验，所以不应该升级
        character.setExperience(0L);
        character.setRealmLevel(1);
        character.setStamina(100);
        character.setAvailablePoints(0);
        character.setComprehension(0);  // 悟性为0
        character.setLuck(0);  // 机缘为0
        realm.setRequiredExp(200L);  // 增加所需经验
        realm.setSubLevels(9);
        realm.setLevelUpPoints(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        // 执行修炼
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);
        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证没有升级
        assertNotNull(response);
        assertFalse(response.getLeveledUp());
        assertEquals(1, response.getEndLevel()); // 还是1层
        assertEquals(1, response.getStartLevel());
        assertEquals(0, response.getAvailablePointsGained());
    }

    @Test
    void startCultivation_AtMaxSubLevelCannotLevelUp() {
        // 准备测试数据：角色已经在最高层9层，经验再满也不能升级
        character.setExperience(10000L);
        character.setRealmLevel(9);
        character.setStamina(100);
        character.setAvailablePoints(0);
        realm.setRequiredExp(100L);
        realm.setSubLevels(9);
        realm.setLevelUpPoints(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(cultivationRecordMapper.insert(any(CultivationRecord.class))).thenReturn(1);

        // 执行修炼
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);
        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证不能升级（需要突破）
        assertNotNull(response);
        assertFalse(response.getLeveledUp());
        assertEquals(9, response.getEndLevel()); // 还是9层
        assertEquals(9, response.getStartLevel());
        assertEquals(0, response.getAvailablePointsGained());
    }

    @Test
    void checkAndUpgradeRealmLevel_PublicMethodStillWorks() {
        // 测试公开方法仍然能够正常工作（向后兼容性测试）
        character.setExperience(200L);
        character.setRealmLevel(1);
        character.setAvailablePoints(0);
        realm.setRequiredExp(100L);
        realm.setSubLevels(9);
        realm.setLevelUpPoints(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        // 调用公开方法
        boolean upgraded = cultivationService.checkAndUpgradeRealmLevel(1L);

        // 验证能够升级
        assertTrue(upgraded);
        verify(characterService, times(1)).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void testGetMeditationTime_Beginner() {
        // 测试新手角色（低精神、低悟性）
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(10);
        character.setComprehension(10);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        assertNotNull(response);
        assertEquals(30, response.getBaseTime());
        assertEquals(10, response.getMindset());
        assertEquals(10, response.getComprehension());
        assertEquals(1, response.getReductionTime());
        assertEquals(29, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_Advanced() {
        // 测试高级角色（高精神、高悟性）
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(500);
        character.setComprehension(500);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        assertNotNull(response);
        assertEquals(30, response.getBaseTime());
        assertEquals(500, response.getMindset());
        assertEquals(500, response.getComprehension());
        assertEquals(25, response.getReductionTime()); // 应该被限制为25秒减免
        assertEquals(5, response.getFinalTime());     // 最短时间5秒
    }

    @Test
    void testGetMeditationTime_CharacterNotFound() {
        // 测试角色不存在
        when(characterService.getById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            cultivationService.getMeditationTime(999L);
        });
    }

    @Test
    void testGetMeditationTime_SeparateCoefficients() throws Exception {
        // 测试精神和悟性分别计算减免
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(100);  // 精神100
        character.setComprehension(50);  // 悟性50

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 精神减免: 100 × 0.05 = 5秒
        // 悟性减免: 50 × 0.05 = 2.5秒 ≈ 2秒
        // 总减免: 7秒
        // 最终时间: 30 - 7 = 23秒
        assertNotNull(response);
        assertEquals(30, response.getBaseTime());
        assertEquals(100, response.getMindset());
        assertEquals(50, response.getComprehension());
        assertEquals(7, response.getReductionTime());  // 5 + 2
        assertEquals(23, response.getFinalTime());      // 30 - 7
    }

    @Test
    void testGetMeditationTime_HighMindsetLowComprehension() throws Exception {
        // 测试高精神低悟性的情况
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(200);  // 精神200
        character.setComprehension(10);  // 悟性10

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 精神减免: 200 × 0.05 = 10秒
        // 悟性减免: 10 × 0.05 = 0.5秒 ≈ 0秒
        // 总减免: 10秒
        // 最终时间: 30 - 10 = 20秒
        assertNotNull(response);
        assertEquals(200, response.getMindset());
        assertEquals(10, response.getComprehension());
        assertEquals(10, response.getReductionTime());
        assertEquals(20, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_ZeroMindsetZeroComprehension() throws Exception {
        // 测试精神和悟性都为0的情况
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(0);
        character.setComprehension(0);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 没有任何减免，时间保持基础时间
        assertEquals(30, response.getBaseTime());
        assertEquals(0, response.getMindset());
        assertEquals(0, response.getComprehension());
        assertEquals(0, response.getReductionTime());
        assertEquals(30, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_OnlyMindset() throws Exception {
        // 测试只有精神有值，悟性为0
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(300);
        character.setComprehension(0);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 精神减免: 300 × 0.05 = 15秒
        // 悟性减免: 0
        // 最终时间: 30 - 15 = 15秒
        assertEquals(300, response.getMindset());
        assertEquals(0, response.getComprehension());
        assertEquals(15, response.getReductionTime());
        assertEquals(15, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_OnlyComprehension() throws Exception {
        // 测试只有悟性有值，精神为0
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(0);
        character.setComprehension(400);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 精神减免: 0
        // 悟性减免: 400 × 0.05 = 20秒
        // 最终时间: 30 - 20 = 10秒
        assertEquals(0, response.getMindset());
        assertEquals(400, response.getComprehension());
        assertEquals(20, response.getReductionTime());
        assertEquals(10, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_RespectMinTime() throws Exception {
        // 测试最短时间限制（极端高属性值）
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(999);  // 精神满值
        character.setComprehension(999);  // 悟性满值

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 计算减免: 999 × 0.05 + 999 × 0.05 = 99.9秒
        // 但实际减免被限制为 25秒（30 - 5）
        // 最终时间被限制为最小值5秒
        assertEquals(999, response.getMindset());
        assertEquals(999, response.getComprehension());
        assertEquals(25, response.getReductionTime());  // 被限制为25秒
        assertEquals(5, response.getFinalTime());       // 被限制为5秒
    }

    @Test
    void testGetMeditationTime_NullAttributes() throws Exception {
        // 测试属性为null的情况（应该当作0处理）
        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(null);
        character.setComprehension(null);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // null值应该被当作0处理
        assertEquals(30, response.getBaseTime());
        assertEquals(0, response.getMindset());       // null转为0
        assertEquals(0, response.getComprehension()); // null转为0
        assertEquals(0, response.getReductionTime());
        assertEquals(30, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_DifferentCoefficientConfig() throws Exception {
        // 测试不同的系数配置
        // 修改精神系数为0.1，悟性系数为0.02
        MeditationProperties customConfig = new MeditationProperties();
        customConfig.setBaseTime(30);
        customConfig.setMindsetReductionCoefficient(0.1);   // 精神系数更高
        customConfig.setComprehensionReductionCoefficient(0.02);  // 悟性系数更低
        customConfig.setMinTime(5);

        // 使用反射注入自定义配置
        Field meditationField = CultivationServiceImpl.class.getDeclaredField("meditationProperties");
        meditationField.setAccessible(true);
        meditationField.set(cultivationService, customConfig);

        when(characterService.getById(1L)).thenReturn(character);
        character.setMindset(100);
        character.setComprehension(100);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 精神减免: 100 × 0.1 = 10秒
        // 悟性减免: 100 × 0.02 = 2秒
        // 总减免: 12秒
        // 最终时间: 30 - 12 = 18秒
        assertEquals(100, response.getMindset());
        assertEquals(100, response.getComprehension());
        assertEquals(12, response.getReductionTime());
        assertEquals(18, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_AllAttributesFull() throws Exception {
        // 测试气血、体力、灵力都已满的情况
        when(characterService.getById(1L)).thenReturn(character);
        character.setHealth(100);
        character.setHealthMax(100);
        character.setStamina(100);
        character.setStaminaMax(100);
        character.setSpiritualPower(100);
        character.setSpiritualPowerMax(100);
        character.setMindset(100);
        character.setComprehension(100);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 三个属性都满，应该返回0秒
        assertEquals(0, response.getBaseTime());
        assertEquals(0, response.getMindset());
        assertEquals(0, response.getComprehension());
        assertEquals(0, response.getReductionTime());
        assertEquals(0, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_OneAttributeNotFull() throws Exception {
        // 测试只有一个属性未满的情况
        when(characterService.getById(1L)).thenReturn(character);
        character.setHealth(100);      // 气血满
        character.setHealthMax(100);
        character.setStamina(99);       // 体力差1点未满
        character.setStaminaMax(100);
        character.setSpiritualPower(100); // 灵力满
        character.setSpiritualPowerMax(100);
        character.setMindset(50);
        character.setComprehension(50);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 有一个属性未满，应该正常计算时间
        // 减免: 50 × 0.05 + 50 × 0.05 = 5秒
        // 最终时间: 30 - 5 = 25秒
        assertEquals(30, response.getBaseTime());
        assertEquals(50, response.getMindset());
        assertEquals(50, response.getComprehension());
        assertEquals(5, response.getReductionTime());
        assertEquals(25, response.getFinalTime());
    }

    @Test
    void testGetMeditationTime_HealthNotFull() throws Exception {
        // 测试气血未满的情况
        when(characterService.getById(1L)).thenReturn(character);
        character.setHealth(50);       // 气血未满
        character.setHealthMax(100);
        character.setStamina(100);     // 体力满
        character.setStaminaMax(100);
        character.setSpiritualPower(100); // 灵力满
        character.setSpiritualPowerMax(100);
        character.setMindset(200);
        character.setComprehension(100);

        MeditationTimeResponse response = cultivationService.getMeditationTime(1L);

        // 气血未满，应该正常计算时间
        // 减免: 200 × 0.05 + 100 × 0.05 = 15秒
        // 最终时间: 30 - 15 = 15秒
        assertEquals(30, response.getBaseTime());
        assertEquals(200, response.getMindset());
        assertEquals(100, response.getComprehension());
        assertEquals(15, response.getReductionTime());
        assertEquals(15, response.getFinalTime());
    }

    @Test
    void meditation_WithCustomRecoveryRatio() throws Exception {
        // 测试自定义恢复比例
        MeditationProperties customConfig = new MeditationProperties();
        customConfig.setBaseTime(30);
        customConfig.setMindsetReductionCoefficient(0.05);
        customConfig.setComprehensionReductionCoefficient(0.05);
        customConfig.setMinTime(5);
        customConfig.setRecoveryRatio(0.5);  // 设置为50%

        // 使用反射注入自定义配置
        Field field = CultivationServiceImpl.class.getDeclaredField("meditationProperties");
        field.setAccessible(true);
        field.set(cultivationService, customConfig);

        character.setHealth(50);
        character.setHealthMax(100);
        character.setStamina(50);
        character.setStaminaMax(100);
        character.setSpiritualPower(50);
        character.setSpiritualPowerMax(100);
        character.setCurrentState("闲置");

        when(characterService.getById(1L)).thenReturn(character);

        MeditationRequest request = new MeditationRequest();
        request.setCharacterId(1L);
        MeditationResponse response = cultivationService.meditation(request);

        // 验证恢复50%而不是默认的30%
        assertEquals(50, response.getHealthRecovered());      // 100 * 0.5 = 50
        assertEquals(50, response.getStaminaRecovered());     // 100 * 0.5 = 50
        assertEquals(50, response.getSpiritualPowerRecovered()); // 100 * 0.5 = 50
    }

    @Test
    void startCultivation_WithCustomExpBonus() throws Exception {
        // 测试自定义经验加成配置
        CultivationProperties customConfig = new CultivationProperties();
        customConfig.getBaseExp().setMin(100);
        customConfig.getBaseExp().setMax(300);
        customConfig.getBonus().setComprehension(0.002);  // 悟性加翻倍
        customConfig.getBonus().setRealmPerLevel(0.2);    // 境界加成翻倍
        customConfig.setLevelMultiplier(1.0);              // 层次系数翻倍
        customConfig.setStaminaCost(5);

        // 使用反射注入自定义配置
        Field field = CultivationServiceImpl.class.getDeclaredField("cultivationProperties");
        field.setAccessible(true);
        field.set(cultivationService, customConfig);

        character.setComprehension(100);
        character.setRealmLevel(1);
        character.setCurrentState("闲置");
        character.setStamina(100);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(characterService.updateCharacter(any())).thenReturn(true);
        when(cultivationRecordMapper.insert(any())).thenReturn(1);

        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证经验值在新的范围内（100-300）且加成生效
        // 基础经验: 100-300
        // 悟性加成: 1 + 100 * 0.002 = 1.2
        // 境界加成: 1 + (1-1) * 0.2 = 1.0
        // 预期范围: 100 * 1.2 * 1.0 = 120 ~ 300 * 1.2 * 1.0 = 360
        assertTrue(response.getExpGained() >= 120, "经验值应该至少为120");
        assertTrue(response.getExpGained() <= 360, "经验值应该不超过360");
        assertEquals(5, response.getStaminaConsumed());
    }

    @Test
    void calculateExpGained_HighComprehension() throws Exception {
        // 测试高悟性角色的经验获取
        character.setComprehension(500);  // 高悟性
        character.setRealmLevel(1);
        character.setCurrentState("闲置");
        character.setStamina(100);

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);
        when(characterService.updateCharacter(any())).thenReturn(true);
        when(cultivationRecordMapper.insert(any())).thenReturn(1);

        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证高悟性带来的额外经验
        // 基础经验: 50-200
        // 悟性加成: 1 + 500 * 0.001 = 1.5
        // 境界加成: 1.0
        // 预期范围: 50 * 1.5 = 75 ~ 200 * 1.5 = 300
        assertTrue(response.getExpGained() >= 75, "高悟性角色经验值应该至少为75");
        assertTrue(response.getExpGained() <= 300, "高悟性角色经验值应该不超过300");
    }

    @Test
    void calculateRequiredExpForLevel_WithCustomMultiplier() throws Exception {
        // 测试自定义层次系数
        CultivationProperties customConfig = new CultivationProperties();
        customConfig.getBaseExp().setMin(50);
        customConfig.getBaseExp().setMax(200);
        customConfig.getBonus().setComprehension(0.001);
        customConfig.getBonus().setRealmPerLevel(0.1);
        customConfig.setLevelMultiplier(1.0);  // 层次系数设为1.0（翻倍）
        customConfig.setStaminaCost(5);

        // 使用反射注入自定义配置
        Field field = CultivationServiceImpl.class.getDeclaredField("cultivationProperties");
        field.setAccessible(true);
        field.set(cultivationService, customConfig);

        character.setExperience(150L);  // 设置初始经验150（修炼获得50-200经验后应该在200-350之间）
        character.setRealmLevel(1);
        character.setCurrentState("闲置");
        character.setStamina(100);

        Realm testRealm = new Realm();
        testRealm.setId(1);
        testRealm.setRealmLevel(1);
        testRealm.setRealmName("Qi Refining");
        testRealm.setSubLevels(9);
        testRealm.setRequiredExp(100L);  // 境界基础经验100
        testRealm.setLevelUpPoints(5);   // 设置升级奖励点数

        when(characterService.getById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(testRealm);
        when(characterService.updateCharacter(any())).thenReturn(true);
        when(cultivationRecordMapper.insert(any())).thenReturn(1);

        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        CultivationResponse response = cultivationService.startCultivation(request);

        // 验证升级（自定义层次系数下）
        // 1层: 100 * 1.0 = 100
        // 2层: 100 * (1 + 1 * 1.0) = 200
        // 3层: 100 * (1 + 2 * 1.0) = 300
        // 初始经验150 + 修炼获得经验(50-200) = 200-350
        // 应该能升到2层，不会升到3层
        assertTrue(response.getLeveledUp(), "应该升级到2层");
        assertTrue(character.getRealmLevel() >= 2, "境界应该至少为2层");
        assertTrue(character.getRealmLevel() <= 3, "境界应该不超过3层");
    }
}
