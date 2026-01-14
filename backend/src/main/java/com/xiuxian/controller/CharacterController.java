package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.AllocatePointsRequest;
import com.xiuxian.dto.request.CharacterCreateRequest;
import com.xiuxian.dto.response.AllocatePointsResponse;
import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.service.CharacterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    /**
     * 创建新角色
     * POST /api/v1/characters
     */
    @PostMapping
    public Result<CharacterResponse> createCharacter(@Valid @RequestBody CharacterCreateRequest request) {
        CharacterResponse response = characterService.createCharacter(request);
        return Result.success(response);
    }

    /**
     * 获取角色详情
     * GET /api/v1/characters/{id}
     */
    @GetMapping("/{id}")
    public Result<CharacterResponse> getCharacterById(@PathVariable("id") Long characterId) {
        CharacterResponse response = characterService.getCharacterById(characterId);
        return Result.success(response);
    }

    /**
     * 检查角色名是否存在
     * GET /api/v1/characters/check-name/{name}
     */
    @GetMapping("/check-name/{name}")
    public Result<Boolean> checkNameExists(@PathVariable("name") String playerName) {
        boolean exists = characterService.checkNameExists(playerName);
        return Result.success(exists);
    }

    /**
     * 分配属性点
     * POST /api/v1/characters/allocate-points
     */
    @PostMapping("/allocate-points")
    public Result<AllocatePointsResponse> allocatePoints(@Valid @RequestBody AllocatePointsRequest request) {
        AllocatePointsResponse response = characterService.allocatePoints(request);
        return Result.success(response);
    }
}
