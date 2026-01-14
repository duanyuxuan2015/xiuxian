package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterEquipment;

import java.util.List;

/**
 * 装备Service接口
 */
public interface EquipmentService extends IService<CharacterEquipment> {

    /**
     * 获取角色已装备的装备列表
     * @param characterId 角色ID
     * @return 装备列表
     */
    List<EquipmentResponse> getCharacterEquipments(Long characterId);

    /**
     * 装备物品
     * @param request 装备请求
     * @return 装备响应
     */
    EquipmentResponse equipItem(EquipRequest request);

    /**
     * 卸下装备
     * @param characterId 角色ID
     * @param equipmentSlot 装备槽位
     * @return 是否成功
     */
    boolean unequipItem(Long characterId, String equipmentSlot);

    /**
     * 计算角色装备总加成
     * @param characterId 角色ID
     * @return 装备加成数据
     */
    EquipmentBonus calculateEquipmentBonus(Long characterId);

    /**
     * 装备加成数据类
     */
    class EquipmentBonus {
        private int attackPower;
        private int defensePower;
        private int healthBonus;
        private int criticalRate;
        private int speedBonus;
        private int physicalResist;
        private int iceResist;
        private int fireResist;
        private int lightningResist;

        public int getAttackPower() {
            return attackPower;
        }

        public void setAttackPower(int attackPower) {
            this.attackPower = attackPower;
        }

        public int getDefensePower() {
            return defensePower;
        }

        public void setDefensePower(int defensePower) {
            this.defensePower = defensePower;
        }

        public int getHealthBonus() {
            return healthBonus;
        }

        public void setHealthBonus(int healthBonus) {
            this.healthBonus = healthBonus;
        }

        public int getCriticalRate() {
            return criticalRate;
        }

        public void setCriticalRate(int criticalRate) {
            this.criticalRate = criticalRate;
        }

        public int getSpeedBonus() {
            return speedBonus;
        }

        public void setSpeedBonus(int speedBonus) {
            this.speedBonus = speedBonus;
        }

        public int getPhysicalResist() {
            return physicalResist;
        }

        public void setPhysicalResist(int physicalResist) {
            this.physicalResist = physicalResist;
        }

        public int getIceResist() {
            return iceResist;
        }

        public void setIceResist(int iceResist) {
            this.iceResist = iceResist;
        }

        public int getFireResist() {
            return fireResist;
        }

        public void setFireResist(int fireResist) {
            this.fireResist = fireResist;
        }

        public int getLightningResist() {
            return lightningResist;
        }

        public void setLightningResist(int lightningResist) {
            this.lightningResist = lightningResist;
        }
    }
}
