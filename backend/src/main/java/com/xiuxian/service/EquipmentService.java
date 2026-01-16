package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.Equipment;

import java.util.List;

/**
 * 装备Service
 */
public interface EquipmentService extends IService<Equipment> {

    /**
     * 获取角色装备列表
     */
    List<EquipmentResponse> getCharacterEquipments(Long characterId);

    /**
     * 穿戴装备
     */
    EquipmentResponse equipItem(EquipRequest request);

    /**
     * 卸下装备
     */
    boolean unequipItem(Long characterId, String equipmentSlot);

    /**
     * 计算装备加成
     */
    EquipmentBonus calculateEquipmentBonus(Long characterId);

    /**
     * 装备加成
     */
    class EquipmentBonus {
        public Integer attackBonus;
        public Integer defenseBonus;
        public Integer healthBonus;
        public Integer staminaBonus;
        public Integer spiritualPowerBonus;
        public Integer criticalRateBonus;
        public Integer speedBonus;
        public Integer physicalResistBonus;
        public Integer iceResistBonus;
        public Integer fireResistBonus;
        public Integer lightningResistBonus;
    }
}
