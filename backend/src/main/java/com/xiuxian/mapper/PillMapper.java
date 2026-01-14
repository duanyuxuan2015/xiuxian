package com.xiuxian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiuxian.entity.Pill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 丹药Mapper接口
 */
@Mapper
public interface PillMapper extends BaseMapper<Pill> {
}
