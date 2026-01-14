package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.EquipSkillRequest;
import com.xiuxian.dto.request.LearnSkillRequest;
import com.xiuxian.dto.response.SkillResponse;
import com.xiuxian.service.SkillService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SkillController.class)
@Import(GlobalExceptionHandler.class)
public class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAvailableSkills_Success() throws Exception {
        SkillResponse skill = new SkillResponse();
        skill.setSkillId(1L);
        skill.setSkillName("Fireball");
        List<SkillResponse> skills = Collections.singletonList(skill);

        when(skillService.getAvailableSkills(anyLong())).thenReturn(skills);

        mockMvc.perform(get("/skill/available/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].skillId").value(1))
                .andExpect(jsonPath("$.data[0].skillName").value("Fireball"));
    }

    @Test
    public void getLearnedSkills_Success() throws Exception {
        SkillResponse skill = new SkillResponse();
        skill.setSkillId(1L);
        skill.setSkillName("Fireball");
        skill.setIsLearned(true);
        List<SkillResponse> skills = Collections.singletonList(skill);

        when(skillService.getLearnedSkills(anyLong())).thenReturn(skills);

        mockMvc.perform(get("/skill/learned/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].skillId").value(1))
                .andExpect(jsonPath("$.data[0].isLearned").value(true));
    }

    @Test
    public void learnSkill_Success() throws Exception {
        LearnSkillRequest request = new LearnSkillRequest();
        request.setCharacterId(1L);
        request.setSkillId(1L);

        SkillResponse response = new SkillResponse();
        response.setSkillId(1L);
        response.setIsLearned(true);

        when(skillService.learnSkill(any(LearnSkillRequest.class))).thenReturn(response);

        mockMvc.perform(post("/skill/learn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.skillId").value(1))
                .andExpect(jsonPath("$.data.isLearned").value(true));
    }

    @Test
    public void equipSkill_Success() throws Exception {
        EquipSkillRequest request = new EquipSkillRequest();
        request.setCharacterId(1L);
        request.setCharacterSkillId(1L);
        request.setSlotIndex(1);

        SkillResponse response = new SkillResponse();
        response.setSkillId(1L);
        response.setIsEquipped(true);

        when(skillService.equipSkill(any(EquipSkillRequest.class))).thenReturn(response);

        mockMvc.perform(post("/skill/equip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.skillId").value(1))
                .andExpect(jsonPath("$.data.isEquipped").value(true));
    }
}
