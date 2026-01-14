package com.xiuxian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiuxian.dto.request.AllocatePointsRequest;
import com.xiuxian.dto.request.CharacterCreateRequest;
import com.xiuxian.dto.response.AllocatePointsResponse;
import com.xiuxian.dto.response.CharacterResponse;
import com.xiuxian.service.CharacterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CharacterController.class)
@Import(GlobalExceptionHandler.class)
public class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharacterService characterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createCharacter_Success() throws Exception {
        CharacterCreateRequest request = new CharacterCreateRequest();
        request.setPlayerName("Test");  // 长度必须在2-6个字符之间
        request.setConstitution(10);
        request.setSpirit(10);
        request.setComprehension(10);
        request.setLuck(10);
        request.setFortune(5);

        CharacterResponse response = new CharacterResponse();
        response.setCharacterId(1L);
        response.setPlayerName("Test");

        when(characterService.createCharacter(any(CharacterCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/characters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.characterId").value(1))
                .andExpect(jsonPath("$.data.playerName").value("Test"));
    }

    @Test
    public void getCharacterById_Success() throws Exception {
        CharacterResponse response = new CharacterResponse();
        response.setCharacterId(1L);
        response.setPlayerName("TestPlayer");

        when(characterService.getCharacterById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/characters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.characterId").value(1))
                .andExpect(jsonPath("$.data.playerName").value("TestPlayer"));
    }

    @Test
    public void checkNameExists_True() throws Exception {
        when(characterService.checkNameExists(anyString())).thenReturn(true);

        mockMvc.perform(get("/characters/check-name/TestPlayer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    // ==================== allocatePoints API 测试 ====================

    @Test
    public void allocatePoints_Success() throws Exception {
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(2);
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(1);
        request.setFortunePoints(0);

        AllocatePointsResponse response = new AllocatePointsResponse();
        response.setCharacterId(1L);
        response.setPlayerName("TestPlayer");
        response.setNewConstitution(12);
        response.setNewSpirit(11);
        response.setNewComprehension(11);
        response.setNewLuck(11);
        response.setNewFortune(10);
        response.setRemainingPoints(0);
        response.setMessage("属性点分配成功");

        when(characterService.allocatePoints(any(AllocatePointsRequest.class))).thenReturn(response);

        mockMvc.perform(post("/characters/allocate-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.characterId").value(1))
                .andExpect(jsonPath("$.data.playerName").value("TestPlayer"))
                .andExpect(jsonPath("$.data.newConstitution").value(12))
                .andExpect(jsonPath("$.data.newSpirit").value(11))
                .andExpect(jsonPath("$.data.newComprehension").value(11))
                .andExpect(jsonPath("$.data.newLuck").value(11))
                .andExpect(jsonPath("$.data.newFortune").value(10))
                .andExpect(jsonPath("$.data.remainingPoints").value(0))
                .andExpect(jsonPath("$.data.message").value("属性点分配成功"));
    }

    @Test
    public void allocatePoints_ValidationError_NegativePoints() throws Exception {
        // 负数点数应该触发验证错误
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(-1); // 负数
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        mockMvc.perform(post("/characters/allocate-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void allocatePoints_PartialAllocation() throws Exception {
        // 测试部分分配点数
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(1);
        request.setSpiritPoints(1);
        request.setComprehensionPoints(1);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        AllocatePointsResponse response = new AllocatePointsResponse();
        response.setCharacterId(1L);
        response.setNewConstitution(51);
        response.setNewSpirit(51);
        response.setNewComprehension(51);
        response.setNewLuck(50);
        response.setNewFortune(50);
        response.setRemainingPoints(7); // 还有剩余点数
        response.setMessage("属性点分配成功");

        when(characterService.allocatePoints(any(AllocatePointsRequest.class))).thenReturn(response);

        mockMvc.perform(post("/characters/allocate-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.remainingPoints").value(7));
    }

    @Test
    public void allocatePoints_SingleAttributeAllocation() throws Exception {
        // 测试所有点数分配到单个属性
        AllocatePointsRequest request = new AllocatePointsRequest();
        request.setCharacterId(1L);
        request.setConstitutionPoints(5);
        request.setSpiritPoints(0);
        request.setComprehensionPoints(0);
        request.setLuckPoints(0);
        request.setFortunePoints(0);

        AllocatePointsResponse response = new AllocatePointsResponse();
        response.setCharacterId(1L);
        response.setNewConstitution(105);
        response.setNewSpirit(50);
        response.setNewComprehension(50);
        response.setNewLuck(50);
        response.setNewFortune(50);
        response.setRemainingPoints(0);
        response.setMessage("属性点分配成功");

        when(characterService.allocatePoints(any(AllocatePointsRequest.class))).thenReturn(response);

        mockMvc.perform(post("/characters/allocate-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.newConstitution").value(105))
                .andExpect(jsonPath("$.data.newSpirit").value(50));
    }
}
