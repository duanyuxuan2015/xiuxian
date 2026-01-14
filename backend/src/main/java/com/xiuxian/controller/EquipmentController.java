package com.xiuxian.controller;

import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.service.EquipmentService;
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
 * 装备控制器
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * 获取角色已装备的装备
     * GET /api/v1/equipment/character/{characterId}
     */
    @GetMapping("/character/{characterId}")
    public Result<List<EquipmentResponse>> getCharacterEquipments(@PathVariable Long characterId) {
        List<EquipmentResponse> equipments = equipmentService.getCharacterEquipments(characterId);
        return Result.success(equipments);
    }

    /**
     * 装备物品
     * POST /api/v1/equipment/equip
     */
    @PostMapping("/equip")
    public Result<EquipmentResponse> equipItem(@Valid @RequestBody EquipRequest request) {
        EquipmentResponse response = equipmentService.equipItem(request);
        return Result.success(response);
    }

    /**
     * 卸下装备
     * DELETE /api/v1/equipment/unequip
     */
    @DeleteMapping("/unequip")
    public Result<Boolean> unequipItem(
            @RequestParam("characterId") Long characterId,
            @RequestParam("equipmentSlot") String equipmentSlot) {
        boolean success = equipmentService.unequipItem(characterId, equipmentSlot);
        return Result.success(success);
    }

    /**
     * 获取装备总加成
     * GET /api/v1/equipment/bonus/{characterId}
     */
    @GetMapping("/bonus/{characterId}")
    public Result<EquipmentService.EquipmentBonus> getEquipmentBonus(@PathVariable Long characterId) {
        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(characterId);
        return Result.success(bonus);
    }
}
