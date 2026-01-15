package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.entity.CharacterEquipment;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.mapper.CharacterEquipmentMapper;
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

        when(characterService.getById(1L)).thenReturn(character);
        when(characterEquipmentMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(ce));
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        EquipmentService.EquipmentBonus bonus = equipmentService.calculateEquipmentBonus(1L);

        assertNotNull(bonus);
        assertEquals(10, bonus.attackBonus);
        assertEquals(5, bonus.defenseBonus);
        assertEquals(20, bonus.healthBonus);
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
}
