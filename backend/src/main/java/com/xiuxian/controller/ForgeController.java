package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.ForgeRequest;
import com.xiuxian.dto.response.EquipmentRecipeResponse;
import com.xiuxian.dto.response.ForgeResponse;
import com.xiuxian.service.ForgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 锻造控制器
 */
@RestController
@RequestMapping("/forge")
public class ForgeController {

    private final ForgeService forgeService;

    public ForgeController(ForgeService forgeService) {
        this.forgeService = forgeService;
    }

    /**
     * 获取可用装备配方列表
     * GET /api/v1/forge/recipes/{characterId}
     */
    @GetMapping("/recipes/{characterId}")
    public Result<List<EquipmentRecipeResponse>> getAvailableRecipes(@PathVariable Long characterId) {
        List<EquipmentRecipeResponse> recipes = forgeService.getAvailableRecipes(characterId);
        return Result.success(recipes);
    }

    /**
     * 获取配方详情
     * GET /api/v1/forge/recipe/{recipeId}
     */
    @GetMapping("/recipe/{recipeId}")
    public Result<EquipmentRecipeResponse> getRecipeDetail(
            @RequestParam("characterId") Long characterId,
            @PathVariable Long recipeId) {
        EquipmentRecipeResponse recipe = forgeService.getRecipeDetail(characterId, recipeId);
        return Result.success(recipe);
    }

    /**
     * 开始锻造
     * POST /api/v1/forge/start
     */
    @PostMapping("/start")
    public Result<ForgeResponse> startForge(@Valid @RequestBody ForgeRequest request) {
        ForgeResponse response = forgeService.startForge(request);
        return Result.success(response);
    }

    /**
     * 获取锻造记录
     * GET /api/v1/forge/records/{characterId}
     */
    @GetMapping("/records/{characterId}")
    public Result<List<ForgeResponse>> getForgeRecords(@PathVariable Long characterId) {
        List<ForgeResponse> records = forgeService.getForgeRecords(characterId);
        return Result.success(records);
    }

    /**
     * 计算锻造成功率
     * GET /api/v1/forge/success-rate
     */
    @GetMapping("/success-rate")
    public Result<Integer> calculateSuccessRate(
            @RequestParam("characterId") Long characterId,
            @RequestParam("recipeId") Long recipeId) {
        int rate = forgeService.calculateSuccessRate(characterId, recipeId);
        return Result.success(rate);
    }
}
