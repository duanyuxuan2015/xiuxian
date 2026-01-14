package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.entity.Sect;
import com.xiuxian.mapper.SectMapper;
import com.xiuxian.service.SectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宗门Service实现类
 */
@Service
public class SectServiceImpl extends ServiceImpl<SectMapper, Sect> implements SectService {

    @Override
    public List<Sect> getAllSects() {
        return this.list();
    }

    @Override
    public Sect getBySectName(String sectName) {
        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sect::getSectName, sectName);
        return this.getOne(wrapper);
    }
}
