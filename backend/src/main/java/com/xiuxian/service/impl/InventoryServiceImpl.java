package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.config.AttributeProperties;
import com.xiuxian.dto.response.SellItemResponse;
import com.xiuxian.dto.response.UsePillResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Material;
import com.xiuxian.entity.Pill;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.MaterialMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 背包Service实现类
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<CharacterInventoryMapper, CharacterInventory> implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final CharacterInventoryMapper characterInventoryMapper;
    private final EquipmentMapper equipmentMapper;
    private final MaterialMapper materialMapper;
    private final PillMapper pillMapper;
    private final CharacterEquipmentMapper characterEquipmentMapper;
    private final CharacterService characterService;
    private final AttributeProperties attributeProperties;

    public InventoryServiceImpl(CharacterInventoryMapper characterInventoryMapper,
                                EquipmentMapper equipmentMapper,
                                MaterialMapper materialMapper,
                                PillMapper pillMapper,
                                CharacterEquipmentMapper characterEquipmentMapper,
                                CharacterService characterService,
                                AttributeProperties attributeProperties) {
        this.characterInventoryMapper = characterInventoryMapper;
        this.equipmentMapper = equipmentMapper;
        this.materialMapper = materialMapper;
        this.pillMapper = pillMapper;
        this.characterEquipmentMapper = characterEquipmentMapper;
        this.characterService = characterService;
        this.attributeProperties = attributeProperties;
    }

    @Override
    @Transactional
    public void addItem(Long characterId, String itemType, Long itemId, int quantity) {
        if (quantity <= 0) {
            return;
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            this.updateById(existing);
            logger.info("更新背包物品: characterId={}, itemType={}, itemId={}, newQuantity={}",
                    characterId, itemType, itemId, existing.getQuantity());
        } else {
            CharacterInventory inventory = new CharacterInventory();
            inventory.setCharacterId(characterId);
            inventory.setItemType(itemType);
            inventory.setItemId(itemId);
            inventory.setQuantity(quantity);
            inventory.setAcquiredAt(LocalDateTime.now());
            this.save(inventory);
            logger.info("添加背包物品: characterId={}, itemType={}, itemId={}, quantity={}",
                    characterId, itemType, itemId, quantity);
        }
    }

    @Override
    @Transactional
    public boolean removeItem(Long characterId, String itemType, Long itemId, int quantity) {
        if (quantity <= 0) {
            return true;
        }

        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);

        if (existing == null || existing.getQuantity() < quantity) {
            return false;
        }

        int newQuantity = existing.getQuantity() - quantity;
        if (newQuantity <= 0) {
            this.removeById(existing.getInventoryId());
            logger.info("移除背包物品: characterId={}, itemType={}, itemId={}",
                    characterId, itemType, itemId);
        } else {
            existing.setQuantity(newQuantity);
            this.updateById(existing);
            logger.info("减少背包物品: characterId={}, itemType={}, itemId={}, newQuantity={}",
                    characterId, itemType, itemId, newQuantity);
        }

        return true;
    }

    @Override
    public int getItemQuantity(Long characterId, String itemType, Long itemId) {
        LambdaQueryWrapper<CharacterInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterInventory::getCharacterId, characterId)
                .eq(CharacterInventory::getItemType, itemType)
                .eq(CharacterInventory::getItemId, itemId);

        CharacterInventory existing = this.getOne(wrapper);
        return existing != null ? existing.getQuantity() : 0;
    }

    @Override
    public boolean hasEnoughItem(Long characterId, String itemType, Long itemId, int quantity) {
        return getItemQuantity(characterId, itemType, itemId) >= quantity;
    }

    @Override
    @Transactional
    public SellItemResponse sellItem(Long characterId, Long inventoryId, Integer quantity) {
        // 1. 查询背包物品
        CharacterInventory inventory = this.getById(inventoryId);
        if (inventory == null) {
            throw new BusinessException(3001, "背包物品不存在");
        }

        // 验证物品属于该角色
        if (!inventory.getCharacterId().equals(characterId)) {
            throw new BusinessException(3002, "无权出售此物品");
        }

        // 验证数量
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(3003, "出售数量必须大于0");
        }

        if (quantity > inventory.getQuantity()) {
            throw new BusinessException(3004, "出售数量超过拥有数量");
        }

        // 1.5. 如果是装备，检查出售后是否会少于已装备数量
        if ("equipment".equals(inventory.getItemType())) {
            Long equipmentId = inventory.getItemId();

            // 查询该装备已装备的数量
            LambdaQueryWrapper<CharacterEquipment> equipmentWrapper = new LambdaQueryWrapper<>();
            equipmentWrapper.eq(CharacterEquipment::getCharacterId, characterId)
                    .eq(CharacterEquipment::getEquipmentId, equipmentId);
            Long equippedCount = characterEquipmentMapper.selectCount(equipmentWrapper);

            // 计算出售后剩余数量
            int remainingQuantity = inventory.getQuantity() - quantity;

            // 检查：剩余数量必须 >= 已装备数量
            if (remainingQuantity < equippedCount) {
                Equipment equipment = equipmentMapper.selectById(equipmentId);
                String equipmentName = equipment != null ? equipment.getEquipmentName() : "该装备";
                throw new BusinessException(6005, String.format(
                        "装备[%s]已装备%d件，出售%d件后剩余%d件，不足以保留已装备的装备。请先卸下装备或减少出售数量。",
                        equipmentName, equippedCount, quantity, remainingQuantity));
            }
        }

        // 2. 计算售价
        Long spiritStones = calculatePrice(inventory, quantity);

        // 3. 从背包移除物品
        String itemType = inventory.getItemType();
        Long itemId = inventory.getItemId();
        boolean removed = removeItem(characterId, itemType, itemId, quantity);

        if (!removed) {
            throw new BusinessException(3005, "移除物品失败");
        }

        // 4. 给角色增加灵石
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        Long currentStones = character.getSpiritStones() != null ? character.getSpiritStones() : 0L;
        character.setSpiritStones(currentStones + spiritStones);
        characterService.updateById(character);

        // 5. 获取物品名称
        String itemName = getItemName(itemType, itemId);

        logger.info("出售物品成功: characterId={}, itemName={}, quantity={}, spiritStones={}",
                characterId, itemName, quantity, spiritStones);

        // 6. 返回结果
        return new SellItemResponse(
                itemName,
                quantity,
                spiritStones,
                character.getSpiritStones(),
                String.format("成功出售 %s x%d，获得 %d 灵石", itemName, quantity, spiritStones)
        );
    }

    /**
     * 计算物品售价
     */
    private Long calculatePrice(CharacterInventory inventory, int quantity) {
        String itemType = inventory.getItemType();
        Long itemId = inventory.getItemId();
        long basePrice = 0;

        switch (itemType) {
            case "equipment":
                Equipment equipment = equipmentMapper.selectById(itemId);
                if (equipment != null) {
                    // 装备售价 = 基础评分 * 10
                    basePrice = equipment.getBaseScore() * 10L;
                }
                break;

            case "material":
                Material material = materialMapper.selectById(itemId);
                if (material != null) {
                    // 材料售价 = 阶数 * 50
                    basePrice = material.getMaterialTier() * 50L;
                }
                break;

            case "pill":
                Pill pill = pillMapper.selectById(itemId);
                if (pill != null) {
                    // 丹药售价 = 阶数 * 80
                    basePrice = pill.getPillTier() * 80L;
                }
                break;

            default:
                basePrice = 10L; // 默认售价
        }

        return basePrice * quantity;
    }

    /**
     * 获取物品名称
     */
    private String getItemName(String itemType, Long itemId) {
        switch (itemType) {
            case "equipment":
                Equipment equipment = equipmentMapper.selectById(itemId);
                return equipment != null ? equipment.getEquipmentName() : "未知装备";

            case "material":
                Material material = materialMapper.selectById(itemId);
                return material != null ? material.getMaterialName() : "未知材料";

            case "pill":
                Pill pill = pillMapper.selectById(itemId);
                return pill != null ? pill.getPillName() : "未知丹药";

            default:
                return "未知物品";
        }
    }

    @Override
    @Transactional
    public UsePillResponse usePill(Long characterId, Long inventoryId, Integer quantity) {
        logger.info("开始使用丹药: characterId={}, inventoryId={}, quantity={}",
                characterId, inventoryId, quantity);

        // 1. 验证角色存在
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证背包物品存在
        CharacterInventory inventory = this.getById(inventoryId);
        if (inventory == null) {
            throw new BusinessException(3001, "背包物品不存在");
        }

        // 3. 验证物品属于该角色
        if (!inventory.getCharacterId().equals(characterId)) {
            throw new BusinessException(3001, "该物品不属于该角色");
        }

        // 4. 验证物品类型为丹药
        if (!"pill".equals(inventory.getItemType())) {
            throw new BusinessException(3005, "该物品不是丹药");
        }

        // 5. 验证数量足够
        if (inventory.getQuantity() < quantity) {
            throw new BusinessException(3006,
                String.format("丹药数量不足，需要%d个，当前%d个", quantity, inventory.getQuantity()));
        }

        // 6. 获取丹药信息
        Pill pill = pillMapper.selectById(inventory.getItemId());
        if (pill == null) {
            throw new BusinessException(3007, "丹药信息不存在");
        }

        // 7. 验证丹药类型可使用
        if (!isUsablePill(pill.getEffectType())) {
            throw new BusinessException(3008,
                String.format("该丹药[%s]不能直接使用", pill.getEffectType()));
        }

        // 8. 检查使用条件
        checkUsageConditions(character, pill, quantity);

        // 保存原始值用于对比
        int originalComprehension = character.getComprehension() != null ? character.getComprehension() : 0;
        int originalHealth = character.getHealth() != null ? character.getHealth() : 0;
        int originalSpirit = character.getSpiritualPower() != null ? character.getSpiritualPower() : 0;
        long originalExp = character.getExperience() != null ? character.getExperience() : 0L;
        int originalConstitution = character.getConstitution() != null ? character.getConstitution() : 0;
        int originalSpiritStat = character.getSpirit() != null ? character.getSpirit() : 0; // 精神属性

        // 9. 应用丹药效果
        applyPillEffect(character, pill, quantity);

        // 10. 扣减丹药数量
        removeItem(characterId, "pill", inventory.getItemId(), quantity);

        // 11. 更新角色信息
        boolean updateSuccess = characterService.updateById(character);
        if (!updateSuccess) {
            throw new BusinessException(3009, "更新角色信息失败");
        }

        // 11.5. 如果是增加体质或精神的丹药，需要重新计算衍生属性（生命值、灵力值上限）
        if ("增加体质".equals(pill.getEffectType()) || "增加精神".equals(pill.getEffectType())) {
            characterService.recalculateDerivedAttributes(characterId);
        }

        // 12. 从数据库重新读取角色信息，确保显示的值是正确的
        PlayerCharacter updatedCharacter = characterService.getById(characterId);
        if (updatedCharacter == null) {
            throw new BusinessException(1003, "无法获取更新后的角色信息");
        }

        // 13. 构建响应
        UsePillResponse response = new UsePillResponse();
        response.setPillName(pill.getPillName());
        response.setQuantityUsed(quantity);
        response.setEffectType(pill.getEffectType());
        response.setTotalEffect(pill.getEffectValue() * quantity);
        response.setMessage(buildSuccessMessage(pill, quantity, updatedCharacter, originalComprehension, originalHealth, originalSpirit, originalExp, originalConstitution, originalSpiritStat));
        response.setCurrentHealth(updatedCharacter.getHealth()); // 注意：使用 health 字段
        response.setCurrentSpirit(updatedCharacter.getSpiritualPower());
        response.setExperience(updatedCharacter.getExperience());
        response.setComprehension(updatedCharacter.getComprehension());
        // 攻击和防御可能通过其他属性计算得出，这里设置为null
        response.setAttack(null);
        response.setDefense(null);

        logger.info("使用丹药成功: {}", response.getMessage());
        return response;
    }

    /**
     * 判断丹药是否可直接使用
     * 注：
     * - 增加体质直接增加体质属性
     * - 增加精神直接增加精神属性
     * - 解除毒素暂无毒素系统
     */
    private boolean isUsablePill(String effectType) {
        return java.util.Set.of("恢复生命", "恢复灵力", "增加经验",
                "增加体质", "增加精神", "改善资质", "解除毒素").contains(effectType);
    }

    /**
     * 检查使用条件
     */
    private void checkUsageConditions(PlayerCharacter character, Pill pill, int quantity) {
        String effectType = pill.getEffectType();

        switch (effectType) {
            case "恢复生命": {
                Integer currentHealth = character.getHealth(); // 使用 health 字段
                Integer maxHealth = character.getHealthMax();
                // 只有当当前值不为null且已满时才抛出异常
                if (currentHealth != null && maxHealth != null && currentHealth >= maxHealth) {
                    throw new BusinessException(3002, "当前生命值已满，无需使用恢复丹药");
                }
                break;
            }

            case "恢复灵力": {
                Integer currentSpirit = character.getSpiritualPower();
                Integer maxSpirit = character.getSpiritualPowerMax();
                // 只有当当前值不为null且已满时才抛出异常
                if (currentSpirit != null && maxSpirit != null && currentSpirit >= maxSpirit) {
                    throw new BusinessException(3003, "当前灵力值已满，无需使用恢复丹药");
                }
                break;
            }

            default:
                break;
        }
    }

    /**
     * 应用丹药效果到角色
     */
    private void applyPillEffect(PlayerCharacter character, Pill pill, int quantity) {
        String effectType = pill.getEffectType();
        int totalEffect = pill.getEffectValue() * quantity;
        int maxAttribute = attributeProperties.getMax().getAttribute(); // 统一声明maxAttribute

        switch (effectType) {
            case "恢复生命":
                int currentHealth = character.getHealth() != null ? character.getHealth() : 0; // 使用 health 字段
                int maxHealth = character.getHealthMax() != null ? character.getHealthMax() : 0;
                int newHealth = Math.min(currentHealth + totalEffect, maxHealth);
                int actualHealthGain = newHealth - currentHealth;
                logger.debug("恢复生命: {} -> {} (+{}, 效果值:{})", currentHealth, newHealth, actualHealthGain, totalEffect);
                character.setHealth(newHealth); // 使用 setHealth
                break;

            case "恢复灵力":
                int currentSpiritualPower = character.getSpiritualPower() != null ? character.getSpiritualPower() : 0;
                int maxSpiritualPower = character.getSpiritualPowerMax() != null ? character.getSpiritualPowerMax() : 0;
                int newSpiritualPower = Math.min(currentSpiritualPower + totalEffect, maxSpiritualPower);
                int actualSpiritualPowerGain = newSpiritualPower - currentSpiritualPower;
                logger.debug("恢复灵力: {} -> {} (+{}, 效果值:{})", currentSpiritualPower, newSpiritualPower, actualSpiritualPowerGain, totalEffect);
                character.setSpiritualPower(newSpiritualPower);
                break;

            case "增加经验":
                long currentExp = character.getExperience() != null ? character.getExperience() : 0L;
                logger.debug("增加经验: {} -> {} (+{})", currentExp, currentExp + totalEffect, totalEffect);
                character.setExperience(currentExp + totalEffect);
                break;

            case "增加体质":
                int constitution = character.getConstitution() != null ? character.getConstitution() : 0;
                int newConstitution = Math.min(constitution + totalEffect, maxAttribute);
                int actualConstitutionGain = newConstitution - constitution;
                logger.debug("增加体质: {} -> {} (+{}, 效果值:{}, 上限:{})",
                    constitution, newConstitution, actualConstitutionGain, totalEffect, maxAttribute);
                character.setConstitution(newConstitution);
                break;

            case "增加精神":
                int spirit = character.getSpirit() != null ? character.getSpirit() : 0;
                int newSpirit = Math.min(spirit + totalEffect, maxAttribute);
                int actualSpiritGain = newSpirit - spirit;
                logger.debug("增加精神: {} -> {} (+{}, 效果值:{}, 上限:{})",
                    spirit, newSpirit, actualSpiritGain, totalEffect, maxAttribute);
                character.setSpirit(newSpirit);
                break;

            case "改善资质":
                int comprehension = character.getComprehension() != null ? character.getComprehension() : 0;
                int newComprehension = Math.min(comprehension + totalEffect, maxAttribute);
                int actualComprehensionGain = newComprehension - comprehension;
                logger.debug("改善资质: {} -> {} (+{}, 效果值:{}, 上限:{})",
                    comprehension, newComprehension, actualComprehensionGain, totalEffect, maxAttribute);
                character.setComprehension(newComprehension);
                break;

            case "解除毒素":
                throw new BusinessException(3010, "当前游戏暂无毒素系统");

            default:
                throw new BusinessException(3004, "不支持的丹药类型: " + effectType);
        }
    }

    /**
     * 构建成功消息
     */
    private String buildSuccessMessage(Pill pill, int quantity, PlayerCharacter character,
                                       int originalComprehension, int originalHealth,
                                       int originalSpirit, long originalExp, int originalConstitution, int originalSpiritStat) {
        String effectType = pill.getEffectType();
        int totalEffect = pill.getEffectValue() * quantity;
        int maxAttribute = attributeProperties.getMax().getAttribute();

        return switch (effectType) {
            case "恢复生命" -> {
                int actualGain = character.getHealth() - originalHealth;
                if (actualGain < totalEffect) {
                    yield String.format("使用成功！生命值 %d -> %d (+%d, 已满)",
                        originalHealth, character.getHealth(), actualGain);
                } else {
                    yield String.format("使用成功！生命值 %d -> %d (+%d)",
                        originalHealth, character.getHealth(), actualGain);
                }
            }
            case "恢复灵力" -> {
                int actualGain = character.getSpiritualPower() - originalSpirit;
                if (actualGain < totalEffect) {
                    yield String.format("使用成功！灵力值 %d -> %d (+%d, 已满)",
                        originalSpirit, character.getSpiritualPower(), actualGain);
                } else {
                    yield String.format("使用成功！灵力值 %d -> %d (+%d)",
                        originalSpirit, character.getSpiritualPower(), actualGain);
                }
            }
            case "增加经验" -> {
                yield String.format("使用成功！经验值 %d -> %d (+%d)",
                    originalExp, character.getExperience(), totalEffect);
            }
            case "增加体质" -> {
                int actualGain = character.getConstitution() - originalConstitution;
                if (actualGain == 0) {
                    yield String.format("使用成功！体质已是最大值(%d)，无法继续提升", maxAttribute);
                } else if (actualGain < totalEffect) {
                    yield String.format("使用成功！体质 %d -> %d (+%d, 已达上限)",
                        originalConstitution, character.getConstitution(), actualGain);
                } else {
                    yield String.format("使用成功！体质 %d -> %d (+%d)",
                        originalConstitution, character.getConstitution(), actualGain);
                }
            }
            case "增加精神" -> {
                int actualGain = character.getSpirit() - originalSpiritStat;
                if (actualGain == 0) {
                    yield String.format("使用成功！精神已是最大值(%d)，无法继续提升", maxAttribute);
                } else if (actualGain < totalEffect) {
                    yield String.format("使用成功！精神 %d -> %d (+%d, 已达上限)",
                        originalSpiritStat, character.getSpirit(), actualGain);
                } else {
                    yield String.format("使用成功！精神 %d -> %d (+%d)",
                        originalSpiritStat, character.getSpirit(), actualGain);
                }
            }
            case "改善资质" -> {
                int actualGain = character.getComprehension() - originalComprehension;
                if (actualGain == 0) {
                    yield String.format("使用成功！悟性已是最大值(%d)，无法继续提升", maxAttribute);
                } else if (actualGain < totalEffect) {
                    yield String.format("使用成功！悟性 %d -> %d (+%d, 已达上限)",
                        originalComprehension, character.getComprehension(), actualGain);
                } else {
                    yield String.format("使用成功！悟性 %d -> %d (+%d)",
                        originalComprehension, character.getComprehension(), actualGain);
                }
            }
            case "解除毒素" -> "使用成功！解除毒素状态";
            default -> String.format("使用成功！%s了%d点%s", getEffectVerb(effectType), totalEffect, getEffectTarget(effectType));
        };
    }

    /**
     * 获取效果动词
     */
    private String getEffectVerb(String effectType) {
        return switch (effectType) {
            case "恢复生命", "恢复灵力" -> "恢复";
            case "增加经验", "增加体质", "增加精神", "改善资质" -> "增加";
            case "解除毒素" -> "解除";
            default -> "产生";
        };
    }

    /**
     * 获取效果目标
     */
    private String getEffectTarget(String effectType) {
        return switch (effectType) {
            case "恢复生命" -> "生命值";
            case "恢复灵力" -> "灵力值";
            case "增加经验" -> "经验";
            case "增加体质" -> "体质";
            case "增加精神" -> "精神";
            case "改善资质" -> "悟性";
            case "解除毒素" -> "毒素状态";
            default -> "效果";
        };
    }
}
