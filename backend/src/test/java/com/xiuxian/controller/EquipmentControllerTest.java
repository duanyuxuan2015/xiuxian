package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.EquipRequest;
import com.xiuxian.dto.response.EquipmentResponse;
import com.xiuxian.service.EquipmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EquipmentController.class)
@Import(GlobalExceptionHandler.class)
public class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentService equipmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getCharacterEquipments_Success() throws Exception {
        EquipmentResponse equipment = new EquipmentResponse();
        equipment.setEquipmentId(1L);
        equipment.setEquipmentName("Iron Sword");
        List<EquipmentResponse> equipments = Collections.singletonList(equipment);

        when(equipmentService.getCharacterEquipments(anyLong())).thenReturn(equipments);

        mockMvc.perform(get("/equipment/character/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].equipmentId").value(1))
                .andExpect(jsonPath("$.data[0].equipmentName").value("Iron Sword"));
    }

    @Test
    public void equipItem_Success() throws Exception {
        EquipRequest request = new EquipRequest();
        request.setCharacterId(1L);
        request.setEquipmentId(1L);
        request.setEquipmentSlot("WEAPON");

        EquipmentResponse response = new EquipmentResponse();
        response.setEquipmentId(1L);
        response.setIsEquipped(true);

        when(equipmentService.equipItem(any(EquipRequest.class))).thenReturn(response);

        mockMvc.perform(post("/equipment/equip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.equipmentId").value(1))
                .andExpect(jsonPath("$.data.isEquipped").value(true));
    }

    @Test
    public void unequipItem_Success() throws Exception {
        when(equipmentService.unequipItem(anyLong(), any())).thenReturn(true);

        mockMvc.perform(delete("/equipment/unequip")
                .param("characterId", "1")
                .param("equipmentSlot", "WEAPON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void getEquipmentBonus_Success() throws Exception {
        EquipmentService.EquipmentBonus bonus = new EquipmentService.EquipmentBonus();
        bonus.attackBonus = 10;

        when(equipmentService.calculateEquipmentBonus(anyLong())).thenReturn(bonus);

        mockMvc.perform(get("/equipment/bonus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.attackBonus").value(10));
    }
}
