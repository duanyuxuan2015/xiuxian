package com.xiuxian.dto.response;

import com.xiuxian.entity.SectShopItem;

/**
 * 宗门商店物品响应DTO
 */
public class SectShopItemResponse {

    private Long itemId;
    private Long sectId;
    private String itemType;
    private Long refItemId;
    private String itemName;
    private Integer itemTier;
    private Integer price;
    private Integer stockLimit;
    private Integer currentStock;
    private String description;
    private Integer requiredPosition;
    private Boolean canBuy;

    // 技能类型特有属性
    private Integer baseDamage;        // 基础伤害
    private Integer spiritualCost;     // 灵力消耗
    private String skillType;          // 技能类型（攻击/防御/辅助）
    private String elementType;        // 元素类型

    // 装备类型特有属性
    private Integer attackBonus;       // 攻击加成
    private Integer defenseBonus;      // 防御加成
    private Integer healthBonus;       // 生命加成
    private String equipmentSlot;      // 装备部位

    // 丹药类型特有属性
    private Integer healAmount;        // 治疗量
    private Integer expBonus;          // 经验加成
    private Integer buffDuration;      // Buff持续时间

    public SectShopItemResponse() {
    }

    public static SectShopItemResponse fromEntity(SectShopItem item, int memberPosition) {
        SectShopItemResponse response = new SectShopItemResponse();
        response.setItemId(item.getItemId());
        response.setSectId(item.getSectId());
        response.setItemType(item.getItemType());
        response.setRefItemId(item.getRefItemId());
        response.setItemName(item.getItemName());
        response.setItemTier(item.getItemTier());
        response.setPrice(item.getPrice());
        response.setDescription(item.getDescription());
        response.setStockLimit(item.getStockLimit());
        response.setCurrentStock(item.getCurrentStock());
        response.setRequiredPosition(item.getRequiredPosition());
        // 库存为null时视为0（无库存），不能购买
        Integer currentStock = item.getCurrentStock();
        response.setCanBuy(memberPosition >= item.getRequiredPosition() && currentStock != null && currentStock > 0);
        return response;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getRefItemId() {
        return refItemId;
    }

    public void setRefItemId(Long refItemId) {
        this.refItemId = refItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemTier() {
        return itemTier;
    }

    public void setItemTier(Integer itemTier) {
        this.itemTier = itemTier;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStockLimit() {
        return stockLimit;
    }

    public void setStockLimit(Integer stockLimit) {
        this.stockLimit = stockLimit;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getStock() {
        return currentStock;
    }

    public void setStock(Integer stock) {
        this.currentStock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRequiredPosition() {
        return requiredPosition;
    }

    public void setRequiredPosition(Integer requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    public Boolean getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(Boolean canBuy) {
        this.canBuy = canBuy;
    }

    public Integer getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(Integer baseDamage) {
        this.baseDamage = baseDamage;
    }

    public Integer getSpiritualCost() {
        return spiritualCost;
    }

    public void setSpiritualCost(Integer spiritualCost) {
        this.spiritualCost = spiritualCost;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public Integer getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(Integer attackBonus) {
        this.attackBonus = attackBonus;
    }

    public Integer getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(Integer defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public Integer getHealthBonus() {
        return healthBonus;
    }

    public void setHealthBonus(Integer healthBonus) {
        this.healthBonus = healthBonus;
    }

    public String getEquipmentSlot() {
        return equipmentSlot;
    }

    public void setEquipmentSlot(String equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    public Integer getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(Integer healAmount) {
        this.healAmount = healAmount;
    }

    public Integer getExpBonus() {
        return expBonus;
    }

    public void setExpBonus(Integer expBonus) {
        this.expBonus = expBonus;
    }

    public Integer getBuffDuration() {
        return buffDuration;
    }

    public void setBuffDuration(Integer buffDuration) {
        this.buffDuration = buffDuration;
    }
}
