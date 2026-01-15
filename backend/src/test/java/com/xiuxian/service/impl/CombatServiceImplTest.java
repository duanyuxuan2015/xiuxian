package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.CombatRecordMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.MonsterService;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.MonsterDropService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.entity.Equipment;
import com.xiuxian.mapper.EquipmentMapper;
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
    private RealmService realmService;
    @Mock
    private CombatRecordMapper combatRecordMapper;
    @Mock
    private MonsterDropService monsterDropService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private EquipmentMapper equipmentMapper;

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

        // Mock Realm with no bonuses by default (使用 lenient 避免在某些测试中未使用时报错)
        Realm realm = new Realm();
        realm.setId(1);
        realm.setRealmName("Mortal");
        realm.setAttackBonus(0);
        realm.setDefenseBonus(0);
        lenient().when(realmService.getById(1)).thenReturn(realm);
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

    @Test
    void startCombat_WithRealmBonus() {
        // 测试境界加成正确应用到攻击力和防御力
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 设置角色属性
        character.setConstitution(10);  // 基础攻击：10*2 + 10 = 30，基础防御：10*1.5 = 15
        character.setSpirit(10);
        character.setHealth(1000);
        character.setRealmId(2);  // 更高境界

        // Mock 高级境界，有攻击和防御加成
        Realm highRealm = new Realm();
        highRealm.setId(2);
        highRealm.setRealmName("Qi Condensation");
        highRealm.setAttackBonus(20);  // 攻击加成20
        highRealm.setDefenseBonus(10);  // 防御加成10
        when(realmService.getById(2)).thenReturn(highRealm);

        // 设置怪物
        monster.setHp(100);
        monster.setAttackPower(10);
        monster.setDefensePower(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        // 有了境界加成，角色更强大，应该更容易胜利
        // 角色攻击力 = 10*2 + 10 + 20 = 50
        // 角色防御力 = 10*1.5 + 10 = 25
        // 战斗日志应该显示正确的攻击和防御值
        assertTrue(response.getCombatLog().size() > 0);
        // 检查战斗日志中是否包含正确的攻击力和防御力
        String initialLog = response.getCombatLog().get(1);
        assertTrue(initialLog.contains("攻击50"), "角色攻击力应该是50（30基础 + 20境界加成）");
        assertTrue(initialLog.contains("防御25"), "角色防御力应该是25（15基础 + 10境界加成）");

        verify(characterService, times(2)).updateCharacter(any(PlayerCharacter.class));
    }

    @Test
    void startCombat_VictoryWithEquipmentDrop() {
        // 测试战斗胜利时装备掉落
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

        // Mock 掉落的装备
        Equipment droppedEquipment = new Equipment();
        droppedEquipment.setEquipmentId(1L);
        droppedEquipment.setEquipmentName("铁剑");

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(Collections.singletonList(1L));
        when(equipmentMapper.selectById(1L)).thenReturn(droppedEquipment);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(1, response.getItemsDropped().size());
        assertEquals("铁剑", response.getItemsDropped().get(0));
        assertTrue(response.getMessage().contains("掉落装备"));
        assertTrue(response.getMessage().contains("铁剑"));

        verify(monsterDropService, times(1)).rollEquipmentDrops(1L, 1L);
        verify(equipmentMapper, times(1)).selectById(1L);
        // 验证装备已添加到背包
        verify(inventoryService, times(1)).addItem(1L, "equipment", 1L, 1);
    }

    @Test
    void startCombat_VictoryWithMultipleEquipmentDrops() {
        // 测试战斗胜利时多个装备掉落
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

        // Mock 掉落的多个装备
        Equipment equipment1 = new Equipment();
        equipment1.setEquipmentId(1L);
        equipment1.setEquipmentName("铁剑");

        Equipment equipment2 = new Equipment();
        equipment2.setEquipmentId(2L);
        equipment2.setEquipmentName("钢刀");

        List<Long> droppedIds = new ArrayList<>();
        droppedIds.add(1L);
        droppedIds.add(2L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(droppedIds);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment1);
        when(equipmentMapper.selectById(2L)).thenReturn(equipment2);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(2, response.getItemsDropped().size());
        assertTrue(response.getItemsDropped().contains("铁剑"));
        assertTrue(response.getItemsDropped().contains("钢刀"));
        assertTrue(response.getMessage().contains("掉落装备"));

        verify(equipmentMapper, times(2)).selectById(any(Long.class));
        // 验证两件装备都已添加到背包
        verify(inventoryService, times(1)).addItem(1L, "equipment", 1L, 1);
        verify(inventoryService, times(1)).addItem(1L, "equipment", 2L, 1);
    }

    @Test
    void startCombat_VictoryWithNoEquipmentDrop() {
        // 测试战斗胜利但没有装备掉落
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

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);
        // 返回空列表，表示没有装备掉落
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(Collections.emptyList());

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(0, response.getItemsDropped().size());
        // 应该不包含"掉落装备"字样
        assertFalse(response.getMessage().contains("掉落装备"));

        verify(monsterDropService, times(1)).rollEquipmentDrops(1L, 1L);
        // 没有掉落就不应该调用 equipmentMapper
        verify(equipmentMapper, never()).selectById(any(Long.class));
        // 没有掉落就不应该添加到背包
        verify(inventoryService, never()).addItem(any(Long.class), any(String.class), any(Long.class), any(Integer.class));
    }

    @Test
    void startCombat_DefeatNoEquipmentDrop() {
        // 测试战斗失败时不掉落装备
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
        assertNotNull(response.getItemsDropped());
        assertEquals(0, response.getItemsDropped().size());
        // 失败时不应该调用掉落服务
        verify(monsterDropService, never()).rollEquipmentDrops(any(Long.class), any(Long.class));
        // 失败时不应该添加到背包
        verify(inventoryService, never()).addItem(any(Long.class), any(String.class), any(Long.class), any(Integer.class));
    }

    @Test
    void startCombat_EquipmentDropWithNullEquipment() {
        // 测试装备掉落但装备不存在的情况
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

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(combatRecordMapper.insert(any(CombatRecord.class))).thenReturn(1);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(Collections.singletonList(999L));
        // 装备不存在，返回null
        when(equipmentMapper.selectById(999L)).thenReturn(null);

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        // 装备不存在时，不应该添加到掉落列表
        assertEquals(0, response.getItemsDropped().size());
        // 装备不存在时，不应该添加到背包
        verify(inventoryService, never()).addItem(any(Long.class), any(String.class), any(Long.class), any(Integer.class));
    }
}
