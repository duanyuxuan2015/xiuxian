package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.AlchemyRequest;
import com.xiuxian.dto.response.AlchemyResponse;
import com.xiuxian.dto.response.MaterialResponse;
import com.xiuxian.dto.response.PillRecipeResponse;
import com.xiuxian.service.AlchemyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlchemyController.class)
@Import(GlobalExceptionHandler.class)
public class AlchemyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlchemyService alchemyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAvailableRecipes_Success() throws Exception {
        PillRecipeResponse recipe = new PillRecipeResponse();
        recipe.setRecipeId(1L);
        recipe.setRecipeName("Health Pill");
        List<PillRecipeResponse> recipes = Collections.singletonList(recipe);

        when(alchemyService.getAvailableRecipes(anyLong())).thenReturn(recipes);

        mockMvc.perform(get("/alchemy/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].recipeId").value(1))
                .andExpect(jsonPath("$.data[0].recipeName").value("Health Pill"));
    }

    @Test
    public void getRecipeDetail_Success() throws Exception {
        PillRecipeResponse recipe = new PillRecipeResponse();
        recipe.setRecipeId(1L);
        recipe.setRecipeName("Health Pill");

        when(alchemyService.getRecipeDetail(anyLong(), anyLong())).thenReturn(recipe);

        mockMvc.perform(get("/alchemy/recipe/1")
                .param("characterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.recipeId").value(1))
                .andExpect(jsonPath("$.data.recipeName").value("Health Pill"));
    }

    @Test
    public void startAlchemy_Success() throws Exception {
        AlchemyRequest request = new AlchemyRequest();
        request.setCharacterId(1L);
        request.setRecipeId(1L);

        AlchemyResponse response = new AlchemyResponse();
        response.setSuccess(true);
        response.setMessage("Alchemy Successful");

        when(alchemyService.startAlchemy(any(AlchemyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/alchemy/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Alchemy Successful"));
    }

    @Test
    public void getAlchemyRecords_Success() throws Exception {
        AlchemyResponse record = new AlchemyResponse();
        record.setSuccess(true);
        List<AlchemyResponse> records = Collections.singletonList(record);

        when(alchemyService.getAlchemyRecords(anyLong())).thenReturn(records);

        mockMvc.perform(get("/alchemy/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].success").value(true));
    }

    @Test
    public void getCharacterMaterials_Success() throws Exception {
        MaterialResponse material = new MaterialResponse();
        material.setMaterialId(1L);
        material.setMaterialName("Herb");
        List<MaterialResponse> materials = Collections.singletonList(material);

        when(alchemyService.getCharacterMaterials(anyLong())).thenReturn(materials);

        mockMvc.perform(get("/alchemy/materials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].materialId").value(1))
                .andExpect(jsonPath("$.data[0].materialName").value("Herb"));
    }

    @Test
    public void calculateSuccessRate_Success() throws Exception {
        when(alchemyService.calculateSuccessRate(anyLong(), anyLong())).thenReturn(80);

        mockMvc.perform(get("/alchemy/success-rate")
                .param("characterId", "1")
                .param("recipeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(80));
    }
}
