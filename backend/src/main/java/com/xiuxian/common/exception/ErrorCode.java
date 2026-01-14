package com.xiuxian.common.exception;

/**
 * 错误码枚举
 *
 * @author CodeGenerator
 * @date 2026-01-13
 */
public enum ErrorCode {
    // 通用错误
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),

    // 角色相关 (1xxx)
    CHARACTER_NOT_FOUND(1001, "角色不存在"),
    CHARACTER_NAME_EXISTS(1002, "角色姓名已存在"),
    INSUFFICIENT_POINTS(1003, "可用点数不足"),
    ATTRIBUTE_VALUE_EXCEED(1004, "属性值超过上限"),

    // 修炼相关 (2xxx)
    INSUFFICIENT_STAMINA(2001, "体力不足"),
    INVALID_STATE(2002, "当前状态不允许此操作"),
    REALM_LEVEL_NOT_READY(2003, "境界层数不足,无法突破"),
    INSUFFICIENT_EXP(2004, "经验值不足"),
    BREAKTHROUGH_FAILED(2005, "突破失败"),

    // 战斗相关 (3xxx)
    MONSTER_NOT_FOUND(3001, "妖兽不存在"),
    COMBAT_NOT_FOUND(3002, "战斗不存在"),
    NOT_YOUR_TURN(3003, "不是你的回合"),
    COMBAT_ALREADY_ENDED(3004, "战斗已结束"),

    // 炼丹相关 (4xxx)
    RECIPE_NOT_FOUND(4001, "丹方不存在"),
    INSUFFICIENT_ALCHEMY_LEVEL(4002, "炼丹等级不足"),
    INSUFFICIENT_MATERIALS(4003, "材料不足"),
    INSUFFICIENT_SPIRITUAL_POWER(4004, "灵力不足"),
    ALCHEMY_NOT_COMPLETE(4005, "炼丹尚未完成"),

    // 锻造相关 (5xxx)
    EQUIPMENT_RECIPE_NOT_FOUND(5001, "装备图纸不存在"),
    INSUFFICIENT_FORGING_LEVEL(5002, "炼器等级不足"),
    FORGE_NOT_COMPLETE(5003, "锻造尚未完成"),

    // 背包相关 (6xxx)
    ITEM_NOT_FOUND(6001, "物品不存在"),
    INSUFFICIENT_QUANTITY(6002, "物品数量不足"),
    EQUIPMENT_NOT_FOUND(6003, "装备不存在"),
    INVALID_EQUIPMENT_SLOT(6004, "无效的装备槽位"),

    // 技能相关 (7xxx)
    SKILL_NOT_FOUND(7001, "技能不存在"),
    SKILL_ALREADY_LEARNED(7002, "技能已学习"),
    INSUFFICIENT_SKILL_COST(7003, "学习费用不足"),
    SKILL_SLOT_FULL(7004, "技能槽位已满"),

    // 宗门相关 (8xxx)
    SECT_NOT_FOUND(8001, "宗门不存在"),
    ALREADY_IN_SECT(8002, "已加入其他宗门"),
    INSUFFICIENT_CONTRIBUTION(8003, "贡献度不足"),
    REALM_NOT_MEET_REQUIREMENT(8004, "境界不满足加入要求"),
    INSUFFICIENT_REPUTATION(8005, "声望不足"),

    // 探索相关 (9xxx)
    EXPLORATION_NOT_COMPLETE(9001, "探索尚未完成"),
    INVALID_EVENT_TYPE(9002, "无效的事件类型");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
