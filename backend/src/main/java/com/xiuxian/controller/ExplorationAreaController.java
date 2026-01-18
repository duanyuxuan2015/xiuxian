package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.ExplorationAreaCreateRequest;
import com.xiuxian.dto.request.ExplorationAreaUpdateRequest;
import com.xiuxian.dto.response.ExplorationAreaDetailResponse;
import com.xiuxian.service.ExplorationAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 探索区域管理控制器
 */
@RestController
@RequestMapping("/admin/exploration-area")
public class ExplorationAreaController {

    @Autowired
    private ExplorationAreaService explorationAreaService;

    /**
     * 创建探索区域
     */
    @PostMapping
    public Result<ExplorationAreaDetailResponse> createArea(@Valid @RequestBody ExplorationAreaCreateRequest request) {
        ExplorationAreaDetailResponse response = explorationAreaService.createArea(request);
        return Result.success("创建成功", response);
    }

    /**
     * 更新探索区域
     */
    @PutMapping
    public Result<ExplorationAreaDetailResponse> updateArea(@Valid @RequestBody ExplorationAreaUpdateRequest request) {
        ExplorationAreaDetailResponse response = explorationAreaService.updateArea(request);
        return Result.success("更新成功", response);
    }

    /**
     * 删除探索区域
     */
    @DeleteMapping("/{areaId}")
    public Result<String> deleteArea(@PathVariable Long areaId) {
        explorationAreaService.deleteArea(areaId);
        return Result.success("删除成功");
    }

    /**
     * 获取探索区域详情
     */
    @GetMapping("/{areaId}")
    public Result<ExplorationAreaDetailResponse> getAreaDetail(@PathVariable Long areaId) {
        ExplorationAreaDetailResponse response = explorationAreaService.getAreaDetail(areaId);
        return Result.success(response);
    }

    /**
     * 获取所有探索区域列表
     */
    @GetMapping("/list")
    public Result<List<ExplorationAreaDetailResponse>> getAllAreas() {
        List<ExplorationAreaDetailResponse> areas = explorationAreaService.getAllAreas();
        return Result.success(areas);
    }
}
