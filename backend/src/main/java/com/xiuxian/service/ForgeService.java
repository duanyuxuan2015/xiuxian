package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.ForgeRequest;
import com.xiuxian.dto.response.EquipmentRecipeResponse;
import com.xiuxian.dto.response.ForgeResponse;
import com.xiuxian.entity.ForgeRecord;

import java.util.List;

/**
 * 锻造Service接口
 */
public interface ForgeService extends IService<ForgeRecord> {

    /**
     * 获取可用装备配方列表
     * @param characterId 角色ID
     * @return 配方列表
     */
    List<EquipmentRecipeResponse> getAvailableRecipes(Long characterId);

    /**
     * 获取配方详情
     * @param characterId 角色ID
     * @param recipeId 配方ID
     * @return 配方详情
     */
    EquipmentRecipeResponse getRecipeDetail(Long characterId, Long recipeId);

    /**
     * 开始锻造
     * @param request 锻造请求
     * @return 锻造结果
     */
    ForgeResponse startForge(ForgeRequest request);

    /**
     * 获取锻造记录
     * @param characterId 角色ID
     * @return 锻造记录列表
     */
    List<ForgeResponse> getForgeRecords(Long characterId);

    /**
     * 计算锻造成功率
     * @param characterId 角色ID
     * @param recipeId 配方ID
     * @return 成功率百分比
     */
    int calculateSuccessRate(Long characterId, Long recipeId);
}
