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
        when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);
        when(characterInventoryMapper.insert(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        verify(characterInventoryMapper).insert(any(CharacterInventory.class));
    }

    @Test
    void addItem_ExistingItem() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(5);

        when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        inventoryService.addItem(1L, "material", 1L, 5);

        assertEquals(10, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void removeItem_Success() {
        CharacterInventory existing = new CharacterInventory();
        existing.setInventoryId(1L);
        existing.setQuantity(10);

        when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);
        when(characterInventoryMapper.updateById(any(CharacterInventory.class))).thenReturn(1);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertTrue(result);
        assertEquals(5, existing.getQuantity());
        verify(characterInventoryMapper).updateById(existing);
    }

    @Test
    void removeItem_NotEnough() {
        CharacterInventory existing = new CharacterInventory();
        existing.setQuantity(3);

        when(characterInventoryMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(existing);

        boolean result = inventoryService.removeItem(1L, "material", 1L, 5);

        assertFalse(result);
        assertEquals(3, existing.getQuantity());
    }
}
