package com.xiuxian.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.BreakthroughRequest;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.request.MeditationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.dto.response.MeditationResponse;
import com.xiuxian.dto.response.MeditationTimeResponse;
import com.xiuxian.entity.CultivationRecord;
import com.xiuxian.service.CultivationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 修炼控制器
 */
@RestController
@RequestMapping("/cultivation")
public class CultivationController {

    private final CultivationService cultivationService;

    public CultivationController(CultivationService cultivationService) {
        this.cultivationService = cultivationService;
    }

    /**
     * 开始修炼
     * POST /api/v1/cultivation/start
     */
    @PostMapping("/start")
    public Result<CultivationResponse> startCultivation(@Valid @RequestBody CultivationRequest request) {
        CultivationResponse response = cultivationService.startCultivation(request);
        return Result.success(response);
    }

    /**
     * 获取修炼记录
     * GET /api/v1/cultivation/records
     */
    @GetMapping("/records")
    public Result<PageResult<CultivationRecord>> getCultivationRecords(
            @RequestParam("characterId") Long characterId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<CultivationRecord> pageResult = cultivationService.getCultivationRecords(characterId, page, pageSize);

        PageResult<CultivationRecord> result = PageResult.of(
                pageResult.getRecords(),
                pageResult.getTotal(),
                (int) pageResult.getCurrent(),
                (int) pageResult.getSize()
        );

        return Result.success(result);
    }

    /**
     * 尝试境界突破
     * POST /api/v1/cultivation/breakthrough
     */
    @PostMapping("/breakthrough")
    public Result<BreakthroughResponse> attemptBreakthrough(@Valid @RequestBody BreakthroughRequest request) {
        BreakthroughResponse response = cultivationService.attemptBreakthrough(request);
        return Result.success(response);
    }

    /**
     * 获取突破成功率
     * GET /api/v1/cultivation/breakthrough-rate
     */
    @GetMapping("/breakthrough-rate")
    public Result<Integer> getBreakthroughRate(@RequestParam("characterId") Long characterId) {
        int rate = cultivationService.calculateBreakthroughRate(characterId);
        return Result.success(rate);
    }

    /**
     * 打坐恢复体力和灵力
     * POST /api/v1/cultivation/meditation
     */
    @PostMapping("/meditation")
    public Result<MeditationResponse> meditation(@Valid @RequestBody MeditationRequest request) {
        MeditationResponse response = cultivationService.meditation(request);
        return Result.success(response);
    }

    /**
     * 获取打坐所需时间
     * GET /api/v1/cultivation/meditation/time
     */
    @GetMapping("/meditation/time")
    public Result<MeditationTimeResponse> getMeditationTime(@RequestParam("characterId") Long characterId) {
        MeditationTimeResponse response = cultivationService.getMeditationTime(characterId);
        return Result.success(response);
    }
}
