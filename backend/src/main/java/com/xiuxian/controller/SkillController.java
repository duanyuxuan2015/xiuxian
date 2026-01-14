package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.EquipSkillRequest;
import com.xiuxian.dto.request.LearnSkillRequest;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 技能控制器
 */
@RestController
@RequestMapping("/skill")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * 获取可学习的技能列表
     * GET /api/v1/skill/available/{characterId}
     */
    @GetMapping("/available/{characterId}")
    public Result<List<SkillResponse>> getAvailableSkills(@PathVariable Long characterId) {
        List<SkillResponse> skills = skillService.getAvailableSkills(characterId);
        return Result.success(skills);
    }

    /**
     * 获取已学习的技能列表
     * GET /api/v1/skill/learned/{characterId}
     */
    @GetMapping("/learned/{characterId}")
    public Result<List<SkillResponse>> getLearnedSkills(@PathVariable Long characterId) {
        List<SkillResponse> skills = skillService.getLearnedSkills(characterId);
        return Result.success(skills);
    }

    /**
     * 获取已装备的技能列表
     * GET /api/v1/skill/equipped/{characterId}
     */
    @GetMapping("/equipped/{characterId}")
    public Result<List<SkillResponse>> getEquippedSkills(@PathVariable Long characterId) {
        List<SkillResponse> skills = skillService.getEquippedSkills(characterId);
        return Result.success(skills);
    }

    /**
     * 学习技能
     * POST /api/v1/skill/learn
     */
    @PostMapping("/learn")
    public Result<SkillResponse> learnSkill(@Valid @RequestBody LearnSkillRequest request) {
        SkillResponse response = skillService.learnSkill(request);
        return Result.success(response);
    }

    /**
     * 装备技能
     * POST /api/v1/skill/equip
     */
    @PostMapping("/equip")
    public Result<SkillResponse> equipSkill(@Valid @RequestBody EquipSkillRequest request) {
        SkillResponse response = skillService.equipSkill(request);
        return Result.success(response);
    }

    /**
     * 卸下技能
     * DELETE /api/v1/skill/unequip
     */
    @DeleteMapping("/unequip")
    public Result<Boolean> unequipSkill(
            @RequestParam("characterId") Long characterId,
            @RequestParam("characterSkillId") Long characterSkillId) {
        boolean success = skillService.unequipSkill(characterId, characterSkillId);
        return Result.success(success);
    }

    /**
     * 升级技能
     * POST /api/v1/skill/upgrade
     */
    @PostMapping("/upgrade")
    public Result<SkillResponse> upgradeSkill(
            @RequestParam("characterId") Long characterId,
            @RequestParam("characterSkillId") Long characterSkillId) {
        SkillResponse response = skillService.upgradeSkill(characterId, characterSkillId);
        return Result.success(response);
    }
}
