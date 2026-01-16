package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.PromotePositionRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.PositionUpgradeInfo;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.entity.SectMember;

import java.util.List;

/**
 * 宗门成员Service接口
 */
public interface SectMemberService extends IService<SectMember> {

    /**
     * 获取所有宗门列表
     * @param characterId 角色ID（用于判断是否已加入）
     * @return 宗门列表
     */
    List<SectResponse> getAllSects(Long characterId);

    /**
     * 获取角色的宗门信息
     * @param characterId 角色ID
     * @return 成员信息
     */
    SectMemberResponse getCharacterSect(Long characterId);

    /**
     * 加入宗门
     * @param request 加入请求
     * @return 成员信息
     */
    SectMemberResponse joinSect(JoinSectRequest request);

    /**
     * 退出宗门
     * @param characterId 角色ID
     * @return 是否成功
     */
    boolean leaveSect(Long characterId);

    /**
     * 获取宗门商店物品
     * @param characterId 角色ID
     * @return 商店物品列表
     */
    List<SectShopItemResponse> getShopItems(Long characterId);

    /**
     * 购买宗门商店物品
     * @param request 购买请求
     * @return 购买结果消息
     */
    String buyShopItem(SectShopBuyRequest request);

    /**
     * 增加宗门贡献
     * @param characterId 角色ID
     * @param contribution 贡献值
     */
    void addContribution(Long characterId, int contribution);

    /**
     * 获取宗门成员列表
     * @param sectId 宗门ID
     * @return 成员列表
     */
    List<SectMemberResponse> getSectMembers(Long sectId);

    /**
     * 根据角色ID获取宗门成员信息
     * @param characterId 角色ID
     * @return 成员信息，未加入宗门则返回null
     */
    SectMember getByCharacterId(Long characterId);

    /**
     * 获取职位升级信息
     * @param characterId 角色ID
     * @return 升级信息
     */
    PositionUpgradeInfo getPromotionInfo(Long characterId);

    /**
     * 申请职位升级
     * @param request 升级请求
     * @return 升级结果消息
     */
    String promotePosition(PromotePositionRequest request);
}
