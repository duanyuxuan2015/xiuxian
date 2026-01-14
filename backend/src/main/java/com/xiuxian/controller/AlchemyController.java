package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.AlchemyRequest;
import com.xiuxian.dto.response.AlchemyResponse;
import com.xiuxian.dto.response.MaterialResponse;
import com.xiuxian.dto.response.PillRecipeResponse;
import com.xiuxian.service.AlchemyService;
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
 * 炼丹控制器
 */
@RestController
@RequestMapping("/alchemy")
public class AlchemyController {

    private final AlchemyService alchemyService;

    public AlchemyController(AlchemyService alchemyService) {
        this.alchemyService = alchemyService;
    }

    /**
     * 获取可用丹方列表
     * GET /api/v1/alchemy/recipes/{characterId}
     */
    @GetMapping("/recipes/{characterId}")
    public Result<List<PillRecipeResponse>> getAvailableRecipes(@PathVariable Long characterId) {
        List<PillRecipeResponse> recipes = alchemyService.getAvailableRecipes(characterId);
        return Result.success(recipes);
    }

    /**
     * 获取丹方详情
     * GET /api/v1/alchemy/recipe/{recipeId}
     */
    @GetMapping("/recipe/{recipeId}")
    public Result<PillRecipeResponse> getRecipeDetail(
            @RequestParam("characterId") Long characterId,
            @PathVariable Long recipeId) {
        PillRecipeResponse recipe = alchemyService.getRecipeDetail(characterId, recipeId);
        return Result.success(recipe);
    }

    /**
     * 开始炼丹
     * POST /api/v1/alchemy/start
     */
    @PostMapping("/start")
    public Result<AlchemyResponse> startAlchemy(@Valid @RequestBody AlchemyRequest request) {
        AlchemyResponse response = alchemyService.startAlchemy(request);
        return Result.success(response);
    }

    /**
     * 获取炼丹记录
     * GET /api/v1/alchemy/records/{characterId}
     */
    @GetMapping("/records/{characterId}")
    public Result<List<AlchemyResponse>> getAlchemyRecords(@PathVariable Long characterId) {
        List<AlchemyResponse> records = alchemyService.getAlchemyRecords(characterId);
        return Result.success(records);
    }

    /**
     * 获取角色拥有的材料
     * GET /api/v1/alchemy/materials/{characterId}
     */
    @GetMapping("/materials/{characterId}")
    public Result<List<MaterialResponse>> getCharacterMaterials(@PathVariable Long characterId) {
        List<MaterialResponse> materials = alchemyService.getCharacterMaterials(characterId);
        return Result.success(materials);
    }

    /**
     * 计算炼丹成功率
     * GET /api/v1/alchemy/success-rate
     */
    @GetMapping("/success-rate")
    public Result<Integer> calculateSuccessRate(
            @RequestParam("characterId") Long characterId,
            @RequestParam("recipeId") Long recipeId) {
        int rate = alchemyService.calculateSuccessRate(characterId, recipeId);
        return Result.success(rate);
    }
}
