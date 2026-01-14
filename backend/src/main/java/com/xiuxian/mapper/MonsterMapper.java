package com.xiuxian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiuxian.entity.Monster;
import org.apache.ibatis.annotations.Mapper;

/**
 * 妖兽Mapper接口
 */
@Mapper
public interface MonsterMapper extends BaseMapper<Monster> {
}
