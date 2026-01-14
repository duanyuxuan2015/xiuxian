package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.entity.Sect;
import com.xiuxian.mapper.SectMapper;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectServiceImplTest {

    @Mock
    private SectMapper sectMapper;

    @InjectMocks
    private SectServiceImpl sectService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = SectServiceImpl.class;
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
            baseMapperField.set(sectService, sectMapper);
        }
    }

    @Test
    void getAllSects() {
        Sect sect = new Sect();
        sect.setSectName("Cloud Sect");

        when(sectMapper.selectList(any())).thenReturn(Collections.singletonList(sect));

        List<Sect> result = sectService.getAllSects();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBySectName() {
        Sect sect = new Sect();
        sect.setSectName("Cloud Sect");

        when(sectMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sect);

        Sect result = sectService.getBySectName("Cloud Sect");

        assertNotNull(result);
        assertEquals("Cloud Sect", result.getSectName());
    }
}
