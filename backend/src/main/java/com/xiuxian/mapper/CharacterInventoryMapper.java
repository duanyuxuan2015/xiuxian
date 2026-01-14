package com.xiuxian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiuxian.entity.CharacterInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色背包Mapper接口
 */
@Mapper
public interface CharacterInventoryMapper extends BaseMapper<CharacterInventory> {
}
