package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.response.InventoryItemResponse;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 背包控制器
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final CharacterService characterService;
    private final InventoryService inventoryService;
    private final CharacterInventoryMapper characterInventoryMapper;
    private final EquipmentMapper equipmentMapper;
    private final MaterialMapper materialMapper;
    private final PillMapper pillMapper;

    public InventoryController(CharacterService characterService,
                              InventoryService inventoryService,
                              CharacterInventoryMapper characterInventoryMapper,
                              EquipmentMapper equipmentMapper,
                              MaterialMapper materialMapper,
                              PillMapper pillMapper) {
        this.characterService = characterService;
        this.inventoryService = inventoryService;
        this.characterInventoryMapper = characterInventoryMapper;
        this.equipmentMapper = equipmentMapper;
        this.materialMapper = materialMapper;
        this.pillMapper = pillMapper;
    }

    /**
     * 获取角色背包列表
     * GET /api/v1/inventory/character/{characterId}
     */
    @GetMapping("/character/{characterId}")
    public Result<List<InventoryItemResponse>> getCharacterInventory(
            @PathVariable Long characterId,
            @RequestParam(required = false) String itemType) {

        // 验证角色是否存在
        if (characterService.getById(characterId) == null) {
            return Result.error(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId);

        // 如果指定了物品类型，进行过滤
        if (itemType != null && !itemType.isEmpty()) {
            wrapper.eq(CharacterInventory::getItemType, itemType);
        }

        wrapper.orderByDesc(CharacterInventory::getAcquiredAt);

        List<CharacterInventory> inventoryList = characterInventoryMapper.selectList(wrapper);
        List<InventoryItemResponse> responses = new ArrayList<>();

        for (CharacterInventory inventory : inventoryList) {
            InventoryItemResponse response = new InventoryItemResponse();
            response.setInventoryId(inventory.getInventoryId());
            response.setItemType(inventory.getItemType());
            response.setItemId(inventory.getItemId());
            response.setQuantity(inventory.getQuantity());
            response.setAcquiredAt(inventory.getAcquiredAt());

            // 根据物品类型获取详细信息
            String type = inventory.getItemType();
            Long itemId = inventory.getItemId();

            switch (type) {
                case "equipment":
                    Equipment equipment = equipmentMapper.selectById(itemId);
                    if (equipment != null) {
                        response.setItemName(equipment.getEquipmentName());
                        response.setItemDetail(String.format("%s | %s | 评分:%d",
                                equipment.getEquipmentType(),
                                equipment.getQuality(),
                                equipment.getBaseScore()));

                        // 填充装备详细属性
                        response.setAttackPower(equipment.getAttackPower());
                        response.setDefensePower(equipment.getDefensePower());
                        response.setHealthBonus(equipment.getHealthBonus());
                        response.setCriticalRate(equipment.getCriticalRate());
                        response.setSpeedBonus(equipment.getSpeedBonus());
                        response.setPhysicalResist(equipment.getPhysicalResist());
                        response.setIceResist(equipment.getIceResist());
                        response.setFireResist(equipment.getFireResist());
                        response.setLightningResist(equipment.getLightningResist());
                        response.setBaseScore(equipment.getBaseScore());
                        response.setEnhancementLevel(equipment.getEnhancementLevel());
                    }
                    break;

                case "material":
                    Material material = materialMapper.selectById(itemId);
                    if (material != null) {
                        response.setItemName(material.getMaterialName());
                        response.setItemDetail(String.format("%s阶 | %s",
                                material.getMaterialTier(),
                                material.getQuality()));
                    }
                    break;

                case "pill":
                    Pill pill = pillMapper.selectById(itemId);
                    if (pill != null) {
                        response.setItemName(pill.getPillName());
                        response.setItemDetail(String.format("%s阶 | %s",
                                pill.getPillTier(),
                                pill.getQuality()));
                    }
                    break;

                default:
                    response.setItemName("未知物品");
                    response.setItemDetail("类型: " + type);
            }

            responses.add(response);
        }

        logger.debug("获取角色{}的背包，共{}件物品", characterId, responses.size());
        return Result.success(responses);
    }

    /**
     * 获取背包统计信息
     * GET /api/v1/inventory/character/{characterId}/summary
     */
    @GetMapping("/character/{characterId}/summary")
    public Result<InventorySummary> getInventorySummary(@PathVariable Long characterId) {

        // 验证角色是否存在
        if (characterService.getById(characterId) == null) {
            return Result.error(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId);

        List<CharacterInventory> inventoryList = characterInventoryMapper.selectList(wrapper);

        InventorySummary summary = new InventorySummary();
        summary.setTotalItems(inventoryList.size());

        int equipmentCount = 0;
        int materialCount = 0;
        int pillCount = 0;

        for (CharacterInventory inventory : inventoryList) {
            switch (inventory.getItemType()) {
                case "equipment":
                    equipmentCount += inventory.getQuantity();
                    break;
                case "material":
                    materialCount += inventory.getQuantity();
                    break;
                case "pill":
                    pillCount += inventory.getQuantity();
                    break;
            }
        }

        summary.setEquipmentCount(equipmentCount);
        summary.setMaterialCount(materialCount);
        summary.setPillCount(pillCount);

        return Result.success(summary);
    }

    /**
     * 出售物品
     * POST /api/v1/inventory/sell
     */
    @PostMapping("/sell")
    public Result<SellItemResponse> sellItem(@RequestBody SellItemRequest request) {
        logger.info("收到出售请求: characterId={}, inventoryId={}, quantity={}",
                request.getCharacterId(), request.getInventoryId(), request.getQuantity());

        try {
            SellItemResponse response = inventoryService.sellItem(
                    request.getCharacterId(),
                    request.getInventoryId(),
                    request.getQuantity()
            );
            return Result.success(response);
        } catch (Exception e) {
            logger.error("出售物品失败: {}", e.getMessage(), e);
            return Result.error(3000, "出售失败: " + e.getMessage());
        }
    }

    /**
     * 出售请求DTO
     */
    public static class SellItemRequest {
        private Long characterId;
        private Long inventoryId;
        private Integer quantity;

        public Long getCharacterId() {
            return characterId;
        }

        public void setCharacterId(Long characterId) {
            this.characterId = characterId;
        }

        public Long getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(Long inventoryId) {
            this.inventoryId = inventoryId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * 背包统计信息
     */
    public static class InventorySummary {
        private Integer totalItems;
        private Integer equipmentCount;
        private Integer materialCount;
        private Integer pillCount;

        public Integer getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(Integer totalItems) {
            this.totalItems = totalItems;
        }

        public Integer getEquipmentCount() {
            return equipmentCount;
        }

        public void setEquipmentCount(Integer equipmentCount) {
            this.equipmentCount = equipmentCount;
        }

        public Integer getMaterialCount() {
            return materialCount;
        }

        public void setMaterialCount(Integer materialCount) {
            this.materialCount = materialCount;
        }

        public Integer getPillCount() {
            return pillCount;
        }

        public void setPillCount(Integer pillCount) {
            this.pillCount = pillCount;
        }

        @Override
        public String toString() {
            return "InventorySummary{" +
                    "totalItems=" + totalItems +
                    ", equipmentCount=" + equipmentCount +
                    ", materialCount=" + materialCount +
                    ", pillCount=" + pillCount +
                    '}';
        }
    }
}
