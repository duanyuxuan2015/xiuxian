package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.RealmMapper;
import com.xiuxian.service.RealmService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 境界Service实现类
 */
@Service
public class RealmServiceImpl extends ServiceImpl<RealmMapper, Realm> implements RealmService {

    @Override
    public Realm getByRealmLevel(Integer realmLevel) {
        LambdaQueryWrapper<Realm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Realm::getRealmLevel, realmLevel);
        return this.getOne(wrapper);
    }

    @Override
    public Realm getNextRealm(Integer currentRealmLevel) {
        LambdaQueryWrapper<Realm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Realm::getRealmLevel, currentRealmLevel + 1);
        return this.getOne(wrapper);
    }

    @Override
    public List<Realm> getAllRealms() {
        LambdaQueryWrapper<Realm> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Realm::getRealmLevel);
        return this.list(wrapper);
    }
}
