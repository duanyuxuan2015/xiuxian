package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.dto.response.SellItemResponse;
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
        // 准备测试数据 - 装备已装备
        CharacterInventory inventory = new CharacterInventory();
        inventory.setInventoryId(1L);
        inventory.setCharacterId(1L);
        inventory.setItemType("equipment");
        inventory.setItemId(10L);
        inventory.setQuantity(1);

        Equipment equipment = new Equipment();
        equipment.setEquipmentName("传说之剑");
        equipment.setBaseScore(100);

        CharacterEquipment equipped = new CharacterEquipment();
        equipped.setCharacterId(1L);
        equipped.setEquipmentId(10L);
        equipped.setEquipmentSlot("weapon");

        // Mock配置
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);

        // 简化Mock配置 - 任何selectOne调用都返回已装备的装备
        lenient().when(characterEquipmentMapper.selectOne(any())).thenReturn(equipped);

        // 执行测试并验证异常
        com.xiuxian.common.exception.BusinessException exception = assertThrows(
            com.xiuxian.common.exception.BusinessException.class,
            () -> inventoryService.sellItem(1L, 1L, 1)
        );

        // 打印异常消息用于调试
        System.out.println("异常消息: " + exception.getMessage());
        System.out.println("异常码: " + exception.getCode());

        // 验证异常信息
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("已装备"), "异常消息应包含'已装备'，实际消息: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("无法出售"), "异常消息应包含'无法出售'");
        assertEquals(6005, exception.getCode());
    }

    @Test
    void sellItem_EquipmentNotEquipped_Success() {
        // 准备测试数据 - 装备未装备
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

        // Mock配置 - 返回null表示装备未装备
        lenient().when(inventoryService.getById(1L)).thenReturn(inventory);
        lenient().when(equipmentMapper.selectById(10L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        // 使用spy来调用实际方法
        InventoryServiceImpl spyService = org.mockito.Mockito.spy(inventoryService);
        org.mockito.Mockito.doReturn(true).when(spyService).removeItem(eq(1L), eq("equipment"), eq(10L), eq(1));

        // 执行测试 - 应该成功
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
        verify(characterEquipmentMapper, never()).selectOne(any(LambdaQueryWrapper.class), anyBoolean());
    }
}
