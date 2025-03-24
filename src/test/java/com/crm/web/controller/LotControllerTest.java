package com.crm.web.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crm.domain.Lot;
import com.crm.domain.User;
import com.crm.service.auth.AuthService;
import com.crm.service.lot.LotService;
import com.crm.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import com.crm.config.TestConfig;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

@WebMvcTest(LotController.class)
@Import(TestConfig.class)
class LotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LotService lotService;

    @MockitoBean
    private AuthService authService;

    private Lot testLot;
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        
        testLot = TestDataFactory.createTestLot(testUser);
    }

    @Test
    @WithMockUser
    void getAllLots_ShouldReturnLots() throws Exception {

        when(lotService.getAllLots()).thenReturn(Collections.singletonList(testLot));

        mockMvc.perform(get("/api/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testLot.getName()))
                .andExpect(jsonPath("$[0].surname").value(testLot.getSurname()));
    }

    @Test
    void getAllLots_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/lots"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getLotById_WhenLotExists_ShouldReturnLot() throws Exception {

        when(lotService.getLotById(1)).thenReturn(testLot);

        mockMvc.perform(get("/api/lots/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testLot.getName()))
                .andExpect(jsonPath("$.surname").value(testLot.getSurname()));
    }

    @Test
    @WithMockUser
    void createLot_WithValidData_ShouldCreateLot() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(lotService.createLot(any(Lot.class), any(User.class)))
                .thenReturn(testLot);

        mockMvc.perform(post("/api/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLot)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(testLot.getName()))
                .andExpect(jsonPath("$.surname").value(testLot.getSurname()));
    }

    @Test
    @WithMockUser
    void createLot_WithInvalidData_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateLot_WithValidData_ShouldUpdateLot() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(lotService.updateLotDetails(eq(1), any(Lot.class), any(User.class)))
                .thenReturn(testLot);

        mockMvc.perform(put("/api/lots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLot)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testLot.getName()))
                .andExpect(jsonPath("$.surname").value(testLot.getSurname()));
    }

    @Test
    @WithMockUser
    void updateLotPhoto_WithValidData_ShouldUpdateLotPhoto() throws Exception {

        MockMultipartFile photoFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(authService.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(multipart("/api/lots/1/photo")
                        .file(photoFile))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteLot_ShouldDeleteLot() throws Exception {
        mockMvc.perform(delete("/api/lots/1"))
                .andExpect(status().isOk());
    }
} 