package com.xiuxian.client.model;

/**
 * 妖兽信息
 * 注意：移除了LocalDateTime字段以避免Gson解析问题
 */
public class Monster {
    private Long monsterId;
    private String monsterName;
    private Integer realmId;
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
    private Integer deleted;

    // Getters
    public Long getMonsterId() { return monsterId; }
    public String getMonsterName() { return monsterName; }
    public Integer getRealmId() { return realmId; }
    public String getMonsterType() { return monsterType; }
    public Integer getSpeed() { return speed; }
    public Integer getHp() { return hp; }
    public Integer getAttackPower() { return attackPower; }
    public Integer getDefensePower() { return defensePower; }
    public String getAttackElement() { return attackElement; }
    public Integer getPhysicalResist() { return physicalResist; }
    public Integer getIceResist() { return iceResist; }
    public Integer getFireResist() { return fireResist; }
    public Integer getLightningResist() { return lightningResist; }
    public Integer getStaminaCost() { return staminaCost; }
    public Integer getExpReward() { return expReward; }
    public Integer getSpiritStonesReward() { return spiritStonesReward; }
    public Integer getDeleted() { return deleted; }

    // 兼容方法 - 为了保持向后兼容
    public Integer getAttack() { return attackPower; }
    public Integer getDefense() { return defensePower; }
    public Integer getHealth() { return hp; }
    public String getRealmName() {
        // 根据realmId返回境界名称
        if (realmId == null) return "未知";
        switch (realmId) {
            case 1: return "炼气";
            case 2: return "筑基";
            case 3: return "金丹";
            case 4: return "元婴";
            case 5: return "化神";
            default: return "境界" + realmId;
        }
    }

    // Setters
    public void setMonsterId(Long monsterId) { this.monsterId = monsterId; }
    public void setMonsterName(String monsterName) { this.monsterName = monsterName; }
    public void setRealmId(Integer realmId) { this.realmId = realmId; }
    public void setMonsterType(String monsterType) { this.monsterType = monsterType; }
    public void setSpeed(Integer speed) { this.speed = speed; }
    public void setHp(Integer hp) { this.hp = hp; }
    public void setAttackPower(Integer attackPower) { this.attackPower = attackPower; }
    public void setDefensePower(Integer defensePower) { this.defensePower = defensePower; }
    public void setAttackElement(String attackElement) { this.attackElement = attackElement; }
    public void setPhysicalResist(Integer physicalResist) { this.physicalResist = physicalResist; }
    public void setIceResist(Integer iceResist) { this.iceResist = iceResist; }
    public void setFireResist(Integer fireResist) { this.fireResist = fireResist; }
    public void setLightningResist(Integer lightningResist) { this.lightningResist = lightningResist; }
    public void setStaminaCost(Integer staminaCost) { this.staminaCost = staminaCost; }
    public void setExpReward(Integer expReward) { this.expReward = expReward; }
    public void setSpiritStonesReward(Integer spiritStonesReward) { this.spiritStonesReward = spiritStonesReward; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
