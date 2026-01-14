package com.xiuxian.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;
import com.xiuxian.service.CombatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 战斗控制器
 */
@RestController
@RequestMapping("/combat")
public class CombatController {

    private final CombatService combatService;

    public CombatController(CombatService combatService) {
        this.combatService = combatService;
    }

    /**
     * 开始战斗
     * POST /api/v1/combat/start
     */
    @PostMapping("/start")
    public Result<CombatResponse> startCombat(@Valid @RequestBody CombatStartRequest request) {
        CombatResponse response = combatService.startCombat(request);
        return Result.success(response);
    }

    /**
     * 获取可挑战的妖兽列表
     * GET /api/v1/combat/monsters
     */
    @GetMapping("/monsters")
    public Result<List<Monster>> getAvailableMonsters(@RequestParam("characterId") Long characterId) {
        List<Monster> monsters = combatService.getAvailableMonsters(characterId);
        return Result.success(monsters);
    }

    /**
     * 获取战斗记录
     * GET /api/v1/combat/records
     */
    @GetMapping("/records")
    public Result<PageResult<CombatRecord>> getCombatRecords(
            @RequestParam("characterId") Long characterId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<CombatRecord> pageResult = combatService.getCombatRecords(characterId, page, pageSize);

        PageResult<CombatRecord> result = PageResult.of(
                pageResult.getRecords(),
                pageResult.getTotal(),
                (int) pageResult.getCurrent(),
                (int) pageResult.getSize()
        );

        return Result.success(result);
    }
}
