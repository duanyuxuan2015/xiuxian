package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AlchemyRequest;
import com.xiuxian.dto.response.AlchemyResponse;
import com.xiuxian.dto.response.MaterialResponse;
import com.xiuxian.dto.response.PillRecipeResponse;
import com.xiuxian.entity.AlchemyRecord;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.PillRecipe;
import com.xiuxian.entity.PillRecipeMaterial;
import com.xiuxian.mapper.AlchemyRecordMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.mapper.PillRecipeMaterialMapper;
import com.xiuxian.mapper.PillRecipeMapper;
import com.xiuxian.service.AlchemyService;
import com.xiuxian.service.CharacterService;
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
 * 炼丹Service实现类
 */
@Service
public class AlchemyServiceImpl extends ServiceImpl<AlchemyRecordMapper, AlchemyRecord> implements AlchemyService {

    private static final Logger logger = LoggerFactory.getLogger(AlchemyServiceImpl.class);
    private static final String ITEM_TYPE_MATERIAL = "material";
    private static final String ITEM_TYPE_PILL = "pill";

    private static final String[] QUALITY_LEVELS = { "下品", "中品", "上品", "极品" };

    private final CharacterService characterService;
    private final InventoryService inventoryService;
    private final PillMapper pillMapper;
    private final PillRecipeMapper recipeMapper;
    private final PillRecipeMaterialMapper recipeMaterialMapper;
    private final MaterialMapper materialMapper;
    private final Random random = new Random();

    public AlchemyServiceImpl(@Lazy CharacterService characterService,
            InventoryService inventoryService,
            PillMapper pillMapper,
            PillRecipeMapper recipeMapper,
            PillRecipeMaterialMapper recipeMaterialMapper,
            MaterialMapper materialMapper) {
        this.characterService = characterService;
        this.inventoryService = inventoryService;
        this.pillMapper = pillMapper;
        this.recipeMapper = recipeMapper;
        this.recipeMaterialMapper = recipeMaterialMapper;
        this.materialMapper = materialMapper;
    }

    @Override
    public List<PillRecipeResponse> getAvailableRecipes(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        int realmLevel = character.getRealmLevel();
        int alchemyLevel = character.getAlchemyLevel() != null ? character.getAlchemyLevel() : 1;

        LambdaQueryWrapper<PillRecipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(PillRecipe::getRecipeTier, realmLevel)
                .le(PillRecipe::getAlchemyLevelRequired, alchemyLevel)
                .orderByAsc(PillRecipe::getRecipeTier);

        List<PillRecipe> recipes = recipeMapper.selectList(wrapper);
        List<PillRecipeResponse> responses = new ArrayList<>();

        for (PillRecipe recipe : recipes) {
            Pill pill = pillMapper.selectById(recipe.getPillId());
            if (pill != null) {
                List<PillRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId,
                        recipe.getRecipeId());
                responses.add(PillRecipeResponse.fromEntity(recipe, pill, materials));
            }
        }

        return responses;
    }

    @Override
    public PillRecipeResponse getRecipeDetail(Long characterId, Long recipeId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        PillRecipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new BusinessException(5001, "丹方不存在");
        }

        Pill pill = pillMapper.selectById(recipe.getPillId());
        if (pill == null) {
            throw new BusinessException(5002, "丹药数据异常");
        }

        List<PillRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId, recipeId);
        return PillRecipeResponse.fromEntity(recipe, pill, materials);
    }

    private List<PillRecipeResponse.MaterialRequirement> getMaterialRequirements(Long characterId, Long recipeId) {
        LambdaQueryWrapper<PillRecipeMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PillRecipeMaterial::getRecipeId, recipeId);
        List<PillRecipeMaterial> recipeMaterials = recipeMaterialMapper.selectList(wrapper);

        List<PillRecipeResponse.MaterialRequirement> requirements = new ArrayList<>();
        for (PillRecipeMaterial rm : recipeMaterials) {
            Material material = materialMapper.selectById(rm.getMaterialId());
            if (material != null) {
                PillRecipeResponse.MaterialRequirement req = new PillRecipeResponse.MaterialRequirement();
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
    public AlchemyResponse startAlchemy(AlchemyRequest request) {
        Long characterId = request.getCharacterId();
        Long recipeId = request.getRecipeId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证丹方
        PillRecipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new BusinessException(5001, "丹方不存在");
        }

        // 3. 验证境界和炼丹等级
        int alchemyLevel = character.getAlchemyLevel() != null ? character.getAlchemyLevel() : 1;
        if (character.getRealmLevel() < recipe.getRecipeTier()) {
            throw new BusinessException(5003, "境界不足，需要境界等级: " + recipe.getRecipeTier());
        }
        if (alchemyLevel < recipe.getAlchemyLevelRequired()) {
            throw new BusinessException(5004, "炼丹等级不足，需要炼丹等级: " + recipe.getAlchemyLevelRequired());
        }

        // 4. 验证丹药数据（在材料验证之前，确保丹方配置的丹药存在）
        Pill pill = pillMapper.selectById(recipe.getPillId());
        if (pill == null) {
            throw new BusinessException(5002, "丹药数据异常");
        }

        // 5. 验证并消耗材料
        List<PillRecipeResponse.MaterialRequirement> materials = getMaterialRequirements(characterId, recipeId);

        // 检查丹方是否配置了材料
        if (materials == null || materials.isEmpty()) {
            throw new BusinessException(5006, "丹方数据有误，未配置材料");
        }

        for (PillRecipeResponse.MaterialRequirement mat : materials) {
            if (!mat.getSufficient()) {
                throw new BusinessException(5005, "材料不足: " + mat.getMaterialName() +
                        " (需要" + mat.getRequiredQuantity() + "，拥有" + mat.getOwnedQuantity() + ")");
            }
        }

        // 消耗材料
        for (PillRecipeResponse.MaterialRequirement mat : materials) {
            inventoryService.removeItem(characterId, ITEM_TYPE_MATERIAL, mat.getMaterialId(),
                    mat.getRequiredQuantity());
        }

        // 6. 计算成功率并判定结果
        int successRate = calculateSuccessRate(characterId, recipeId);
        boolean success = random.nextInt(100) < successRate;

        // 7. 创建炼丹记录
        AlchemyRecord record = new AlchemyRecord();
        record.setCharacterId(characterId);
        record.setRecipeId(recipeId);
        record.setPillId(recipe.getPillId());
        record.setSuccess(success);
        record.setCreatedAt(LocalDateTime.now());

        int expGained;
        if (success) {
            // 成功：计算产出数量和品质
            int quantity = calculateOutputQuantity(alchemyLevel, recipe.getAlchemyLevelRequired());
            String quality = calculateOutputQuality(alchemyLevel, recipe.getAlchemyLevelRequired());

            record.setQuantity(quantity);
            record.setResultQuality(quality);
            expGained = recipe.getAlchemyLevelRequired() * 20;
            record.setExperienceGained(expGained);

            // 添加丹药到背包
            inventoryService.addItem(characterId, ITEM_TYPE_PILL, recipe.getPillId(), quantity);

            logger.info("炼丹成功: characterId={}, recipe={}, pill={}, quality={}, quantity={}",
                    characterId, recipe.getRecipeName(), pill.getPillName(), quality, quantity);
        } else {
            // 失败：少量经验
            record.setQuantity(0);
            record.setResultQuality(null);
            expGained = recipe.getAlchemyLevelRequired() * 5;
            record.setExperienceGained(expGained);

            logger.info("炼丹失败: characterId={}, recipe={}", characterId, recipe.getRecipeName());
        }

        this.save(record);

        // 7. 增加炼丹经验并检查升级
        int currentAlchemyExp = character.getAlchemyExp() != null ? character.getAlchemyExp() : 0;
        int newAlchemyExp = currentAlchemyExp + expGained;
        character.setAlchemyExp(newAlchemyExp);

        // 检查是否升级（升级公式：100 * level）
        int expNeededForNextLevel = 100 * character.getAlchemyLevel();
        boolean leveledUp = false;
        while (newAlchemyExp >= expNeededForNextLevel) {
            newAlchemyExp -= expNeededForNextLevel;
            character.setAlchemyExp(newAlchemyExp);
            character.setAlchemyLevel(character.getAlchemyLevel() + 1);
            leveledUp = true;
            logger.info("炼丹等级提升: characterId={}, newLevel={}", characterId, character.getAlchemyLevel());
            expNeededForNextLevel = 100 * character.getAlchemyLevel();
        }

        // 更新角色数据
        characterService.updateById(character);

        // 8. 构建响应
        AlchemyResponse response = AlchemyResponse.fromEntity(record, recipe.getRecipeName(), pill);

        if (leveledUp) {
            response.setMessage(response.getMessage() + String.format("，炼丹等级提升至%d级！", character.getAlchemyLevel()));
        }

        return response;
    }

    @Override
    public int calculateSuccessRate(Long characterId, Long recipeId) {
        PlayerCharacter character = characterService.getById(characterId);
        PillRecipe recipe = recipeMapper.selectById(recipeId);

        if (character == null || recipe == null) {
            return 0;
        }

        int baseRate = recipe.getBaseSuccessRate();
        int alchemyLevel = character.getAlchemyLevel() != null ? character.getAlchemyLevel() : 1;
        int requiredLevel = recipe.getAlchemyLevelRequired();

        // 炼丹等级高于要求时，每高1级增加5%成功率
        int levelBonus = Math.max(0, (alchemyLevel - requiredLevel) * 5);

        // 境界加成：每高一个境界增加2%
        int realmBonus = Math.max(0, (character.getRealmLevel() - recipe.getRecipeTier()) * 2);

        int finalRate = baseRate + levelBonus + realmBonus;
        return Math.min(95, finalRate); // 最高95%成功率
    }

    private int calculateOutputQuantity(int alchemyLevel, int requiredLevel) {
        // 基础产出1个，等级差每3级多产出1个
        int bonus = (alchemyLevel - requiredLevel) / 3;
        int base = 1 + Math.max(0, bonus);

        // 10%几率额外+1
        if (random.nextInt(100) < 10) {
            base++;
        }

        return Math.min(base, 5); // 最多5个
    }

    private String calculateOutputQuality(int alchemyLevel, int requiredLevel) {
        // 根据等级差决定品质
        int diff = alchemyLevel - requiredLevel;
        int qualityIndex = 0; // 默认下品

        if (diff >= 6) {
            // 高出6级以上，有机会出极品
            int roll = random.nextInt(100);
            if (roll < 10) {
                qualityIndex = 3; // 极品 10%
            } else if (roll < 40) {
                qualityIndex = 2; // 上品 30%
            } else if (roll < 80) {
                qualityIndex = 1; // 中品 40%
            } else {
                qualityIndex = 0; // 下品 20%
            }
        } else if (diff >= 3) {
            // 高出3级以上
            int roll = random.nextInt(100);
            if (roll < 20) {
                qualityIndex = 2; // 上品 20%
            } else if (roll < 60) {
                qualityIndex = 1; // 中品 40%
            } else {
                qualityIndex = 0; // 下品 40%
            }
        } else if (diff >= 0) {
            // 等级相当
            int roll = random.nextInt(100);
            if (roll < 30) {
                qualityIndex = 1; // 中品 30%
            } else {
                qualityIndex = 0; // 下品 70%
            }
        }

        return QUALITY_LEVELS[qualityIndex];
    }

    @Override
    public List<AlchemyResponse> getAlchemyRecords(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<AlchemyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlchemyRecord::getCharacterId, characterId)
                .orderByDesc(AlchemyRecord::getCreatedAt)
                .last("LIMIT 50");

        List<AlchemyRecord> records = this.list(wrapper);
        List<AlchemyResponse> responses = new ArrayList<>();

        for (AlchemyRecord record : records) {
            PillRecipe recipe = recipeMapper.selectById(record.getRecipeId());
            Pill pill = pillMapper.selectById(record.getPillId());
            String recipeName = recipe != null ? recipe.getRecipeName() : "未知丹方";
            responses.add(AlchemyResponse.fromEntity(record, recipeName, pill));
        }

        return responses;
    }

    @Override
    public List<MaterialResponse> getCharacterMaterials(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        List<Material> allMaterials = materialMapper.selectList(null);
        List<MaterialResponse> responses = new ArrayList<>();

        for (Material material : allMaterials) {
            int quantity = inventoryService.getItemQuantity(characterId, ITEM_TYPE_MATERIAL, material.getMaterialId());
            if (quantity > 0) {
                responses.add(MaterialResponse.fromEntity(material, quantity));
            }
        }

        return responses;
    }
}
