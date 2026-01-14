package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.entity.ExplorationArea;
import com.xiuxian.entity.ExplorationEvent;
import com.xiuxian.entity.ExplorationRecord;
import com.xiuxian.entity.PlayerCharacter;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExplorationServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private ExplorationAreaMapper areaMapper;
    @Mock
    private ExplorationEventMapper eventMapper;
    @Mock
    private MaterialMapper materialMapper;
    @Mock
    private PillMapper pillMapper;
    @Mock
    private MonsterMapper monsterMapper;
    @Mock
    private ExplorationRecordMapper explorationRecordMapper; // Implicitly used by ServiceImpl

    @InjectMocks
    private ExplorationServiceImpl explorationService;

    private PlayerCharacter character;
    private ExplorationArea area;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = ExplorationServiceImpl.class;
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
            baseMapperField.set(explorationService, explorationRecordMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setRealmLevel(1);
        character.setCurrentSpirit(100);

        area = new ExplorationArea();
        area.setAreaId(1L);
        area.setAreaName("Mystic Forest");
        area.setRequiredRealmLevel(1);
        area.setSpiritCost(10);
        area.setDangerLevel(1);
    }

    @Test
    void startExploration_Success_NoEvent() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);
        when(eventMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(explorationRecordMapper.insert(any(ExplorationRecord.class))).thenReturn(1);

        ExplorationResponse response = explorationService.startExploration(request);

        assertNotNull(response);
        assertEquals("无事", response.getEventType());
    }

    @Test
    void startExploration_InsufficientSpirit() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setCurrentSpirit(5);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9003, e.getCode());
        }
    }

    @Test
    void startExploration_RealmTooLow() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        character.setRealmLevel(1);
        area.setRequiredRealmLevel(5); // 境界要求太高

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(1L)).thenReturn(area);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9002, e.getCode());
            assertTrue(e.getMessage().contains("境界不足"));
        }
    }

    @Test
    void startExploration_AreaNotFound() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectById(999L)).thenReturn(null);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(9001, e.getCode());
        }
    }

    @Test
    void getAllAreas_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectList(any())).thenReturn(Collections.singletonList(area));

        List<ExplorationAreaResponse> areas = explorationService.getAllAreas(1L);

        assertNotNull(areas);
        assertEquals(1, areas.size());
    }

    @Test
    void getAllAreas_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            explorationService.getAllAreas(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void startExploration_CharacterNotFound() {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(999L);
        request.setAreaId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            explorationService.startExploration(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getAllAreas_EmptyList() {
        when(characterService.getById(1L)).thenReturn(character);
        when(areaMapper.selectList(any())).thenReturn(Collections.emptyList());

        List<ExplorationAreaResponse> areas = explorationService.getAllAreas(1L);

        assertNotNull(areas);
        assertEquals(0, areas.size());
    }
}
