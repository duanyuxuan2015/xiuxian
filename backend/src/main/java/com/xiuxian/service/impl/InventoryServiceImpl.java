package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.mapper.CharacterInventoryMapper;
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
}
