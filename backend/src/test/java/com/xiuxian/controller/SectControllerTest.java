package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.JoinSectRequest;
import com.xiuxian.dto.request.SectShopBuyRequest;
import com.xiuxian.dto.response.SectMemberResponse;
import com.xiuxian.dto.response.SectResponse;
import com.xiuxian.dto.response.SectShopItemResponse;
import com.xiuxian.service.SectMemberService;
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

@WebMvcTest(controllers = SectController.class)
@Import(GlobalExceptionHandler.class)
public class SectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectMemberService sectMemberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllSects_Success() throws Exception {
        SectResponse sect = new SectResponse();
        sect.setSectId(1L);
        sect.setSectName("Heavenly Sect");
        List<SectResponse> sects = Collections.singletonList(sect);

        when(sectMemberService.getAllSects(anyLong())).thenReturn(sects);

        mockMvc.perform(get("/sect/list")
                .param("characterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].sectId").value(1))
                .andExpect(jsonPath("$.data[0].sectName").value("Heavenly Sect"));
    }

    @Test
    public void getCharacterSect_Success() throws Exception {
        SectMemberResponse member = new SectMemberResponse();
        member.setMemberId(1L);
        member.setSectName("Heavenly Sect");
        member.setPosition("Member");

        when(sectMemberService.getCharacterSect(anyLong())).thenReturn(member);

        mockMvc.perform(get("/sect/my/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.sectName").value("Heavenly Sect"));
    }

    @Test
    public void joinSect_Success() throws Exception {
        JoinSectRequest request = new JoinSectRequest();
        request.setCharacterId(1L);
        request.setSectId(1L);

        SectMemberResponse response = new SectMemberResponse();
        response.setMemberId(1L);
        response.setSectName("Heavenly Sect");

        when(sectMemberService.joinSect(any(JoinSectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/sect/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.memberId").value(1));
    }

    @Test
    public void leaveSect_Success() throws Exception {
        when(sectMemberService.leaveSect(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/sect/leave/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void getShopItems_Success() throws Exception {
        SectShopItemResponse item = new SectShopItemResponse();
        item.setItemId(1L);
        item.setItemName("Spirit Pill");
        List<SectShopItemResponse> items = Collections.singletonList(item);

        when(sectMemberService.getShopItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/sect/shop/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].itemId").value(1))
                .andExpect(jsonPath("$.data[0].itemName").value("Spirit Pill"));
    }
}
