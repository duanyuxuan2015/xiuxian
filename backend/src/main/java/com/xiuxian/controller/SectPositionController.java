package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.PromotePositionRequest;
import com.xiuxian.dto.response.PositionUpgradeInfo;
import com.xiuxian.service.SectMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 宗门职位升级Controller
 */
@RestController
@RequestMapping("/sect/position")
public class SectPositionController {

    private final SectMemberService sectMemberService;

    @Autowired
    public SectPositionController(SectMemberService sectMemberService) {
        this.sectMemberService = sectMemberService;
    }

    /**
     * 获取职位升级信息
     * @param characterId 角色ID
     * @return 升级信息
     */
    @GetMapping("/upgrade-info/{characterId}")
    public Result<PositionUpgradeInfo> getUpgradeInfo(@PathVariable Long characterId) {
        PositionUpgradeInfo info = sectMemberService.getPromotionInfo(characterId);
        return Result.success(info);
    }

    /**
     * 申请职位升级
     * @param request 升级请求
     * @return 升级结果消息
     */
    @PostMapping("/promote")
    public Result<String> promotePosition(@Valid @RequestBody PromotePositionRequest request) {
        String message = sectMemberService.promotePosition(request);
        return Result.success(message);
    }
}
