package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.Monster;
import com.xiuxian.mapper.MonsterMapper;
import com.xiuxian.service.MonsterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 妖兽Service实现类
 */
@Service
public class MonsterServiceImpl extends ServiceImpl<MonsterMapper, Monster> implements MonsterService {

    @Override
    public List<Monster> getMonstersByRealmId(Integer realmId) {
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Monster::getRealmId, realmId)
                .orderByAsc(Monster::getMonsterType)
                .orderByAsc(Monster::getHp);
        return this.list(wrapper);
    }

    @Override
    public List<Monster> getAllMonsters() {
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Monster::getRealmId)
                .orderByAsc(Monster::getMonsterType);
        return this.list(wrapper);
    }
}
