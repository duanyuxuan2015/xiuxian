package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.common.response.PageResult;
import com.xiuxian.dto.request.ForgeRequest;
import com.xiuxian.dto.response.EquipmentRecipeResponse;
import com.xiuxian.dto.response.ForgeResponse;
import com.xiuxian.entity.ForgeRecord;
import com.xiuxian.service.ForgeService;
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

@WebMvcTest(controllers = ForgeController.class)
@Import(GlobalExceptionHandler.class)
public class ForgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForgeService forgeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAvailableRecipes_Success() throws Exception {
        EquipmentRecipeResponse recipe = new EquipmentRecipeResponse();
        recipe.setRecipeId(1L);
        recipe.setRecipeName("Sword Recipe");
        List<EquipmentRecipeResponse> recipes = Collections.singletonList(recipe);

        when(forgeService.getAvailableRecipes(anyLong())).thenReturn(recipes);

        mockMvc.perform(get("/forge/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].recipeId").value(1))
                .andExpect(jsonPath("$.data[0].recipeName").value("Sword Recipe"));
    }

    @Test
    public void startForge_Success() throws Exception {
        ForgeRequest request = new ForgeRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        ForgeResponse response = new ForgeResponse();
        response.setRecordId(1L);
        response.setSuccess(true);

        when(forgeService.startForge(any(ForgeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/forge/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recordId").value(1))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    public void getForgeRecords_Success() throws Exception {
        ForgeResponse record = new ForgeResponse();
        record.setRecordId(1L);
        record.setSuccess(true);
        List<ForgeResponse> records = Collections.singletonList(record);

        when(forgeService.getForgeRecords(anyLong())).thenReturn(records);

        mockMvc.perform(get("/forge/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].recordId").value(1));
    }
}
