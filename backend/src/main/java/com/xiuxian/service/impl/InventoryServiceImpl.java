package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 背包Service实现类
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<CharacterInventoryMapper, CharacterInventory> implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final CharacterInventoryMapper characterInventoryMapper;
    private final EquipmentMapper equipmentMapper;
    private final MaterialMapper materialMapper;
    private final PillMapper pillMapper;
    private final CharacterEquipmentMapper characterEquipmentMapper;
    private final CharacterService characterService;

    public InventoryServiceImpl(CharacterInventoryMapper characterInventoryMapper,
                                EquipmentMapper equipmentMapper,
                                MaterialMapper materialMapper,
                                PillMapper pillMapper,
                                CharacterEquipmentMapper characterEquipmentMapper,
                                CharacterService characterService) {
        this.characterInventoryMapper = characterInventoryMapper;
        this.equipmentMapper = equipmentMapper;
        this.materialMapper = materialMapper;
        this.pillMapper = pillMapper;
        this.characterEquipmentMapper = characterEquipmentMapper;
        this.characterService = characterService;
    }

    @Override
    @Transactional
    public void addItem(Long characterId, String itemType, Long itemId, int quantity) {
        if (quantity <= 0) {
            return;
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            this.updateById(existing);
            logger.info("更新背包物品: characterId={}, itemType={}, itemId={}, newQuantity={}",
                    characterId, itemType, itemId, existing.getQuantity());
        } else {
            CharacterInventory inventory = new CharacterInventory();
            inventory.setCharacterId(characterId);
            inventory.setItemType(itemType);
            inventory.setItemId(itemId);
            inventory.setQuantity(quantity);
            inventory.setAcquiredAt(LocalDateTime.now());
            this.save(inventory);
            logger.info("添加背包物品: characterId={}, itemType={}, itemId={}, quantity={}",
                    characterId, itemType, itemId, quantity);
        }
    }

    @Override
    @Transactional
    public boolean removeItem(Long characterId, String itemType, Long itemId, int quantity) {
        if (quantity <= 0) {
            return true;
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);

        if (existing == null || existing.getQuantity() < quantity) {
            return false;
        }

        int newQuantity = existing.getQuantity() - quantity;
        if (newQuantity <= 0) {
            this.removeById(existing.getInventoryId());
            logger.info("移除背包物品: characterId={}, itemType={}, itemId={}",
                    characterId, itemType, itemId);
        } else {
            existing.setQuantity(newQuantity);
            this.updateById(existing);
            logger.info("减少背包物品: characterId={}, itemType={}, itemId={}, newQuantity={}",
                    characterId, itemType, itemId, newQuantity);
        }

        return true;
    }

    @Override
    public int getItemQuantity(Long characterId, String itemType, Long itemId) {
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);
        return existing != null ? existing.getQuantity() : 0;
    }

    @Override
    public boolean hasEnoughItem(Long characterId, String itemType, Long itemId, int quantity) {
        return getItemQuantity(characterId, itemType, itemId) >= quantity;
    }

    @Override
    @Transactional
    public SellItemResponse sellItem(Long characterId, Long inventoryId, Integer quantity) {
        // 1. 查询背包物品
        CharacterInventory inventory = this.getById(inventoryId);
        if (inventory == null) {
            throw new BusinessException(3001, "背包物品不存在");
        }

        // 验证物品属于该角色
        if (!inventory.getCharacterId().equals(characterId)) {
            throw new BusinessException(3002, "无权出售此物品");
        }

        // 验证数量
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(3003, "出售数量必须大于0");
        }

        if (quantity > inventory.getQuantity()) {
            throw new BusinessException(3004, "出售数量超过拥有数量");
        }

        // 1.5. 如果是装备，检查出售后是否会少于已装备数量
        if ("equipment".equals(inventory.getItemType())) {
            Long equipmentId = inventory.getItemId();

            // 查询该装备已装备的数量
            LambdaQueryWrapper<CharacterEquipment> equipmentWrapper = new LambdaQueryWrapper<>();
            equipmentWrapper.eq(CharacterEquipment::getCharacterId, characterId)
                    .eq(CharacterEquipment::getEquipmentId, equipmentId);
            Long equippedCount = characterEquipmentMapper.selectCount(equipmentWrapper);

            // 计算出售后剩余数量
            int remainingQuantity = inventory.getQuantity() - quantity;

            // 检查：剩余数量必须 >= 已装备数量
            if (remainingQuantity < equippedCount) {
                Equipment equipment = equipmentMapper.selectById(equipmentId);
                String equipmentName = equipment != null ? equipment.getEquipmentName() : "该装备";
                throw new BusinessException(6005, String.format(
                        "装备[%s]已装备%d件，出售%d件后剩余%d件，不足以保留已装备的装备。请先卸下装备或减少出售数量。",
                        equipmentName, equippedCount, quantity, remainingQuantity));
            }
        }

        // 2. 计算售价
        Long spiritStones = calculatePrice(inventory, quantity);

        // 3. 从背包移除物品
        String itemType = inventory.getItemType();
        Long itemId = inventory.getItemId();
        boolean removed = removeItem(characterId, itemType, itemId, quantity);

        if (!removed) {
            throw new BusinessException(3005, "移除物品失败");
        }

        // 4. 给角色增加灵石
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        Long currentStones = character.getSpiritStones() != null ? character.getSpiritStones() : 0L;
        character.setSpiritStones(currentStones + spiritStones);
        characterService.updateById(character);

        // 5. 获取物品名称
        String itemName = getItemName(itemType, itemId);

        logger.info("出售物品成功: characterId={}, itemName={}, quantity={}, spiritStones={}",
                characterId, itemName, quantity, spiritStones);

        // 6. 返回结果
        return new SellItemResponse(
                itemName,
                quantity,
                spiritStones,
                character.getSpiritStones(),
                String.format("成功出售 %s x%d，获得 %d 灵石", itemName, quantity, spiritStones)
        );
    }

    /**
     * 计算物品售价
     */
    private Long calculatePrice(CharacterInventory inventory, int quantity) {
        String itemType = inventory.getItemType();
        Long itemId = inventory.getItemId();
        long basePrice = 0;

        switch (itemType) {
            case "equipment":
                Equipment equipment = equipmentMapper.selectById(itemId);
                if (equipment != null) {
                    // 装备售价 = 基础评分 * 10
                    basePrice = equipment.getBaseScore() * 10L;
                }
                break;

            case "material":
                Material material = materialMapper.selectById(itemId);
                if (material != null) {
                    // 材料售价 = 阶数 * 50
                    basePrice = material.getMaterialTier() * 50L;
                }
                break;

            case "pill":
                Pill pill = pillMapper.selectById(itemId);
                if (pill != null) {
                    // 丹药售价 = 阶数 * 80
                    basePrice = pill.getPillTier() * 80L;
                }
                break;

            default:
                basePrice = 10L; // 默认售价
        }

        return basePrice * quantity;
    }

    /**
     * 获取物品名称
     */
    private String getItemName(String itemType, Long itemId) {
        switch (itemType) {
            case "equipment":
                Equipment equipment = equipmentMapper.selectById(itemId);
                return equipment != null ? equipment.getEquipmentName() : "未知装备";

            case "material":
                Material material = materialMapper.selectById(itemId);
                return material != null ? material.getMaterialName() : "未知材料";

            case "pill":
                Pill pill = pillMapper.selectById(itemId);
                return pill != null ? pill.getPillName() : "未知丹药";

            default:
                return "未知物品";
        }
    }
}
