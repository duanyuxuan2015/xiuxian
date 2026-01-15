package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.BreakthroughRequest;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.request.MeditationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.dto.response.MeditationResponse;
import com.xiuxian.entity.CultivationRecord;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CultivationRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.RealmService;
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
        character.setHealth(100);  // 添加生命值
        character.setHealthMax(100);  // 添加最大生命值
        character.setSpiritualPower(100);  // 添加灵力
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
}
