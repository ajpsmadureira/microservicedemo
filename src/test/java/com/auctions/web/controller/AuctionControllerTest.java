package com.auctions.web.controller;

import com.auctions.domain.Auction;
import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.service.auction.AuctionService;
import com.auctions.service.auth.AuthService;
import com.auctions.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@Import(TestControllerConfig.class)
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuctionService auctionService;

    @MockitoBean
    private AuthService authService;

    private Auction testAuction;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        
        testAuction = TestDataFactory.createTestAuction(testUser, testLot);
    }

    @Test
    @WithMockUser
    void getAllAuctions_whenAuthenticated_shouldReturnAuctions() throws Exception {

        when(auctionService.getAllAuctions()).thenReturn(Collections.singletonList(testAuction));

        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startTime").value(testAuction.getStartTime().toString()))
                .andExpect(jsonPath("$[0].stopTime").value(testAuction.getStopTime().toString()))
                .andExpect(jsonPath("$[0].lotId").value(testAuction.getLotId().toString()));
    }

    @Test
    void getAllAuctions_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAuctionById_whenAuctionExists_shouldReturnAuction() throws Exception {

        when(auctionService.getAuctionById(1)).thenReturn(testAuction);

        mockMvc.perform(get("/api/auctions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value(testAuction.getStartTime().toString()))
                .andExpect(jsonPath("$.stopTime").value(testAuction.getStopTime().toString()))
                .andExpect(jsonPath("$.lotId").value(testAuction.getLotId().toString()));
    }

    @Test
    @WithMockUser
    void createAuction_whenDataIsValid_shouldCreateAuction() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(auctionService.createAuction(any(Auction.class), any(User.class)))
                .thenReturn(testAuction);

        mockMvc.perform(post("/api/auctions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime").value(testAuction.getStartTime().toString()))
                .andExpect(jsonPath("$.stopTime").value(testAuction.getStopTime().toString()))
                .andExpect(jsonPath("$.lotId").value(testAuction.getLotId().toString()));
    }

    @Test
    @WithMockUser
    void createAuction_whenDataIsInvalid_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateAuction_whenDataIsValid_shouldUpdateAuction() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(auctionService.updateAuctionDetails(eq(1), any(Auction.class), any(User.class)))
                .thenReturn(testAuction);

        mockMvc.perform(put("/api/auctions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAuction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value(testAuction.getStartTime().toString()))
                .andExpect(jsonPath("$.stopTime").value(testAuction.getStopTime().toString()))
                .andExpect(jsonPath("$.lotId").value(testAuction.getLotId().toString()));
    }

    @Test
    @WithMockUser
    void deleteAuction() throws Exception {
        mockMvc.perform(delete("/api/auctions/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void startAuction() throws Exception {
        mockMvc.perform(post("/api/auctions/1/start"))
                .andExpect(status().isOk());
    }
}