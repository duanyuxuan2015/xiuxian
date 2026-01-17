package com.xiuxian.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 创建怪物请求DTO
 */
public class MonsterCreateRequest {

    @NotBlank(message = "怪物名称不能为空")
    @Size(min = 2, max = 50, message = "怪物名称长度必须在2-50个字符之间")
    private String monsterName;

    @NotNull(message = "境界不能为空")
    private Integer realmId;

    @NotBlank(message = "怪物类型不能为空")
    private String monsterType;

    @NotNull(message = "速度不能为空")
    @Min(value = 1, message = "速度必须大于等于1")
    @Max(value = 999, message = "速度必须小于等于999")
    private Integer speed;

    @NotNull(message = "生命值不能为空")
    @Min(value = 1, message = "生命值必须大于等于1")
    @Max(value = 99999, message = "生命值必须小于等于99999")
    private Integer hp;

    @NotNull(message = "攻击力不能为空")
    @Min(value = 0, message = "攻击力必须大于等于0")
    @Max(value = 9999, message = "攻击力必须小于等于9999")
    private Integer attackPower;

    @NotNull(message = "防御力不能为空")
    @Min(value = 0, message = "防御力必须大于等于0")
    @Max(value = 9999, message = "防御力必须小于等于9999")
    private Integer defensePower;

    @NotBlank(message = "攻击元素不能为空")
    private String attackElement;

    @Min(value = 0, message = "物理抗性必须大于等于0")
    @Max(value = 100, message = "物理抗性必须小于等于100")
    private Integer physicalResist = 0;

    @Min(value = 0, message = "冰抗性必须大于等于0")
    @Max(value = 100, message = "冰抗性必须小于等于100")
    private Integer iceResist = 0;

    @Min(value = 0, message = "火抗性必须大于等于0")
    @Max(value = 100, message = "火抗性必须小于等于100")
    private Integer fireResist = 0;

    @Min(value = 0, message = "雷抗性必须大于等于0")
    @Max(value = 100, message = "雷抗性必须小于等于100")
    private Integer lightningResist = 0;

    @NotNull(message = "体力消耗不能为空")
    @Min(value = 0, message = "体力消耗必须大于等于0")
    @Max(value = 100, message = "体力消耗必须小于等于100")
    private Integer staminaCost;

    @NotNull(message = "经验奖励不能为空")
    @Min(value = 0, message = "经验奖励必须大于等于0")
    private Integer expReward;

    @NotNull(message = "灵石奖励不能为空")
    @Min(value = 0, message = "灵石奖励必须大于等于0")
    private Integer spiritStonesReward;

    private List<MonsterDropRequest> drops;

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

    public List<MonsterDropRequest> getDrops() {
        return drops;
    }

    public void setDrops(List<MonsterDropRequest> drops) {
        this.drops = drops;
    }
}
