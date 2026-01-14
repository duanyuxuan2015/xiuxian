package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.entity.Monster;

import java.util.List;

/**
 * 妖兽Service接口
 */
public interface MonsterService extends IService<Monster> {

    /**
     * 根据境界ID获取妖兽列表
     * @param realmId 境界ID
     * @return 妖兽列表
     */
    List<Monster> getMonstersByRealmId(Integer realmId);

    /**
     * 获取所有妖兽列表
     * @return 妖兽列表
     */
    List<Monster> getAllMonsters();
}
