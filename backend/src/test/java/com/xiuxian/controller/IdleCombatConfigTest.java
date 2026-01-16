package com.xiuxian.controller;

import com.xiuxian.config.IdleCombatProperties;
import com.xiuxian.dto.response.IdleCombatConfigResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 挂机配置测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IdleCombatConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdleCombatProperties idleCombatProperties;

    @Test
    void testGetIdleCombatConfig() throws Exception {
        // 测试获取挂机配置
        mockMvc.perform(get("/combat/idle-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.maxBattles").exists())
                .andExpect(jsonPath("$.data.maxBattles").isNumber());
    }

    @Test
    void testIdleCombatPropertiesDefaultValue() {
        // 验证配置属性正确加载
        // 默认值应该是30
        int maxBattles = idleCombatProperties.getMaxBattles();

        // 验证值大于0
        assertTrue(maxBattles > 0, "最大战斗轮数应该大于0");

        // 验证默认值为30（可以通过application.yml修改）
        // 如果测试环境没有配置文件，会使用默认值30
        assertTrue(maxBattles == 30, "默认值应该是30，实际值: " + maxBattles);

        System.out.println("最大战斗轮数配置: " + maxBattles);
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
