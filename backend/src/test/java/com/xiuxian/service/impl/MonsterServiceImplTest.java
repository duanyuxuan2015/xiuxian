package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.entity.Monster;
import com.xiuxian.mapper.MonsterMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MonsterServiceImplTest {

    @Mock
    private MonsterMapper monsterMapper;

    @InjectMocks
    private MonsterServiceImpl monsterService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = MonsterServiceImpl.class;
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
            baseMapperField.set(monsterService, monsterMapper);
        }
    }

    @Test
    void getMonstersByRealmId() {
        Monster monster = new Monster();
        monster.setMonsterId(1L);

        when(monsterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(monster));

        List<Monster> result = monsterService.getMonstersByRealmId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllMonsters() {
        Monster monster = new Monster();
        monster.setMonsterId(1L);

        when(monsterMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(monster));

        List<Monster> result = monsterService.getAllMonsters();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
