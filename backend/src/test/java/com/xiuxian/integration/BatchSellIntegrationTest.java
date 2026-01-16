package com.xiuxian.integration;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 批量出售功能集成测试
 *
 * 测试场景：
 * 1. 批量出售多个装备
 * 2. 按评分筛选装备后批量出售
 * 3. 批量出售过程中的部分失败处理
 * 4. 批量出售后的灵石计算
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BatchSellIntegrationTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private CharacterInventoryMapper characterInventoryMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    private PlayerCharacter testCharacter;
    private List<Long> equipmentIds;

    @BeforeEach
    void setUp() {
        // 创建测试角色
        testCharacter = new PlayerCharacter();
        testCharacter.setPlayerName("测试玩家");
        testCharacter.setCharacterName("测试角色");
        testCharacter.setSpiritStones(10000L);
        testCharacter.setRealmId(1);
        testCharacter.setRealmLevel(1);
        testCharacter.setExperience(0L);
        testCharacter.setSpiritualPower(100);
        testCharacter.setSpiritualPowerMax(100);
        testCharacter.setStamina(100);
        testCharacter.setStaminaMax(100);
        testCharacter.setHealth(100);
        testCharacter.setHealthMax(100);
        testCharacter.setCurrentHealth(100);
        testCharacter.setCurrentSpirit(100);
        testCharacter.setMindset(50);
        testCharacter.setCritRate(5.0);
        testCharacter.setCritDamage(150.0);
        testCharacter.setSpeed(10.0);
        testCharacter.setConstitution(10);
        testCharacter.setSpirit(10);
        testCharacter.setComprehension(10);
        testCharacter.setLuck(10);
        testCharacter.setFortune(10);
        characterService.save(testCharacter);

        // 创建测试装备（不同评分）
        equipmentIds = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Equipment equipment = new Equipment();
            equipment.setEquipmentName("测试装备" + i);
            equipment.setEquipmentType("武器");
            equipment.setQuality("普通");
            equipment.setBaseScore(100 * i); // 评分: 100, 200, 300, 400, 500
            equipmentMapper.insert(equipment);
            equipmentIds.add(equipment.getEquipmentId());
        }
    }

    @Test
    void testBatchSell_AllItems() {
        // 添加装备到背包
        for (int i = 0; i < 5; i++) {
            inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(i), 1);
        }

        // 批量出售所有装备
        int successCount = 0;
        long totalStones = 0;
        for (int i = 0; i < 5; i++) {
            try {
                // 查询背包物品
                CharacterInventory inventory = characterInventoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                        .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                        .eq(CharacterInventory::getItemType, "equipment")
                        .eq(CharacterInventory::getItemId, equipmentIds.get(i))
                );

                assertNotNull(inventory);

                // 出售
                SellItemResponse response = inventoryService.sellItem(
                    testCharacter.getCharacterId(),
                    inventory.getInventoryId(),
                    1
                );

                successCount++;
                totalStones += response.getTotalSpiritStones();
            } catch (Exception e) {
                // 记录失败
            }
        }

        // 验证结果
        assertEquals(5, successCount);
        assertEquals(15000L, totalStones); // (100+200+300+400+500) * 10

        // 验证角色灵石增加
        PlayerCharacter updatedCharacter = characterService.getById(testCharacter.getCharacterId());
        assertEquals(25000L, updatedCharacter.getSpiritStones()); // 10000 + 15000
    }

    @Test
    void testBatchSell_WithScoreFilter() {
        // 添加装备到背包
        for (int i = 0; i < 5; i++) {
            inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(i), 1);
        }

        // 只出售评分低于300的装备（100, 200）
        int threshold = 300;
        int successCount = 0;
        long totalStones = 0;

        for (int i = 0; i < 5; i++) {
            Equipment equipment = equipmentMapper.selectById(equipmentIds.get(i));
            if (equipment.getBaseScore() < threshold) {
                // 查询背包物品
                CharacterInventory inventory = characterInventoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                        .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                        .eq(CharacterInventory::getItemType, "equipment")
                        .eq(CharacterInventory::getItemId, equipmentIds.get(i))
                );

                // 出售
                SellItemResponse response = inventoryService.sellItem(
                    testCharacter.getCharacterId(),
                    inventory.getInventoryId(),
                    1
                );

                successCount++;
                totalStones += response.getTotalSpiritStones();
            }
        }

        // 验证只出售了2件装备（评分100和200）
        assertEquals(2, successCount);
        assertEquals(3000L, totalStones); // (100+200) * 10

        // 验证角色灵石增加
        PlayerCharacter updatedCharacter = characterService.getById(testCharacter.getCharacterId());
        assertEquals(13000L, updatedCharacter.getSpiritStones()); // 10000 + 3000

        // 验证背包中还有3件装备
        long remainingCount = characterInventoryMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
        );
        assertEquals(3, remainingCount);
    }

    @Test
    void testBatchSell_WithMultipleQuantity() {
        // 添加同一装备多件
        Long equipmentId = equipmentIds.get(0); // 评分100
        inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentId, 5);

        // 批量出售（每次出售1件，共5次）
        int successCount = 0;
        long totalStones = 0;

        for (int i = 0; i < 5; i++) {
            // 查询背包物品
            CharacterInventory inventory = characterInventoryMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                    .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                    .eq(CharacterInventory::getItemType, "equipment")
                    .eq(CharacterInventory::getItemId, equipmentId)
            );

            if (inventory != null) {
                // 出售1件
                SellItemResponse response = inventoryService.sellItem(
                    testCharacter.getCharacterId(),
                    inventory.getInventoryId(),
                    1
                );

                successCount++;
                totalStones += response.getTotalSpiritStones();
            }
        }

        // 验证成功出售5次
        assertEquals(5, successCount);
        assertEquals(5000L, totalStones); // 100 * 10 * 5

        // 验证角色灵石增加
        PlayerCharacter updatedCharacter = characterService.getById(testCharacter.getCharacterId());
        assertEquals(15000L, updatedCharacter.getSpiritStones()); // 10000 + 5000

        // 验证装备已全部出售
        CharacterInventory inventory = characterInventoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, equipmentId)
        );
        assertNull(inventory);
    }

    @Test
    void testBatchSell_WithPartialFailure() {
        // 添加装备
        for (int i = 0; i < 3; i++) {
            inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(i), 1);
        }

        // 批量出售，故意使用不存在的inventoryId模拟失败
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < 3; i++) {
            try {
                // 前两次使用正确的ID，第三次使用错误的ID
                Long inventoryId;
                if (i < 2) {
                    CharacterInventory inventory = characterInventoryMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                            .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                            .eq(CharacterInventory::getItemType, "equipment")
                            .eq(CharacterInventory::getItemId, equipmentIds.get(i))
                    );
                    inventoryId = inventory.getInventoryId();
                } else {
                    inventoryId = 99999L; // 不存在的ID
                }

                inventoryService.sellItem(
                    testCharacter.getCharacterId(),
                    inventoryId,
                    1
                );

                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }

        // 验证结果
        assertEquals(2, successCount);
        assertEquals(1, failCount);
    }

    @Test
    void testSellItem_PriceCalculation() {
        // 测试不同评分装备的价格计算
        inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(0), 1); // 评分100
        inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(4), 1); // 评分500

        // 出售评分100的装备
        CharacterInventory inventory1 = characterInventoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, equipmentIds.get(0))
        );

        SellItemResponse response1 = inventoryService.sellItem(
            testCharacter.getCharacterId(),
            inventory1.getInventoryId(),
            1
        );

        assertEquals(1000L, response1.getTotalSpiritStones()); // 100 * 10

        // 出售评分500的装备
        CharacterInventory inventory2 = characterInventoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, equipmentIds.get(4))
        );

        SellItemResponse response2 = inventoryService.sellItem(
            testCharacter.getCharacterId(),
            inventory2.getInventoryId(),
            1
        );

        assertEquals(5000L, response2.getTotalSpiritStones()); // 500 * 10

        // 验证总灵石
        PlayerCharacter updatedCharacter = characterService.getById(testCharacter.getCharacterId());
        assertEquals(16000L, updatedCharacter.getSpiritStones()); // 10000 + 1000 + 5000
    }

    @Test
    void testSellItem_MultipleQuantity() {
        // 测试一次出售多件
        inventoryService.addItem(testCharacter.getCharacterId(), "equipment", equipmentIds.get(0), 10);

        CharacterInventory inventory = characterInventoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, equipmentIds.get(0))
        );

        // 一次出售5件
        SellItemResponse response = inventoryService.sellItem(
            testCharacter.getCharacterId(),
            inventory.getInventoryId(),
            5
        );

        assertEquals(5, response.getQuantity());
        assertEquals(5000L, response.getTotalSpiritStones()); // 100 * 10 * 5

        // 验证剩余数量
        CharacterInventory updatedInventory = characterInventoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharacterInventory>()
                .eq(CharacterInventory::getCharacterId, testCharacter.getCharacterId())
                .eq(CharacterInventory::getItemType, "equipment")
                .eq(CharacterInventory::getItemId, equipmentIds.get(0))
        );

        assertNotNull(updatedInventory);
        assertEquals(5, updatedInventory.getQuantity()); // 10 - 5 = 5
    }
}
