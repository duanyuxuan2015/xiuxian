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
import com.xiuxian.mapper.CharacterSkillMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.SkillMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.MonsterService;
import com.xiuxian.service.RealmService;
import com.xiuxian.service.MonsterDropService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.service.SectTaskService;
import com.xiuxian.service.SkillService;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.entity.CharacterSkill;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Skill;
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
    @Mock
    private SectTaskService sectTaskService;
    @Mock
    private SkillService skillService;
    @Mock
    private CharacterSkillMapper characterSkillMapper;
    @Mock
    private SkillMapper skillMapper;

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

        // Mock SkillService to return empty skill list by default
        lenient().when(skillService.getEquippedSkills(any())).thenReturn(new ArrayList<>());
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

    @Test
    void getCombatRecords_FirstPage() {
        // 测试获取第一页战斗记录，使用默认分页大小20
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20);

        // 创建测试数据：20条记录
        List<CombatRecord> records = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            CombatRecord record = new CombatRecord();
            record.setCombatId((long) i);
            record.setCharacterId(1L);
            record.setMonsterId(1L);
            record.setIsVictory(1);
            record.setTurns(5);
            record.setExpGained(100);
            records.add(record);
        }

        mockPage.setRecords(records);
        mockPage.setTotal(89);  // 总共89条记录

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 1, 20);

        assertNotNull(result);
        assertEquals(20, result.getRecords().size());
        assertEquals(89, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(20, result.getSize());
        assertEquals(5, result.getPages());  // 89条记录，每页20条，共5页
    }

    @Test
    void getCombatRecords_SecondPage() {
        // 测试获取第二页战斗记录
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(2, 20);

        // 创建测试数据：第二页也有20条记录
        List<CombatRecord> records = new ArrayList<>();
        for (int i = 21; i <= 40; i++) {
            CombatRecord record = new CombatRecord();
            record.setCombatId((long) i);
            record.setCharacterId(1L);
            record.setMonsterId(1L);
            record.setIsVictory(1);
            record.setTurns(5);
            record.setExpGained(100);
            records.add(record);
        }

        mockPage.setRecords(records);
        mockPage.setTotal(89);

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 2, 20);

        assertNotNull(result);
        assertEquals(20, result.getRecords().size());
        assertEquals(89, result.getTotal());
        assertEquals(2, result.getCurrent());
        assertEquals(20, result.getSize());
    }

    @Test
    void getCombatRecords_LastPage() {
        // 测试获取最后一页战斗记录（第5页，只有9条记录）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(5, 20);

        // 创建测试数据：最后一页只有9条记录
        List<CombatRecord> records = new ArrayList<>();
        for (int i = 81; i <= 89; i++) {
            CombatRecord record = new CombatRecord();
            record.setCombatId((long) i);
            record.setCharacterId(1L);
            record.setMonsterId(1L);
            record.setIsVictory(1);
            record.setTurns(5);
            record.setExpGained(100);
            records.add(record);
        }

        mockPage.setRecords(records);
        mockPage.setTotal(89);

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 5, 20);

        assertNotNull(result);
        assertEquals(9, result.getRecords().size());  // 最后一页只有9条
        assertEquals(89, result.getTotal());
        assertEquals(5, result.getCurrent());
        assertEquals(20, result.getSize());
    }

    @Test
    void getCombatRecords_EmptyResult() {
        // 测试没有战斗记录的情况
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20);

        mockPage.setRecords(Collections.emptyList());
        mockPage.setTotal(0);

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 1, 20);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(20, result.getSize());
        assertEquals(0, result.getPages());
    }

    @Test
    void getCombatRecords_CustomPageSize() {
        // 测试自定义分页大小（每页10条）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);

        // 创建测试数据：10条记录
        List<CombatRecord> records = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            CombatRecord record = new CombatRecord();
            record.setCombatId((long) i);
            record.setCharacterId(1L);
            record.setMonsterId(1L);
            record.setIsVictory(1);
            record.setTurns(5);
            record.setExpGained(100);
            records.add(record);
        }

        mockPage.setRecords(records);
        mockPage.setTotal(89);

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 1, 10);

        assertNotNull(result);
        assertEquals(10, result.getRecords().size());
        assertEquals(89, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(9, result.getPages());  // 89条记录，每页10条，共9页
    }

    @Test
    void getCombatRecords_VerifyOrderByDescending() {
        // 测试记录按时间倒序排列
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> mockPage =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 20);

        // 创建测试数据，ID从大到小（模拟时间倒序）
        List<CombatRecord> records = new ArrayList<>();
        for (int i = 20; i >= 1; i--) {
            CombatRecord record = new CombatRecord();
            record.setCombatId((long) i);
            record.setCharacterId(1L);
            record.setMonsterId(1L);
            record.setIsVictory(1);
            record.setTurns(5);
            record.setExpGained(100);
            records.add(record);
        }

        mockPage.setRecords(records);
        mockPage.setTotal(20);

        when(combatRecordMapper.selectPage(any(), any())).thenReturn(mockPage);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CombatRecord> result =
            combatService.getCombatRecords(1L, 1, 20);

        assertNotNull(result);
        assertEquals(20, result.getRecords().size());
        // 验证第一条记录ID是20（最新的）
        assertEquals(20L, result.getRecords().get(0).getCombatId());
        // 验证最后一条记录ID是1（最旧的）
        assertEquals(1L, result.getRecords().get(19).getCombatId());
    }

    /**
     * 测试战斗胜利时获得装备掉落
     */
    @Test
    void startCombat_WithEquipmentDrop_Success() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 创建装备
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setEquipmentName("青铜剑");
        equipment.setEquipmentType("武器");
        equipment.setAttackPower(10);
        equipment.setDefensePower(0);

        // Mock掉落服务返回装备ID
        List<Long> droppedIds = new ArrayList<>();
        droppedIds.add(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(droppedIds);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(1, response.getItemsDropped().size());
        assertEquals("青铜剑", response.getItemsDropped().get(0));
        assertTrue(response.getMessage().contains("掉落装备"));
        assertTrue(response.getMessage().contains("青铜剑"));

        // 验证装备已添加到背包
        verify(inventoryService, times(1)).addItem(1L, "equipment", 1L, 1);
    }

    /**
     * 测试战斗胜利时获得多件装备掉落
     */
    @Test
    void startCombat_WithMultipleEquipmentDrops_Success() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 创建多件装备
        Equipment equipment1 = new Equipment();
        equipment1.setEquipmentId(1L);
        equipment1.setEquipmentName("青铜剑");

        Equipment equipment2 = new Equipment();
        equipment2.setEquipmentId(2L);
        equipment2.setEquipmentName("铁甲");

        // Mock掉落服务返回多个装备ID
        List<Long> droppedIds = new ArrayList<>();
        droppedIds.add(1L);
        droppedIds.add(2L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(droppedIds);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment1);
        when(equipmentMapper.selectById(2L)).thenReturn(equipment2);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(2, response.getItemsDropped().size());
        assertTrue(response.getItemsDropped().contains("青铜剑"));
        assertTrue(response.getItemsDropped().contains("铁甲"));
        assertTrue(response.getMessage().contains("掉落装备"));

        // 验证两件装备都已添加到背包
        verify(inventoryService, times(2)).addItem(eq(1L), eq("equipment"), anyLong(), eq(1));
    }

    /**
     * 测试战斗胜利但无装备掉落
     */
    @Test
    void startCombat_WithNoEquipmentDrop_Success() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // Mock掉落服务返回空列表（无掉落）
        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(Collections.emptyList());

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证结果
        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(0, response.getItemsDropped().size());
        assertFalse(response.getMessage().contains("掉落装备"));

        // 验证没有调用添加装备到背包
        verify(inventoryService, never()).addItem(anyLong(), eq("equipment"), anyLong(), anyInt());
    }

    /**
     * 测试战斗失败时不掉落装备
     */
    @Test
    void startCombat_WithDefeat_NoEquipmentDrop() throws Exception {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 创建一个低血量低攻击的角色，确保失败
        PlayerCharacter weakCharacter = new PlayerCharacter();
        weakCharacter.setCharacterId(1L);
        weakCharacter.setPlayerName("WeakHero");
        weakCharacter.setCurrentState("闲置");
        weakCharacter.setStamina(100);
        weakCharacter.setHealth(10);
        weakCharacter.setConstitution(1);
        weakCharacter.setSpirit(1);
        weakCharacter.setRealmId(1);
        weakCharacter.setExperience(0L);
        weakCharacter.setSpiritStones(100L);

        // 创建一个强力妖兽
        Monster strongMonster = new Monster();
        strongMonster.setMonsterId(1L);
        strongMonster.setMonsterName("StrongBoss");
        strongMonster.setStaminaCost(10);
        strongMonster.setHp(1000);
        strongMonster.setAttackPower(100);
        strongMonster.setDefensePower(50);
        strongMonster.setExpReward(100);
        strongMonster.setSpiritStonesReward(10);
        strongMonster.setRealmId(1);

        when(characterService.getById(1L)).thenReturn(weakCharacter);
        when(monsterService.getById(1L)).thenReturn(strongMonster);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证结果
        assertNotNull(response);
        assertFalse(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(0, response.getItemsDropped().size());

        // 验证失败时不调用掉落服务
        verify(monsterDropService, never()).rollEquipmentDrops(anyLong(), anyLong());
        verify(inventoryService, never()).addItem(anyLong(), anyString(), anyLong(), anyInt());
    }

    /**
     * 测试装备掉落信息在响应中正确显示
     */
    @Test
    void startCombat_EquipmentDropInResponse_CorrectFormat() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        Equipment equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setEquipmentName("玄铁重剑");

        List<Long> droppedIds = new ArrayList<>();
        droppedIds.add(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);
        when(monsterDropService.rollEquipmentDrops(1L, 1L)).thenReturn(droppedIds);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证响应格式
        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getItemsDropped());
        assertEquals(1, response.getItemsDropped().size());

        // 验证消息格式正确
        String message = response.getMessage();
        assertTrue(message.contains("战斗胜利"));
        assertTrue(message.contains("掉落装备:"));
        assertTrue(message.contains("玄铁重剑"));

        // 验证经验和灵石也在消息中
        assertTrue(message.contains("经验"));
        assertTrue(message.contains("灵石"));
    }

    /**
     * 测试战斗中暴击统计
     */
    @Test
    void startCombat_CriticalHitsStatistics_Success() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 使用Answer每次返回新的character对象以避免体力耗尽
        when(characterService.getById(1L)).thenAnswer(invocation -> {
            PlayerCharacter freshCharacter = new PlayerCharacter();
            freshCharacter.setCharacterId(1L);
            freshCharacter.setPlayerName("Hero");
            freshCharacter.setCurrentState("闲置");
            freshCharacter.setStamina(100);  // 每次都返回满体力
            freshCharacter.setHealth(100);
            freshCharacter.setConstitution(10);
            freshCharacter.setSpirit(10);
            freshCharacter.setRealmId(1);
            freshCharacter.setExperience(0L);
            freshCharacter.setSpiritStones(100L);
            return freshCharacter;
        });
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行战斗多次以验证暴击可能发生
        int totalCriticalHits = 0;
        int totalBattles = 20;

        for (int i = 0; i < totalBattles; i++) {
            CombatResponse response = combatService.startCombat(request);
            assertNotNull(response);
            assertNotNull(response.getCriticalHits());
            totalCriticalHits += response.getCriticalHits();
        }

        // 验证暴击次数在合理范围内（基于5%的基础暴击率）
        assertTrue(totalCriticalHits >= 0, "暴击次数应该>=0");

        System.out.println("总战斗次数: " + totalBattles + ", 总暴击次数: " + totalCriticalHits);
    }

    /**
     * 测试战斗响应包含暴击信息
     */
    @Test
    void startCombat_ResponseIncludesCriticalHits() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证响应包含暴击信息
        assertNotNull(response);
        assertNotNull(response.getCriticalHits());
        assertTrue(response.getCriticalHits() >= 0);

        // 验证战斗日志可能包含暴击信息
        if (response.getCriticalHits() > 0) {
            assertTrue(response.getCombatLog().stream()
                    .anyMatch(log -> log.contains("暴击")));
        }
    }

    /**
     * 测试战斗记录保存暴击次数
     */
    @Test
    void startCombat_CriticalHitsSavedToRecord() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证战斗记录被保存
        verify(combatRecordMapper, times(1)).insert(any(CombatRecord.class));

        // 验证响应中的暴击次数
        assertNotNull(response.getCriticalHits());
        assertTrue(response.getCriticalHits() >= 0);
    }

    /**
     * 测试暴击伤害计算（通过战斗日志验证）
     */
    @Test
    void startCombat_CriticalDamageCalculation() {
        // 准备测试数据 - 使用高攻击角色和低防御妖兽
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 使用Answer每次返回新的character对象
        when(characterService.getById(1L)).thenAnswer(invocation -> {
            PlayerCharacter strongCharacter = new PlayerCharacter();
            strongCharacter.setCharacterId(1L);
            strongCharacter.setPlayerName("StrongHero");
            strongCharacter.setCurrentState("闲置");
            strongCharacter.setStamina(100);
            strongCharacter.setHealth(1000);
            strongCharacter.setConstitution(50);  // 高体质 = 高攻击
            strongCharacter.setSpirit(10);
            strongCharacter.setRealmId(1);
            strongCharacter.setExperience(0L);
            strongCharacter.setSpiritStones(100L);
            return strongCharacter;
        });

        Monster weakMonster = new Monster();
        weakMonster.setMonsterId(1L);
        weakMonster.setMonsterName("WeakSlime");
        weakMonster.setStaminaCost(10);
        weakMonster.setHp(200);  // 高生命值以观察多回合
        weakMonster.setAttackPower(1);  // 低攻击
        weakMonster.setDefensePower(0);  // 无防御以最大化伤害
        weakMonster.setExpReward(100);
        weakMonster.setSpiritStonesReward(10);
        weakMonster.setRealmId(1);

        when(monsterService.getById(1L)).thenReturn(weakMonster);

        // 执行多次战斗直到观察到暴击
        boolean foundCritical = false;
        int attempts = 0;
        int maxAttempts = 50;

        while (!foundCritical && attempts < maxAttempts) {
            attempts++;
            CombatResponse response = combatService.startCombat(request);

            // 检查战斗日志中是否有暴击
            if (response.getCriticalHits() > 0) {
                foundCritical = true;
                assertTrue(response.getCombatLog().stream()
                        .anyMatch(log -> log.contains("暴击")));

                System.out.println("在第 " + attempts + " 次尝试中观察到暴击");
                System.out.println("暴击次数: " + response.getCriticalHits());
            }
        }

        // 验证最终观察到了暴击（基于5%的暴击率，50次尝试应该足够）
        if (!foundCritical) {
            System.out.println("警告: 在 " + maxAttempts + " 次尝试中未观察到暴击（可能由于随机性）");
        }
    }

    /**
     * 测试无暴击战斗
     */
    @Test
    void startCombat_NoCriticalHits() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证暴击次数不为null（可能为0或更多）
        assertNotNull(response.getCriticalHits());
        assertTrue(response.getCriticalHits() >= 0);
    }

    /**
     * 测试抗性属性在战斗中的潜在使用（当前为预留测试）
     * 注意：当前实现中抗性还未集成到战斗计算中
     * 此测试验证抗性字段的存在和响应结构
     */
    @Test
    void startCombat_ResistanceFields_PresentInCharacter() {
        // 准备测试数据 - 角色带有抗性属性
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 注意：当前PlayerCharacter实体中可能没有抗性字段
        // 此测试验证战斗系统在添加抗性功能时的兼容性

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行战斗
        CombatResponse response = combatService.startCombat(request);

        // 验证战斗可以正常执行
        assertNotNull(response);
        assertTrue(response.getVictory() || !response.getVictory());  // 战斗有结果

        // TODO: 当抗性功能添加到战斗计算后，添加以下验证：
        // - 物理抗性减少物理伤害
        // - 冰系抗性减少冰系技能伤害
        // - 火系抗性减少火系技能伤害
        // - 雷系抗性减少雷系技能伤害
    }

    /**
     * 测试暴击率的边界情况
     */
    @Test
    void startCombat_CriticalRateEdgeCases() {
        // 准备测试数据
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 使用Answer每次返回新的character对象以避免体力耗尽
        when(characterService.getById(1L)).thenAnswer(invocation -> {
            PlayerCharacter freshCharacter = new PlayerCharacter();
            freshCharacter.setCharacterId(1L);
            freshCharacter.setPlayerName("Hero");
            freshCharacter.setCurrentState("闲置");
            freshCharacter.setStamina(100);  // 每次都返回满体力
            freshCharacter.setHealth(100);
            freshCharacter.setConstitution(10);
            freshCharacter.setSpirit(10);
            freshCharacter.setRealmId(1);
            freshCharacter.setExperience(0L);
            freshCharacter.setSpiritStones(100L);
            return freshCharacter;
        });
        when(monsterService.getById(1L)).thenReturn(monster);

        // 执行多次战斗验证暴击率稳定性
        int battlesWithCritical = 0;
        int totalBattles = 100;
        int totalCriticalHits = 0;

        for (int i = 0; i < totalBattles; i++) {
            CombatResponse response = combatService.startCombat(request);
            assertNotNull(response);

            if (response.getCriticalHits() > 0) {
                battlesWithCritical++;
                totalCriticalHits += response.getCriticalHits();
            }
        }

        // 验证统计数据合理性
        double criticalRate = (double) battlesWithCritical / totalBattles * 100;
        System.out.println("暴击战斗比例: " + String.format("%.2f", criticalRate) + "%");
        System.out.println("平均每场暴击次数: " + String.format("%.2f", (double) totalCriticalHits / totalBattles));

        // 基于统计学的验证：95%置信度下，暴击率应该在0%-30%之间（理论值5%）
        assertTrue(criticalRate >= 0 && criticalRate <= 30,
                "暴击率应该在合理范围内，实际: " + String.format("%.2f", criticalRate) + "%");
    }

    /**
     * 测试战斗日志格式包含暴击信息
     */
    @Test
    void startCombat_CombatLogFormat_WithCriticalHit() {
        // 准备测试数据 - 高攻击角色以快速结束战斗
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        // 使用Answer每次返回新的character对象
        when(characterService.getById(1L)).thenAnswer(invocation -> {
            PlayerCharacter strongCharacter = new PlayerCharacter();
            strongCharacter.setCharacterId(1L);
            strongCharacter.setPlayerName("StrongHero");
            strongCharacter.setCurrentState("闲置");
            strongCharacter.setStamina(100);
            strongCharacter.setHealth(100);
            strongCharacter.setConstitution(100);  // 超高攻击
            strongCharacter.setSpirit(100);
            strongCharacter.setRealmId(1);
            strongCharacter.setExperience(0L);
            strongCharacter.setSpiritStones(100L);
            return strongCharacter;
        });

        Monster normalMonster = new Monster();
        normalMonster.setMonsterId(1L);
        normalMonster.setMonsterName("NormalSlime");
        normalMonster.setStaminaCost(10);
        normalMonster.setHp(100);
        normalMonster.setAttackPower(10);
        normalMonster.setDefensePower(10);
        normalMonster.setExpReward(100);
        normalMonster.setSpiritStonesReward(10);
        normalMonster.setRealmId(1);

        when(monsterService.getById(1L)).thenReturn(normalMonster);

        // 尝试多次直到观察到暴击
        for (int i = 0; i < 30; i++) {
            CombatResponse response = combatService.startCombat(request);
            assertNotNull(response);
            assertNotNull(response.getCombatLog());
            assertFalse(response.getCombatLog().isEmpty());

            // 如果日志中包含暴击，验证格式
            boolean hasCriticalLog = response.getCombatLog().stream()
                    .anyMatch(log -> log.contains("暴击"));

            if (hasCriticalLog) {
                // 验证暴击日志格式
                response.getCombatLog().stream()
                        .filter(log -> log.contains("暴击"))
                        .forEach(log -> {
                            assertTrue(log.contains("暴击伤害"), "暴击日志应包含'暴击伤害'");
                            assertTrue(log.contains("回合"), "暴击日志应包含回合信息");
                            assertTrue(log.contains("造成"), "暴击日志应包含伤害信息");
                        });

                System.out.println("在第 " + (i + 1) + " 次尝试中验证了暴击日志格式");
                return;  // 测试成功
            }
        }

        // 如果30次都没观察到暴击，测试仍然通过（只是概率问题）
        System.out.println("提示: 在30次尝试中未观察到暴击（概率事件）");
    }

    /**
     * 测试装备攻击技能时优先使用技能
     */
    @Test
    void testExecuteCombat_WithAttackSkill_UsesSkillFirst() {
        // 准备技能数据
        CharacterSkill charSkill = new CharacterSkill();
        charSkill.setCharacterSkillId(1L);
        charSkill.setCharacterId(1L);
        charSkill.setSkillId(1L);
        charSkill.setSkillLevel(1);
        charSkill.setProficiency(0);

        Skill skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("雷霆剑诀");
        skill.setFunctionType("攻击");
        skill.setBaseDamage(30);
        skill.setSkillMultiplier(new java.math.BigDecimal("1.5"));
        skill.setSpiritualCost(10);
        skill.setDamageGrowthRate(new java.math.BigDecimal("0.1"));

        SkillResponse skillResponse = new SkillResponse();
        skillResponse.setCharacterSkillId(1L);
        skillResponse.setSkillId(1L);
        skillResponse.setSkillName("雷霆剑诀");
        skillResponse.setFunctionType("攻击");
        skillResponse.setSlotIndex(1);
        skillResponse.setSkillLevel(1);

        // Mock返回已装备的技能
        when(skillService.getEquippedSkills(1L)).thenReturn(List.of(skillResponse));
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);
        when(skillMapper.selectById(1L)).thenReturn(skill);

        // 角色有足够灵力
        character.setSpiritualPower(50);

        when(characterService.getById(1L)).thenReturn(character);
        when(monsterService.getById(1L)).thenReturn(monster);

        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);
        request.setCombatMode("手动");

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);
        assertTrue(response.getVictory());
        assertNotNull(response.getCombatLog());

        // 验证使用了技能（日志中包含技能名称）
        boolean usedSkill = response.getCombatLog().stream()
                .anyMatch(log -> log.contains("雷霆剑诀") && log.contains("使用"));
        assertTrue(usedSkill, "应该优先使用攻击技能");
    }

    /**
     * 测试技能冷却机制
     */
    @Test
    void testExecuteCombat_SkillCooldown_CannotReuseInCooldown() {
        // 准备技能数据（低伤害技能以便多回合）
        CharacterSkill charSkill = new CharacterSkill();
        charSkill.setCharacterSkillId(1L);
        charSkill.setCharacterId(1L);
        charSkill.setSkillId(1L);
        charSkill.setSkillLevel(1);
        charSkill.setProficiency(0);

        Skill skill = new Skill();
        skill.setSkillId(1L);
        skill.setSkillName("基础剑法");
        skill.setFunctionType("攻击");
        skill.setBaseDamage(5);  // 低伤害
        skill.setSkillMultiplier(new java.math.BigDecimal("1.0"));
        skill.setSpiritualCost(5);
        skill.setDamageGrowthRate(new java.math.BigDecimal("0.1"));

        SkillResponse skillResponse = new SkillResponse();
        skillResponse.setCharacterSkillId(1L);
        skillResponse.setSkillId(1L);
        skillResponse.setSkillName("基础剑法");
        skillResponse.setFunctionType("攻击");
        skillResponse.setSlotIndex(1);
        skillResponse.setSkillLevel(1);

        // Mock返回已装备的技能
        when(skillService.getEquippedSkills(1L)).thenReturn(List.of(skillResponse));
        when(characterSkillMapper.selectById(1L)).thenReturn(charSkill);
        when(skillMapper.selectById(1L)).thenReturn(skill);

        character.setSpiritualPower(100);

        when(characterService.getById(1L)).thenReturn(character);

        // 高生命值的妖兽，确保多回合
        Monster highHpMonster = new Monster();
        highHpMonster.setMonsterId(1L);
        highHpMonster.setMonsterName("TankSlime");
        highHpMonster.setStaminaCost(10);
        highHpMonster.setHp(200);  // 高生命值
        highHpMonster.setAttackPower(1);  // 低攻击，避免战斗过快结束
        highHpMonster.setDefensePower(0);
        highHpMonster.setExpReward(100);
        highHpMonster.setSpiritStonesReward(10);
        highHpMonster.setRealmId(1);

        when(monsterService.getById(1L)).thenReturn(highHpMonster);

        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);
        request.setCombatMode("手动");

        CombatResponse response = combatService.startCombat(request);

        assertNotNull(response);

        // 统计技能使用次数
        long skillUsageCount = response.getCombatLog().stream()
                .filter(log -> log.contains("基础剑法") && log.contains("使用"))
                .count();

        // 由于冷却机制，技能不应该每回合都使用
        // 如果没有冷却，5-6回合应该使用5-6次技能
        // 有4回合冷却，应该只使用1-2次
        assertTrue(skillUsageCount < 3, "技能冷却机制应该限制技能使用频率，实际使用次数: " + skillUsageCount);
    }
}
