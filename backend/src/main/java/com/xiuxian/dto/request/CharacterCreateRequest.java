package com.xiuxian.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 角色创建请求DTO
 */
public class CharacterCreateRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 6, message = "角色名称长度必须在2-6个字符之间")
    private String playerName;

    @Min(value = 1, message = "体质属性最小为1")
    @Max(value = 100000, message = "体质属性最大为100000")
    private Integer constitution;

    @Min(value = 1, message = "精神属性最小为1")
    @Max(value = 100000, message = "精神属性最大为100000")
    private Integer spirit;

    @Min(value = 1, message = "悟性属性最小为1")
    @Max(value = 100000, message = "悟性属性最大为100000")
    private Integer comprehension;

    @Min(value = 1, message = "机缘属性最小为1")
    @Max(value = 100000, message = "机缘属性最大为100000")
    private Integer luck;

    @Min(value = 1, message = "气运属性最小为1")
    @Max(value = 100000, message = "气运属性最大为100000")
    private Integer fortune;

    public CharacterCreateRequest() {
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getConstitution() {
        return constitution;
    }

    public void setConstitution(Integer constitution) {
        this.constitution = constitution;
    }

    public Integer getSpirit() {
        return spirit;
    }

    public void setSpirit(Integer spirit) {
        this.spirit = spirit;
    }

    public Integer getComprehension() {
        return comprehension;
    }

    public void setComprehension(Integer comprehension) {
        this.comprehension = comprehension;
    }

    public Integer getLuck() {
        return luck;
    }

    public void setLuck(Integer luck) {
        this.luck = luck;
    }

    public Integer getFortune() {
        return fortune;
    }

    public void setFortune(Integer fortune) {
        this.fortune = fortune;
    }

    /**
     * 获取总分配点数
     */
    public int getTotalPoints() {
        int total = 0;
        if (constitution != null) total += constitution;
        if (spirit != null) total += spirit;
        if (comprehension != null) total += comprehension;
        if (luck != null) total += luck;
        if (fortune != null) total += fortune;
        return total;
    }
}
