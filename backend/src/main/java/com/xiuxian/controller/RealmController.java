package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.response.Result;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.RealmMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 境界管理控制器
 */
@RestController
@RequestMapping("/admin/realms")
public class RealmController {

    private static final Logger logger = LoggerFactory.getLogger(RealmController.class);

    @Autowired
    private RealmMapper realmMapper;

    /**
     * 获取所有境界列表
     */
    @GetMapping
    public Result<List<Realm>> getAllRealms() {
        logger.info("获取所有境界列表");

        LambdaQueryWrapper<Realm> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Realm::getRealmLevel);

        List<Realm> realms = realmMapper.selectList(wrapper);

        logger.info("境界列表查询成功: count={}", realms.size());
        return Result.success(realms);
    }
}
