package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.service.SectMemberService;
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
 * 宗门控制器
 */
@RestController
@RequestMapping("/sect")
public class SectController {

    private final SectMemberService sectMemberService;

    public SectController(SectMemberService sectMemberService) {
        this.sectMemberService = sectMemberService;
    }

    /**
     * 获取所有宗门列表
     * GET /api/v1/sect/list
     */
    @GetMapping("/list")
    public Result<List<SectResponse>> getAllSects(@RequestParam("characterId") Long characterId) {
        List<SectResponse> sects = sectMemberService.getAllSects(characterId);
        return Result.success(sects);
    }

    /**
     * 获取角色的宗门信息
     * GET /api/v1/sect/my/{characterId}
     */
    @GetMapping("/my/{characterId}")
    public Result<SectMemberResponse> getCharacterSect(@PathVariable Long characterId) {
        SectMemberResponse member = sectMemberService.getCharacterSect(characterId);
        return Result.success(member);
    }

    /**
     * 加入宗门
     * POST /api/v1/sect/join
     */
    @PostMapping("/join")
    public Result<SectMemberResponse> joinSect(@Valid @RequestBody JoinSectRequest request) {
        SectMemberResponse response = sectMemberService.joinSect(request);
        return Result.success(response);
    }

    /**
     * 退出宗门
     * DELETE /api/v1/sect/leave/{characterId}
     */
    @DeleteMapping("/leave/{characterId}")
    public Result<Boolean> leaveSect(@PathVariable Long characterId) {
        boolean success = sectMemberService.leaveSect(characterId);
        return Result.success(success);
    }

    /**
     * 获取宗门商店物品
     * GET /api/v1/sect/shop/{characterId}
     */
    @GetMapping("/shop/{characterId}")
    public Result<List<SectShopItemResponse>> getShopItems(@PathVariable Long characterId) {
        List<SectShopItemResponse> items = sectMemberService.getShopItems(characterId);
        return Result.success(items);
    }

    /**
     * 购买宗门商店物品
     * POST /api/v1/sect/shop/buy
     */
    @PostMapping("/shop/buy")
    public Result<String> buyShopItem(@Valid @RequestBody SectShopBuyRequest request) {
        String message = sectMemberService.buyShopItem(request);
        return Result.success(message);
    }

    /**
     * 获取宗门成员列表
     * GET /api/v1/sect/members/{sectId}
     */
    @GetMapping("/members/{sectId}")
    public Result<List<SectMemberResponse>> getSectMembers(@PathVariable Long sectId) {
        List<SectMemberResponse> members = sectMemberService.getSectMembers(sectId);
        return Result.success(members);
    }
}
