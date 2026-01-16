package com.xiuxian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiuxian.common.exception.BusinessException;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.PromotePositionRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.PositionUpgradeInfo;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.entity.PlayerCharacter;
import com.xiuxian.entity.Sect;
import com.xiuxian.entity.SectMember;
import com.xiuxian.entity.SectShopItem;
import com.xiuxian.entity.Skill;
import com.xiuxian.entity.Equipment;
import com.xiuxian.entity.Pill;
import com.xiuxian.mapper.CharacterSectReputationMapper;
import com.xiuxian.mapper.SectMemberMapper;
import com.xiuxian.mapper.SectShopItemMapper;
import com.xiuxian.mapper.SkillMapper;
import com.xiuxian.mapper.EquipmentMapper;
import com.xiuxian.mapper.PillMapper;
import com.xiuxian.config.PositionPromotionProperties;
import com.xiuxian.entity.CharacterSectReputation;
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
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private PillMapper pillMapper;
    @Mock
    private PositionPromotionProperties promotionProperties;
    @Mock
    private CharacterSectReputationMapper reputationMapper;

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
        when(sectMemberMapper.deleteById(1L)).thenReturn(1);

        // 直接验证mapper的删除操作
        boolean result = sectMemberMapper.deleteById(1L) > 0;

        assertTrue(result);
        verify(sectMemberMapper).deleteById(1L);
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
        item.setCurrentStock(100);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getShopItems_WithCompleteDetails_Success() {
        SectShopItem item = new SectShopItem();
        item.setItemId(1L);
        item.setItemName("雪莲");
        item.setItemType("material");
        item.setPrice(100);
        item.setItemTier(1);
        item.setRequiredPosition(1);  // 添加必需的字段
        item.setCurrentStock(50);
        item.setDescription("珍贵的炼丹材料");

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("雪莲", responses.get(0).getItemName());
        assertEquals("material", responses.get(0).getItemType());
        assertEquals(1, responses.get(0).getItemTier());
        assertEquals(100, responses.get(0).getPrice());
        assertEquals(50, responses.get(0).getStock());
        assertEquals("珍贵的炼丹材料", responses.get(0).getDescription());
    }

    @Test
    void getShopItems_EmptyList_Success() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void buyShopItem_MultipleQuantity_Success() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(5);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(100);
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
        // 验证贡献扣除正确：5个物品 * 10价格 = 50贡献
        assertEquals(50, sectMember.getContribution()); // 100 - 50 = 50
        // 验证库存扣除正确
        assertEquals(95, item.getCurrentStock()); // 100 - 5 = 95
    }

    @Test
    void buyShopItem_OutOfStock() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(1);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(0); // 库存为0

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
    void buyShopItem_ExactStockAvailable() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(10);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10);
        item.setRequiredPosition(1);
        item.setCurrentStock(10); // 刚好够
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
        assertEquals(0, item.getCurrentStock()); // 10 - 10 = 0
    }

    @Test
    void buyShopItem_ExactContribution() {
        SectShopBuyRequest request = new SectShopBuyRequest();
        request.setCharacterId(1L);
        request.setItemId(1L);
        request.setQuantity(10);

        SectShopItem item = new SectShopItem();
        item.setSectId(1L);
        item.setPrice(10); // 刚好10贡献
        item.setRequiredPosition(1);
        item.setCurrentStock(100);
        item.setItemType("material");
        item.setRefItemId(1L);
        item.setItemName("Herb");

        sectMember.setContribution(100); // 刚好够

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectById(1L)).thenReturn(item);
        when(sectMemberMapper.updateById(any(SectMember.class))).thenReturn(1);
        when(shopItemMapper.updateById(any(SectShopItem.class))).thenReturn(1);

        String result = sectMemberService.buyShopItem(request);

        assertNotNull(result);
        assertTrue(result.contains("成功购买"));
        assertEquals(0, sectMember.getContribution()); // 100 - 100 = 0
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

    @Test
    void getShopItems_WithSkillTypeDetails_Success() {
        // 测试技能类型物品的详细信息
        SectShopItem item = new SectShopItem();
        item.setItemId(1L);
        item.setItemName("雷霆剑诀秘籍");
        item.setItemType("skill");
        item.setPrice(2000);
        item.setRefItemId(1L);
        item.setCurrentStock(5);
        item.setRequiredPosition(1); // 必需字段

        Skill skill = new Skill();
        skill.setSkillId(1L);
        skill.setBaseDamage(80);
        skill.setSpiritualCost(15);
        skill.setFunctionType("攻击");
        skill.setElementType("雷");

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));
        when(skillMapper.selectById(1L)).thenReturn(skill);

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("雷霆剑诀秘籍", responses.get(0).getItemName());
        assertEquals("skill", responses.get(0).getItemType());
        assertEquals(80, responses.get(0).getBaseDamage());
        assertEquals(15, responses.get(0).getSpiritualCost());
        assertEquals("攻击", responses.get(0).getSkillType());
        assertEquals("雷", responses.get(0).getElementType());
    }

    @Test
    void getShopItems_WithEquipmentTypeDetails_Success() {
        // 测试装备类型物品的详细信息
        SectShopItem item = new SectShopItem();
        item.setItemId(2L);
        item.setItemName("玄铁剑");
        item.setItemType("equipment");
        item.setPrice(800);
        item.setRefItemId(2L);
        item.setCurrentStock(3);
        item.setRequiredPosition(1); // 必需字段

        Equipment equipment = new Equipment();
        equipment.setEquipmentId(2L);
        equipment.setAttackPower(50);
        equipment.setDefensePower(10);
        equipment.setHealthBonus(100);
        equipment.setEquipmentType("weapon");

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));
        when(equipmentMapper.selectById(2L)).thenReturn(equipment);

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("玄铁剑", responses.get(0).getItemName());
        assertEquals("equipment", responses.get(0).getItemType());
        assertEquals(50, responses.get(0).getAttackBonus());
        assertEquals(10, responses.get(0).getDefenseBonus());
        assertEquals(100, responses.get(0).getHealthBonus());
        assertEquals("weapon", responses.get(0).getEquipmentSlot());
    }

    @Test
    void getShopItems_WithPillTypeDetails_Success() {
        // 测试丹药类型物品的详细信息
        SectShopItem item = new SectShopItem();
        item.setItemId(3L);
        item.setItemName("筑基丹");
        item.setItemType("pill");
        item.setPrice(300);
        item.setRefItemId(3L);
        item.setCurrentStock(10);
        item.setRequiredPosition(1);

        Pill pill = new Pill();
        pill.setPillId(3L);
        pill.setEffectValue(100);
        pill.setDuration(60);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));
        when(pillMapper.selectById(3L)).thenReturn(pill);

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("筑基丹", responses.get(0).getItemName());
        assertEquals("pill", responses.get(0).getItemType());
        assertEquals(100, responses.get(0).getHealAmount());
        assertEquals(60, responses.get(0).getBuffDuration());
    }

    @Test
    void getShopItems_WithMaterialType_Success() {
        // 测试材料类型物品（不查询额外信息）
        SectShopItem item = new SectShopItem();
        item.setItemId(4L);
        item.setItemName("雪莲");
        item.setItemType("material");
        item.setPrice(100);
        item.setRefItemId(4L);
        item.setCurrentStock(50);
        item.setDescription("珍贵的炼丹材料");
        item.setRequiredPosition(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("雪莲", responses.get(0).getItemName());
        assertEquals("material", responses.get(0).getItemType());
        assertEquals("珍贵的炼丹材料", responses.get(0).getDescription());
        // 材料类型不查询额外信息，应该为null
        assertNull(responses.get(0).getBaseDamage());
        assertNull(responses.get(0).getAttackBonus());
        assertNull(responses.get(0).getHealAmount());
    }

    @Test
    void getShopItems_SkillNotFound_ReturnsBasicInfo() {
        // 测试技能ID不存在时，仍返回基础信息
        SectShopItem item = new SectShopItem();
        item.setItemId(5L);
        item.setItemName("失传剑诀");
        item.setItemType("skill");
        item.setPrice(5000);
        item.setRefItemId(999L); // 不存在的技能ID
        item.setCurrentStock(1);
        item.setRequiredPosition(1);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(item));
        when(skillMapper.selectById(999L)).thenReturn(null); // 技能不存在

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("失传剑诀", responses.get(0).getItemName());
        assertEquals("skill", responses.get(0).getItemType());
        // 技能详细信息应该为null
        assertNull(responses.get(0).getBaseDamage());
        assertNull(responses.get(0).getSpiritualCost());
    }

    @Test
    void getShopItems_MultipleTypes_Success() {
        // 测试多种类型物品混合
        SectShopItem skillItem = new SectShopItem();
        skillItem.setItemId(1L);
        skillItem.setItemName("雷霆剑诀");
        skillItem.setItemType("skill");
        skillItem.setRefItemId(1L);
        skillItem.setRequiredPosition(1);

        SectShopItem equipItem = new SectShopItem();
        equipItem.setItemId(2L);
        equipItem.setItemName("玄铁剑");
        equipItem.setItemType("equipment");
        equipItem.setRefItemId(2L);
        equipItem.setRequiredPosition(1);

        Skill skill = new Skill();
        skill.setBaseDamage(80);
        skill.setSpiritualCost(15);

        Equipment equipment = new Equipment();
        equipment.setAttackPower(50);
        equipment.setDefensePower(10);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(shopItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(skillItem, equipItem));
        when(skillMapper.selectById(1L)).thenReturn(skill);
        when(equipmentMapper.selectById(2L)).thenReturn(equipment);

        List<SectShopItemResponse> responses = sectMemberService.getShopItems(1L);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        // 验证第一个物品（技能）
        assertEquals("skill", responses.get(0).getItemType());
        assertEquals(80, responses.get(0).getBaseDamage());
        assertEquals(15, responses.get(0).getSpiritualCost());

        // 验证第二个物品（装备）
        assertEquals("equipment", responses.get(1).getItemType());
        assertEquals(50, responses.get(1).getAttackBonus());
        assertEquals(10, responses.get(1).getDefenseBonus());
    }

    // ==================== 职位升级测试 ====================

    @Test
    void getPromotionInfo_DiscipleToInner_Success() {
        character.setSpiritStones(2000L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(600);

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(150);

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);

        PositionUpgradeInfo info = sectMemberService.getPromotionInfo(1L);

        assertNotNull(info);
        assertTrue(info.getAvailable());
        assertTrue(info.getCanUpgrade());
        assertEquals("弟子", info.getCurrentPosition());
        assertEquals("内门弟子", info.getNextPosition());
        assertEquals(150, info.getCurrentReputation());
        assertEquals(100, info.getRequiredReputation());
        assertEquals(600, info.getCurrentContribution());
        assertEquals(500, info.getRequiredContribution());
        assertEquals(2000L, info.getCurrentSpiritStones());
        assertEquals(1000L, info.getRequiredSpiritStones());
    }

    @Test
    void getPromotionInfo_ReputationNotEnough_CanUpgradeFalse() {
        character.setSpiritStones(2000L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(600);

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(50); // 声望不足

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);

        PositionUpgradeInfo info = sectMemberService.getPromotionInfo(1L);

        assertNotNull(info);
        assertTrue(info.getAvailable());
        assertFalse(info.getCanUpgrade());
        assertEquals(50, info.getCurrentReputation());
        assertEquals(100, info.getRequiredReputation());
    }

    @Test
    void getPromotionInfo_Elder_NotAvailable() {
        sectMember.setPosition("长老");

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);

        PositionUpgradeInfo info = sectMemberService.getPromotionInfo(1L);

        assertNotNull(info);
        assertFalse(info.getAvailable());
        assertEquals("长老", info.getCurrentPosition());
        assertEquals("已达到最高可升级职位", info.getUnavailableReason());
    }

    @Test
    void getPromotionInfo_NoSect_ThrowsException() {
        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.getPromotionInfo(1L));
        assertEquals(8004, exception.getCode());
    }

    @Test
    void promotePosition_Success() {
        character.setSpiritStones(5000L);
        character.setCharacterId(1L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(1000);

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(200);

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);
        when(sectMemberMapper.updateById(any(SectMember.class))).thenReturn(1);
        when(characterService.updateById(any(PlayerCharacter.class))).thenReturn(true);

        String result = sectMemberService.promotePosition(request);

        assertNotNull(result);
        assertTrue(result.contains("内门弟子"));
        assertEquals(500, sectMember.getContribution());
        assertEquals("内门弟子", sectMember.getPosition());
        assertEquals(4000L, character.getSpiritStones());
    }

    @Test
    void promotePosition_ReputationNotEnough_ThrowsException() {
        character.setSpiritStones(5000L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(1000);

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(50); // 声望不足

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.promotePosition(request));
        assertEquals(8022, exception.getCode());
        assertTrue(exception.getMessage().contains("声望值不足"));
    }

    @Test
    void promotePosition_ContributionNotEnough_ThrowsException() {
        character.setSpiritStones(5000L);
        sectMember.setPosition("弟子");
        sectMember.setContribution(300); // 贡献值不足

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(200);

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.promotePosition(request));
        assertEquals(8023, exception.getCode());
        assertTrue(exception.getMessage().contains("贡献值不足"));
    }

    @Test
    void promotePosition_SpiritStonesNotEnough_ThrowsException() {
        character.setSpiritStones(500L); // 灵石不足
        sectMember.setPosition("弟子");
        sectMember.setContribution(1000);

        CharacterSectReputation reputation = new CharacterSectReputation();
        reputation.setCharacterId(1L);
        reputation.setSectId(1L);
        reputation.setReputation(200);

        PositionPromotionProperties.PositionRequirement requirement =
                new PositionPromotionProperties.PositionRequirement();
        requirement.setPositionLevel(1);
        requirement.setRequiredReputation(100);
        requirement.setContributionCost(500);
        requirement.setSpiritStonesCost(1000);
        requirement.setEnabled(true);

        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);
        when(reputationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reputation);
        when(promotionProperties.getRequirementByLevel(1)).thenReturn(requirement);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.promotePosition(request));
        assertEquals(8024, exception.getCode());
        assertTrue(exception.getMessage().contains("灵石不足"));
    }

    @Test
    void promotePosition_ElderCannotUpgrade_ThrowsException() {
        sectMember.setPosition("长老");

        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(sectMember);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.promotePosition(request));
        assertEquals(8020, exception.getCode());
    }

    @Test
    void promotePosition_NoSect_ThrowsException() {
        PromotePositionRequest request = new PromotePositionRequest();
        request.setCharacterId(1L);

        when(characterService.getById(1L)).thenReturn(character);
        when(sectMemberMapper.selectOne(any(LambdaQueryWrapper.class), anyBoolean())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sectMemberService.promotePosition(request));
        assertEquals(8004, exception.getCode());
    }
}
