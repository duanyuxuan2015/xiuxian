package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ForgeRequest;
import com.xiuxian.dto.response.EquipmentRecipeResponse;
import com.xiuxian.dto.response.ForgeResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.EquipmentRecipe;
import com.xiuxian.entity.EquipmentRecipeMaterial;
import com.xiuxian.entity.ForgeRecord;
import com.xiuxian.entity.Material;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.EquipmentRecipeMaterialMapper;
import com.xiuxian.mapper.EquipmentRecipeMapper;
import com.xiuxian.mapper.ForgeRecordMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.ForgeService;
import com.xiuxian.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 锻造Service实现类
 */
@Service
public class ForgeServiceImpl extends ServiceImpl<ForgeRecordMapper, ForgeRecord> implements ForgeService {

    private static final Logger logger = LoggerFactory.getLogger(ForgeServiceImpl.class);
    private static final String ITEM_TYPE_MATERIAL = "material";
    private static final String ITEM_TYPE_EQUIPMENT = "equipment";

    private static final String[] QUALITY_LEVELS = { "下品", "中品", "上品", "极品" };

    private final CharacterService characterService;
    private final InventoryService inventoryService;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentRecipeMapper recipeMapper;
    private final EquipmentRecipeMaterialMapper recipeMaterialMapper;
    private final MaterialMapper materialMapper;
    private final Random random = new Random();

    public ForgeServiceImpl(@Lazy CharacterService characterService,
            InventoryService inventoryService,
            EquipmentMapper equipmentMapper,
            EquipmentRecipeMapper recipeMapper,
            EquipmentRecipeMaterialMapper recipeMaterialMapper,
            MaterialMapper materialMapper) {
        this.characterService = characterService;
        this.inventoryService = inventoryService;
        this.equipmentMapper = equipmentMapper;
        this.recipeMapper = recipeMapper;
        this.recipeMaterialMapper = recipeMaterialMapper;
        this.materialMapper = materialMapper;
    }

    @Override
    public List<EquipmentRecipeResponse> getAvailableRecipes(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        int realmLevel = character.getRealmLevel();
        int forgeLevel = character.getForgeLevel() != null ? character.getForgeLevel() : 1;

        LambdaQueryWrapper<EquipmentRecipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(EquipmentRecipe::getRecipeTier, realmLevel)
                .le(EquipmentRecipe::getForgingLevelRequired, forgeLevel)
                .orderByAsc(EquipmentRecipe::getRecipeTier);

        List<EquipmentRecipe> recipes = recipeMapper.selectList(wrapper);
        List<EquipmentRecipeResponse> responses = new ArrayList<>();

        for (EquipmentRecipe recipe : recipes) {
            Equipment equipment = equipmentMapper.selectById(recipe.getEquipmentId());
            if (equipment != null) {
                List<EquipmentRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId,
                        recipe.getRecipeId());
                responses.add(EquipmentRecipeResponse.fromEntity(recipe, equipment, materials));
            }
        }

        return responses;
    }

    @Override
    public EquipmentRecipeResponse getRecipeDetail(Long characterId, Long recipeId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        EquipmentRecipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new BusinessException(6001, "装备配方不存在");
        }

        Equipment equipment = equipmentMapper.selectById(recipe.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException(6002, "装备数据异常");
        }

        List<EquipmentRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId, recipeId);
        return EquipmentRecipeResponse.fromEntity(recipe, equipment, materials);
    }

    private List<EquipmentRecipeResponse.MaterialRequirement> getMaterialRequirements(Long characterId, Long recipeId) {
        LambdaQueryWrapper<EquipmentRecipeMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentRecipeMaterial::getRecipeId, recipeId);
        List<EquipmentRecipeMaterial> recipeMaterials = recipeMaterialMapper.selectList(wrapper);

        List<EquipmentRecipeResponse.MaterialRequirement> requirements = new ArrayList<>();
        for (EquipmentRecipeMaterial rm : recipeMaterials) {
            Material material = materialMapper.selectById(rm.getMaterialId());
            if (material != null) {
                EquipmentRecipeResponse.MaterialRequirement req = new EquipmentRecipeResponse.MaterialRequirement();
                req.setMaterialId(material.getMaterialId());
                req.setMaterialName(material.getMaterialName());
                req.setRequiredQuantity(rm.getQuantityRequired());
                int owned = inventoryService.getItemQuantity(characterId, ITEM_TYPE_MATERIAL, material.getMaterialId());
                req.setOwnedQuantity(owned);
                req.setSufficient(owned >= rm.getQuantityRequired());
                requirements.add(req);
            }
        }
        return requirements;
    }

    @Override
    @Transactional
    public ForgeResponse startForge(ForgeRequest request) {
        Long characterId = request.getCharacterId();
        Long recipeId = request.getRecipeId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证配方
        EquipmentRecipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new BusinessException(6001, "装备配方不存在");
        }

        // 3. 验证境界和锻造等级
        int forgeLevel = character.getForgeLevel() != null ? character.getForgeLevel() : 1;
        if (character.getRealmLevel() < recipe.getRecipeTier()) {
            throw new BusinessException(6003, "境界不足，需要境界等级: " + recipe.getRecipeTier());
        }
        if (forgeLevel < recipe.getForgingLevelRequired()) {
            throw new BusinessException(6004, "锻造等级不足，需要锻造等级: " + recipe.getForgingLevelRequired());
        }

        // 4. 验证并消耗材料
        List<EquipmentRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId, recipeId);
        for (EquipmentRecipeResponse.MaterialRequirement mat : materials) {
            if (!mat.getSufficient()) {
                throw new BusinessException(6005, "材料不足: " + mat.getMaterialName() +
                        " (需要" + mat.getRequiredQuantity() + "，拥有" + mat.getOwnedQuantity() + ")");
            }
        }

        // 消耗材料
        for (EquipmentRecipeResponse.MaterialRequirement mat : materials) {
            inventoryService.removeItem(characterId, ITEM_TYPE_MATERIAL, mat.getMaterialId(),
                    mat.getRequiredQuantity());
        }

        // 5. 验证装备数据
        Equipment equipment = equipmentMapper.selectById(recipe.getEquipmentId());
        if (equipment == null) {
            throw new BusinessException(6002, "装备数据异常");
        }

        // 6. 计算成功率并判定结果
        int successRate = calculateSuccessRate(characterId, recipeId);
        boolean success = random.nextInt(100) < successRate;

        // 7. 创建锻造记录
        ForgeRecord record = new ForgeRecord();
        record.setCharacterId(characterId);
        record.setRecipeId(recipeId);
        record.setEquipmentId(recipe.getEquipmentId());
        record.setSuccess(success);
        record.setCreatedAt(LocalDateTime.now());

        int expGained;
        if (success) {
            // 成功：计算品质
            String quality = calculateOutputQuality(forgeLevel, recipe.getForgingLevelRequired());
            record.setResultQuality(quality);
            expGained = recipe.getForgingLevelRequired() * 25;
            record.setExperienceGained(expGained);

            // 添加装备到背包
            inventoryService.addItem(characterId, ITEM_TYPE_EQUIPMENT, recipe.getEquipmentId(), 1);

            logger.info("锻造成功: characterId={}, recipe={}, equipment={}, quality={}",
                    characterId, recipe.getRecipeName(), equipment.getEquipmentName(), quality);
        } else {
            // 失败：少量经验
            record.setResultQuality(null);
            expGained = recipe.getForgingLevelRequired() * 8;
            record.setExperienceGained(expGained);

            logger.info("锻造失败: characterId={}, recipe={}", characterId, recipe.getRecipeName());
        }

        this.save(record);

        return ForgeResponse.fromEntity(record, recipe.getRecipeName(), equipment);
    }

    @Override
    public int calculateSuccessRate(Long characterId, Long recipeId) {
        PlayerCharacter character = characterService.getById(characterId);
        EquipmentRecipe recipe = recipeMapper.selectById(recipeId);

        if (character == null || recipe == null) {
            return 0;
        }

        int baseRate = recipe.getBaseSuccessRate();
        int forgeLevel = character.getForgeLevel() != null ? character.getForgeLevel() : 1;
        int requiredLevel = recipe.getForgingLevelRequired();

        // 锻造等级高于要求时，每高1级增加4%成功率
        int levelBonus = Math.max(0, (forgeLevel - requiredLevel) * 4);

        // 境界加成：每高一个境界增加2%
        int realmBonus = Math.max(0, (character.getRealmLevel() - recipe.getRecipeTier()) * 2);

        int finalRate = baseRate + levelBonus + realmBonus;
        return Math.min(95, finalRate); // 最高95%成功率
    }

    private String calculateOutputQuality(int forgeLevel, int requiredLevel) {
        int diff = forgeLevel - requiredLevel;
        int qualityIndex = 0; // 默认下品

        if (diff >= 5) {
            int roll = random.nextInt(100);
            if (roll < 8) {
                qualityIndex = 3; // 极品 8%
            } else if (roll < 35) {
                qualityIndex = 2; // 上品 27%
            } else if (roll < 75) {
                qualityIndex = 1; // 中品 40%
            } else {
                qualityIndex = 0; // 下品 25%
            }
        } else if (diff >= 2) {
            int roll = random.nextInt(100);
            if (roll < 15) {
                qualityIndex = 2; // 上品 15%
            } else if (roll < 55) {
                qualityIndex = 1; // 中品 40%
            } else {
                qualityIndex = 0; // 下品 45%
            }
        } else if (diff >= 0) {
            int roll = random.nextInt(100);
            if (roll < 25) {
                qualityIndex = 1; // 中品 25%
            } else {
                qualityIndex = 0; // 下品 75%
            }
        }

        return QUALITY_LEVELS[qualityIndex];
    }

    @Override
    public List<ForgeResponse> getForgeRecords(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<ForgeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForgeRecord::getCharacterId, characterId)
                .orderByDesc(ForgeRecord::getCreatedAt)
                .last("LIMIT 50");

        List<ForgeRecord> records = this.list(wrapper);
        List<ForgeResponse> responses = new ArrayList<>();

        for (ForgeRecord record : records) {
            EquipmentRecipe recipe = recipeMapper.selectById(record.getRecipeId());
            Equipment equipment = equipmentMapper.selectById(record.getEquipmentId());
            String recipeName = recipe != null ? recipe.getRecipeName() : "未知配方";
            responses.add(ForgeResponse.fromEntity(record, recipeName, equipment));
        }

        return responses;
    }
}
