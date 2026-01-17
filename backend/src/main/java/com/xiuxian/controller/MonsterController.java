package com.xiuxian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.common.response.Result;
import com.xiuxian.dto.request.MonsterCreateRequest;
import com.xiuxian.dto.request.MonsterDropRequest;
import com.xiuxian.dto.request.MonsterUpdateRequest;
import com.xiuxian.dto.response.EquipmentSelectOption;
import com.xiuxian.dto.response.MonsterDetailResponse;
import com.xiuxian.dto.response.MonsterDetailResponse.MonsterDropResponse;
import com.xiuxian.dto.response.MonsterListItemResponse;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.MonsterDrop;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MonsterDropMapper;
import com.xiuxian.mapper.MonsterMapper;
import com.xiuxian.mapper.RealmMapper;
import com.xiuxian.service.EquipmentService;
import com.xiuxian.service.MonsterDropService;
import com.xiuxian.service.MonsterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 怪物管理控制器（管理端）
 */
@RestController
@RequestMapping("/admin/monster")
public class MonsterController {

    private static final Logger logger = LoggerFactory.getLogger(MonsterController.class);

    @Autowired
    private MonsterService monsterService;

    @Autowired
    private MonsterDropService monsterDropService;

    @Autowired
    private MonsterMapper monsterMapper;

    @Autowired
    private MonsterDropMapper monsterDropMapper;

    @Autowired
    private RealmMapper realmMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired(required = false)
    private EquipmentService equipmentService;

    /**
     * 分页查询怪物列表
     */
    @GetMapping("/list")
    public Result<PageResult<MonsterListItemResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        logger.info("分页查询怪物列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);

        // 构建查询条件
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Monster::getMonsterName, keyword)
                    .or()
                    .like(Monster::getMonsterType, keyword);
        }
        wrapper.orderByDesc(Monster::getCreatedAt);

        // 分页查询
        Page<Monster> pageParam = new Page<>(page, pageSize);
        Page<Monster> result = monsterMapper.selectPage(pageParam, wrapper);

        // 转换为响应DTO
        List<MonsterListItemResponse> items = result.getRecords().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());

        PageResult<MonsterListItemResponse> pageResult =
                PageResult.of(items, result.getTotal(), page, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 获取怪物详情（包含掉落配置）
     */
    @GetMapping("/{monsterId}")
    public Result<MonsterDetailResponse> getDetail(@PathVariable Long monsterId) {
        logger.info("获取怪物详情: monsterId={}", monsterId);

        Monster monster = monsterMapper.selectById(monsterId);
        if (monster == null) {
            return Result.error("怪物不存在");
        }

        // 获取掉落配置
        LambdaQueryWrapper<MonsterDrop> dropWrapper = new LambdaQueryWrapper<>();
        dropWrapper.eq(MonsterDrop::getMonsterId, monsterId);
        List<MonsterDrop> drops = monsterDropMapper.selectList(dropWrapper);

        // 构建响应
        MonsterDetailResponse response = convertToDetail(monster, drops);
        return Result.success(response);
    }

    /**
     * 创建怪物
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody MonsterCreateRequest request) {
        logger.info("创建怪物: monsterName={}", request.getMonsterName());

        // 检查名称唯一性
        if (!checkNameUnique(request.getMonsterName(), null)) {
            return Result.error("怪物名称已存在");
        }

        // 验证掉落配置
        if (!CollectionUtils.isEmpty(request.getDrops())) {
            validateDrops(request.getDrops());
        }

        // 转换并保存怪物基础信息
        Monster monster = new Monster();
        BeanUtils.copyProperties(request, monster);
        monsterMapper.insert(monster);

        logger.info("怪物基础信息已保存: monsterId={}", monster.getMonsterId());

        // 保存掉落配置
        if (!CollectionUtils.isEmpty(request.getDrops())) {
            saveDropsToDatabase(monster.getMonsterId(), request.getDrops());
            logger.info("掉落配置已保存: count={}", request.getDrops().size());
        }

        return Result.success("创建成功", monster.getMonsterId());
    }

    /**
     * 更新怪物
     */
    @PutMapping("/{monsterId}")
    public Result<Void> update(
            @PathVariable Long monsterId,
            @Valid @RequestBody MonsterUpdateRequest request) {

        logger.info("更新怪物: monsterId={}", monsterId);

        Monster monster = monsterMapper.selectById(monsterId);
        if (monster == null) {
            return Result.error("怪物不存在");
        }

        // 检查名称唯一性
        if (request.getMonsterName() != null && !request.getMonsterName().isEmpty() &&
                !checkNameUnique(request.getMonsterName(), monsterId)) {
            return Result.error("怪物名称已存在");
        }

        // 验证掉落配置
        if (!CollectionUtils.isEmpty(request.getDrops())) {
            validateDrops(request.getDrops());
        }

        // 更新基础信息
        BeanUtils.copyProperties(request, monster);
        monsterMapper.updateById(monster);

        // 更新掉落配置（先删除后插入）
        monsterDropService.deleteDropsByMonsterId(monsterId);
        if (!CollectionUtils.isEmpty(request.getDrops())) {
            saveDropsToDatabase(monsterId, request.getDrops());
        }

        logger.info("怪物更新成功: monsterId={}", monsterId);
        return Result.success("更新成功");
    }

    /**
     * 删除怪物
     */
    @DeleteMapping("/{monsterId}")
    public Result<Void> delete(@PathVariable Long monsterId) {
        logger.info("删除怪物: monsterId={}", monsterId);

        Monster monster = monsterMapper.selectById(monsterId);
        if (monster == null) {
            return Result.error("怪物不存在");
        }

        // 删除掉落配置
        monsterDropService.deleteDropsByMonsterId(monsterId);

        // 删除怪物
        monsterMapper.deleteById(monsterId);

        logger.info("怪物删除成功: monsterId={}", monsterId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除怪物
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> monsterIds) {
        logger.info("批量删除怪物: count={}", monsterIds.size());

        if (CollectionUtils.isEmpty(monsterIds)) {
            return Result.error("删除列表不能为空");
        }

        // 删除掉落配置
        for (Long monsterId : monsterIds) {
            monsterDropService.deleteDropsByMonsterId(monsterId);
        }

        // 批量删除怪物
        monsterMapper.deleteBatchIds(monsterIds);

        logger.info("批量删除成功: count={}", monsterIds.size());
        return Result.success("批量删除成功");
    }

    /**
     * 批量保存掉落配置
     */
    @PostMapping("/{monsterId}/drops")
    public Result<Void> saveDrops(
            @PathVariable Long monsterId,
            @RequestBody List<MonsterDropRequest> dropRequests) {

        logger.info("保存掉落配置: monsterId={}, count={}", monsterId, dropRequests.size());

        // 验证怪物存在
        Monster monster = monsterMapper.selectById(monsterId);
        if (monster == null) {
            return Result.error("怪物不存在");
        }

        // 验证
        validateDrops(dropRequests);

        // 删除原有配置
        monsterDropService.deleteDropsByMonsterId(monsterId);

        // 保存新配置
        saveDropsToDatabase(monsterId, dropRequests);

        logger.info("掉落配置保存成功: monsterId={}, count={}", monsterId, dropRequests.size());
        return Result.success("保存成功");
    }

    /**
     * 获取所有装备列表（用于选择器）
     */
    @GetMapping("/equipment/list")
    public Result<List<EquipmentSelectOption>> getEquipmentList() {
        logger.info("获取装备列表");

        List<Equipment> equipments = equipmentMapper.selectList(null);

        List<EquipmentSelectOption> options = equipments.stream()
                .map(this::convertToEquipmentOption)
                .collect(Collectors.toList());

        logger.info("装备列表查询成功: count={}", options.size());
        return Result.success(options);
    }

    /**
     * 检查名称唯一性
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkName(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {

        logger.info("检查名称唯一性: name={}, excludeId={}", name, excludeId);

        boolean isUnique = checkNameUnique(name, excludeId);
        return Result.success(isUnique);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查名称唯一性
     */
    private boolean checkNameUnique(String monsterName, Long excludeId) {
        LambdaQueryWrapper<Monster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Monster::getMonsterName, monsterName);
        if (excludeId != null) {
            wrapper.ne(Monster::getMonsterId, excludeId);
        }
        return monsterMapper.selectCount(wrapper) == 0;
    }

    /**
     * 验证掉落配置
     */
    private void validateDrops(List<MonsterDropRequest> drops) {
        // 检查重复装备
        Set<Long> equipmentIds = new HashSet<>();
        for (MonsterDropRequest drop : drops) {
            if (!equipmentIds.add(drop.getEquipmentId())) {
                throw new BusinessException("存在重复的装备配置");
            }
        }

        // 检查品质范围
        List<String> qualityOrder = Arrays.asList("凡品", "良品", "上品", "极品", "仙品");
        for (MonsterDropRequest drop : drops) {
            if (drop.getMinQuality() != null && drop.getMaxQuality() != null) {
                int minIndex = qualityOrder.indexOf(drop.getMinQuality());
                int maxIndex = qualityOrder.indexOf(drop.getMaxQuality());
                if (minIndex != -1 && maxIndex != -1 && minIndex > maxIndex) {
                    throw new BusinessException("最低品质不能高于最高品质");
                }
            }
        }
    }

    /**
     * 保存掉落配置到数据库
     */
    private void saveDropsToDatabase(Long monsterId, List<MonsterDropRequest> requests) {
        List<MonsterDrop> drops = requests.stream()
                .map(req -> {
                    MonsterDrop drop = new MonsterDrop();
                    drop.setMonsterId(monsterId);
                    drop.setEquipmentId(req.getEquipmentId());
                    drop.setDropRate(req.getDropRate());
                    drop.setDropQuantity(req.getDropQuantity());
                    drop.setMinQuality(req.getMinQuality());
                    drop.setMaxQuality(req.getMaxQuality());
                    drop.setIsGuaranteed(req.getIsGuaranteed() != null && req.getIsGuaranteed() ? 1 : 0);
                    return drop;
                })
                .collect(Collectors.toList());

        monsterDropService.saveBatch(drops);
    }

    /**
     * 转换为列表项响应DTO
     */
    private MonsterListItemResponse convertToListItem(Monster monster) {
        MonsterListItemResponse response = new MonsterListItemResponse();
        BeanUtils.copyProperties(monster, response);

        // 查询境界名称
        if (monster.getRealmId() != null) {
            Realm realm = realmMapper.selectById(monster.getRealmId());
            if (realm != null) {
                response.setRealmName(realm.getRealmName());
            }
        }

        return response;
    }

    /**
     * 转换为详情响应DTO
     */
    private MonsterDetailResponse convertToDetail(Monster monster, List<MonsterDrop> drops) {
        MonsterDetailResponse response = new MonsterDetailResponse();
        BeanUtils.copyProperties(monster, response);

        // 查询境界名称
        if (monster.getRealmId() != null) {
            Realm realm = realmMapper.selectById(monster.getRealmId());
            if (realm != null) {
                response.setRealmName(realm.getRealmName());
            }
        }

        // 转换掉落配置
        if (!CollectionUtils.isEmpty(drops)) {
            List<MonsterDropResponse> dropResponses = drops.stream()
                    .map(this::convertToDropResponse)
                    .collect(Collectors.toList());
            response.setDrops(dropResponses);
        }

        return response;
    }

    /**
     * 转换掉落配置响应DTO
     */
    private MonsterDropResponse convertToDropResponse(MonsterDrop drop) {
        MonsterDropResponse response = new MonsterDropResponse();
        response.setMonsterDropId(drop.getMonsterDropId());
        response.setMonsterId(drop.getMonsterId());
        response.setEquipmentId(drop.getEquipmentId());
        response.setDropRate(drop.getDropRate());
        response.setDropQuantity(drop.getDropQuantity());
        response.setMinQuality(drop.getMinQuality());
        response.setMaxQuality(drop.getMaxQuality());
        response.setIsGuaranteed(drop.getIsGuaranteed() != null && drop.getIsGuaranteed() == 1);

        // 查询装备信息
        Equipment equipment = equipmentMapper.selectById(drop.getEquipmentId());
        if (equipment != null) {
            response.setEquipmentName(equipment.getEquipmentName());
            response.setEquipmentType(equipment.getEquipmentType());
            response.setQuality(equipment.getQuality());
        }

        return response;
    }

    /**
     * 转换为装备选择器选项DTO
     */
    private EquipmentSelectOption convertToEquipmentOption(Equipment equipment) {
        EquipmentSelectOption option = new EquipmentSelectOption();
        option.setEquipmentId(equipment.getEquipmentId());
        option.setEquipmentName(equipment.getEquipmentName());
        option.setEquipmentType(equipment.getEquipmentType());
        option.setQuality(equipment.getQuality());
        option.setBaseScore(equipment.getBaseScore());
        return option;
    }
}
