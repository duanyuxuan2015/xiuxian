package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ForgeRequest;
import com.xiuxian.dto.response.EquipmentRecipeResponse;
import com.xiuxian.dto.response.ForgeResponse;
import com.xiuxian.entity.*;
import com.xiuxian.mapper.*;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgeServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private EquipmentRecipeMapper recipeMapper;
    @Mock
    private EquipmentRecipeMaterialMapper recipeMaterialMapper;
    @Mock
    private MaterialMapper materialMapper;
    @Mock
    private ForgeRecordMapper forgeRecordMapper; // Implicitly used by ServiceImpl

    @InjectMocks
    private ForgeServiceImpl forgeService;

    private PlayerCharacter character;
    private EquipmentRecipe recipe;
    private Equipment equipment;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = ForgeServiceImpl.class;
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
            baseMapperField.set(forgeService, forgeRecordMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setRealmLevel(1);
        character.setForgeLevel(1);

        recipe = new EquipmentRecipe();
        recipe.setRecipeId(1L);
        recipe.setRecipeTier(1);
        recipe.setForgingLevelRequired(1);
        recipe.setEquipmentId(1L);
        recipe.setBaseSuccessRate(50);

        equipment = new Equipment();
        equipment.setEquipmentId(1L);
        equipment.setEquipmentName("Iron Sword");
    }

    @Test
    void getAvailableRecipes_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectList(any())).thenReturn(Collections.singletonList(recipe));
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);

        List<EquipmentRecipeResponse> responses = forgeService.getAvailableRecipes(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getRecipeId());
    }

    @Test
    void startForge_Success() {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);
        when(equipmentMapper.selectById(1L)).thenReturn(equipment);
        when(recipeMaterialMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(forgeRecordMapper.insert(any(ForgeRecord.class))).thenReturn(1);

        ForgeResponse response = forgeService.startForge(request);

        assertNotNull(response);
    }

    @Test
    void startForge_LevelTooLow() {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        character.setForgeLevel(1);
        recipe.setForgingLevelRequired(10);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);

        try {
            forgeService.startForge(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(6004, e.getCode());
            assertTrue(e.getMessage().contains("锻造等级不足"));
        }
    }

    @Test
    void startForge_RecipeNotFound() {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(1L);
        request.setRecipeId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(999L)).thenReturn(null);

        try {
            forgeService.startForge(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(6001, e.getCode());
        }
    }

    @Test
    void getAvailableRecipes_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            forgeService.getAvailableRecipes(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startForge_CharacterNotFound() {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(999L);
        request.setRecipeId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            forgeService.startForge(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startForge_EquipmentNotFound() {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);
        when(equipmentMapper.selectById(1L)).thenReturn(null); // 装备不存在

        try {
            forgeService.startForge(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(6002, e.getCode());
        }
    }

    @Test
    void getAvailableRecipes_EmptyList() {
        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<EquipmentRecipeResponse> responses = forgeService.getAvailableRecipes(1L);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }
}
