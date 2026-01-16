package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.config.IdleCombatProperties;
import com.xiuxian.dto.request.CombatStartRequest;
import com.xiuxian.dto.response.CombatResponse;
import com.xiuxian.entity.CombatRecord;
import com.xiuxian.entity.Monster;
import com.xiuxian.service.CombatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CombatController.class)
@Import(GlobalExceptionHandler.class)
public class CombatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CombatService combatService;

    @MockBean
    private IdleCombatProperties idleCombatProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void startCombat_Success() throws Exception {
        CombatStartRequest request = new CombatStartRequest();
        request.setCharacterId(1L);
        request.setMonsterId(1L);

        CombatResponse response = new CombatResponse();
        response.setVictory(true);
        response.setMessage("Victory!");

        when(combatService.startCombat(any(CombatStartRequest.class))).thenReturn(response);

        mockMvc.perform(post("/combat/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.victory").value(true))
                .andExpect(jsonPath("$.data.message").value("Victory!"));
    }

    @Test
    public void getAvailableMonsters_Success() throws Exception {
        Monster monster = new Monster();
        monster.setMonsterId(1L);
        monster.setMonsterName("Slime");
        List<Monster> monsters = Collections.singletonList(monster);

        when(combatService.getAvailableMonsters(anyLong())).thenReturn(monsters);

        mockMvc.perform(get("/combat/monsters?characterId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].monsterId").value(1))
                .andExpect(jsonPath("$.data[0].monsterName").value("Slime"));
    }

    @Test
    public void getCombatRecords_Success() throws Exception {
        CombatRecord record = new CombatRecord();
        record.setCombatId(1L);
        record.setIsVictory(1);
        Page<CombatRecord> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(record));
        pageResult.setTotal(1);

        when(combatService.getCombatRecords(anyLong(), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/combat/records")
                .param("characterId", "1")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].combatId").value(1))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
