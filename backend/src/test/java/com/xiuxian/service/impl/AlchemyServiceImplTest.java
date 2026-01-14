package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.AlchemyRequest;
import com.xiuxian.dto.response.AlchemyResponse;
import com.xiuxian.dto.response.PillRecipeResponse;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlchemyServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private PillMapper pillMapper;
    @Mock
    private PillRecipeMapper recipeMapper;
    @Mock
    private PillRecipeMaterialMapper recipeMaterialMapper;
    @Mock
    private MaterialMapper materialMapper;
    @Mock
    private AlchemyRecordMapper alchemyRecordMapper;

    @InjectMocks
    private AlchemyServiceImpl alchemyService;

    private PlayerCharacter character;
    private PillRecipe recipe;
    private Pill pill;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = AlchemyServiceImpl.class;
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
            baseMapperField.set(alchemyService, alchemyRecordMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setRealmLevel(1);
        character.setAlchemyLevel(1);

        recipe = new PillRecipe();
        recipe.setRecipeId(1L);
        recipe.setRecipeTier(1);
        recipe.setAlchemyLevelRequired(1);
        recipe.setPillId(1L);
        recipe.setBaseSuccessRate(50);

        pill = new Pill();
        pill.setPillId(1L);
        pill.setPillName("Energy Pill");
    }

    @Test
    void getAvailableRecipes_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectList(any())).thenReturn(Collections.singletonList(recipe));
        when(pillMapper.selectById(1L)).thenReturn(pill);

        List<PillRecipeResponse> responses = alchemyService.getAvailableRecipes(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getRecipeId());
    }

    @Test
    void startAlchemy_Success() {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);
        when(pillMapper.selectById(1L)).thenReturn(pill);
        when(recipeMaterialMapper.selectList(any())).thenReturn(Collections.emptyList());

        // Mocking save to avoid actual DB call (ServiceImpl usually proxies Mapper)
        // Since AlchemyServiceImpl extends ServiceImpl, we might need to mock
        // baseMapper or method.
        // However, standard Mockito mocks on Mapper usually handle ServiceImpl calls if
        // correct.
        // Here we just ensure no exception is thrown.

        // Note: ServiceImpl.save calls baseMapper.insert.
        // We'll trust Mockito to mock the mapper which is autowired into ServiceImpl.
        // But ServiceImpl uses generics, so alchemyRecordMapper is the baseMapper.
        when(alchemyRecordMapper.insert(any(AlchemyRecord.class))).thenReturn(1);

        AlchemyResponse response = alchemyService.startAlchemy(request);

        assertNotNull(response);
        verify(inventoryService, times(0)).removeItem(anyLong(), anyString(), anyLong(), anyInt());
    }

    @Test
    void startAlchemy_Failure_LevelTooLow() {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        recipe.setAlchemyLevelRequired(10);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);

        try {
            alchemyService.startAlchemy(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(5004, e.getCode());
        }
    }

    @Test
    void startAlchemy_RecipeNotFound() {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(1L);
        request.setRecipeId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(999L)).thenReturn(null);

        try {
            alchemyService.startAlchemy(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(5001, e.getCode());
        }
    }

    @Test
    void getAvailableRecipes_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            alchemyService.getAvailableRecipes(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startAlchemy_CharacterNotFound() {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(999L);
        request.setRecipeId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            alchemyService.startAlchemy(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startAlchemy_PillNotFound() {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectById(1L)).thenReturn(recipe);
        when(pillMapper.selectById(1L)).thenReturn(null); // 丹药不存在

        try {
            alchemyService.startAlchemy(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(5002, e.getCode());
        }
    }

    @Test
    void getAvailableRecipes_EmptyList() {
        when(characterService.getById(1L)).thenReturn(character);
        when(recipeMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<PillRecipeResponse> responses = alchemyService.getAvailableRecipes(1L);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }
}
