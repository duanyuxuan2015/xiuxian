package com.xiuxian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiuxian.entity.PlayerCharacter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 */
@Mapper
public interface CharacterMapper extends BaseMapper<PlayerCharacter> {
}
