package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.AllocatePointsRequest;
import com.xiuxian.dto.request.CharacterCreateRequest;
import com.xiuxian.dto.response.AllocatePointsResponse;
import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.entity.PlayerCharacter;

/**
 * 角色Service接口
 */
public interface CharacterService extends IService<PlayerCharacter> {

    /**
     * 创建新角色
     * 
     * @param request 角色创建请求
     * @return 创建的角色响应
     */
    CharacterResponse createCharacter(CharacterCreateRequest request);

    /**
     * 根据ID获取角色详情
     * 
     * @param characterId 角色ID
     * @return 角色响应
     */
    CharacterResponse getCharacterById(Long characterId);

    /**
     * 检查角色名是否已存在
     * 
     * @param playerName 角色名
     * @return 是否存在
     */
    boolean checkNameExists(String playerName);

    /**
     * 更新角色信息
     *
     * @param character 角色实体
     * @return 是否更新成功
     */
    boolean updateCharacter(PlayerCharacter character);

    /**
     * 分配属性点
     *
     * @param request 属性点分配请求
     * @return 属性点分配响应
     */
    AllocatePointsResponse allocatePoints(AllocatePointsRequest request);
}
