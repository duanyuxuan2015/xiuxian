package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.AlchemyRequest;
import com.xiuxian.dto.response.AlchemyResponse;
import com.xiuxian.dto.response.MaterialResponse;
import com.xiuxian.dto.response.PillRecipeResponse;
import com.xiuxian.entity.AlchemyRecord;

import java.util.List;

/**
 * 炼丹Service接口
 */
public interface AlchemyService extends IService<AlchemyRecord> {

    /**
     * 获取可用丹方列表
     * @param characterId 角色ID
     * @return 丹方列表
     */
    List<PillRecipeResponse> getAvailableRecipes(Long characterId);

    /**
     * 获取丹方详情（含材料需求）
     * @param characterId 角色ID
     * @param recipeId 丹方ID
     * @return 丹方详情
     */
    PillRecipeResponse getRecipeDetail(Long characterId, Long recipeId);

    /**
     * 开始炼丹
     * @param request 炼丹请求
     * @return 炼丹结果
     */
    AlchemyResponse startAlchemy(AlchemyRequest request);

    /**
     * 获取炼丹记录
     * @param characterId 角色ID
     * @return 炼丹记录列表
     */
    List<AlchemyResponse> getAlchemyRecords(Long characterId);

    /**
     * 获取角色拥有的材料
     * @param characterId 角色ID
     * @return 材料列表
     */
    List<MaterialResponse> getCharacterMaterials(Long characterId);

    /**
     * 计算炼丹成功率
     * @param characterId 角色ID
     * @param recipeId 丹方ID
     * @return 成功率百分比
     */
    int calculateSuccessRate(Long characterId, Long recipeId);
}
