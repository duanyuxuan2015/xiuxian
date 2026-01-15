package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.MonsterDrop;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MonsterDropMapper;
import com.xiuxian.service.MonsterDropService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MonsterDropService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class MonsterDropServiceImplTest {

    @Mock
    private MonsterDropMapper monsterDropMapper;

    @Mock
    private EquipmentMapper equipmentMapper;

    @InjectMocks
    private MonsterDropServiceImpl monsterDropService;

    private Equipment testEquipment;
    private MonsterDrop testDropConfig;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = MonsterDropServiceImpl.class;
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
            baseMapperField.set(monsterDropService, monsterDropMapper);
        }

        // 初始化测试装备数据
        testEquipment = new Equipment();
        testEquipment.setEquipmentId(1L);
        testEquipment.setEquipmentName("新手木剑");
        testEquipment.setEquipmentType("武器");
        testEquipment.setQuality("普通");
        testEquipment.setAttackPower(10);

        // 初始化测试掉落配置
        testDropConfig = new MonsterDrop();
        testDropConfig.setMonsterDropId(1L);
        testDropConfig.setMonsterId(1L);
        testDropConfig.setEquipmentId(1L);
        testDropConfig.setDropRate(new BigDecimal("30.00"));
        testDropConfig.setDropQuantity(1);
        testDropConfig.setIsGuaranteed(0);
    }

    @Test
    void testGetDropsByMonsterId_存在掉落配置() {
        // Given
        List<MonsterDrop> expectedDrops = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expectedDrops);

        // When
        List<MonsterDrop> result = monsterDropService.getDropsByMonsterId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getMonsterId());
        assertEquals(1L, result.get(0).getEquipmentId());
        verify(monsterDropMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testGetDropsByMonsterId_不存在掉落配置() {
        // Given
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // When
        List<MonsterDrop> result = monsterDropService.getDropsByMonsterId(999L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(monsterDropMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testRollEquipmentDrops_掉落成功() {
        // Given - 设置30%掉落率，但测试时会调用真实随机
        List<MonsterDrop> dropConfigs = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);
        when(equipmentMapper.selectById(1L)).thenReturn(testEquipment);

        // When - 执行多次，统计掉落次数
        int dropCount = 0;
        int totalTests = 100;
        for (int i = 0; i < totalTests; i++) {
            List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);
            if (!result.isEmpty()) {
                dropCount++;
            }
        }

        // Then - 30%掉落率，100次测试应该在10-50次之间掉落
        assertTrue(dropCount >= 10 && dropCount <= 50,
                "掉落次数应该在合理范围内(10-50)，实际: " + dropCount);
    }

    @Test
    void testRollEquipmentDrops_100PercentDropRate() {
        // Given - 设置100%掉落率
        testDropConfig.setDropRate(new BigDecimal("100.00"));
        testDropConfig.setIsGuaranteed(0);
        List<MonsterDrop> dropConfigs = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);
        when(equipmentMapper.selectById(1L)).thenReturn(testEquipment);

        // When
        List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);

        // Then - 100%掉落率应该必定掉落
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    void testRollEquipmentDrops_0PercentDropRate() {
        // Given - 设置0%掉落率
        testDropConfig.setDropRate(new BigDecimal("0.00"));
        List<MonsterDrop> dropConfigs = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);

        // When - 执行多次
        for (int i = 0; i < 10; i++) {
            List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);
            
            // Then - 0%掉落率应该永不掉落
            assertTrue(result.isEmpty(), "0%掉落率不应该掉落任何装备");
        }
    }

    @Test
    void testRollEquipmentDrops_必掉装备() {
        // Given - 设置必掉标记
        testDropConfig.setIsGuaranteed(1);
        testDropConfig.setDropRate(new BigDecimal("0.00")); // 即使掉落率为0
        List<MonsterDrop> dropConfigs = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);
        when(equipmentMapper.selectById(1L)).thenReturn(testEquipment);

        // When
        List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);

        // Then - 必掉装备应该必定掉落
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0));
    }

    @Test
    void testRollEquipmentDrops_多个掉落配置() {
        // Given - 创建多个掉落配置
        MonsterDrop drop1 = new MonsterDrop();
        drop1.setMonsterId(1L);
        drop1.setEquipmentId(1L);
        drop1.setDropRate(new BigDecimal("100.00")); // 100%掉落
        drop1.setIsGuaranteed(0);

        MonsterDrop drop2 = new MonsterDrop();
        drop2.setMonsterId(1L);
        drop2.setEquipmentId(2L);
        drop2.setDropRate(new BigDecimal("100.00")); // 100%掉落
        drop2.setIsGuaranteed(0);

        Equipment equip2 = new Equipment();
        equip2.setEquipmentId(2L);
        equip2.setEquipmentName("铁剑");

        List<MonsterDrop> dropConfigs = Arrays.asList(drop1, drop2);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);
        when(equipmentMapper.selectById(1L)).thenReturn(testEquipment);
        when(equipmentMapper.selectById(2L)).thenReturn(equip2);

        // When
        List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    void testRollEquipmentDrops_装备不存在() {
        // Given - 装备不存在
        testDropConfig.setDropRate(new BigDecimal("100.00"));
        List<MonsterDrop> dropConfigs = Arrays.asList(testDropConfig);
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(dropConfigs);
        when(equipmentMapper.selectById(1L)).thenReturn(null); // 装备不存在

        // When
        List<Long> result = monsterDropService.rollEquipmentDrops(1L, 1L);

        // Then - 装备不存在时不应该掉落
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testRollEquipmentDrops_无掉落配置() {
        // Given - 怪物没有掉落配置
        when(monsterDropMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // When
        List<Long> result = monsterDropService.rollEquipmentDrops(999L, 1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(equipmentMapper, never()).selectById(any());
    }
}
