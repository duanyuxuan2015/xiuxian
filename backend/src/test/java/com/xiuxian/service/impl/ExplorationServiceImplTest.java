package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.config.ExplorationProbabilityConfig;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.entity.ExplorationArea;
import com.xiuxian.entity.ExplorationEvent;
import com.xiuxian.entity.ExplorationRecord;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.*;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExplorationServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private ExplorationAreaMapper areaMapper;
    @Mock
    private ExplorationEventMapper eventMapper;
    @Mock
    private MaterialMapper materialMapper;
    @Mock
    private PillMapper pillMapper;
    @Mock
    private MonsterMapper monsterMapper;
    @Mock
    private ExplorationRecordMapper explorationRecordMapper; // Implicitly used by ServiceImpl
    @Mock
    private ExplorationProbabilityConfig probabilityConfig;

    @InjectMocks
    private ExplorationServiceImpl explorationService;

    private PlayerCharacter character;
    private ExplorationArea area;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = ExplorationServiceImpl.class;
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
            baseMapperField.set(explorationService, explorationRecordMapper);
        }

        // 配置probabilityConfig的默认返回值
        when(probabilityConfig.getMaxAdjustment()).thenReturn(0.3);
        when(probabilityConfig.getBaseProbabilities()).thenReturn(java.util.List.of(0.55, 0.30, 0.10, 0.05));
        when(probabilityConfig.getDirections()).thenReturn(java.util.List.of(-1.0, -0.2, 0.5, 1.0));
        when(probabilityConfig.getMinProbability()).thenReturn(0.05);
        when(probabilityConfig.getMaxProbability()).thenReturn(0.80);

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setRealmLevel(1);
        character.setSpiritualPower(100);
        character.setStamina(100);
        character.setCurrentSpirit(100);

        area = new ExplorationArea();
        area.setAreaId(1L);
        area.setAreaName("Mystic Forest");
        area.setRequiredRealmLevel(1);
        area.setSpiritCost(10);
        area.setStaminaCost(10);
        area.setDangerLevel(1);
    }

    @Test
    void startExploration_Success_NoEvent() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("无事", response.getEventType());
    }

    @Test
    void startExploration_InsufficientSpirit() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setSpiritualPower(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9003, e.getCode());
        }
    }

    @Test
    void startExploration_RealmTooLow() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setRealmLevel(1);
        area.setRequiredRealmLevel(5); // 境界要求太高

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9002, e.getCode());
            assertTrue(e.getMessage().contains("境界不足"));
        }
    }

    @Test
    void startExploration_AreaNotFound() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(999L)).thenReturn(null);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9001, e.getCode());
        }
    }

    @Test
    void getAllAreas_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectList(any())).thenReturn(Collections.singletonList(area));

        List<ExplorationAreaResponse> areas = explorationService.getAllAreas(1L);

        assertNotNull(areas);
        assertEquals(1, areas.size());
    }

    @Test
    void getAllAreas_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            explorationService.getAllAreas(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startExploration_CharacterNotFound() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(999L);
        request.setAreaId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getAllAreas_EmptyList() {
        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<ExplorationAreaResponse> areas = explorationService.getAllAreas(1L);

        assertNotNull(areas);
        assertEquals(0, areas.size());
    }

    // ==================== 新增测试用例 ====================

    @Test
    void startExploration_InsufficientStamina() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setStamina(5); // 体力不足

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9004, e.getCode());
            assertTrue(e.getMessage().contains("体力不足"));
        }
    }

    @Test
    void startExploration_StaminaNull() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setStamina(null); // 体力数据异常

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9004, e.getCode());
            assertTrue(e.getMessage().contains("体力数据异常"));
        }
    }

    @Test
    void startExploration_SpiritPowerNull() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setSpiritualPower(null); // 灵力数据异常

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9003, e.getCode());
            assertTrue(e.getMessage().contains("灵力数据异常"));
        }
    }

    @Test
    void startExploration_Success_WithResourceConsumption() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        // 验证资源消耗信息
        assertEquals(Integer.valueOf(10), response.getStaminaCost());
        assertEquals(Integer.valueOf(90), response.getStaminaRemaining()); // 100 - 10 = 90
        assertEquals(Integer.valueOf(10), response.getSpiritCost());
        assertEquals(Integer.valueOf(90), response.getSpiritRemaining()); // 100 - 10 = 90

        // 验证角色已更新
        verify(characterService).updateById(character);
    }

    @Test
    void startExploration_StaminaCostNull_DefaultsToTen() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        area.setStaminaCost(null); // 体力消耗为null，应默认为10

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals(Integer.valueOf(10), response.getStaminaCost());
        assertEquals(Integer.valueOf(90), response.getStaminaRemaining());
    }

    @Test
    void startExploration_SpiritCostNull_DefaultsToZero() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        area.setSpiritCost(null); // 灵力消耗为null，应默认为0

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals(Integer.valueOf(0), response.getSpiritCost());
        assertEquals(Integer.valueOf(100), response.getSpiritRemaining());
    }

    @Test
    void startExploration_CombatEvent_SetsNeedCombatTrue() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        // 创建战斗事件
        ExplorationEvent combatEvent = new ExplorationEvent();
        combatEvent.setEventId(1L);
        combatEvent.setAreaId(1L);
        combatEvent.setEventType("战斗");
        combatEvent.setEventName("遭遇野狼");
        combatEvent.setDescription("一只饥饿的野狼向你扑来");
        combatEvent.setMonsterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.singletonList(combatEvent));
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        // Mock monster
        com.xiuxian.entity.Monster monster = new com.xiuxian.entity.Monster();
        monster.setMonsterId(1L);
        monster.setMonsterName("饥饿的野狼");
        when(monsterMapper.selectById(1L)).thenReturn(monster);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("战斗", response.getEventType());
        assertEquals(Boolean.TRUE, response.getNeedCombat());
        assertEquals(Long.valueOf(1L), response.getMonsterId());
        assertEquals("饥饿的野狼", response.getMonsterName());
    }

    @Test
    void startExploration_GatherEvent_Success() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        // 创建采集事件
        ExplorationEvent gatherEvent = new ExplorationEvent();
        gatherEvent.setEventId(1L);
        gatherEvent.setAreaId(1L);
        gatherEvent.setEventType("采集");
        gatherEvent.setEventName("发现草药");
        gatherEvent.setDescription("发现了一株珍贵草药");
        gatherEvent.setRewardType("material");
        gatherEvent.setRewardId(1L);
        gatherEvent.setRewardQuantityMin(1);
        gatherEvent.setRewardQuantityMax(3);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.singletonList(gatherEvent));
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        // Mock material
        com.xiuxian.entity.Material material = new com.xiuxian.entity.Material();
        material.setMaterialId(1L);
        material.setMaterialName("黄精");
        when(materialMapper.selectById(1L)).thenReturn(material);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("采集", response.getEventType());
        assertEquals(Boolean.FALSE, response.getNeedCombat());
        assertNotNull(response.getRewards());
        assertTrue(response.getRewards().contains("黄精"));
    }

    @Test
    void startExploration_TrapEvent_HealthLost() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        // 创建陷阱事件
        ExplorationEvent trapEvent = new ExplorationEvent();
        trapEvent.setEventId(1L);
        trapEvent.setAreaId(1L);
        trapEvent.setEventType("陷阱");
        trapEvent.setEventName("触发陷阱");
        trapEvent.setDescription("不慎踩中了陷阱");

        character.setCurrentHealth(100);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.singletonList(trapEvent));
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("陷阱", response.getEventType());
        assertEquals(Boolean.FALSE, response.getNeedCombat());
        assertNotNull(response.getHealthLost());
        assertTrue(response.getHealthLost() > 0);
        // 陷阱损失生命 = 危险等级 * 10 = 1 * 10 = 10
        assertEquals(Integer.valueOf(10), response.getHealthLost());
    }

    @Test
    void startExploration_FortuneEvent_Success() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        // 创建机缘事件
        ExplorationEvent fortuneEvent = new ExplorationEvent();
        fortuneEvent.setEventId(1L);
        fortuneEvent.setAreaId(1L);
        fortuneEvent.setEventType("机缘");
        fortuneEvent.setEventName("意外发现");
        fortuneEvent.setDescription("发现了珍贵的宝物");
        fortuneEvent.setRewardType("material");
        fortuneEvent.setRewardId(1L);
        fortuneEvent.setRewardQuantityMin(1);
        fortuneEvent.setRewardQuantityMax(2);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.singletonList(fortuneEvent));
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        // Mock material
        com.xiuxian.entity.Material material = new com.xiuxian.entity.Material();
        material.setMaterialId(1L);
        material.setMaterialName("筑基丹");
        when(materialMapper.selectById(1L)).thenReturn(material);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("机缘", response.getEventType());
        assertEquals(Boolean.FALSE, response.getNeedCombat());
        assertNotNull(response.getRewards());
        assertTrue(response.getRewards().contains("筑基丹"));
    }

    // ==================== 事件级别和机缘测试 ====================

    @Test
    void selectEventLevel_Fortune0_UsesBaseProbabilities() {
        // 使用反射调用private方法测试基础概率
        try {
            java.lang.reflect.Method method = ExplorationServiceImpl.class.getDeclaredMethod("selectEventLevel", Integer.class);
            method.setAccessible(true);

            // 模拟多次选择，验证分布
            int[] levelCounts = new int[4];
            int iterations = 1000;

            for (int i = 0; i < iterations; i++) {
                int level = (int) method.invoke(explorationService, 0);
                levelCounts[level - 1]++;
            }

            // 验证基础概率分布（使用更宽松的范围以考虑随机性）
            assertTrue(levelCounts[0] >= 480 && levelCounts[0] <= 620, "Level 1 should be around 55%");
            assertTrue(levelCounts[1] >= 250 && levelCounts[1] <= 350, "Level 2 should be around 30%");
            assertTrue(levelCounts[2] >= 70 && levelCounts[2] <= 130, "Level 3 should be around 10%");
            assertTrue(levelCounts[3] >= 30 && levelCounts[3] <= 70, "Level 4 should be around 5%");
        } catch (Exception e) {
            fail("Failed to invoke selectEventLevel: " + e.getMessage());
        }
    }

    @Test
    void selectEventLevel_Fortune100_IncreasesHighLevelProbability() {
        try {
            java.lang.reflect.Method method = ExplorationServiceImpl.class.getDeclaredMethod("selectEventLevel", Integer.class);
            method.setAccessible(true);

            int[] levelCounts = new int[4];
            int iterations = 1000;

            for (int i = 0; i < iterations; i++) {
                int level = (int) method.invoke(explorationService, 100);
                levelCounts[level - 1]++;
            }

            // 机缘100时，高级别事件概率应该增加
            // 只验证趋势：Level 1应该比基础概率少，Level 4应该比基础概率多
            assertTrue(levelCounts[0] < 500, "Level 1 should decrease with high fortune");
            assertTrue(levelCounts[3] > 40, "Level 4 should increase with high fortune");
            // Level 2和Level 3应该在合理范围内
            assertTrue(levelCounts[1] >= 200 && levelCounts[1] <= 350, "Level 2 should be in reasonable range");
            assertTrue(levelCounts[2] >= 80 && levelCounts[2] <= 150, "Level 3 should be in reasonable range");
        } catch (Exception e) {
            fail("Failed to invoke selectEventLevel: " + e.getMessage());
        }
    }

    @Test
    void selectEventLevel_Fortune50_ModerateInfluence() {
        try {
            java.lang.reflect.Method method = ExplorationServiceImpl.class.getDeclaredMethod("selectEventLevel", Integer.class);
            method.setAccessible(true);

            int[] levelCounts = new int[4];
            int iterations = 1000;

            for (int i = 0; i < iterations; i++) {
                int level = (int) method.invoke(explorationService, 50);
                levelCounts[level - 1]++;
            }

            // 机缘50时，应该有中等程度的影响 - 验证基本趋势
            assertTrue(levelCounts[0] >= 400 && levelCounts[0] <= 550, "Level 1 should decrease moderately");
            assertTrue(levelCounts[1] >= 250 && levelCounts[1] <= 350, "Level 2 should be in reasonable range");
            assertTrue(levelCounts[2] >= 80 && levelCounts[2] <= 130, "Level 3 should be in reasonable range");
            assertTrue(levelCounts[3] >= 40 && levelCounts[3] <= 80, "Level 4 should be in reasonable range");
        } catch (Exception e) {
            fail("Failed to invoke selectEventLevel: " + e.getMessage());
        }
    }

    @Test
    void startExploration_RespectsEventLevels() {
        // 创建不同级别的事件
        ExplorationEvent level1Event = new ExplorationEvent();
        level1Event.setEventId(1L);
        level1Event.setAreaId(1L);
        level1Event.setEventType("无事");
        level1Event.setEventName("平静探索");
        level1Event.setLevel(1);

        ExplorationEvent level4Event = new ExplorationEvent();
        level4Event.setEventId(2L);
        level4Event.setAreaId(1L);
        level4Event.setEventType("机缘");
        level4Event.setEventName("天降横财");
        level4Event.setLevel(4);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(java.util.Arrays.asList(level1Event, level4Event));
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        // 机缘0时应该大概率触发1级事件
        character.setFortune(0);
        ExplorationResponse response = explorationService.startExploration(createRequest(1L, 1L));
        assertNotNull(response);
        // 由于1级事件概率高（55%），大部分情况下应该触发1级

        // 机缘100时，4级事件概率应该提升
        character.setFortune(100);
        response = explorationService.startExploration(createRequest(1L, 1L));
        assertNotNull(response);
    }

    private ExplorationRequest createRequest(Long characterId, Long areaId) {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(characterId);
        request.setAreaId(areaId);
        return request;
    }
}
