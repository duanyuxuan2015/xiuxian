package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.CultivationRequest;
import com.xiuxian.dto.response.BreakthroughResponse;
import com.xiuxian.dto.response.CultivationResponse;
import com.xiuxian.entity.CultivationRecord;
import com.xiuxian.service.CultivationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CultivationController.class)
@Import(GlobalExceptionHandler.class)
public class CultivationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CultivationService cultivationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void startCultivation_Success() throws Exception {
        CultivationRequest request = new CultivationRequest();
        request.setCharacterId(1L);

        CultivationResponse response = new CultivationResponse();
        response.setCultivationId(1L);
        response.setExpGained(100);

        when(cultivationService.startCultivation(any(CultivationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/cultivation/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.cultivationId").value(1))
                .andExpect(jsonPath("$.data.expGained").value(100));
    }

    @Test
    public void getCultivationRecords_Success() throws Exception {
        CultivationRecord record = new CultivationRecord();
        record.setCultivationId(1L);
        record.setExpGained(100);
        Page<CultivationRecord> pageResult = new Page<>();
        pageResult.setRecords(Collections.singletonList(record));
        pageResult.setTotal(1);

        when(cultivationService.getCultivationRecords(anyLong(), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/cultivation/records")
                .param("characterId", "1")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].cultivationId").value(1))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    public void attemptBreakthrough_Success() throws Exception {
        BreakthroughResponse response = new BreakthroughResponse();
        response.setSuccess(true);
        response.setMessage("Breakthrough Successful");

        when(cultivationService.attemptBreakthrough(any())).thenReturn(response);

        com.xiuxian.dto.request.BreakthroughRequest request = new com.xiuxian.dto.request.BreakthroughRequest();
        request.setCharacterId(1L);

        mockMvc.perform(post("/cultivation/breakthrough")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    public void getBreakthroughRate_Success() throws Exception {
        when(cultivationService.calculateBreakthroughRate(anyLong())).thenReturn(80);

        mockMvc.perform(get("/cultivation/breakthrough-rate")
                .param("characterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(80));
    }
}
