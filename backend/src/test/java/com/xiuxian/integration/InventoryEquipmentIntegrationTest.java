package com.xiuxian.integration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 背包与装备系统集成测试
 * 测试从战斗掉落装备到背包，再从背包装备物品的完整流程
 */
@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class InventoryEquipmentIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private CharacterInventoryMapper characterInventoryMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    private Long testCharacterId = 1L;
    private Long testEquipmentId = 1L;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId);
        characterInventoryMapper.delete(wrapper);
    }

    @Test
    void testAddEquipmentToInventory() {
        // 测试添加装备到背包
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 1);

        // 验证装备已添加到背包
        int quantity = inventoryService.getItemQuantity(testCharacterId, "equipment", testEquipmentId);
        assertEquals(1, quantity);

        // 验证背包中有物品
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId)
                .eq(CharacterInventory::getItemType, "equipment");
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(1, items.size());
        assertEquals(testEquipmentId, items.get(0).getItemId());
    }

    @Test
    void testAddMultipleEquipmentsToInventory() {
        // 测试添加多件装备到背包
        inventoryService.addItem(testCharacterId, "equipment", 1L, 1);
        inventoryService.addItem(testCharacterId, "equipment", 2L, 1);
        inventoryService.addItem(testCharacterId, "equipment", 3L, 1);

        // 验证背包中有3件装备
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId)
                .eq(CharacterInventory::getItemType, "equipment");
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(3, items.size());
    }

    @Test
    void testAddSameEquipmentMultipleTimes() {
        // 测试添加相同装备多次（应该堆叠数量）
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 1);
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 1);
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 1);

        // 验证数量堆叠
        int quantity = inventoryService.getItemQuantity(testCharacterId, "equipment", testEquipmentId);
        assertEquals(3, quantity);

        // 验证背包中只有1条记录
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId)
                .eq(CharacterInventory::getItemType, "equipment");
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(1, items.size());
        assertEquals(3, items.get(0).getQuantity());
    }

    @Test
    void testRemoveEquipmentFromInventory() {
        // 先添加装备
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 3);

        // 移除部分数量
        boolean result = inventoryService.removeItem(testCharacterId, "equipment", testEquipmentId, 1);
        assertTrue(result);

        // 验证剩余数量
        int quantity = inventoryService.getItemQuantity(testCharacterId, "equipment", testEquipmentId);
        assertEquals(2, quantity);
    }

    @Test
    void testRemoveAllEquipmentFromInventory() {
        // 先添加装备
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 3);

        // 移除全部数量
        boolean result = inventoryService.removeItem(testCharacterId, "equipment", testEquipmentId, 3);
        assertTrue(result);

        // 验证装备已从背包移除
        int quantity = inventoryService.getItemQuantity(testCharacterId, "equipment", testEquipmentId);
        assertEquals(0, quantity);

        // 验证背包中没有该装备的记录
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId)
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, testEquipmentId);
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(0, items.size());
    }

    @Test
    void testCheckEnoughItems() {
        // 添加装备
        inventoryService.addItem(testCharacterId, "equipment", testEquipmentId, 5);

        // 检查是否有足够的装备
        assertTrue(inventoryService.hasEnoughItem(testCharacterId, "equipment", testEquipmentId, 3));
        assertTrue(inventoryService.hasEnoughItem(testCharacterId, "equipment", testEquipmentId, 5));
        assertFalse(inventoryService.hasEnoughItem(testCharacterId, "equipment", testEquipmentId, 6));
    }

    @Test
    void testMixedItemTypes() {
        // 测试混合物品类型（装备、材料、丹药）
        inventoryService.addItem(testCharacterId, "equipment", 1L, 1);
        inventoryService.addItem(testCharacterId, "material", 1L, 10);
        inventoryService.addItem(testCharacterId, "pill", 1L, 5);

        // 验证各类型物品数量
        assertEquals(1, inventoryService.getItemQuantity(testCharacterId, "equipment", 1L));
        assertEquals(10, inventoryService.getItemQuantity(testCharacterId, "material", 1L));
        assertEquals(5, inventoryService.getItemQuantity(testCharacterId, "pill", 1L));

        // 验证背包中有3条记录
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId);
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(3, items.size());
    }

    @Test
    void testInventoryAfterRemovingNonExistentItem() {
        // 尝试移除不存在的物品
        boolean result = inventoryService.removeItem(testCharacterId, "equipment", 999L, 1);
        assertFalse(result);

        // 验证背包仍然为空
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId);
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(0, items.size());
    }

    @Test
    void testInventoryCapacity() {
        // 测试添加大量装备（模拟背包容量测试）
        for (int i = 1; i <= 20; i++) {
            inventoryService.addItem(testCharacterId, "equipment", (long) i, 1);
        }

        // 验证背包中有20件装备
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, testCharacterId)
                .eq(CharacterInventory::getItemType, "equipment");
        List<CharacterInventory> items = characterInventoryMapper.selectList(wrapper);
        assertEquals(20, items.size());
    }

    @Test
    void testGetItemQuantityForNonExistentItem() {
        // 获取不存在物品的数量应该返回0
        int quantity = inventoryService.getItemQuantity(testCharacterId, "equipment", 999L);
        assertEquals(0, quantity);
    }
}
