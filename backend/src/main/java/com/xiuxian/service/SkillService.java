package com.xiuxian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiuxian.dto.request.EquipSkillRequest;
import com.xiuxian.dto.request.LearnSkillRequest;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.entity.CharacterSkill;

import java.util.List;

/**
 * 技能Service接口
 */
public interface SkillService extends IService<CharacterSkill> {

    /**
     * 获取可学习的技能列表
     * @param characterId 角色ID
     * @return 技能列表
     */
    List<SkillResponse> getAvailableSkills(Long characterId);

    /**
     * 获取角色已学习的技能列表
     * @param characterId 角色ID
     * @return 技能列表
     */
    List<SkillResponse> getLearnedSkills(Long characterId);

    /**
     * 获取角色已装备的技能列表
     * @param characterId 角色ID
     * @return 技能列表
     */
    List<SkillResponse> getEquippedSkills(Long characterId);

    /**
     * 学习技能
     * @param request 学习请求
     * @return 技能响应
     */
    SkillResponse learnSkill(LearnSkillRequest request);

    /**
     * 装备技能到槽位
     * @param request 装备请求
     * @return 技能响应
     */
    SkillResponse equipSkill(EquipSkillRequest request);

    /**
     * 卸下技能
     * @param characterId 角色ID
     * @param characterSkillId 角色技能ID
     * @return 是否成功
     */
    boolean unequipSkill(Long characterId, Long characterSkillId);

    /**
     * 升级技能
     * @param characterId 角色ID
     * @param characterSkillId 角色技能ID
     * @return 技能响应
     */
    SkillResponse upgradeSkill(Long characterId, Long characterSkillId);

    /**
     * 增加技能熟练度
     * @param characterId 角色ID
     * @param skillId 技能ID
     * @param proficiency 增加的熟练度
     */
    void addProficiency(Long characterId, Long skillId, int proficiency);

    /**
     * 获取所有技能列表
     * @return 所有技能列表
     */
    List<SkillResponse> getAllSkills();
}
