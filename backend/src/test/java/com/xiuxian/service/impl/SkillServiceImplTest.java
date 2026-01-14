package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.EquipSkillRequest;
import com.xiuxian.dto.request.LearnSkillRequest;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.entity.CharacterSkill;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Skill;
import com.xiuxian.mapper.CharacterSkillMapper;
import com.xiuxian.mapper.SkillMapper;
import com.xiuxian.service.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private CharacterSkillMapper characterSkillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private PlayerCharacter character;
    private Skill skill;
    private CharacterSkill charSkill;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = SkillServiceImpl.class;
        Field baseMapperField = null;
        while (clazz != null && baseMapperField == null) {
            try {
                baseMapperField = clazz.getSuperclass().getDeclaredField("baseMapper");
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (baseMapperField != null) {
            baseMapperField.setAccessible(true);
            baseMapperField.set(skillService, characterSkillMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setRealmLevel(1);

        skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("Fireball");
        skill.setTier(1);
        skill.setBaseDamage(10);  // 添加基础伤害
        skill.setDamageGrowthRate(new java.math.BigDecimal("0.1"));  // 添加伤害成长率

        charSkill = new CharacterSkill();
        charSkill.setCharacterSkillId(1L);
        charSkill.setCharacterId(1L);
        charSkill.setSkillId(1L);
        charSkill.setSkillLevel(1);
    }

    @Test
    void getAvailableSkills_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(skillMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(skill));
        when(characterSkillMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<SkillResponse> responses = skillService.getAvailableSkills(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Fireball", responses.get(0).getSkillName());
    }

    @Test
    void learnSkill_Success() {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(1L);
        request.setSkillId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(skillMapper.selectById(1L)).thenReturn(skill);
        when(characterSkillMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null); // Not learned yet
        when(characterSkillMapper.insert(any(CharacterSkill.class))).thenReturn(1);

        SkillResponse response = skillService.learnSkill(request);

        assertNotNull(response);
    }

    @Test
    void equipSkill_Success() {
        EquipSkillRequest request = new EquipSkillRequest();
        request.setCharacterId(1L);
        request.setCharacterSkillId(1L);
        request.setSlotIndex(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);
        when(characterSkillMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null); // Slot empty
        when(characterSkillMapper.updateById(any(CharacterSkill.class))).thenReturn(1);
        when(skillMapper.selectById(1L)).thenReturn(skill);

        SkillResponse response = skillService.equipSkill(request);

        assertNotNull(response);
        assertTrue(response.getIsEquipped());
    }

    @Test
    void learnSkill_AlreadyLearned() {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(1L);
        request.setSkillId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(skillMapper.selectById(1L)).thenReturn(skill);
        when(characterSkillMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(charSkill); // Already learned

        try {
            skillService.learnSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7003, e.getCode());  // 应该是7003
        }
    }

    @Test
    void learnSkill_RealmTooLow() {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(1L);
        request.setSkillId(1L);

        character.setRealmLevel(1);
        skill.setTier(5); // 技能品阶要求太高

        when(characterService.getById(1L)).thenReturn(character);
        when(skillMapper.selectById(1L)).thenReturn(skill);
        // 移除不必要的stub: when(characterSkillMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        try {
            skillService.learnSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7002, e.getCode());  // 应该是7002
        }
    }

    @Test
    void learnSkill_SkillNotFound() {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(1L);
        request.setSkillId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(skillMapper.selectById(999L)).thenReturn(null);

        try {
            skillService.learnSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7001, e.getCode());  // 应该是7001
        }
    }

    @Test
    void equipSkill_SkillNotBelongToCharacter() {
        EquipSkillRequest request = new EquipSkillRequest();
        request.setCharacterId(1L);
        request.setCharacterSkillId(999L);  // 使用不存在的技能ID
        request.setSlotIndex(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(999L)).thenReturn(null);  // 返回null表示技能不存在

        try {
            skillService.equipSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7004, e.getCode());  // 应该是7004
        }
    }

    @Test
    void upgradeSkill_Success() {
        charSkill.setProficiency(100);  // 设置足够的熟练度(技能等级1需要100熟练度升级)

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);
        when(skillMapper.selectById(1L)).thenReturn(skill);
        when(characterSkillMapper.updateById(any(CharacterSkill.class))).thenReturn(1);

        SkillResponse response = skillService.upgradeSkill(1L, 1L);

        assertNotNull(response);
    }

    @Test
    void upgradeSkill_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.upgradeSkill(999L, 1L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void upgradeSkill_SkillNotFound() {
        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(999L)).thenReturn(null);

        try {
            skillService.upgradeSkill(1L, 999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7004, e.getCode());
        }
    }

    @Test
    void unequipSkill_Success() {
        charSkill.setSlotIndex(1);
        charSkill.setIsEquipped(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);
        when(characterSkillMapper.updateById(any(CharacterSkill.class))).thenReturn(1);

        boolean result = skillService.unequipSkill(1L, 1L);

        assertTrue(result);
    }

    @Test
    void unequipSkill_SkillNotEquipped() {
        charSkill.setIsEquipped(false);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);

        try {
            skillService.unequipSkill(1L, 1L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(7005, e.getCode());
        }
    }

    @Test
    void getLearnedSkills_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(charSkill));
        when(skillMapper.selectById(1L)).thenReturn(skill);

        List<SkillResponse> responses = skillService.getLearnedSkills(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getEquippedSkills_Success() {
        charSkill.setIsEquipped(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterSkillMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(charSkill));
        when(skillMapper.selectById(1L)).thenReturn(skill);

        List<SkillResponse> responses = skillService.getEquippedSkills(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getAvailableSkills_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.getAvailableSkills(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getLearnedSkills_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.getLearnedSkills(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getEquippedSkills_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.getEquippedSkills(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void equipSkill_CharacterNotFound() {
        EquipSkillRequest request = new EquipSkillRequest();
        request.setCharacterId(999L);
        request.setCharacterSkillId(1L);
        request.setSlotIndex(1);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.equipSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void learnSkill_CharacterNotFound() {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(999L);
        request.setSkillId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            skillService.learnSkill(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }
}
