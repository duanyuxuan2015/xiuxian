package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.entity.Realm;
import com.xiuxian.mapper.RealmMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RealmServiceImplTest {

    @Mock
    private RealmMapper realmMapper;

    @InjectMocks
    private RealmServiceImpl realmService;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = RealmServiceImpl.class;
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
            baseMapperField.set(realmService, realmMapper);
        }
    }

    @Test
    void getByRealmLevel() {
        Realm realm = new Realm();
        realm.setRealmLevel(1);

        when(realmMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(realm);

        Realm result = realmService.getByRealmLevel(1);

        assertNotNull(result);
        assertEquals(1, result.getRealmLevel());
    }

    @Test
    void getNextRealm() {
        Realm nextRealm = new Realm();
        nextRealm.setRealmLevel(2);

        when(realmMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(nextRealm);

        Realm result = realmService.getNextRealm(1);

        assertNotNull(result);
        assertEquals(2, result.getRealmLevel());
    }
}
