package com.xiuxian.dto.response;

import java.util.List;

/**
 * 怪物详情响应DTO
 */
public class MonsterDetailResponse {

    // 基础信息
    private Long monsterId;
    private String monsterName;
    private Integer realmId;
    private String realmName;
    private String monsterType;
    private Integer speed;
    private Integer hp;
    private Integer attackPower;
    private Integer defensePower;
    private String attackElement;
    private Integer physicalResist;
    private Integer iceResist;
    private Integer fireResist;
    private Integer lightningResist;
    private Integer staminaCost;
    private Integer expReward;
    private Integer spiritStonesReward;

    // 掉落配置
    private List<MonsterDropResponse> drops;

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public Integer getRealmId() {
        return realmId;
    }

    public void setRealmId(Integer realmId) {
        this.realmId = realmId;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(String monsterType) {
        this.monsterType = monsterType;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(Integer attackPower) {
        this.attackPower = attackPower;
    }

    public Integer getDefensePower() {
        return defensePower;
    }

    public void setDefensePower(Integer defensePower) {
        this.defensePower = defensePower;
    }

    public String getAttackElement() {
        return attackElement;
    }

    public void setAttackElement(String attackElement) {
        this.attackElement = attackElement;
    }

    public Integer getPhysicalResist() {
        return physicalResist;
    }

    public void setPhysicalResist(Integer physicalResist) {
        this.physicalResist = physicalResist;
    }

    public Integer getIceResist() {
        return iceResist;
    }

    public void setIceResist(Integer iceResist) {
        this.iceResist = iceResist;
    }

    public Integer getFireResist() {
        return fireResist;
    }

    public void setFireResist(Integer fireResist) {
        this.fireResist = fireResist;
    }

    public Integer getLightningResist() {
        return lightningResist;
    }

    public void setLightningResist(Integer lightningResist) {
        this.lightningResist = lightningResist;
    }

    public Integer getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(Integer staminaCost) {
        this.staminaCost = staminaCost;
    }

    public Integer getExpReward() {
        return expReward;
    }

    public void setExpReward(Integer expReward) {
        this.expReward = expReward;
    }

    public Integer getSpiritStonesReward() {
        return spiritStonesReward;
    }

    public void setSpiritStonesReward(Integer spiritStonesReward) {
        this.spiritStonesReward = spiritStonesReward;
    }

    public List<MonsterDropResponse> getDrops() {
        return drops;
    }

    public void setDrops(List<MonsterDropResponse> drops) {
        this.drops = drops;
    }

    /**
     * 怪物掉落配置响应
     */
    public static class MonsterDropResponse {
        private Long monsterDropId;
        private Long monsterId;

        // 新字段
        private String itemType; // equipment/material
        private Long itemId;

        // 兼容旧字段
        @Deprecated
        private Long equipmentId;

        // 动态字段（根据itemType变化）
        private String itemName; // 物品名称
        private String equipmentName;
        private String equipmentType;
        private String quality;
        private Integer materialTier; // 材料品阶

        private java.math.BigDecimal dropRate;
        private Integer dropQuantity;
        private String minQuality;
        private String maxQuality;
        private Boolean isGuaranteed;

        public Long getMonsterDropId() {
            return monsterDropId;
        }

        public void setMonsterDropId(Long monsterDropId) {
            this.monsterDropId = monsterDropId;
        }

        public Long getMonsterId() {
            return monsterId;
        }

        public void setMonsterId(Long monsterId) {
            this.monsterId = monsterId;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public Long getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(Long equipmentId) {
            this.equipmentId = equipmentId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getEquipmentName() {
            return equipmentName;
        }

        public void setEquipmentName(String equipmentName) {
            this.equipmentName = equipmentName;
        }

        public String getEquipmentType() {
            return equipmentType;
        }

        public void setEquipmentType(String equipmentType) {
            this.equipmentType = equipmentType;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public Integer getMaterialTier() {
            return materialTier;
        }

        public void setMaterialTier(Integer materialTier) {
            this.materialTier = materialTier;
        }

        public java.math.BigDecimal getDropRate() {
            return dropRate;
        }

        public void setDropRate(java.math.BigDecimal dropRate) {
            this.dropRate = dropRate;
        }

        public Integer getDropQuantity() {
            return dropQuantity;
        }

        public void setDropQuantity(Integer dropQuantity) {
            this.dropQuantity = dropQuantity;
        }

        public String getMinQuality() {
            return minQuality;
        }

        public void setMinQuality(String minQuality) {
            this.minQuality = minQuality;
        }

        public String getMaxQuality() {
            return maxQuality;
        }

        public void setMaxQuality(String maxQuality) {
            this.maxQuality = maxQuality;
        }

        public Boolean getIsGuaranteed() {
            return isGuaranteed;
        }

        public void setIsGuaranteed(Boolean isGuaranteed) {
            this.isGuaranteed = isGuaranteed;
        }
    }
}
