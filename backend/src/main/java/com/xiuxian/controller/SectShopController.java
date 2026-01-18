package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.SectShopItemCreateRequest;
import com.xiuxian.dto.request.SectShopItemUpdateRequest;
import com.xiuxian.dto.response.SectShopItemDetailResponse;
import com.xiuxian.dto.response.SectShopItemListItemResponse;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectShopItem;
import com.xiuxian.entity.Skill;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.mapper.SectMapper;
import com.xiuxian.mapper.SectShopItemMapper;
import com.xiuxian.mapper.SkillMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 宗门商店配置管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/sect-shop")
public class SectShopController {

    private static final Logger logger = LoggerFactory.getLogger(SectShopController.class);

    @Autowired
    private SectShopItemMapper sectShopItemMapper;

    @Autowired
    private SectMapper sectMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private PillMapper pillMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private SkillMapper skillMapper;

    /**
     * 分页查询商店物品列表
     */
    @GetMapping("/list")
    public Result<PageResult<SectShopItemListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long sectId,
            @RequestParam(required = false) String itemType) {

        logger.info("分页查询商店物品列表: page={}, pageSize={}, keyword={}, sectId={}, itemType={}",
                page, pageSize, keyword, sectId, itemType);

        // 构建查询条件
        LambdaQueryWrapper<SectShopItem> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SectShopItem::getItemName, keyword);
        }
        if (sectId != null) {
            wrapper.eq(SectShopItem::getSectId, sectId);
        }
        if (itemType != null && !itemType.isEmpty()) {
            wrapper.eq(SectShopItem::getItemType, itemType);
        }
        wrapper.orderByDesc(SectShopItem::getItemId);

        // 分页查询
        Page<SectShopItem> pageParam = new Page<>(page, pageSize);
        Page<SectShopItem> result = sectShopItemMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<SectShopItemListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<SectShopItemListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取商店物品详情
     */
    @GetMapping("/{itemId}")
    public Result<SectShopItemDetailResponse> getDetail(@PathVariable Long itemId) {
        logger.info("获取商店物品详情: itemId={}", itemId);

        SectShopItem shopItem = sectShopItemMapper.selectById(itemId);
        if (shopItem == null) {
            return Result.error("商店物品不存在");
        }

        SectShopItemDetailResponse response = convertToDetail(shopItem);
        return Result.success(response);
    }

    /**
     * 创建商店物品
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SectShopItemCreateRequest request) {
        logger.info("创建商店物品: itemName={}", request.getItemName());

        // 验证宗门是否存在
        Sect sect = sectMapper.selectById(request.getSectId());
        if (sect == null) {
            return Result.error("宗门不存在");
        }

        // 转换并保存
        SectShopItem shopItem = new SectShopItem();
        BeanUtils.copyProperties(request, shopItem);
        sectShopItemMapper.insert(shopItem);

        logger.info("商店物品创建成功: itemId={}", shopItem.getItemId());
        return Result.success("创建成功", shopItem.getItemId());
    }

    /**
     * 更新商店物品
     */
    @PutMapping("/{itemId}")
    public Result<Void> update(
            @PathVariable Long itemId,
            @Valid @RequestBody SectShopItemUpdateRequest request) {

        logger.info("更新商店物品: itemId={}, itemName={}", itemId, request.getItemName());

        SectShopItem shopItem = sectShopItemMapper.selectById(itemId);
        if (shopItem == null) {
            return Result.error("商店物品不存在");
        }

        // 验证宗门是否存在
        if (request.getSectId() != null) {
            Sect sect = sectMapper.selectById(request.getSectId());
            if (sect == null) {
                return Result.error("宗门不存在");
            }
        }

        // 更新
        BeanUtils.copyProperties(request, shopItem);
        sectShopItemMapper.updateById(shopItem);

        logger.info("商店物品更新成功: itemId={}", itemId);
        return Result.success("更新成功");
    }

    /**
     * 删除商店物品
     */
    @DeleteMapping("/{itemId}")
    public Result<Void> delete(@PathVariable Long itemId) {
        logger.info("删除商店物品: itemId={}", itemId);

        SectShopItem shopItem = sectShopItemMapper.selectById(itemId);
        if (shopItem == null) {
            return Result.error("商店物品不存在");
        }

        sectShopItemMapper.deleteById(itemId);

        logger.info("商店物品删除成功: itemId={}", itemId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除商店物品
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> itemIds) {
        logger.info("批量删除商店物品: count={}", itemIds.size());

        if (itemIds == null || itemIds.isEmpty()) {
            return Result.error("删除列表不能为空");
        }

        sectShopItemMapper.deleteBatchIds(itemIds);

        logger.info("批量删除成功: count={}", itemIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 获取所有宗门列表（用于下拉选择）
     */
    @GetMapping("/sects")
    public Result<List<Map<String, Object>>> getSects() {
        logger.info("获取所有宗门列表");

        LambdaQueryWrapper<Sect> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Sect::getSectName);

        List<Sect> sects = sectMapper.selectList(wrapper);
        List<Map<String, Object>> result = sects.stream()
                .map(sect -> {
                    Map<String, Object> map = Map.of(
                        "sectId", sect.getSectId(),
                        "sectName", sect.getSectName()
                    );
                    return map;
                })
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 根据物品类型获取物品列表（用于下拉选择）
     */
    @GetMapping("/items")
    public Result<List<Map<String, Object>>> getItemsByType(@RequestParam String itemType) {
        logger.info("获取物品列表: itemType={}", itemType);

        List<Map<String, Object>> result;

        // 支持英文和中文名称
        switch (itemType.toLowerCase()) {
            case "equipment":
            case "装备": {
                LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(Equipment::getEquipmentId, Equipment::getEquipmentName);
                wrapper.orderByAsc(Equipment::getEquipmentName);
                List<Equipment> equipments = equipmentMapper.selectList(wrapper);
                result = equipments.stream()
                        .map(e -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("itemId", e.getEquipmentId());
                            map.put("itemName", e.getEquipmentName());
                            return map;
                        })
                        .collect(Collectors.toList());
                break;
            }

            case "pill":
            case "丹药": {
                LambdaQueryWrapper<Pill> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(Pill::getPillId, Pill::getPillName);
                wrapper.orderByAsc(Pill::getPillName);
                List<Pill> pills = pillMapper.selectList(wrapper);
                result = pills.stream()
                        .map(p -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("itemId", p.getPillId());
                            map.put("itemName", p.getPillName());
                            return map;
                        })
                        .collect(Collectors.toList());
                break;
            }

            case "material":
            case "材料": {
                LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(Material::getMaterialId, Material::getMaterialName);
                wrapper.orderByAsc(Material::getMaterialName);
                List<Material> materials = materialMapper.selectList(wrapper);
                result = materials.stream()
                        .map(m -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("itemId", m.getMaterialId());
                            map.put("itemName", m.getMaterialName());
                            return map;
                        })
                        .collect(Collectors.toList());
                break;
            }

            case "skill":
            case "技能": {
                LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
                wrapper.select(Skill::getSkillId, Skill::getSkillName);
                wrapper.orderByAsc(Skill::getSkillName);
                List<Skill> skills = skillMapper.selectList(wrapper);
                result = skills.stream()
                        .map(s -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("itemId", s.getSkillId());
                            map.put("itemName", s.getSkillName());
                            return map;
                        })
                        .collect(Collectors.toList());
                break;
            }

            default:
                // 对于不支持的物品类型，返回空列表
                // 用户可以手动输入物品ID和名称
                result = List.of();
                break;
        }

        return Result.success(result);
    }

    /**
     * 获取所有物品类型（用于下拉选择）
     */
    @GetMapping("/item-types")
    public Result<List<String>> getItemTypes() {
        logger.info("获取所有物品类型");

        LambdaQueryWrapper<SectShopItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SectShopItem::getItemType);
        wrapper.isNotNull(SectShopItem::getItemType);
        wrapper.groupBy(SectShopItem::getItemType);
        wrapper.orderByAsc(SectShopItem::getItemType);

        List<String> itemTypes = sectShopItemMapper.selectList(wrapper).stream()
                .map(SectShopItem::getItemType)
                .distinct()
                .collect(Collectors.toList());

        return Result.success(itemTypes);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换为列表项响应DTO
     */
    private SectShopItemListItemResponse convertToListItem(SectShopItem shopItem) {
        SectShopItemListItemResponse response = new SectShopItemListItemResponse();
        response.setItemId(shopItem.getItemId());
        response.setItemType(shopItem.getItemType());
        response.setItemName(shopItem.getItemName());
        response.setItemTier(shopItem.getItemTier());
        response.setPrice(shopItem.getPrice());
        response.setCurrentStock(shopItem.getCurrentStock());

        // 获取宗门名称
        if (shopItem.getSectId() != null) {
            Sect sect = sectMapper.selectById(shopItem.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private SectShopItemDetailResponse convertToDetail(SectShopItem shopItem) {
        SectShopItemDetailResponse response = new SectShopItemDetailResponse();
        BeanUtils.copyProperties(shopItem, response);

        // 获取宗门名称
        if (shopItem.getSectId() != null) {
            Sect sect = sectMapper.selectById(shopItem.getSectId());
            if (sect != null) {
                response.setSectName(sect.getSectName());
            }
        }

        return response;
    }
}
