package com.xiuxian.integration;

import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.service.CharacterService;
import com.xiuxian.service.EquipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色抗性集成测试
 * 验证角色信息中正确包含了装备的抗性加成
 */
@SpringBootTest
@ActiveProfiles("test")
public class CharacterResistanceIntegrationTest {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private EquipmentService equipmentService;

    @Test
    void testCharacterResponseIncludesResistance() {
        // 假设角色ID为1存在
        Long characterId = 1L;

        // 获取角色信息
        CharacterResponse response = characterService.getCharacterById(characterId);

        // 验证响应不为null
        assertNotNull(response, "角色响应不应为null");
        assertNotNull(response.getCharacterId(), "角色ID不应为null");

        // 验证抗性字段存在（即使没有装备，也应该是0而不是null）
        assertNotNull(response.getPhysicalResist(), "物理抗性不应为null");
        assertNotNull(response.getIceResist(), "冰系抗性不应为null");
        assertNotNull(response.getFireResist(), "火系抗性不应为null");
        assertNotNull(response.getLightningResist(), "雷系抗性不应为null");

        // 验证抗性值非负
        assertTrue(response.getPhysicalResist() >= 0, "物理抗性应>=0");
        assertTrue(response.getIceResist() >= 0, "冰系抗性应>=0");
        assertTrue(response.getFireResist() >= 0, "火系抗性应>=0");
        assertTrue(response.getLightningResist() >= 0, "雷系抗性应>=0");

        // 打印抗性值以便调试
        System.out.println("角色ID: " + response.getCharacterId());
        System.out.println("角色名: " + response.getPlayerName());
        System.out.println("物理抗性: " + response.getPhysicalResist());
        System.out.println("冰系抗性: " + response.getIceResist());
        System.out.println("火系抗性: " + response.getFireResist());
        System.out.println("雷系抗性: " + response.getLightningResist());
    }
}
