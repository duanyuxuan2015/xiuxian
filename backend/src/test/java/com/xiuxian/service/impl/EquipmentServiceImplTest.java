package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AutoEquipRequest;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.AutoEquipResponse;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterEquipmentMapper;
import com.xiuxian.mapper.CharacterInventoryMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipmentServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private CharacterEquipmentMapper characterEquipmentMapper;
    @Mock
    private CharacterInventoryMapper characterInventoryMapper;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private PlayerCharacter character;
    private Equipment equipment;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = EquipmentServiceImpl.class;
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
            baseMapperField.set(equipmentService, equipmentMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);

        equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setEquipmentName("Iron Sword");
        equipment.setEquipmentType("武器");
        equipment.setAttackPower(10);
    }

    @Test
    void getCharacterEquipments_Success() {
        CharacterEquipment ce = new CharacterEquipment();
        ce.setEquipmentId(1L);
        ce.setEquipmentSlot("武器");

        when(characterService.getById(1L)).thenReturn(character);
        // Mocking list() method of ServiceImpl
        // Since we can't easily mock the final 'list' method of ServiceImpl without spy
        // or PowerMock,
        // and standard Mockito cannot mock final methods or class-internal calls easily
        // unless 'equipmentService' is a spy.
        // However, standard patterns often use Repository/Mapper directly.
        // But here 'this.list' calls baseMapper.selectList.
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(ce));
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        List<EquipmentResponse> responses = equipmentService.getCharacterEquipments(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Iron Sword", responses.get(0).getEquipmentName());
    }

    @Test
    void equipItem_Success() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("武器");

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null); // No existing item
        when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

        EquipmentResponse response = equipmentService.equipItem(request);

        assertNotNull(response);
        assertEquals("武器", response.getEquipmentSlot());
    }

    @Test
    void equipItem_InvalidSlot() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("InvalidSlot");

        lenient().when(characterService.getById(1L)).thenReturn(character);
        lenient().when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        try {
            equipmentService.equipItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(4002, e.getCode());
        }
    }

    @Test
    void equipItem_TypeMismatch() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("头盔");

        equipment.setEquipmentType("武器"); // 武器类型不能装备到头盔槽位

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        try {
            equipmentService.equipItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(4003, e.getCode());
            assertTrue(e.getMessage().contains("装备类型不匹配"));
        }
    }

    @Test
    void equipItem_EquipmentNotFound() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(999L);
        request.setEquipmentSlot("武器");

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(999L)).thenReturn(null);

        try {
            equipmentService.equipItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(4001, e.getCode());
        }
    }

    @Test
    void equipItem_CharacterNotFound() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(999L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("武器");

        when(characterService.getById(999L)).thenReturn(null);

        try {
            equipmentService.equipItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void unequipItem_Success() {
        CharacterEquipment ce = new CharacterEquipment();
        ce.setCharacterEquipmentId(1L);
        ce.setCharacterId(1L);
        ce.setEquipmentId(1L);
        ce.setEquipmentSlot("武器");

        when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(ce);
        when(characterEquipmentMapper.deleteById(1L)).thenReturn(1);

        boolean result = equipmentService.unequipItem(1L, "武器");

        assertTrue(result);
    }

    @Test
    void unequipItem_SlotEmpty() {
        when(characterService.getById(1L)).thenReturn(character);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        boolean result = equipmentService.unequipItem(1L, "武器");

        // 槽位为空时应该返回false，不抛出异常
        assertFalse(result);
    }

    @Test
    void unequipItem_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            equipmentService.unequipItem(999L, "武器");
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void calculateEquipmentBonus_Success() {
        CharacterEquipment ce = new CharacterEquipment();
        ce.setEquipmentId(1L);
        ce.setEquipmentSlot("武器");

        equipment.setAttackPower(10);
        equipment.setDefensePower(5);
        equipment.setHealthBonus(20);
        equipment.setCriticalRate(3);
        equipment.setPhysicalResist(10);
        equipment.setSpeedBonus(2);
        equipment.setIceResist(5);
        equipment.setFireResist(8);
        equipment.setLightningResist(7);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(ce));
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(1L);

        assertNotNull(bonus);
        assertEquals(10, bonus.attackBonus);
        assertEquals(5, bonus.defenseBonus);
        assertEquals(20, bonus.healthBonus);
        assertEquals(3, bonus.criticalRateBonus);
        assertEquals(2, bonus.speedBonus);
        assertEquals(10, bonus.physicalResistBonus);
        assertEquals(5, bonus.iceResistBonus);
        assertEquals(8, bonus.fireResistBonus);
        assertEquals(7, bonus.lightningResistBonus);
    }

    @Test
    void calculateEquipmentBonus_NoEquipment() {
        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(1L);

        assertNotNull(bonus);
        assertEquals(0, bonus.attackBonus);
        assertEquals(0, bonus.defenseBonus);
        assertEquals(0, bonus.healthBonus);
        assertEquals(0, bonus.criticalRateBonus);
        assertEquals(0, bonus.speedBonus);
        assertEquals(0, bonus.physicalResistBonus);
        assertEquals(0, bonus.iceResistBonus);
        assertEquals(0, bonus.fireResistBonus);
        assertEquals(0, bonus.lightningResistBonus);
    }

    @Test
    void getCharacterEquipments_NoEquipment() {
        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<EquipmentResponse> responses = equipmentService.getCharacterEquipments(1L);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void getCharacterEquipments_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            equipmentService.getCharacterEquipments(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    // ==================== 9槽位系统测试 ====================

    @Test
    void equipItem_AllNineSlots() {
        // 测试所有9个槽位
        String[] slots = {"武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链"};
        String[] types = {"武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指", "戒指", "项链"};

        for (int i = 0; i < slots.length; i++) {
            EquipRequest request = new EquipRequest();
            request.setCharacterId(1L);
            request.setEquipmentId(1L);
            request.setEquipmentSlot(slots[i]);

            equipment.setEquipmentType(types[i]);

            when(characterService.getById(1L)).thenReturn(character);
            when(equipmentMapper.selectById(1L)).thenReturn(equipment);
            lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

            EquipmentResponse response = equipmentService.equipItem(request);

            assertNotNull(response, "槽位 " + slots[i] + " 应该装备成功");
            assertEquals(slots[i], response.getEquipmentSlot());
        }
    }

    @Test
    void equipItem_RingSlot1_Success() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("戒指1");

        equipment.setEquipmentType("戒指");

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

        EquipmentResponse response = equipmentService.equipItem(request);

        assertNotNull(response);
        assertEquals("戒指1", response.getEquipmentSlot());
    }

    @Test
    void equipItem_RingSlot2_Success() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("戒指2");

        equipment.setEquipmentType("戒指");

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

        EquipmentResponse response = equipmentService.equipItem(request);

        assertNotNull(response);
        assertEquals("戒指2", response.getEquipmentSlot());
    }

    @Test
    void equipItem_RingSlot_TypeMismatch() {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("戒指1");

        equipment.setEquipmentType("武器"); // 武器不能装备到戒指槽位

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        try {
            equipmentService.equipItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(4003, e.getCode());
            assertTrue(e.getMessage().contains("装备类型不匹配"));
        }
    }

    @Test
    void equipItem_InvalidSlot_NewSystem() {
        // 测试旧槽位名称不再有效
        String[] oldSlots = {"护甲", "腰带", "鞋子"};

        for (String oldSlot : oldSlots) {
            EquipRequest request = new EquipRequest();
            request.setCharacterId(1L);
            request.setEquipmentId(1L);
            request.setEquipmentSlot(oldSlot);

            lenient().when(characterService.getById(1L)).thenReturn(character);
            lenient().when(equipmentMapper.selectById(1L)).thenReturn(equipment);

            try {
                equipmentService.equipItem(request);
                fail("旧槽位 " + oldSlot + " 应该抛出异常");
            } catch (BusinessException e) {
                assertEquals(4002, e.getCode());
                assertTrue(e.getMessage().contains("无效的装备槽位"));
            }
        }
    }

    @Test
    void unequipItem_AllNineSlots() {
        // 测试卸下所有9个槽位
        String[] slots = {"武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链"};

        for (String slot : slots) {
            CharacterEquipment ce = new CharacterEquipment();
            ce.setCharacterEquipmentId(1L);
            ce.setCharacterId(1L);
            ce.setEquipmentId(1L);
            ce.setEquipmentSlot(slot);

            when(characterService.getById(1L)).thenReturn(character);
            lenient().when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(ce);
            when(characterEquipmentMapper.deleteById(1L)).thenReturn(1);

            boolean result = equipmentService.unequipItem(1L, slot);

            assertTrue(result, "槽位 " + slot + " 应该卸下成功");
        }
    }

    @Test
    void equipItem_ReplacesExistingEquipment() {
        // 测试装备到戒指1槽位时，如果槽位已有装备，会替换掉
        CharacterEquipment existing = new CharacterEquipment();
        existing.setCharacterEquipmentId(2L);
        existing.setCharacterId(1L);
        existing.setEquipmentId(2L);
        existing.setEquipmentSlot("戒指1");

        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("戒指1");

        equipment.setEquipmentType("戒指");

        when(characterService.getById(1L)).thenReturn(character);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        when(characterEquipmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        when(characterEquipmentMapper.deleteById(2L)).thenReturn(1);
        when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

        EquipmentResponse response = equipmentService.equipItem(request);

        assertNotNull(response);
        assertEquals("戒指1", response.getEquipmentSlot());
        verify(characterEquipmentMapper).deleteById(2L); // 验证旧装备被卸下
    }

    @Test
    void calculateEquipmentBonus_NineSlotsWithEquipment() {
        // 测试9个槽位都有装备时的属性加成
        java.util.List<CharacterEquipment> equipments = new java.util.ArrayList<>();
        String[] slots = {"武器", "头盔", "铠甲", "护手", "护腿", "靴子", "戒指1", "戒指2", "项链"};

        for (String slot : slots) {
            CharacterEquipment ce = new CharacterEquipment();
            ce.setEquipmentId(1L);
            ce.setEquipmentSlot(slot);
            equipments.add(ce);
        }

        equipment.setAttackPower(10);
        equipment.setDefensePower(5);
        equipment.setHealthBonus(20);
        equipment.setCriticalRate(3);
        equipment.setSpeedBonus(2);
        equipment.setPhysicalResist(5);
        equipment.setIceResist(3);
        equipment.setFireResist(4);
        equipment.setLightningResist(6);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(equipments);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(1L);

        assertNotNull(bonus);
        // 9件装备，每件+10攻击，总共90
        assertEquals(90, bonus.attackBonus);
        // 9件装备，每件+5防御，总共45
        assertEquals(45, bonus.defenseBonus);
        // 9件装备，每件+20气血，总共180
        assertEquals(180, bonus.healthBonus);
        // 9件装备，每件+3暴击，总共27
        assertEquals(27, bonus.criticalRateBonus);
        // 9件装备，每件+2速度，总共18
        assertEquals(18, bonus.speedBonus);
        // 9件装备，每件+5物理抗性，总共45
        assertEquals(45, bonus.physicalResistBonus);
        // 9件装备，每件+3冰系抗性，总共27
        assertEquals(27, bonus.iceResistBonus);
        // 9件装备，每件+4火系抗性，总共36
        assertEquals(36, bonus.fireResistBonus);
        // 9件装备，每件+6电系抗性，总共54
        assertEquals(54, bonus.lightningResistBonus);
    }

    @Test
    void testAutoEquip_ByBaseScore_Success() {
        // 测试按baseScore一键装备
        AutoEquipRequest request = new AutoEquipRequest();
        request.setCharacterId(1L);
        request.setPriorityAttribute(null);

        // Mock角色存在
        when(characterService.getById(1L)).thenReturn(character);

        // Mock当前装备为空
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        // Mock背包中有装备
        CharacterInventory inventory = new CharacterInventory();
        inventory.setItemId(1L);
        inventory.setItemType("equipment");

        when(characterInventoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(inventory));

        // Mock装备信息
        equipment.setBaseScore(100);
        equipment.setEquipmentType("武器");
        when(equipmentMapper.selectBatchIds(any())).thenReturn(Collections.singletonList(equipment));
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        // Mock插入装备操作
        when(characterEquipmentMapper.insert(any(CharacterEquipment.class))).thenReturn(1);

        AutoEquipResponse response = equipmentService.autoEquip(request);

        assertNotNull(response);
        assertEquals(1, response.getTotalChanges()); // 装备了一件装备
    }

    @Test
    void testAutoEquip_EmptyInventory() {
        // 测试背包为空时的情况
        AutoEquipRequest request = new AutoEquipRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(new ArrayList<>());
        when(characterInventoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        assertThrows(BusinessException.class, () -> {
            equipmentService.autoEquip(request);
        });
    }

    @Test
    void testPreviewAutoEquip_NoChanges() {
        // 测试预览功能（当前已是最优配置）
        AutoEquipRequest request = new AutoEquipRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(new ArrayList<>());
        when(characterInventoryMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(new ArrayList<>());

        AutoEquipResponse preview = equipmentService.previewAutoEquip(request);

        assertNotNull(preview);
        assertEquals(0, preview.getTotalChanges());
    }
}
