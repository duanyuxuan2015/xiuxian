package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.entity.Realm;

import java.util.List;

/**
 * 境界Service接口
 */
public interface RealmService extends IService<Realm> {

    /**
     * 根据境界等级获取境界信息
     * @param realmLevel 境界等级
     * @return 境界信息
     */
    Realm getByRealmLevel(Integer realmLevel);

    /**
     * 获取下一个境界
     * @param currentRealmLevel 当前境界等级
     * @return 下一个境界，如果已是最高境界则返回null
     */
    Realm getNextRealm(Integer currentRealmLevel);

    /**
     * 获取所有境界列表
     * @return 境界列表
     */
    List<Realm> getAllRealms();
}
