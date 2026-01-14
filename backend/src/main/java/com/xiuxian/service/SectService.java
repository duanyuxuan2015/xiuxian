package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.entity.Sect;

import java.util.List;

/**
 * 宗门Service接口
 */
public interface SectService extends IService<Sect> {

    /**
     * 获取所有宗门列表
     * @return 宗门列表
     */
    List<Sect> getAllSects();

    /**
     * 根据宗门名称获取宗门
     * @param sectName 宗门名称
     * @return 宗门信息
     */
    Sect getBySectName(String sectName);
}
