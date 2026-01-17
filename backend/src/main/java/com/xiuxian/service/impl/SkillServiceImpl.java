package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.EquipSkillRequest;
import com.xiuxian.dto.request.LearnSkillRequest;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
// ... (imports)

// ...

// ...
import com.xiuxian.config.CombatConstants;
import com.xiuxian.entity.CharacterSkill;
import com.xiuxian.entity.Skill;
import com.xiuxian.mapper.CharacterSkillMapper;
import com.xiuxian.mapper.SkillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技能Service实现类
 */
@Service
public class SkillServiceImpl extends ServiceImpl<CharacterSkillMapper, CharacterSkill> implements SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillServiceImpl.class);
    private static final int MAX_SKILL_SLOTS = 8;

    private final CharacterService characterService;
    private final SkillMapper skillMapper;
    private final RealmService realmService;
    private final InventoryService inventoryService;

    public SkillServiceImpl(@Lazy CharacterService characterService,
                            SkillMapper skillMapper,
                            RealmService realmService,
                            @Lazy InventoryService inventoryService) {
        this.characterService = characterService;
        this.skillMapper = skillMapper;
        this.realmService = realmService;
        this.inventoryService = inventoryService;
    }

    @Override
    public List<SkillResponse> getAvailableSkills(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 获取真正的境界等级（通过realmId查询realm表）
        Realm realm = realmService.getById(character.getRealmId());
        int realmLevel = realm != null ? realm.getRealmLevel() : 1;

        // 获取所有可学习的技能（按阶位排序）
        LambdaQueryWrapper<Skill> skillWrapper = new LambdaQueryWrapper<>();
        skillWrapper.le(Skill::getTier, realmLevel)
                .orderByAsc(Skill::getTier)
                .orderByAsc(Skill::getFunctionType);
        List<Skill> skills = skillMapper.selectList(skillWrapper);

        // 获取角色已学习的技能
        Map<Long, CharacterSkill> learnedMap = getLearnedSkillMap(characterId);

        List<SkillResponse> responses = new ArrayList<>();
        for (Skill skill : skills) {
            CharacterSkill charSkill = learnedMap.get(skill.getSkillId());
            responses.add(SkillResponse.fromEntity(skill, charSkill));
        }

        return responses;
    }

    @Override
    public List<SkillResponse> getLearnedSkills(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId)
                .orderByDesc(CharacterSkill::getIsEquipped)
                .orderByAsc(CharacterSkill::getSlotIndex);
        List<CharacterSkill> charSkills = this.list(wrapper);

        List<SkillResponse> responses = new ArrayList<>();
        for (CharacterSkill charSkill : charSkills) {
            Skill skill = skillMapper.selectById(charSkill.getSkillId());
            if (skill != null) {
                responses.add(SkillResponse.fromEntity(skill, charSkill));
            }
        }

        return responses;
    }

    @Override
    public List<SkillResponse> getEquippedSkills(Long characterId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId)
                .eq(CharacterSkill::getIsEquipped, true)
                .orderByAsc(CharacterSkill::getSlotIndex);
        List<CharacterSkill> charSkills = this.list(wrapper);

        List<SkillResponse> responses = new ArrayList<>();
        for (CharacterSkill charSkill : charSkills) {
            Skill skill = skillMapper.selectById(charSkill.getSkillId());
            if (skill != null) {
                responses.add(SkillResponse.fromEntity(skill, charSkill));
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public SkillResponse learnSkill(LearnSkillRequest request) {
        Long characterId = request.getCharacterId();
        Long skillId = request.getSkillId();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证技能
        Skill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            throw new BusinessException(7001, "技能不存在");
        }

        // 3. 验证境界
        // 获取真正的境界等级（通过realmId查询realm表）
        Realm realm = realmService.getById(character.getRealmId());
        int realmLevel = realm != null ? realm.getRealmLevel() : 1;

        logger.debug("学习技能境界检查: characterId={}, characterRealmLevel={}, skillId={}, skillTier={}, skillName={}",
                characterId, realmLevel, skillId, skill.getTier(), skill.getSkillName());

        if (realmLevel < skill.getTier()) {
            throw new BusinessException(7002, "境界不足，需要境界等级: " + skill.getTier());
        }

        // 4. 检查是否已学习
        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId)
                .eq(CharacterSkill::getSkillId, skillId);
        CharacterSkill existing = this.getOne(wrapper);
        if (existing != null) {
            throw new BusinessException(7003, "已学习该技能");
        }

        // 5. 检查背包中是否有该技能书
        if (!inventoryService.hasEnoughItem(characterId, "skill", skillId, 1)) {
            throw new BusinessException(7010, "背包中没有该技能书，无法学习");
        }

        // 6. 学习技能
        CharacterSkill charSkill = new CharacterSkill();
        charSkill.setCharacterId(characterId);
        charSkill.setSkillId(skillId);
        charSkill.setSkillLevel(1);
        charSkill.setProficiency(0);
        charSkill.setIsEquipped(false);
        charSkill.setLearnedAt(LocalDateTime.now());
        this.save(charSkill);

        logger.info("学习技能: characterId={}, skillId={}, skillName={}",
                characterId, skillId, skill.getSkillName());

        // 7. 自动消耗背包中的技能书
        boolean consumed = inventoryService.removeItem(characterId, "skill", skillId, 1);
        if (consumed) {
            logger.info("自动消耗技能书: characterId={}, skillId={}, skillName={}",
                    characterId, skillId, skill.getSkillName());
        } else {
            logger.warn("技能书消耗失败（但技能已学习）: characterId={}, skillId={}",
                    characterId, skillId);
        }

        return SkillResponse.fromEntity(skill, charSkill);
    }

    @Override
    @Transactional
    public SkillResponse equipSkill(EquipSkillRequest request) {
        Long characterId = request.getCharacterId();
        Long characterSkillId = request.getCharacterSkillId();
        Integer slotIndex = request.getSlotIndex();

        // 1. 验证角色
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        // 2. 验证角色技能
        CharacterSkill charSkill = this.getById(characterSkillId);
        if (charSkill == null || !charSkill.getCharacterId().equals(characterId)) {
            throw new BusinessException(7004, "角色技能不存在");
        }

        // 2.5. 验证槽位类型与技能类型是否匹配
        Skill skill = skillMapper.selectById(charSkill.getSkillId());
        if (skill == null) {
            throw new BusinessException(7001, "技能数据不存在");
        }

        if (!CombatConstants.canEquipToSlot(skill.getFunctionType(), slotIndex)) {
            throw new BusinessException(7006,
                String.format("技能类型[%s]不能装备到槽位%d，槽位%d-%d为%s槽位，%d-%d为%s槽位",
                    skill.getFunctionType(),
                    slotIndex,
                    CombatConstants.ATTACK_SLOT_MIN,
                    CombatConstants.ATTACK_SLOT_MAX,
                    "攻击",
                    CombatConstants.SUPPORT_SLOT_MIN,
                    CombatConstants.SUPPORT_SLOT_MAX,
                    "防御/辅助"));
        }

        // 3. 检查槽位是否已有技能，如有则先卸下
        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId)
                .eq(CharacterSkill::getSlotIndex, slotIndex)
                .eq(CharacterSkill::getIsEquipped, true);
        CharacterSkill existingInSlot = this.getOne(wrapper);
        if (existingInSlot != null && !existingInSlot.getCharacterSkillId().equals(characterSkillId)) {
            existingInSlot.setIsEquipped(false);
            existingInSlot.setSlotIndex(null);
            this.updateById(existingInSlot);
            logger.info("卸下槽位技能: characterId={}, slot={}, skillId={}",
                    characterId, slotIndex, existingInSlot.getSkillId());
        }

        // 4. 装备技能
        charSkill.setIsEquipped(true);
        charSkill.setSlotIndex(slotIndex);
        this.updateById(charSkill);

        logger.info("装备技能: characterId={}, slot={}, skillId={}, skillName={}",
                characterId, slotIndex, skill.getSkillId(), skill.getSkillName());

        return SkillResponse.fromEntity(skill, charSkill);
    }

    @Override
    @Transactional
    public boolean unequipSkill(Long characterId, Long characterSkillId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        CharacterSkill charSkill = this.getById(characterSkillId);
        if (charSkill == null || !charSkill.getCharacterId().equals(characterId)) {
            throw new BusinessException(7004, "角色技能不存在");
        }

        if (!Boolean.TRUE.equals(charSkill.getIsEquipped())) {
            throw new BusinessException(7005, "该技能未装备");
        }

        charSkill.setIsEquipped(false);
        charSkill.setSlotIndex(null);
        this.updateById(charSkill);

        logger.info("卸下技能: characterId={}, characterSkillId={}", characterId, characterSkillId);

        return true;
    }

    @Override
    @Transactional
    public SkillResponse upgradeSkill(Long characterId, Long characterSkillId) {
        PlayerCharacter character = characterService.getById(characterId);
        if (character == null) {
            throw new BusinessException(1003, "角色不存在");
        }

        CharacterSkill charSkill = this.getById(characterSkillId);
        if (charSkill == null || !charSkill.getCharacterId().equals(characterId)) {
            throw new BusinessException(7004, "角色技能不存在");
        }

        Skill skill = skillMapper.selectById(charSkill.getSkillId());
        if (skill == null) {
            throw new BusinessException(7001, "技能数据异常");
        }

        // 检查是否达到最大等级
        if (charSkill.getSkillLevel() >= 99) {
            throw new BusinessException(7009, "技能已达到最大等级");
        }

        // 检查熟练度是否足够
        int requiredProficiency = charSkill.getSkillLevel() * 100;
        if (charSkill.getProficiency() < requiredProficiency) {
            throw new BusinessException(7008,
                    "熟练度不足，需要: " + requiredProficiency + "，当前: " + charSkill.getProficiency());
        }

        // 升级技能
        charSkill.setSkillLevel(charSkill.getSkillLevel() + 1);
        charSkill.setProficiency(charSkill.getProficiency() - requiredProficiency);
        this.updateById(charSkill);

        logger.info("技能升级: characterId={}, skillId={}, newLevel={}",
                characterId, skill.getSkillId(), charSkill.getSkillLevel());

        return SkillResponse.fromEntity(skill, charSkill);
    }

    @Override
    @Transactional
    public void addProficiency(Long characterId, Long skillId, int proficiency) {
        if (proficiency <= 0) {
            return;
        }

        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId)
                .eq(CharacterSkill::getSkillId, skillId);
        CharacterSkill charSkill = this.getOne(wrapper);

        if (charSkill != null) {
            charSkill.setProficiency(charSkill.getProficiency() + proficiency);
            this.updateById(charSkill);
            logger.info("增加技能熟练度: characterId={}, skillId={}, added={}, total={}",
                    characterId, skillId, proficiency, charSkill.getProficiency());
        }
    }

    private Map<Long, CharacterSkill> getLearnedSkillMap(Long characterId) {
        LambdaQueryWrapper<CharacterSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CharacterSkill::getCharacterId, characterId);
        List<CharacterSkill> charSkills = this.list(wrapper);
        return charSkills.stream()
                .collect(Collectors.toMap(CharacterSkill::getSkillId, cs -> cs));
    }

    @Override
    public List<SkillResponse> getAllSkills() {
        // 获取所有技能（按阶位和类型排序）
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Skill::getTier)
                .orderByAsc(Skill::getFunctionType)
                .orderByAsc(Skill::getSkillId);
        List<Skill> skills = skillMapper.selectList(wrapper);

        // 转换为响应对象（不包含角色技能信息）
        List<SkillResponse> responses = new ArrayList<>();
        for (Skill skill : skills) {
            responses.add(SkillResponse.fromEntity(skill, null));
        }

        logger.info("获取所有技能列表: 总数={}", responses.size());
        return responses;
    }
}
