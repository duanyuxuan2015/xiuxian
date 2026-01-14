package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.dto.request.ExplorationRequest;
import com.xiuxian.dto.response.ExplorationAreaResponse;
import com.xiuxian.dto.response.ExplorationResponse;
import com.xiuxian.entity.ExplorationRecord;
import com.xiuxian.service.ExplorationService;
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

@WebMvcTest(controllers = ExplorationController.class)
@Import(GlobalExceptionHandler.class)
public class ExplorationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExplorationService explorationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllAreas_Success() throws Exception {
        ExplorationAreaResponse area = new ExplorationAreaResponse();
        area.setAreaId(1L);
        area.setAreaName("Forest");
        List<ExplorationAreaResponse> areas = Collections.singletonList(area);

        when(explorationService.getAllAreas(anyLong())).thenReturn(areas);

        mockMvc.perform(get("/exploration/areas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].areaId").value(1))
                .andExpect(jsonPath("$.data[0].areaName").value("Forest"));
    }

    @Test
    public void startExploration_Success() throws Exception {
        ExplorationRequest request = new ExplorationRequest();
        request.setCharacterId(1L);
        request.setAreaId(1L);

        ExplorationResponse response = new ExplorationResponse();
        response.setRecordId(1L);
        response.setResult("Found items");

        when(explorationService.startExploration(any(ExplorationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/exploration/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recordId").value(1))
                .andExpect(jsonPath("$.data.result").value("Found items"));
    }

    @Test
    public void getExplorationRecords_Success() throws Exception {
        ExplorationResponse record = new ExplorationResponse();
        record.setRecordId(1L);
        record.setResult("Success");
        List<ExplorationResponse> records = Collections.singletonList(record);

        when(explorationService.getExplorationRecords(anyLong())).thenReturn(records);

        mockMvc.perform(get("/exploration/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].recordId").value(1));
    }
}
