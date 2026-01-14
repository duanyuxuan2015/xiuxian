package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CombatRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CombatServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private MonsterService monsterService;
    @Mock
    private CombatRecordMapper combatRecordMapper;

    @InjectMocks
    private CombatServiceImpl combatService;

    private PlayerCharacter character;
    private Monster monster;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = CombatServiceImpl.class;
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
            baseMapperField.set(combatService, combatRecordMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("Hero");
        character.setCurrentState("闲置");
        character.setStamina(100);
        character.setHealth(100);
        character.setConstitution(10);
        character.setSpirit(10);
        character.setRealmId(1);
        character.setExperience(0L);  // 添加经验值
        character.setSpiritStones(100L);  // 添加灵石

        monster = new Monster();
        monster.setMonsterId(1L);
        monster.setMonsterName("Slime");
        monster.setStaminaCost(10);
        monster.setHp(50);
        monster.setAttackPower(10);
        monster.setDefensePower(5);
        monster.setExpReward(100);  // 修复：从100L改为100
        monster.setSpiritStonesReward(10);  // 添加灵石奖励
        monster.setRealmId(1);
    }

    @Test
    void startCombat_Success() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getCombatLog().size() > 0);
        verify(characterService, times(2)).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void startCombat_NotIdle() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        character.setCurrentState("修炼中");
        when(characterService.getById(1L)).thenReturn(character);

        try {
            combatService.startCombat(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2002, e.getCode());
        }
    }

    @Test
    void getAvailableMonsters_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        // monsterService.list() usually returns list of monsters
        when(monsterService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(monster));

        List<Monster> monsters = combatService.getAvailableMonsters(1L);

        assertNotNull(monsters);
        assertEquals(1, monsters.size());
        assertEquals("Slime", monsters.get(0).getMonsterName());
    }

    @Test
    void startCombat_InsufficientStamina() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        character.setStamina(5); // 体力不足，需要10
        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        try {
            combatService.startCombat(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(2003, e.getCode());  // 实际是2003
            assertTrue(e.getMessage().contains("体力不足"));
        }
    }

    @Test
    void startCombat_MonsterNotFound() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(999L)).thenReturn(null);

        try {
            combatService.startCombat(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(3001, e.getCode());
        }
    }

    @Test
    void getAvailableMonsters_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            combatService.getAvailableMonsters(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startCombat_CharacterNotFound() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(999L);
        request.setMonsterId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            combatService.startCombat(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getAvailableMonsters_EmptyList() {
        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<Monster> monsters = combatService.getAvailableMonsters(1L);

        assertNotNull(monsters);
        assertEquals(0, monsters.size());
    }

    @Test
    void startCombat_DefeatVerification() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 设置怪物非常强大,确保角色会失败
        monster.setHp(10000);
        monster.setAttackPower(1000);

        // 设置角色很弱
        character.setConstitution(1);
        character.setSpirit(1);
        character.setHealth(10);
        character.setExperience(50L);
        character.setSpiritStones(50L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertFalse(response.getVictory());
        assertEquals(0, response.getExpGained()); // 失败不获得经验
        assertEquals(0, response.getSpiritStonesGained()); // 失败不获得灵石
        assertTrue(response.getMessage().contains("战斗失败"));
        verify(characterService, times(2)).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void startCombat_VictoryRewards() {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 设置角色很强大,确保会胜利
        character.setConstitution(100);
        character.setSpirit(100);
        character.setHealth(1000);
        character.setExperience(0L);
        character.setSpiritStones(0L);

        // 设置怪物很弱
        monster.setHp(1);
        monster.setAttackPower(1);
        monster.setDefensePower(0);
        monster.setExpReward(100);
        monster.setSpiritStonesReward(50);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertEquals(100, response.getExpGained());
        assertEquals(50, response.getSpiritStonesGained());
        assertTrue(response.getMessage().contains("战斗胜利"));
        verify(characterService, times(2)).updateCharacter(any(PlayerCharacter.class));
    }
}
