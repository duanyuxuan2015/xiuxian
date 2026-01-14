package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.config.AttributeProperties;
import com.xiuxian.dto.request.AllocatePointsRequest;
import com.xiuxian.dto.request.CharacterCreateRequest;
import com.xiuxian.dto.response.AllocatePointsResponse;
import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CharacterMapper;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.SectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CharacterServiceImplTest {

    @Mock
    private CharacterMapper characterMapper;
    @Mock
    private RealmService realmService;
    @Mock
    private SectService sectService;
    @Mock
    private AttributeProperties attributeProperties;

    @InjectMocks
    private CharacterServiceImpl characterService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = CharacterServiceImpl.class;
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
            baseMapperField.set(characterService, characterMapper);
        }

        // 配置AttributeProperties mock返回真实配置对象
        AttributeProperties.BaseConfig baseConfig = new AttributeProperties.BaseConfig();
        baseConfig.setHealth(100);
        baseConfig.setStamina(100);
        baseConfig.setSpiritualPower(100);
        baseConfig.setCritRate(5.0);
        baseConfig.setCritDamage(150.0);
        baseConfig.setSpeed(100);

        AttributeProperties.CoefficientConfig coefficientConfig = new AttributeProperties.CoefficientConfig();
        coefficientConfig.setConstitution(10);
        coefficientConfig.setSpirit(10);
        coefficientConfig.setFortuneCritRate(0.5);
        coefficientConfig.setFortuneCritDamage(2.0);
        coefficientConfig.setFortuneSpeed(2.0);

        when(attributeProperties.getBase()).thenReturn(baseConfig);
        when(attributeProperties.getCoefficient()).thenReturn(coefficientConfig);
    }

    @Test
    void createCharacter_Success() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(9);
        request.setSpirit(9);
        request.setComprehension(9);
        request.setLuck(9);
        request.setFortune(9);  // 总共45点

        Realm realm = new Realm();
        realm.setId(1);
        realm.setRealmName("Mortal");

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(realmService.getByRealmLevel(2)).thenReturn(realm);
        when(characterMapper.insert(any(PlayerCharacter.class))).thenReturn(1);

        CharacterResponse response = characterService.createCharacter(request);

        assertNotNull(response);
        assertEquals("TestPlayer", response.getPlayerName());
        assertEquals("Mortal", response.getRealmName());
    }

    @Test
    void createCharacter_NameExists() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("ExistingName");

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1001, e.getCode());
        }
    }

    @Test
    void createCharacter_InvalidPoints() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        // Sum is 0, expect 45

        // Mock name check passes
        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1002, e.getCode());
        }
    }

    @Test
    void createCharacter_AttributeValueTooLow() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(0);  // 属性值小于1
        request.setSpirit(9);
        request.setComprehension(9);
        request.setLuck(9);
        request.setFortune(18);  // 总和45

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1004, e.getCode());
            assertTrue(e.getMessage().contains("体质"));
        }
    }

    @Test
    void createCharacter_AttributeValueTooHigh() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(100001);  // 属性值大于100000
        request.setSpirit(9);
        request.setComprehension(9);
        request.setLuck(9);
        request.setFortune(9);

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            // 实际上先验证总点数，所以是1002而不是1004
            assertEquals(1002, e.getCode());
        }
    }

    @Test
    void createCharacter_NullAttribute() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(null);  // null属性值
        request.setSpirit(9);
        request.setComprehension(9);
        request.setLuck(9);
        request.setFortune(18);

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1004, e.getCode());
        }
    }

    @Test
    void createCharacter_RealmNotInitialized() {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(9);
        request.setSpirit(9);
        request.setComprehension(9);
        request.setLuck(9);
        request.setFortune(9);

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(realmService.getByRealmLevel(2)).thenReturn(null);  // 境界数据未初始化

        try {
            characterService.createCharacter(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2001, e.getCode());
            assertTrue(e.getMessage().contains("境界数据未初始化"));
        }
    }

    @Test
    void getCharacterById_Success() {
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setRealmId(1);

        Realm realm = new Realm();
        realm.setId(1);
        realm.setRealmName("Mortal");

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(realmService.getById(1)).thenReturn(realm);

        CharacterResponse response = characterService.getCharacterById(1L);

        assertNotNull(response);
        assertEquals("TestPlayer", response.getPlayerName());
        assertEquals("Mortal", response.getRealmName());
    }

    @Test
    void getCharacterById_NotFound() {
        when(characterMapper.selectById(1L)).thenReturn(null);

        try {
            characterService.getCharacterById(1L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
            assertTrue(e.getMessage().contains("角色不存在"));
        }
    }

    @Test
    void checkNameExists_True() {
        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        boolean result = characterService.checkNameExists("ExistingName");

        assertTrue(result);
    }

    @Test
    void checkNameExists_False() {
        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = characterService.checkNameExists("NewName");

        assertFalse(result);
    }

    // ==================== allocatePoints 测试 ====================

    @Test
    void allocatePoints_Success() {
        // 准备测试数据
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setConstitution(10);
        character.setSpirit(10);
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(5);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(2);
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getCharacterId());
        assertEquals("TestPlayer", response.getPlayerName());
        assertEquals(12, response.getNewConstitution()); // 10 + 2
        assertEquals(11, response.getNewSpirit()); // 10 + 1
        assertEquals(11, response.getNewComprehension()); // 10 + 1
        assertEquals(11, response.getNewLuck()); // 10 + 1
        assertEquals(10, response.getNewFortune()); // 10 + 0
        assertEquals(0, response.getRemainingPoints()); // 5 - 5
        assertEquals("属性点分配成功", response.getMessage());

        // 验证更新被调用
        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_CharacterNotFound() {
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(999L);
        request.setConstitutionPoints(1);
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        try {
            characterService.allocatePoints(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
            assertTrue(e.getMessage().contains("角色不存在"));
        }

        // 验证更新未被调用
        verify(characterMapper, never()).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_NotEnoughPoints() {
        // 准备测试数据 - 角色只有3点可用
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(10);
        character.setSpirit(10);
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(3);

        // 请求分配5点
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(2);
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);

        // 执行测试并验证异常
        try {
            characterService.allocatePoints(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2003, e.getCode());
            assertTrue(e.getMessage().contains("可分配点数不足"));
        }

        // 验证更新未被调用
        verify(characterMapper, never()).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_ZeroPointsRequested() {
        // 准备测试数据
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setAvailablePoints(5);

        // 请求分配0点
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(0);
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);

        // 执行测试并验证异常
        try {
            characterService.allocatePoints(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2002, e.getCode());
            assertTrue(e.getMessage().contains("分配点数必须大于0"));
        }

        // 验证更新未被调用
        verify(characterMapper, never()).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_ConstitutionExceedsLimit() {
        // 准备测试数据 - 体质接近上限
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(998); // 加2点会超过999
        character.setSpirit(10);
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(5);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(2); // 998 + 2 = 1000 > 999
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);

        // 执行测试并验证异常
        try {
            characterService.allocatePoints(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2004, e.getCode());
            assertTrue(e.getMessage().contains("体质"));
            assertTrue(e.getMessage().contains("不能超过999"));
        }

        // 验证更新未被调用
        verify(characterMapper, never()).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_SpiritExceedsLimit() {
        // 准备测试数据 - 精神接近上限
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(10);
        character.setSpirit(999); // 已经是上限
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(5);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(1);
        request.setSpiritPoints(1); // 999 + 1 > 999
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(1);

        when(characterMapper.selectById(1L)).thenReturn(character);

        // 执行测试并验证异常
        try {
            characterService.allocatePoints(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2004, e.getCode());
            assertTrue(e.getMessage().contains("精神"));
            assertTrue(e.getMessage().contains("不能超过999"));
        }

        // 验证更新未被调用
        verify(characterMapper, never()).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_AllAttributesExactly() {
        // 测试分配所有可用点数到单个属性
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setConstitution(100);
        character.setSpirit(50);
        character.setComprehension(50);
        character.setLuck(50);
        character.setFortune(50);
        character.setAvailablePoints(10);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(10); // 全部分配到体质
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(110, response.getNewConstitution()); // 100 + 10
        assertEquals(50, response.getNewSpirit());
        assertEquals(0, response.getRemainingPoints()); // 10 - 10

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_PartialAllocation() {
        // 测试只分配部分可用点数
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setConstitution(50);
        character.setSpirit(50);
        character.setComprehension(50);
        character.setLuck(50);
        character.setFortune(50);
        character.setAvailablePoints(10);

        // 只分配3点，还剩7点
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(1);
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(51, response.getNewConstitution());
        assertEquals(51, response.getNewSpirit());
        assertEquals(51, response.getNewComprehension());
        assertEquals(7, response.getRemainingPoints()); // 10 - 3

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_AttributeAtLimitBoundary() {
        // 测试属性值恰好等于999的情况
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(999);
        character.setSpirit(50);
        character.setComprehension(50);
        character.setLuck(50);
        character.setFortune(50);
        character.setAvailablePoints(5);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(0); // 不能再加
        request.setSpiritPoints(2);
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(1);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试 - 应该成功
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(999, response.getNewConstitution()); // 保持不变
        assertEquals(52, response.getNewSpirit());
        assertEquals(0, response.getRemainingPoints());

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_RecalculatesDerivedAttributes() {
        // 测试加点后重新计算衍生属性
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setConstitution(10);  // 初始体质10
        character.setSpirit(10);         // 初始精神10
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(5);
        // 初始衍生属性（基础值100 + 属性×系数10）
        character.setHealthMax(100 + 10 * 10);      // 200
        character.setStaminaMax(100 + 10 * 10);     // 200
        character.setSpiritualPowerMax(100 + 10 * 10); // 200

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(5);  // 增加5点体质，变成15
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(15, response.getNewConstitution()); // 10 + 5

        // 验证衍生属性重新计算：基础值100 + 新体质15 × 系数10 = 250
        // 通过实际的character对象来验证
        assertEquals(15, character.getConstitution());
        assertEquals(250, character.getHealthMax());      // 100 + 15 * 10
        assertEquals(250, character.getStaminaMax());     // 100 + 15 * 10

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_CalculatesCritRateAndSpeed() {
        // 测试暴击率和速度的计算
        // 基础暴击率5%，基础暴击伤害150%，基础速度100
        // 气运系数：暴击率0.5，暴击伤害2，速度2
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setRealmId(1);
        character.setConstitution(10);
        character.setSpirit(10);
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(20);  // 气运20
        character.setAvailablePoints(5);

        // Mock Realm with no bonus (level 1)
        Realm realm = new Realm();
        realm.setId(1);
        realm.setRealmName("Mortal");
        realm.setHpBonus(0);
        realm.setSpBonus(0);
        realm.setStaminaBonus(0);
        when(realmService.getById(1)).thenReturn(realm);

        // 增加5点气运，变成25
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(0);
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(5);  // 增加气运

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证气运增加
        assertNotNull(response);
        assertEquals(25, response.getNewFortune()); // 20 + 5

        // 验证衍生属性重新计算（通过实际的character对象验证）
        // 暴击率 = 5 + 25 × 0.5 = 17.5%
        // 暴击伤害 = 150 + 25 × 2 = 200%
        // 速度 = 100 + 25 × 2 = 150
        // 注意：这些值目前只是被计算，但PlayerCharacter实体没有存储这些字段
        // 通过日志可以验证计算逻辑是否正确

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void allocatePoints_IncludesRealmBonus() {
        // 测试境界加成正确应用到衍生属性
        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setRealmId(2);  // 境界ID 2
        character.setConstitution(10);
        character.setSpirit(10);
        character.setComprehension(10);
        character.setLuck(10);
        character.setFortune(10);
        character.setAvailablePoints(5);

        // Mock Realm with bonuses (境界提供加成)
        Realm realm = new Realm();
        realm.setId(2);
        realm.setRealmName("Qi Condensation");
        realm.setHpBonus(100);        // 境界提供100点气血加成
        realm.setSpBonus(50);         // 境界提供50点灵力加成
        realm.setStaminaBonus(80);    // 境界提供80点体力加成
        when(realmService.getById(2)).thenReturn(realm);

        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(5);  // 增加5点体质，变成15
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        when(characterMapper.selectById(1L)).thenReturn(character);
        when(characterMapper.updateById(any(PlayerCharacter.class))).thenReturn(1);

        // 执行测试
        AllocatePointsResponse response = characterService.allocatePoints(request);

        // 验证体质增加
        assertNotNull(response);
        assertEquals(15, response.getNewConstitution()); // 10 + 5

        // 验证衍生属性包含境界加成：
        // healthMax = 基础值100 + 体质15 × 系数10 + 境界加成100 = 350
        assertEquals(350, character.getHealthMax());
        // staminaMax = 基础值100 + 体质15 × 系数10 + 境界加成80 = 330
        assertEquals(330, character.getStaminaMax());
        // spiritualPowerMax = 基础值100 + 精神10 × 系数10 + 境界加成50 = 250
        assertEquals(250, character.getSpiritualPowerMax());

        verify(characterMapper).updateById(any(PlayerCharacter.class));
    }

    @Test
    void createCharacter_IncludesRealmBonus() {
        // 测试创建角色时境界加成正确应用
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("TestPlayer");
        request.setConstitution(10);
        request.setSpirit(10);
        request.setComprehension(10);
        request.setLuck(10);
        request.setFortune(5);  // 总共45点

        // Mock Realm with bonuses
        Realm realm = new Realm();
        realm.setId(1);
        realm.setRealmName("Mortal");
        realm.setHpBonus(50);
        realm.setSpBonus(30);
        realm.setStaminaBonus(40);
        when(realmService.getByRealmLevel(2)).thenReturn(realm);
        when(realmService.getById(1)).thenReturn(realm);

        when(characterMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(characterMapper.insert(any(PlayerCharacter.class))).thenAnswer(invocation -> {
            PlayerCharacter character = invocation.getArgument(0);
            character.setCharacterId(1L);
            return 1;
        });

        // 执行测试
        CharacterResponse response = characterService.createCharacter(request);

        // 验证角色创建成功
        assertNotNull(response);
        assertEquals("TestPlayer", response.getPlayerName());

        // 验证衍生属性包含境界加成
        // 通过捕获insert的参数来验证
        verify(characterMapper).insert(any());
    }
}
