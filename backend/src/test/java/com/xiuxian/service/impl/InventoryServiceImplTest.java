package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.entity.CharacterInventory;
import com.xiuxian.mapper.CharacterInventoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private CharacterInventoryMapper characterInventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = InventoryServiceImpl.class;
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
            baseMapperField.set(inventoryService, characterInventoryMapper);
        }
    }

    @Test
    void addItem_NewItem() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        when(characterInventoryMapper.insert(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        verify(characterInventoryMapper).insert(any(CharacterInventory.class));
    }

    @Test
    void addItem_ExistingItem() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(5);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        assertEquals(10, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void addItem_ZeroQuantity() {
        // 添加0数量不应该执行任何操作
        inventoryService.addItem(1L, "material", 1L, 0);

        verify(characterInventoryMapper, never()).insert(any(CharacterInventory.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void addItem_NegativeQuantity() {
        // 添加负数数量不应该执行任何操作
        inventoryService.addItem(1L, "material", 1L, -5);

        verify(characterInventoryMapper, never()).insert(any(CharacterInventory.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void removeItem_Success() {
        CharacterInventory existing = new CharacterInventory();
        existing.setInventoryId(1L);
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertTrue(result);
        assertEquals(5, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void removeItem_AllItems() {
        CharacterInventory existing = new CharacterInventory();
        existing.setInventoryId(1L);
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.deleteById(1L)).thenReturn(1);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 10);

        assertTrue(result);
        verify(characterInventoryMapper).deleteById(1L);
    }

    @Test
    void removeItem_NotEnough() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(3);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertFalse(result);
        assertEquals(3, existing.getQuantity());
    }

    @Test
    void removeItem_ItemNotExists() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertFalse(result);
    }

    @Test
    void removeItem_ZeroQuantity() {
        // 移除0数量应该返回true但不执行任何操作
        boolean result = inventoryService.removeItem(1L, "material", 1L, 0);

        assertTrue(result);
        verify(characterInventoryMapper, never()).deleteById(any(Long.class));
        verify(characterInventoryMapper, never()).updateById(any(CharacterInventory.class));
    }

    @Test
    void getItemQuantity_Exists() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(15);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        int quantity = inventoryService.getItemQuantity(1L, "material", 1L);

        assertEquals(15, quantity);
    }

    @Test
    void getItemQuantity_NotExists() {
        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        int quantity = inventoryService.getItemQuantity(1L, "material", 1L);

        assertEquals(0, quantity);
    }

    @Test
    void hasEnoughItem_True() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(10);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertTrue(result);
    }

    @Test
    void hasEnoughItem_False() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(3);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertFalse(result);
    }

    @Test
    void hasEnoughItem_ExactlyEquals() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(5);

        lenient().when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.hasEnoughItem(1L, "material", 1L, 5);

        assertTrue(result);
    }
}
