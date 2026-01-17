package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.dto.response.UsePillResponse;
import com.xiuxian.entity.CharacterInventory;

/**
 * 背包Service接口
 */
public interface InventoryService extends IService<CharacterInventory> {

    /**
     * 添加物品到背包
     * @param characterId 角色ID
     * @param itemType 物品类型 (material, pill, equipment)
     * @param itemId 物品ID
     * @param quantity 数量
     */
    void addItem(Long characterId, String itemType, Long itemId, int quantity);

    /**
     * 从背包移除物品
     * @param characterId 角色ID
     * @param itemType 物品类型
     * @param itemId 物品ID
     * @param quantity 数量
     * @return 是否成功
     */
    boolean removeItem(Long characterId, String itemType, Long itemId, int quantity);

    /**
     * 获取物品数量
     * @param characterId 角色ID
     * @param itemType 物品类型
     * @param itemId 物品ID
     * @return 数量
     */
    int getItemQuantity(Long characterId, String itemType, Long itemId);

    /**
     * 检查是否有足够物品
     * @param characterId 角色ID
     * @param itemType 物品类型
     * @param itemId 物品ID
     * @param quantity 需要数量
     * @return 是否足够
     */
    boolean hasEnoughItem(Long characterId, String itemType, Long itemId, int quantity);

    /**
     * 售出物品
     * @param characterId 角色ID
     * @param inventoryId 背包物品ID
     * @param quantity 售出数量
     * @return 售出结果
     */
    SellItemResponse sellItem(Long characterId, Long inventoryId, Integer quantity);

    /**
     * 使用丹药
     * @param characterId 角色ID
     * @param inventoryId 背包物品ID
     * @param quantity 使用数量
     * @return 使用结果
     */
    UsePillResponse usePill(Long characterId, Long inventoryId, Integer quantity);
}
