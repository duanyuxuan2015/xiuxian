package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.config.AttributeProperties;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.dto.response.UsePillResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.service.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private CharacterInventoryMapper characterInventoryMapper;

    @Mock
    private EquipmentMapper equipmentMapper;

    @Mock
    private MaterialMapper materialMapper;

    @Mock
    private PillMapper pillMapper;

    @Mock
    private CharacterEquipmentMapper characterEquipmentMapper;

    @Mock
    private CharacterService characterService;

    @Mock
    private AttributeProperties attributeProperties;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = InventoryServiceImpl.class;
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
            baseMapperField.set(inventoryService, characterInventoryMapper);
        }

        // 配置 AttributeProperties Mock
        AttributeProperties.MaxConfig maxConfig = new AttributeProperties.MaxConfig();
        maxConfig.setAttribute(999);
        maxConfig.setTotalPoints(9999);

        lenient().when(attributeProperties.getMax()).thenReturn(maxConfig);

        // 配置 characterService.recalculateDerivedAttributes 方法 - 使用 lenient 模式，什么都不做
        org.mockito.Mockito.lenient().doNothing().when(characterService).recalculateDerivedAttributes(anyLong());
    }

    @Test
    void addItem_NewItem() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        when(characterInventoryMapper.insert(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        verify(characterInventoryMapper).insert(any(CharacterInventory.class));
    }

    @Test
    void addItem_ExistingItem() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(5);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        assertEquals(10, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void addItem_ZeroQuantity() {
        // 添加0数量不应该执行任何操作
        inventoryService.addItem(1L, "material", 1L, 0);

        verify(characterInventoryMapper, never()).insert(any(CharacterInventory.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void addItem_NegativeQuantity() {
        // 添加负数数量不应该执行任何操作
        inventoryService.addItem(1L, "material", 1L, -5);

        verify(characterInventoryMapper, never()).insert(any(CharacterInventory.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void removeItem_Success() {
        CharacterInventory existing = new CharacterInventory();
        existing.setInventoryId(1L);
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertTrue(result);
        assertEquals(5, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void removeItem_AllItems() {
        CharacterInventory existing = new CharacterInventory();
        existing.setInventoryId(1L);
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.deleteById(1L)).thenReturn(1);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 10);

        assertTrue(result);
        verify(characterInventoryMapper).deleteById(1L);
    }

    @Test
    void removeItem_NotEnough() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(3);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertFalse(result);
        assertEquals(3, existing.getQuantity());
    }

    @Test
    void removeItem_ItemNotExists() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertFalse(result);
    }

    @Test
    void removeItem_ZeroQuantity() {
        // 移除0数量应该返回true但不执行任何操作
        boolean result = inventoryService.removeItem(1L, "material", 1L, 0);

        assertTrue(result);
        verify(characterInventoryMapper, never()).deleteById(any(Long.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void getItemQuantity_Exists() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(15);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        int quantity = inventoryService.getItemQuantity(1L, "material", 1L);

        assertEquals(15, quantity);
    }

    @Test
    void getItemQuantity_NotExists() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        int quantity = inventoryService.getItemQuantity(1L, "material", 1L);

        assertEquals(0, quantity);
    }

    @Test
    void hasEnoughItem_True() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertTrue(result);
    }

    @Test
    void hasEnoughItem_False() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(3);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertFalse(result);
    }

    @Test
    void hasEnoughItem_ExactlyEquals() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(5);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertTrue(result);
    }

    // ==================== sellItem 测试 ====================

    @Test
    void sellItem_Success_Equipment() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(3);

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("铁剑");
        equipment.setBaseScore(50); // 售价 = 50 * 10 = 500

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(1000L);

        // Mock配置 - 使用doCallRealMethod来调用实际方法
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("equipment"), eq(10L), eq(1));

        // 执行测试
        SellItemResponse response = spyService.sellItem(1L, 1L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("铁剑", response.getItemName());
        assertEquals(1, response.getQuantity());
        assertEquals(500L, response.getTotalSpiritStones());
        assertEquals(1500L, response.getRemainingSpiritStones());
        assertTrue(response.getMessage().contains("成功出售"));

        // 验证角色灵石已更新
        assertEquals(1500L, character.getSpiritStones());
    }

    @Test
    void sellItem_Success_Material() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(2L);
        inventory.setCharacterId(1L);
        inventory.setItemType("material");
        inventory.setItemId(20L);
        inventory.setQuantity(5);

        Material material = new Material();
        material.setMaterialName("灵草");
        material.setMaterialTier(3); // 售价 = 3 * 50 = 150

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(500L);

        // Mock配置
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory);
        lenient().when(materialMapper.selectById(20L)).thenReturn(material);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("material"), eq(20L), eq(2));

        // 执行测试 - 出售2个
        SellItemResponse response = spyService.sellItem(1L, 2L, 2);

        // 验证结果
        assertNotNull(response);
        assertEquals("灵草", response.getItemName());
        assertEquals(2, response.getQuantity());
        assertEquals(300L, response.getTotalSpiritStones()); // 150 * 2
        assertEquals(800L, response.getRemainingSpiritStones()); // 500 + 300
    }

    @Test
    void sellItem_Success_Pill() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(3L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(30L);
        inventory.setQuantity(10);

        Pill pill = new Pill();
        pill.setPillName("聚气丹");
        pill.setPillTier(2); // 售价 = 2 * 80 = 160

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(0L);

        // Mock配置
        lenient().when(inventoryService.getById(3L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(30L)).thenReturn(pill);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(30L), eq(5));

        // 执行测试 - 出售5个
        SellItemResponse response = spyService.sellItem(1L, 3L, 5);

        // 验证结果
        assertNotNull(response);
        assertEquals("聚气丹", response.getItemName());
        assertEquals(5, response.getQuantity());
        assertEquals(800L, response.getTotalSpiritStones()); // 160 * 5
        assertEquals(800L, response.getRemainingSpiritStones()); // 0 + 800
    }

    @Test
    void sellItem_ItemNotExists() {
        // Mock配置 - 物品不存在
        when(inventoryService.getById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.sellItem(1L, 999L, 1);
        });
    }

    @Test
    void sellItem_UnauthorizedAccess() {
        // 准备测试数据 - 物品属于其他角色
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(999L); // 属于角色999

        // Mock配置
        when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.sellItem(1L, 1L, 1); // 角色1尝试出售角色999的物品
        });
    }

    @Test
    void sellItem_InvalidQuantity_Zero() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        // Mock配置
        when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.sellItem(1L, 1L, 0); // 数量为0
        });
    }

    @Test
    void sellItem_InvalidQuantity_Negative() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        // Mock配置
        when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.sellItem(1L, 1L, -1); // 负数
        });
    }

    @Test
    void sellItem_ExceedsOwnedQuantity() {
        // 准备测试数据 - 只有3个
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(3);

        // Mock配置
        when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.sellItem(1L, 1L, 5); // 尝试出售5个
        });
    }

    @Test
    void sellItem_CharacterNotExists() {
        // 准备测试数据
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(3);

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("铁剑");
        equipment.setBaseScore(50);

        // Mock配置
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterService.getById(1L)).thenReturn(null); // 角色不存在

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("equipment"), eq(10L), eq(1));

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            spyService.sellItem(1L, 1L, 1);
        });
    }

    @Test
    void sellItem_EquipmentEquipped_ThrowsException() {
        // 准备测试数据 - 拥有1件，已装备1件，出售1件（应该失败）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(1); // 拥有1件

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("传说之剑");
        equipment.setBaseScore(100);

        // Mock配置
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);

        // 返回已装备数量为1
        lenient().when(characterEquipmentMapper.selectCount(any())).thenReturn(1L);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.sellItem(1L, 1L, 1) // 出售1件，1-1=0 < 1，应该失败
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("已装备"), "异常消息应包含'已装备'");
        assertEquals(6005, exception.getCode());
    }

    @Test
    void sellItem_EquipmentNotEquipped_Success() {
        // 准备测试数据 - 装备未装备（已装备数量为0）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(1);

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("精钢剑");
        equipment.setBaseScore(80);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(500L);

        // Mock配置 - 返回已装备数量为0
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectCount(any())).thenReturn(0L);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("equipment"), eq(10L), eq(1));

        // 执行测试 - 应该成功（1-1=0 >= 0）
        SellItemResponse response = spyService.sellItem(1L, 1L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("精钢剑", response.getItemName());
        assertEquals(1, response.getQuantity());
        assertEquals(800L, response.getTotalSpiritStones()); // 80 * 10
        assertEquals(1300L, response.getRemainingSpiritStones()); // 500 + 800
        assertTrue(response.getMessage().contains("成功出售"));
    }

    @Test
    void sellItem_NonEquipment_NoEquippedCheck() {
        // 准备测试数据 - 材料类型（不是装备，不需要检查装备状态）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(2L);
        inventory.setCharacterId(1L);
        inventory.setItemType("material");
        inventory.setItemId(20L);
        inventory.setQuantity(5);

        Material material = new Material();
        material.setMaterialName("铁矿石");
        material.setMaterialTier(2);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(100L);

        // Mock配置 - 材料类型不应该查询装备表
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory);
        lenient().when(materialMapper.selectById(20L)).thenReturn(material);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("material"), eq(20L), eq(3));

        // 执行测试 - 应该成功且不查询装备表
        SellItemResponse response = spyService.sellItem(1L, 2L, 3);

        // 验证结果
        assertNotNull(response);
        assertEquals("铁矿石", response.getItemName());
        assertEquals(3, response.getQuantity());

        // 验证没有查询装备表
        verify(characterEquipmentMapper, never()).selectCount(any());
    }

    @Test
    void sellItem_MultipleEquipment_SellOne_Success() {
        // 准备测试数据 - 拥有13件，已装备1件，出售1件（应该成功）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(13); // 拥有13件

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("龙鳞甲");
        equipment.setBaseScore(150);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritStones(1000L);

        // Mock配置 - 返回已装备数量为1
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectCount(any())).thenReturn(1L);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("equipment"), eq(10L), eq(1));

        // 执行测试 - 应该成功（13-1=12 >= 1）
        SellItemResponse response = spyService.sellItem(1L, 1L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("龙鳞甲", response.getItemName());
        assertEquals(1, response.getQuantity());
        assertEquals(1500L, response.getTotalSpiritStones()); // 150 * 10
        assertEquals(2500L, response.getRemainingSpiritStones()); // 1000 + 1500
        assertTrue(response.getMessage().contains("成功出售"));
    }

    @Test
    void sellItem_MultipleEquipment_SellExcess_ThrowsException() {
        // 准备测试数据 - 拥有5件，已装备2件，出售4件（应该失败）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(5); // 拥有5件

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("精钢剑");
        equipment.setBaseScore(80);

        // Mock配置 - 返回已装备数量为2
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectCount(any())).thenReturn(2L);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.sellItem(1L, 1L, 4) // 出售4件，5-4=1 < 2，应该失败
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("已装备"), "异常消息应包含'已装备'");
        assertEquals(6005, exception.getCode());
    }

    // ==================== usePill 测试 ====================

    @Test
    void usePill_RestoreHealth_Success() {
        // 准备测试数据 - 恢复生命丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("小回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(100); // 恢复100点生命

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(500); // 当前生命500
        character.setHealthMax(1000);    // 最大生命1000
        character.setSpiritualPower(800);
        character.setSpiritualPowerMax(1000);
        character.setExperience(5000L);
        character.setComprehension(80);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(1));

        // 执行测试 - 使用1个
        UsePillResponse response = spyService.usePill(1L, 1L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("小回春丹", response.getPillName());
        assertEquals(1, response.getQuantityUsed());
        assertEquals("恢复生命", response.getEffectType());
        assertEquals(100, response.getTotalEffect());
        assertTrue(response.getMessage().contains("生命值"));
        assertTrue(response.getMessage().contains("500 -> 600"));

        // 验证角色属性已更新
        assertEquals(600, character.getHealth()); // 500 + 100
        assertEquals(800, character.getSpiritualPower());
        assertEquals(5000L, character.getExperience());
        assertEquals(80, character.getComprehension());
    }

    @Test
    void usePill_RestoreHealth_Multiple() {
        // 测试使用多个恢复生命丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("中回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(150);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(400);
        character.setHealthMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(2));

        // 执行测试 - 使用2个
        UsePillResponse response = spyService.usePill(1L, 1L, 2);

        // 验证结果
        assertNotNull(response);
        assertEquals(2, response.getQuantityUsed());
        assertEquals(300, response.getTotalEffect()); // 150 * 2
        assertEquals(700, character.getHealth()); // 400 + 300
    }

    @Test
    void usePill_RestoreHealth_ExceedsMax() {
        // 测试恢复生命超过最大值
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("大回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(200);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(900);
        character.setHealthMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 1L, 1);

        // 验证结果 - 不超过最大值
        assertEquals(1000, character.getHealth()); // 900 + 200 = 1100 -> 限制为1000
    }

    @Test
    void usePill_RestoreHealth_FullHealth_ThrowsException() {
        // 测试生命值已满时使用恢复生命丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("小回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(100);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(1000);
        character.setHealthMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 1L, 1)
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("当前生命值已满"));
        assertEquals(3002, exception.getCode());
    }

    @Test
    void usePill_RestoreSpirit_Success() {
        // 测试恢复灵力丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(2L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(20L);
        inventory.setQuantity(3);

        Pill pill = new Pill();
        pill.setPillName("聚气丹");
        pill.setEffectType("恢复灵力");
        pill.setEffectValue(80);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(1000);
        character.setHealthMax(1000);
        character.setSpiritualPower(500);
        character.setSpiritualPowerMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(20L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(20L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 2L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("聚气丹", response.getPillName());
        assertEquals("恢复灵力", response.getEffectType());
        assertEquals(80, response.getTotalEffect());
        assertEquals(580, character.getSpiritualPower()); // 500 + 80
    }

    @Test
    void usePill_RestoreSpirit_FullSpirit_ThrowsException() {
        // 测试灵力值已满时使用恢复灵力丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(2L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(20L);
        inventory.setQuantity(3);

        Pill pill = new Pill();
        pill.setEffectType("恢复灵力");
        pill.setEffectValue(80);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritualPower(1000);
        character.setSpiritualPowerMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(20L)).thenReturn(pill);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 2L, 1)
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("当前灵力值已满"));
        assertEquals(3003, exception.getCode());
    }

    @Test
    void usePill_AddExperience_Success() {
        // 测试增加经验丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(3L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(30L);
        inventory.setQuantity(10);

        Pill pill = new Pill();
        pill.setPillName("经验丹");
        pill.setEffectType("增加经验");
        pill.setEffectValue(200);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setExperience(5000L);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(3L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(30L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(30L), eq(3));

        // 执行测试 - 使用3个
        UsePillResponse response = spyService.usePill(1L, 3L, 3);

        // 验证结果
        assertNotNull(response);
        assertEquals("经验丹", response.getPillName());
        assertEquals("增加经验", response.getEffectType());
        assertEquals(600, response.getTotalEffect()); // 200 * 3
        assertEquals(5600L, character.getExperience()); // 5000 + 600
    }

    @Test
    void usePill_ImproveComprehension_Success() {
        // 测试改善资质丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(4L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(40L);
        inventory.setQuantity(2);

        Pill pill = new Pill();
        pill.setPillName("洗髓丹");
        pill.setEffectType("改善资质");
        pill.setEffectValue(5);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setComprehension(80);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(4L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(40L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(40L), eq(2));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 4L, 2);

        // 验证结果
        assertNotNull(response);
        assertEquals("洗髓丹", response.getPillName());
        assertEquals("改善资质", response.getEffectType());
        assertEquals(10, response.getTotalEffect()); // 5 * 2
        assertEquals(90, character.getComprehension()); // 80 + 10
    }

    @Test
    void usePill_VerifyAttributesIncreased_AfterUse() {
        // 测试丹药使用后属性确实增长
        // 测试场景：使用改善资质丹药，验证悟性确实增加了
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(4L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(40L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("洗髓丹");
        pill.setEffectType("改善资质");
        pill.setEffectValue(10);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setComprehension(50); // 初始悟性50

        int originalComprehension = character.getComprehension(); // 保存原始值

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(4L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(40L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(40L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 4L, 1);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getMessage().contains("50 -> 60"), "消息应显示属性增长");
        assertTrue(response.getMessage().contains("+10"), "消息应显示增长值");

        // 验证悟性确实增加了
        assertEquals(60, character.getComprehension());
        assertTrue(character.getComprehension() > originalComprehension,
            "使用后悟性应该大于使用前");

        // 验证响应中的悟性值也正确
        assertEquals(60, response.getComprehension());
    }

    @Test
    void usePill_VerifyMultipleAttributesIncreased_AfterUse() {
        // 测试使用多个丹药后，多个属性都正确增长
        // 测试场景：先使用恢复生命丹药，再使用恢复灵力丹药，验证两个属性都正确增长
        CharacterInventory inventory1 = new CharacterInventory();
        inventory1.setInventoryId(1L);
        inventory1.setCharacterId(1L);
        inventory1.setItemType("pill");
        inventory1.setItemId(10L);
        inventory1.setQuantity(2);

        Pill healthPill = new Pill();
        healthPill.setPillName("小回春丹");
        healthPill.setEffectType("恢复生命");
        healthPill.setEffectValue(100);

        CharacterInventory inventory2 = new CharacterInventory();
        inventory2.setInventoryId(2L);
        inventory2.setCharacterId(1L);
        inventory2.setItemType("pill");
        inventory2.setItemId(20L);
        inventory2.setQuantity(2);

        Pill spiritPill = new Pill();
        spiritPill.setPillName("聚气丹");
        spiritPill.setEffectType("恢复灵力");
        spiritPill.setEffectValue(80);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(500);
        character.setHealthMax(2000);
        character.setSpiritualPower(600);
        character.setSpiritualPowerMax(2000);

        int originalHealth = character.getHealth();
        int originalSpirit = character.getSpiritualPower();

        // Mock配置 - 使用恢复生命丹药
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory1);
        lenient().when(pillMapper.selectById(10L)).thenReturn(healthPill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(1));

        // 执行测试 - 使用恢复生命丹药
        UsePillResponse response1 = spyService.usePill(1L, 1L, 1);

        // 验证生命值增长了
        assertTrue(character.getHealth() > originalHealth,
            "使用恢复生命丹药后，当前生命值应该增长");
        assertEquals(600, character.getHealth());

        // 配置恢复灵力丹药
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory2);
        lenient().when(pillMapper.selectById(20L)).thenReturn(spiritPill);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(20L), eq(1));

        // 执行测试 - 使用恢复灵力丹药
        UsePillResponse response2 = spyService.usePill(1L, 2L, 1);

        // 验证灵力值增长了
        assertTrue(character.getSpiritualPower() > originalSpirit,
            "使用恢复灵力丹药后，当前灵力值应该增长");
        assertEquals(680, character.getSpiritualPower());

        // 验证两个属性都正确增长
        assertTrue(character.getHealth() > originalHealth, "生命值应该增长");
        assertTrue(character.getSpiritualPower() > originalSpirit, "灵力值应该增长");
    }

    @Test
    void usePill_ImproveComprehension_ExceedsMax() {
        // 测试改善资质超过上限
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(4L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(40L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("洗髓丹");
        pill.setEffectType("改善资质");
        pill.setEffectValue(10);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setComprehension(995); // 接近上限999

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(4L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(40L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(40L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 4L, 1);

        // 验证结果 - 不超过上限999
        assertEquals(999, character.getComprehension()); // 995 + 10 = 1005 -> 限制为999
    }

    @Test
    void usePill_RemovePoison_NotImplemented() {
        // 测试解除毒素丹药（当前未实现）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(5L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(50L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setEffectType("解除毒素");
        pill.setEffectValue(1);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(5L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(50L)).thenReturn(pill);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 5L, 1)
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("暂无毒素系统"));
        assertEquals(3010, exception.getCode());
    }

    @Test
    void usePill_UnusableType_DefensePill_ThrowsException() {
        // 测试使用不可用的丹药类型（增加防御）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(7L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(70L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setEffectType("增加防御");
        pill.setEffectValue(10);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(7L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(70L)).thenReturn(pill);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 7L, 1)
        );

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("不能直接使用"));
        assertEquals(3008, exception.getCode());
    }

    @Test
    void usePill_InventoryItemNotExists_ThrowsException() {
        // 测试背包物品不存在
        // 不需要 stubbing getById，让实现返回 null 即可

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.usePill(1L, 999L, 1);
        });
    }

    @Test
    void usePill_CharacterNotExists_ThrowsException() {
        // 测试角色不存在
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(null);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.usePill(1L, 1L, 1);
        });
    }

    @Test
    void usePill_UnauthorizedAccess_ThrowsException() {
        // 测试物品不属于该角色
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(999L); // 属于角色999

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(new PlayerCharacter());
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        assertThrows(com.xiuxian.common.exception.BusinessException.class, () -> {
            inventoryService.usePill(1L, 1L, 1); // 角色1尝试使用角色999的物品
        });
    }

    @Test
    void usePill_NotPillType_ThrowsException() {
        // 测试物品不是丹药
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment"); // 装备不是丹药

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(new PlayerCharacter());
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 1L, 1)
        );

        assertEquals(3005, exception.getCode());
        assertTrue(exception.getMessage().contains("不是丹药"));
    }

    @Test
    void usePill_NotEnoughQuantity_ThrowsException() {
        // 测试数量不足
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(2); // 只有2个

        Pill pill = new Pill();
        pill.setEffectType("增加经验");
        pill.setEffectValue(100);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(new PlayerCharacter());
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);

        // 执行测试并验证异常 - 尝试使用3个
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 1L, 3)
        );

        assertEquals(3006, exception.getCode());
        assertTrue(exception.getMessage().contains("丹药数量不足"));
    }

    @Test
    void usePill_PillNotExists_ThrowsException() {
        // 测试丹药信息不存在
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(999L);
        inventory.setQuantity(5);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(new PlayerCharacter());
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(999L)).thenReturn(null); // 丹药不存在

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.usePill(1L, 1L, 1)
        );

        assertEquals(3007, exception.getCode());
        assertTrue(exception.getMessage().contains("丹药信息不存在"));
    }

    @Test
    void usePill_VerifyRemoveItemCalled() {
        // 测试是否正确调用了removeItem
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("经验丹");
        pill.setEffectType("增加经验");
        pill.setEffectValue(200);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setExperience(5000L);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(2));

        // 执行测试
        spyService.usePill(1L, 1L, 2);

        // 验证removeItem被正确调用
        org.mockito.Mockito.verify(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(2));
    }

    @Test
    void usePill_VerifyCharacterUpdated() {
        // 测试角色信息是否更新
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(5);

        Pill pill = new Pill();
        pill.setPillName("小回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(100);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(500);
        character.setHealthMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(1));

        // 执行测试
        spyService.usePill(1L, 1L, 1);

        // 验证updateById被调用
        verify(characterService).updateById(character);
    }

    @Test
    void usePill_NullCurrentHealth_Success() {
        // 测试当角色currentHealth为null时，使用恢复生命丹药不会抛出空指针异常
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(10L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("小回春丹");
        pill.setEffectType("恢复生命");
        pill.setEffectValue(100);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setHealth(null); // 当前生命值为null
        character.setHealthMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(10L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(10L), eq(1));

        // 执行测试 - 不应该抛出NullPointerException
        UsePillResponse response = spyService.usePill(1L, 1L, 1);

        // 验证结果 - 0 + 100 = 100
        assertNotNull(response);
        assertEquals("小回春丹", response.getPillName());
        assertEquals(100, character.getHealth());
    }

    @Test
    void usePill_NullSpiritualPower_Success() {
        // 测试当角色spiritualPower为null时，使用恢复灵力丹药不会抛出空指针异常
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(2L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(20L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("小回灵丹");
        pill.setEffectType("恢复灵力");
        pill.setEffectValue(80);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpiritualPower(null); // 当前灵力值为null
        character.setSpiritualPowerMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(2L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(20L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(20L), eq(1));

        // 执行测试 - 不应该抛出NullPointerException
        UsePillResponse response = spyService.usePill(1L, 2L, 1);

        // 验证结果 - 0 + 80 = 80
        assertNotNull(response);
        assertEquals("小回灵丹", response.getPillName());
        assertEquals(80, character.getSpiritualPower());
    }

    @Test
    void usePill_AddConstitution_Success() {
        // 测试使用增加体质丹药（力量丹）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(8L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(50L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("力量丹");
        pill.setEffectType("增加体质");
        pill.setEffectValue(20); // 增加20点体质

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(100); // 初始体质100

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(8L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(50L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(50L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 8L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("力量丹", response.getPillName());
        assertEquals("增加体质", response.getEffectType());
        assertEquals(20, response.getTotalEffect());
        assertEquals(120, character.getConstitution()); // 100 + 20 = 120
        assertTrue(response.getMessage().contains("体质"));
    }

    @Test
    void usePill_AddConstitution_ExceedsMax() {
        // 测试增加体质丹药超过上限
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(8L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(50L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("力量丹");
        pill.setEffectType("增加体质");
        pill.setEffectValue(50);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(980); // 接近上限999

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(8L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(50L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(50L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 8L, 1);

        // 验证结果 - 不超过最大值999
        assertEquals(999, character.getConstitution()); // 980 + 50 = 1030 -> 限制为999
    }

    @Test
    void usePill_AddSpirit_Success() {
        // 测试使用增加精神丹药（铁壁丹）
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(9L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(51L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("铁壁丹");
        pill.setEffectType("增加精神");
        pill.setEffectValue(20); // 增加20点精神

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpirit(100); // 初始精神100

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(9L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(51L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(51L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 9L, 1);

        // 验证结果
        assertNotNull(response);
        assertEquals("铁壁丹", response.getPillName());
        assertEquals("增加精神", response.getEffectType());
        assertEquals(20, response.getTotalEffect());
        assertEquals(120, character.getSpirit()); // 100 + 20 = 120
        assertTrue(response.getMessage().contains("精神"));
    }

    @Test
    void usePill_AddSpirit_ExceedsMax() {
        // 测试增加精神丹药超过上限
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(9L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(51L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("铁壁丹");
        pill.setEffectType("增加精神");
        pill.setEffectValue(50);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpirit(980); // 接近上限999

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(9L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(51L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(51L), eq(1));

        // 执行测试
        UsePillResponse response = spyService.usePill(1L, 9L, 1);

        // 验证结果 - 不超过最大值999
        assertEquals(999, character.getSpirit()); // 980 + 50 = 1030 -> 限制为999
    }

    @Test
    void usePill_AddConstitution_RecalculatesDerivedAttributes() {
        // 测试增加体质丹药后会重新计算衍生属性
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(10L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(52L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("力量丹");
        pill.setEffectType("增加体质");
        pill.setEffectValue(20);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setConstitution(100);
        character.setHealthMax(1000); // 初始生命最大值
        character.setStaminaMax(1000);

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(10L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(52L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(52L), eq(1));

        // 执行测试
        spyService.usePill(1L, 10L, 1);

        // 验证 recalculateDerivedAttributes 被调用
        verify(characterService).recalculateDerivedAttributes(1L);
    }

    @Test
    void usePill_AddSpirit_RecalculatesDerivedAttributes() {
        // 测试增加精神丹药后会重新计算衍生属性
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(11L);
        inventory.setCharacterId(1L);
        inventory.setItemType("pill");
        inventory.setItemId(53L);
        inventory.setQuantity(1);

        Pill pill = new Pill();
        pill.setPillName("铁壁丹");
        pill.setEffectType("增加精神");
        pill.setEffectValue(20);

        PlayerCharacter character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setSpirit(100);
        character.setSpiritualPowerMax(1000); // 初始灵力最大值

        // Mock配置
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(inventoryService.getById(11L)).thenReturn(inventory);
        lenient().when(pillMapper.selectById(53L)).thenReturn(pill);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("pill"), eq(53L), eq(1));

        // 执行测试
        spyService.usePill(1L, 11L, 1);

        // 验证 recalculateDerivedAttributes 被调用
        verify(characterService).recalculateDerivedAttributes(1L);
    }
}
