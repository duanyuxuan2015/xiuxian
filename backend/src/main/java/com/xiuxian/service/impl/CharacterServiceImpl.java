package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.config.AttributeProperties;
import com.xiuxian.dto.request.AllocatePointsRequest;
import com.xiuxian.dto.request.CharacterCreateRequest;
import com.xiuxian.dto.response.AllocatePointsResponse;
import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
import com.xiuxian.entity.Sect;
import com.xiuxian.mapper.CharacterMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.SectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色Service实现类
 */
@Service
public class CharacterServiceImpl extends ServiceImpl<CharacterMapper, PlayerCharacter> implements CharacterService {

    private static final Logger logger = LoggerFactory.getLogger(CharacterServiceImpl.class);

    // ... constants omitted for brevity in replacement check, but actually I need
    // to match exact content.
    // Wait, replacing a huge block is risky. I should use replacement chunks.

    /**
     * 初始属性点基础值
     */
    private static final int BASE_ATTRIBUTE = 5;

    /**
     * 初始可分配点数
     */
    private static final int INITIAL_DISTRIBUTABLE_POINTS = 20;

    /**
     * 初始总点数 = 基础值×5 + 可分配点数 = 25 + 20 = 45
     */
    private static final int TOTAL_INITIAL_POINTS = BASE_ATTRIBUTE * 5 + INITIAL_DISTRIBUTABLE_POINTS;

    private final RealmService realmService;
    private final SectService sectService;
    private final AttributeProperties attributeProperties;
    private final EquipmentService equipmentService;

    public CharacterServiceImpl(RealmService realmService, SectService sectService,
                                AttributeProperties attributeProperties,
                                @Lazy EquipmentService equipmentService) {
        this.realmService = realmService;
        this.sectService = sectService;
        this.attributeProperties = attributeProperties;
        this.equipmentService = equipmentService;
    }

    @Override
    @Transactional
    public CharacterResponse createCharacter(CharacterCreateRequest request) {
        // 1. 验证角色名是否已存在
        if (checkNameExists(request.getPlayerName())) {
            throw new BusinessException(1001, "角色名称已存在");
        }

        // 2. 验证属性点分配
        int totalPoints = request.getTotalPoints();
        if (totalPoints != TOTAL_INITIAL_POINTS) {
            throw new BusinessException(1002, "属性点分配不正确，总点数应为" + TOTAL_INITIAL_POINTS + "，当前为" + totalPoints);
        }

        // 3. 验证单个属性范围
        validateAttributeRange(request.getConstitution(), "体质");
        validateAttributeRange(request.getSpirit(), "精神");
        validateAttributeRange(request.getComprehension(), "悟性");
        validateAttributeRange(request.getLuck(), "机缘");
        validateAttributeRange(request.getFortune(), "气运");

        // 4. 获取初始境界（炼气期）
        Realm initialRealm = realmService.getByRealmLevel(2);
        if (initialRealm == null) {
            throw new BusinessException(2001, "境界数据未初始化");
        }

        // 5. 创建角色实体
        PlayerCharacter character = new PlayerCharacter();
        character.setPlayerName(request.getPlayerName());
        character.setRealmId(initialRealm.getId());
        character.setRealmLevel(1);
        character.setExperience(0L);
        character.setAvailablePoints(0);
        character.setSpiritualPower(100);
        character.setSpiritualPowerMax(100);
        character.setStamina(100);
        character.setStaminaMax(100);
        character.setHealth(100);
        character.setHealthMax(100);
        character.setMindset(50);
        character.setConstitution(request.getConstitution());
        character.setSpirit(request.getSpirit());
        character.setComprehension(request.getComprehension());
        character.setLuck(request.getLuck());
        character.setFortune(request.getFortune());
        character.setSpiritStones(0L);
        character.setContribution(0);
        character.setReputation(0);
        character.setAlchemyLevel(1);
        character.setAlchemyExp(0);
        character.setForgingLevel(1);
        character.setForgingExp(0);
        character.setCurrentState("闲置");
        character.setDeleted(0);

        // 6. 根据五维属性重新计算衍生属性
        recalculateDerivedAttributes(character);

        // 7. 保存角色
        this.save(character);

        logger.info("创建角色成功: characterId={}, playerName={}", character.getCharacterId(), character.getPlayerName());

        // 7. 返回响应
        CharacterResponse response = CharacterResponse.fromEntity(character, initialRealm.getRealmName(), null);
        // 设置真正的境界等级
        response.setRealmLevel(initialRealm.getRealmLevel());
        // 设置境界层级
        response.setRealmSubLevel(character.getRealmLevel());
        return response;
    }

    @Override
    public CharacterResponse getCharacterById(Long characterId) {
        // 1. 查询角色
        PlayerCharacter character = this.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 查询境界信息
        Realm realm = realmService.getById(character.getRealmId());
        String realmName = realm != null ? realm.getRealmName() : "未知";
        Integer realmLevel = realm != null ? realm.getRealmLevel() : 1; // 获取真正的境界等级（1-14）

        // 3. 查询宗门信息
        String sectName = null;
        if (character.getSectId() != null) {
            Sect sect = sectService.getById(character.getSectId());
            sectName = sect != null ? sect.getSectName() : null;
        }

        // 4. 构建响应，包含攻击力和防御力
        CharacterResponse response = CharacterResponse.fromEntity(character, realmName, sectName);
        // 覆盖realmLevel为真正的境界等级（而不是层级）
        response.setRealmLevel(realmLevel);
        // 设置境界层级（1-9层）
        response.setRealmSubLevel(character.getRealmLevel());
        response.setAttack(calculateAttack(character));
        response.setDefense(calculateDefense(character));

        // 5. 获取装备抗性加成
        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(characterId);
        response.setPhysicalResist(bonus.physicalResistBonus);
        response.setIceResist(bonus.iceResistBonus);
        response.setFireResist(bonus.fireResistBonus);
        response.setLightningResist(bonus.lightningResistBonus);

        return response;
    }

    @Override
    public boolean checkNameExists(String playerName) {
        LambdaQueryWrapper<PlayerCharacter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayerCharacter::getPlayerName, playerName);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean updateCharacter(PlayerCharacter character) {
        return this.updateById(character);
    }

    /**
     * 验证属性值范围
     */
    private void validateAttributeRange(Integer value, String attributeName) {
        if (value == null || value < 1 || value > 100000) {
            throw new BusinessException(1004, attributeName + "属性值必须在1-100000之间");
        }
    }

    @Override
    @Transactional
    public AllocatePointsResponse allocatePoints(AllocatePointsRequest request) {
        // 1. 查询角色
        PlayerCharacter character = this.getById(request.getCharacterId());
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证可分配点数
        int requestedPoints = request.getTotalPoints();
        if (requestedPoints <= 0) {
            throw new BusinessException(2002, "分配点数必须大于0");
        }
        if (character.getAvailablePoints() < requestedPoints) {
            throw new BusinessException(2003, "可分配点数不足，当前可用：" + character.getAvailablePoints() + "，请求分配：" + requestedPoints);
        }

        // 3. 验证并计算新属性值
        int newConstitution = character.getConstitution() + request.getConstitutionPoints();
        int newSpirit = character.getSpirit() + request.getSpiritPoints();
        int newComprehension = character.getComprehension() + request.getComprehensionPoints();
        int newLuck = character.getLuck() + request.getLuckPoints();
        int newFortune = character.getFortune() + request.getFortunePoints();

        // 4. 验证属性上限（999）
        validateAllocationAttributeLimit(newConstitution, "体质");
        validateAllocationAttributeLimit(newSpirit, "精神");
        validateAllocationAttributeLimit(newComprehension, "悟性");
        validateAllocationAttributeLimit(newLuck, "机缘");
        validateAllocationAttributeLimit(newFortune, "气运");

        // 5. 更新角色属性
        character.setConstitution(newConstitution);
        character.setSpirit(newSpirit);
        character.setComprehension(newComprehension);
        character.setLuck(newLuck);
        character.setFortune(newFortune);
        character.setAvailablePoints(character.getAvailablePoints() - requestedPoints);

        // 6. 重新计算衍生属性（气血、体力、灵力）
        recalculateDerivedAttributes(character);

        // 7. 保存更新
        this.updateById(character);

        logger.info("分配属性点成功: characterId={}, constitution={}, spirit={}, comprehension={}, luck={}, fortune={}, remainingPoints={}",
                character.getCharacterId(), newConstitution, newSpirit, newComprehension, newLuck, newFortune, character.getAvailablePoints());

        // 7. 构造响应（包含衍生属性）
        AllocatePointsResponse response = new AllocatePointsResponse();
        response.setCharacterId(character.getCharacterId());
        response.setPlayerName(character.getPlayerName());
        response.setNewConstitution(newConstitution);
        response.setNewSpirit(newSpirit);
        response.setNewComprehension(newComprehension);
        response.setNewLuck(newLuck);
        response.setNewFortune(newFortune);
        response.setRemainingPoints(character.getAvailablePoints());
        response.setMessage("属性点分配成功");

        // 填充衍生属性
        response.setNewAttack(calculateAttack(character));
        response.setNewDefense(calculateDefense(character));
        response.setNewHealthMax(character.getHealthMax());
        response.setNewStaminaMax(character.getStaminaMax());
        response.setNewSpiritualPowerMax(character.getSpiritualPowerMax());
        response.setNewCritRate(character.getCritRate());
        response.setNewCritDamage(character.getCritDamage());
        response.setNewSpeed(character.getSpeed());

        return response;
    }

    /**
     * 验证分配后的属性上限
     */
    private void validateAllocationAttributeLimit(int value, String attributeName) {
        if (value > 999) {
            throw new BusinessException(2004, attributeName + "属性不能超过999，当前值：" + value);
        }
    }

    /**
     * 重新计算衍生属性（基于五维属性和境界加成）
     * 根据FR-008要求：体质影响气血和体力，精神影响灵力，气运影响暴击和速度
     * 境界提升会提供额外的属性加成
     */
    private void recalculateDerivedAttributes(PlayerCharacter character) {
        // 从配置读取基础值
        int baseHealth = attributeProperties.getBase().getHealth();
        int baseStamina = attributeProperties.getBase().getStamina();
        int baseSpiritualPower = attributeProperties.getBase().getSpiritualPower();
        double baseCritRate = attributeProperties.getBase().getCritRate();
        double baseCritDamage = attributeProperties.getBase().getCritDamage();
        double baseSpeed = attributeProperties.getBase().getSpeed();

        // 从配置读取系数
        int constitutionCoefficient = attributeProperties.getCoefficient().getConstitution();
        int spiritCoefficient = attributeProperties.getCoefficient().getSpirit();
        double fortuneCritRateCoefficient = attributeProperties.getCoefficient().getFortuneCritRate();
        double fortuneCritDamageCoefficient = attributeProperties.getCoefficient().getFortuneCritDamage();
        double fortuneSpeedCoefficient = attributeProperties.getCoefficient().getFortuneSpeed();

        // 获取境界加成
        Realm realm = realmService.getById(character.getRealmId());
        int realmHpBonus = (realm != null && realm.getHpBonus() != null) ? realm.getHpBonus() : 0;
        int realmSpBonus = (realm != null && realm.getSpBonus() != null) ? realm.getSpBonus() : 0;
        int realmStaminaBonus = (realm != null && realm.getStaminaBonus() != null) ? realm.getStaminaBonus() : 0;

        // 计算气血最大值 = 基础值 + 体质 × 系数 + 境界加成
        int newHealthMax = baseHealth + character.getConstitution() * constitutionCoefficient + realmHpBonus;

        // 计算体力最大值 = 基础值 + 体质 × 系数 + 境界加成
        int newStaminaMax = baseStamina + character.getConstitution() * constitutionCoefficient + realmStaminaBonus;

        // 计算灵力最大值 = 基础值 + 精神 × 系数 + 境界加成
        int newSpiritualPowerMax = baseSpiritualPower + character.getSpirit() * spiritCoefficient + realmSpBonus;

        // 计算暴击率（%）= 基础暴击率 + 气运 × 暴击率系数
        double critRate = baseCritRate + character.getFortune() * fortuneCritRateCoefficient;

        // 计算暴击伤害（%）= 基础暴击伤害 + 气运 × 暴击伤害系数
        double critDamage = baseCritDamage + character.getFortune() * fortuneCritDamageCoefficient;

        // 计算速度 = 基础速度 + 气运 × 速度系数
        double speed = baseSpeed + character.getFortune() * fortuneSpeedCoefficient;

        // 更新衍生属性最大值
        character.setHealthMax(newHealthMax);
        character.setStaminaMax(newStaminaMax);
        character.setSpiritualPowerMax(newSpiritualPowerMax);

        // 更新暴击率、暴击伤害、速度
        character.setCritRate(critRate);
        character.setCritDamage(critDamage);
        character.setSpeed(speed);

        // 如果当前值超过新的最大值，则调整为最大值
        if (character.getCurrentHealth() != null && character.getCurrentHealth() > newHealthMax) {
            character.setCurrentHealth(newHealthMax);
        }
        if (character.getStamina() != null && character.getStamina() > newStaminaMax) {
            character.setStamina(newStaminaMax);
        }
        if (character.getSpiritualPower() != null && character.getSpiritualPower() > newSpiritualPowerMax) {
            character.setSpiritualPower(newSpiritualPowerMax);
        }

        // 同时更新health字段（用于兼容）
        character.setHealth(newHealthMax);

        logger.debug("重新计算衍生属性: characterId={}, healthMax={}, staminaMax={}, spiritualPowerMax={}, " +
                        "critRate={}%, critDamage={}%, speed={}, realmHpBonus={}, realmSpBonus={}, realmStaminaBonus={}",
                character.getCharacterId(), newHealthMax, newStaminaMax, newSpiritualPowerMax,
                String.format("%.2f", critRate), String.format("%.2f", critDamage), String.format("%.2f", speed),
                realmHpBonus, realmSpBonus, realmStaminaBonus);
    }

    /**
     * 计算角色攻击力
     * 攻击力 = 体质 × 攻击力体质系数 + 精神 × 攻击力精神系数 + 境界攻击加成 + (境界层级 - 1) × 层级攻击系数
     */
    private int calculateAttack(PlayerCharacter character) {
        // 从配置读取系数
        double attackConstitutionCoefficient = attributeProperties.getCoefficient().getAttackConstitution();
        double attackSpiritCoefficient = attributeProperties.getCoefficient().getAttackSpirit();
        double attackPerLevel = attributeProperties.getCoefficient().getAttackPerLevel();

        // 基础攻击力 = 体质 × 攻击力体质系数 + 精神 × 攻击力精神系数
        int baseAttack = (int) (character.getConstitution() * attackConstitutionCoefficient
                + character.getSpirit() * attackSpiritCoefficient);

        // 获取境界攻击加成
        Realm realm = realmService.getById(character.getRealmId());
        int realmAttackBonus = (realm != null && realm.getAttackBonus() != null) ? realm.getAttackBonus() : 0;

        // 境界层级加成（层级从1开始，所以减1，这样第1层没有额外加成）
        int realmLevel = character.getRealmLevel() != null ? character.getRealmLevel() : 1;
        int levelAttackBonus = (int) ((realmLevel - 1) * attackPerLevel);

        return baseAttack + realmAttackBonus + levelAttackBonus;
    }

    /**
     * 计算角色防御力
     * 防御力 = 体质 × 防御力体质系数 + 境界防御加成 + (境界层级 - 1) × 层级防御系数
     */
    private int calculateDefense(PlayerCharacter character) {
        // 从配置读取系数
        double defenseConstitutionCoefficient = attributeProperties.getCoefficient().getDefenseConstitution();
        double defensePerLevel = attributeProperties.getCoefficient().getDefensePerLevel();

        // 基础防御力 = 体质 × 防御力体质系数
        int baseDefense = (int) (character.getConstitution() * defenseConstitutionCoefficient);

        // 获取境界防御加成
        Realm realm = realmService.getById(character.getRealmId());
        int realmDefenseBonus = (realm != null && realm.getDefenseBonus() != null) ? realm.getDefenseBonus() : 0;

        // 境界层级加成（层级从1开始，所以减1，这样第1层没有额外加成）
        int realmLevel = character.getRealmLevel() != null ? character.getRealmLevel() : 1;
        int levelDefenseBonus = (int) ((realmLevel - 1) * defensePerLevel);

        return baseDefense + realmDefenseBonus + levelDefenseBonus;
    }

    @Override
    @Transactional
    public void recalculateDerivedAttributes(Long characterId) {
        PlayerCharacter character = this.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 调用私有方法重新计算衍生属性
        recalculateDerivedAttributes(character);

        // 保存更新
        this.updateById(character);

        logger.info("重新计算衍生属性: characterId={}, healthMax={}, staminaMax={}, spiritualPowerMax={}",
                characterId, character.getHealthMax(), character.getStaminaMax(), character.getSpiritualPowerMax());
    }
}
