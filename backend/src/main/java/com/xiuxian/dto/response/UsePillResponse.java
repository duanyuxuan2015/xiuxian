package com.xiuxian.dto.response;

/**
 * 使用丹药响应DTO
 */
public class UsePillResponse {
    private String pillName;         // 丹药名称
    private Integer quantityUsed;     // 使用数量
    private String effectType;        // 效果类型
    private Integer totalEffect;      // 总效果值
    private String message;           // 结果消息

    // 更新后的角色属性
    private Integer currentHealth;
    private Integer currentSpirit;
    private Long experience;
    private Integer comprehension;
    private Integer attack;
    private Integer defense;

    public UsePillResponse() {
    }

    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public Integer getTotalEffect() {
        return totalEffect;
    }

    public void setTotalEffect(Integer totalEffect) {
        this.totalEffect = totalEffect;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(Integer currentHealth) {
        this.currentHealth = currentHealth;
    }

    public Integer getCurrentSpirit() {
        return currentSpirit;
    }

    public void setCurrentSpirit(Integer currentSpirit) {
        this.currentSpirit = currentSpirit;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Integer getComprehension() {
        return comprehension;
    }

    public void setComprehension(Integer comprehension) {
        this.comprehension = comprehension;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }
}
