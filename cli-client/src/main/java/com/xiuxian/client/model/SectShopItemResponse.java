package com.xiuxian.client.model;

/**
 * 宗门商店物品响应
 */
public class SectShopItemResponse {
    private Long itemId;  // 后端字段名为 itemId
    private String itemName;
    private String itemType;
    private Integer itemTier;
    private Integer price;
    private Integer stock;
    private String description;
    private Integer requiredPosition;  // 所需职位等级
    private Boolean canBuy;  // 是否可购买

    // 技能类型特有属性
    private Integer baseDamage;
    private Integer spiritualCost;
    private String skillType;
    private String elementType;

    // 装备类型特有属性
    private Integer attackBonus;
    private Integer defenseBonus;
    private Integer healthBonus;
    private String equipmentSlot;

    // 丹药类型特有属性
    private Integer healAmount;
    private Integer expBonus;
    private Integer buffDuration;

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public Long getShopItemId() { return itemId; }  // 兼容旧代码
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public Integer getItemTier() { return itemTier; }
    public Integer getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getDescription() { return description; }
    public Integer getRequiredPosition() { return requiredPosition; }
    public Boolean getCanBuy() { return canBuy; }
    public Integer getBaseDamage() { return baseDamage; }
    public Integer getSpiritualCost() { return spiritualCost; }
    public String getSkillType() { return skillType; }
    public String getElementType() { return elementType; }
    public Integer getAttackBonus() { return attackBonus; }
    public Integer getDefenseBonus() { return defenseBonus; }
    public Integer getHealthBonus() { return healthBonus; }
    public String getEquipmentSlot() { return equipmentSlot; }
    public Integer getHealAmount() { return healAmount; }
    public Integer getExpBonus() { return expBonus; }
    public Integer getBuffDuration() { return buffDuration; }

    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setShopItemId(Long shopItemId) { this.itemId = shopItemId; }  // 兼容旧代码
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setItemTier(Integer itemTier) { this.itemTier = itemTier; }
    public void setPrice(Integer price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setDescription(String description) { this.description = description; }
    public void setRequiredPosition(Integer requiredPosition) { this.requiredPosition = requiredPosition; }
    public void setCanBuy(Boolean canBuy) { this.canBuy = canBuy; }
    public void setBaseDamage(Integer baseDamage) { this.baseDamage = baseDamage; }
    public void setSpiritualCost(Integer spiritualCost) { this.spiritualCost = spiritualCost; }
    public void setSkillType(String skillType) { this.skillType = skillType; }
    public void setElementType(String elementType) { this.elementType = elementType; }
    public void setAttackBonus(Integer attackBonus) { this.attackBonus = attackBonus; }
    public void setDefenseBonus(Integer defenseBonus) { this.defenseBonus = defenseBonus; }
    public void setHealthBonus(Integer healthBonus) { this.healthBonus = healthBonus; }
    public void setEquipmentSlot(String equipmentSlot) { this.equipmentSlot = equipmentSlot; }
    public void setHealAmount(Integer healAmount) { this.healAmount = healAmount; }
    public void setExpBonus(Integer expBonus) { this.expBonus = expBonus; }
    public void setBuffDuration(Integer buffDuration) { this.buffDuration = buffDuration; }
}
