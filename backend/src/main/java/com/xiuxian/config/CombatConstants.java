package com.xiuxian.config;

/**
 * 战斗系统常量配置
 */
public class CombatConstants {

    /**
     * 技能冷却回合数
     */
    public static final int SKILL_COOLDOWN_TURNS = 4;

    /**
     * 攻击槽位范围（槽位1-5）
     */
    public static final int ATTACK_SLOT_MIN = 1;
    public static final int ATTACK_SLOT_MAX = 5;

    /**
     * 防御/辅助槽位范围（槽位6-8）
     */
    public static final int SUPPORT_SLOT_MIN = 6;
    public static final int SUPPORT_SLOT_MAX = 8;

    /**
     * 技能类型常量
     */
    public static final String FUNCTION_TYPE_ATTACK = "攻击";
    public static final String FUNCTION_TYPE_SPELL = "法术";
    public static final String FUNCTION_TYPE_DEFENSE = "防御";
    public static final String FUNCTION_TYPE_SUPPORT = "辅助";

    /**
     * 判断是否为攻击类技能（包括攻击和法术）
     *
     * @param functionType 技能功能类型
     * @return 是否为攻击类技能
     */
    public static boolean isAttackSkill(String functionType) {
        return FUNCTION_TYPE_ATTACK.equals(functionType) ||
               FUNCTION_TYPE_SPELL.equals(functionType);
    }

    /**
     * 判断是否为防御/辅助类技能
     *
     * @param functionType 技能功能类型
     * @return 是否为防御/辅助类技能
     */
    public static boolean isSupportSkill(String functionType) {
        return FUNCTION_TYPE_DEFENSE.equals(functionType) ||
               FUNCTION_TYPE_SUPPORT.equals(functionType);
    }

    /**
     * 判断技能是否可以装备到指定槽位
     *
     * @param functionType 技能功能类型
     * @param slotIndex    槽位索引（1-8）
     * @return 是否可以装备
     */
    public static boolean canEquipToSlot(String functionType, int slotIndex) {
        boolean isAttack = isAttackSkill(functionType);
        boolean isSupport = isSupportSkill(functionType);

        boolean isAttackSlot = slotIndex >= ATTACK_SLOT_MIN && slotIndex <= ATTACK_SLOT_MAX;
        boolean isSupportSlot = slotIndex >= SUPPORT_SLOT_MIN && slotIndex <= SUPPORT_SLOT_MAX;

        return (isAttack && isAttackSlot) || (isSupport && isSupportSlot);
    }

    /**
     * 获取槽位类型描述
     *
     * @param slotIndex 槽位索引
     * @return 槽位类型描述
     */
    public static String getSlotTypeDescription(int slotIndex) {
        if (slotIndex >= ATTACK_SLOT_MIN && slotIndex <= ATTACK_SLOT_MAX) {
            return "[攻击]";
        } else if (slotIndex >= SUPPORT_SLOT_MIN && slotIndex <= SUPPORT_SLOT_MAX) {
            return "[防御/辅助]";
        }
        return "[未知]";
    }

    private CombatConstants() {
        // 防止实例化
    }
}
