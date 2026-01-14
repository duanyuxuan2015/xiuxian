package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectMember;
import com.xiuxian.entity.SectShopItem;
import com.xiuxian.mapper.SectMemberMapper;
import com.xiuxian.mapper.SectShopItemMapper;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.InventoryService;
import com.xiuxian.service.SectService;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectMemberServiceImplTest {

    @Mock
    private CharacterService characterService;
    @Mock
    private SectService sectService;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private SectShopItemMapper shopItemMapper;
    @Mock
    private SectMemberMapper sectMemberMapper;

    @InjectMocks
    private SectMemberServiceImpl sectMemberService;

    private PlayerCharacter character;
    private Sect sect;
    private SectMember sectMember;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射设置 baseMapper - 需要在父类中查找
        Class<?> clazz = SectMemberServiceImpl.class;
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
            baseMapperField.set(sectMemberService, sectMemberMapper);
        }

        character = new PlayerCharacter();
        character.setCharacterId(1L);
        character.setPlayerName("TestPlayer");
        character.setCharacterName("TestPlayer");
        character.setRealmLevel(1);

        sect = new Sect();
        sect.setSectId(1L);
        sect.setSectName("Cloud Sect");
        sect.setRequiredRealmLevel(1);

        sectMember = new SectMember();
        sectMember.setMemberId(1L);
        sectMember.setCharacterId(1L);
        sectMember.setSectId(1L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(100);
    }

    @Test
    void getAllSects_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectService.list()).thenReturn(Collections.singletonList(sect));
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null); // Not a member yet
        when(sectMemberMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);

        List<SectResponse> responses = sectMemberService.getAllSects(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Cloud Sect", responses.get(0).getSectName());
        assertEquals(10, responses.get(0).getMemberCount());
    }

    @Test
    void joinSect_Success() {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(1L);
        request.setSectId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectService.getById(1L)).thenReturn(sect);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null); // Not in sect
        when(sectMemberMapper.insert(any(SectMember.class))).thenReturn(1);

        SectMemberResponse response = sectMemberService.joinSect(request);

        assertNotNull(response);
        assertEquals("Cloud Sect", response.getSectName());
    }

    @Test
    void buyShopItem_Success() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(1);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(10);
        item.setItemType("material");
        item.setRefItemId(1L);
        item.setItemName("Herb");

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectById(1L)).thenReturn(item);
        when(sectMemberMapper.updateById(any(SectMember.class))).thenReturn(1);
        when(shopItemMapper.updateById(any(SectShopItem.class))).thenReturn(1);

        String result = sectMemberService.buyShopItem(request);

        assertNotNull(result);
        assertTrue(result.contains("成功购买"));
    }

    @Test
    void joinSect_AlreadyInSect() {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(1L);
        request.setSectId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectService.getById(1L)).thenReturn(sect);  // 添加这个mock
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember); // Already in sect

        try {
            sectMemberService.joinSect(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8003, e.getCode());
        }
    }

    @Test
    void joinSect_RealmTooLow() {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(1L);
        request.setSectId(1L);

        character.setRealmLevel(1);
        sect.setRequiredRealmLevel(5); // 境界要求太高

        when(characterService.getById(1L)).thenReturn(character);
        when(sectService.getById(1L)).thenReturn(sect);

        try {
            sectMemberService.joinSect(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8002, e.getCode());
        }
    }

    @Test
    void leaveSect_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(sectMemberMapper.deleteById(1L)).thenReturn(1);

        boolean result = sectMemberService.leaveSect(1L);

        assertTrue(result);
    }

    @Test
    void leaveSect_NotInSect() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null); // Not in sect

        try {
            sectMemberService.leaveSect(1L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8004, e.getCode());
        }
    }

    @Test
    void buyShopItem_InsufficientContribution() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(1);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(1000); // 价格太高
        item.setRequiredPosition(1);
        item.setCurrentStock(10);

        sectMember.setContribution(10); // 贡献不足

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectById(1L)).thenReturn(item);

        try {
            sectMemberService.buyShopItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8008, e.getCode());
        }
    }

    @Test
    void buyShopItem_InsufficientStock() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(10);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(5); // 库存不足

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectById(1L)).thenReturn(item);

        try {
            sectMemberService.buyShopItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8007, e.getCode());
        }
    }

    @Test
    void getCharacterSect_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(sectService.getById(1L)).thenReturn(sect);

        SectMemberResponse response = sectMemberService.getCharacterSect(1L);

        assertNotNull(response);
        assertEquals("Cloud Sect", response.getSectName());
    }

    @Test
    void getShopItems_Success() {
        SectShopItem item = new SectShopItem();
        item.setItemId(1L);
        item.setItemName("Herb");
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(100);  // 添加库存字段

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getSectMembers_Success() {
        when(sectService.getById(1L)).thenReturn(sect);
        when(sectMemberMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(sectMember));
        when(characterService.getById(1L)).thenReturn(character);

        List<SectMemberResponse> responses = sectMemberService.getSectMembers(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void joinSect_SectNotFound() {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(1L);
        request.setSectId(999L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.joinSect(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8001, e.getCode());
        }
    }

    @Test
    void joinSect_CharacterNotFound() {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(999L);
        request.setSectId(1L);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.joinSect(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void buyShopItem_CharacterNotFound() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(999L);
        request.setItemId(1L);
        request.setQuantity(1);

        when(characterService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.buyShopItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void buyShopItem_NotInSect() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        try {
            sectMemberService.buyShopItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8004, e.getCode());
        }
    }

    @Test
    void buyShopItem_ItemNotFound() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(999L);
        request.setQuantity(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectById(999L)).thenReturn(null);

        try {
            sectMemberService.buyShopItem(request);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8005, e.getCode());
        }
    }

    @Test
    void getCharacterSect_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.getCharacterSect(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getShopItems_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.getShopItems(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }

    @Test
    void getSectMembers_SectNotFound() {
        when(sectService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.getSectMembers(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(8001, e.getCode());
        }
    }

    @Test
    void getAllSects_CharacterNotFound() {
        when(characterService.getById(999L)).thenReturn(null);

        try {
            sectMemberService.getAllSects(999L);
            fail("Should throw BusinessException");
        } catch (BusinessException e) {
            assertEquals(1003, e.getCode());
        }
    }
}
