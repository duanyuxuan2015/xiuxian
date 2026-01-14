package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.service.ExplorationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 探索控制器
 */
@RestController
@RequestMapping("/exploration")
public class ExplorationController {

    private final ExplorationService explorationService;

    public ExplorationController(ExplorationService explorationService) {
        this.explorationService = explorationService;
    }

    /**
     * 获取所有探索区域
     * GET /api/v1/exploration/areas/{characterId}
     */
    @GetMapping("/areas/{characterId}")
    public Result<List<ExplorationAreaResponse>> getAllAreas(@PathVariable Long characterId) {
        List<ExplorationAreaResponse> areas = explorationService.getAllAreas(characterId);
        return Result.success(areas);
    }

    /**
     * 开始探索
     * POST /api/v1/exploration/start
     */
    @PostMapping("/start")
    public Result<ExplorationResponse> startExploration(@Valid @RequestBody ExplorationRequest request) {
        ExplorationResponse response = explorationService.startExploration(request);
        return Result.success(response);
    }

    /**
     * 获取探索记录
     * GET /api/v1/exploration/records/{characterId}
     */
    @GetMapping("/records/{characterId}")
    public Result<List<ExplorationResponse>> getExplorationRecords(@PathVariable Long characterId) {
        List<ExplorationResponse> records = explorationService.getExplorationRecords(characterId);
        return Result.success(records);
    }
}
