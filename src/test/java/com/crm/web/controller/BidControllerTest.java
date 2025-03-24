package com.crm.web.controller;

import com.crm.domain.Bid;
import com.crm.domain.Lot;
import com.crm.domain.User;
import com.crm.service.auth.AuthService;
import com.crm.service.bid.BidService;
import com.crm.util.TestDataFactory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BidController.class)
@Import(TestControllerConfig.class)
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BidService bidService;

    @MockitoBean
    private AuthService authService;

    private Bid testBid;
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        Lot testLot = TestDataFactory.createTestLot(testUser);

        testBid = TestDataFactory.createTestBid(testUser, testLot);
    }

    @Test
    @WithMockUser
    void createBid() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(bidService.createBid(any(Bid.class), any(User.class)))
                .thenReturn(testBid);

        mockMvc.perform(post("/api/bids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBid)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBid.getId()))
                .andExpect(jsonPath("$.amount").value(testBid.getAmount()))
                .andExpect(jsonPath("$.until").value(testBid.getUntil().toString()))
                .andExpect(jsonPath("$.lotId").value(testBid.getLotId()))
                .andExpect(jsonPath("$.createdByUserId").value(testBid.getCreatedByUserId()))
                .andExpect(jsonPath("$.lastModifiedByUserId").value(testBid.getLastModifiedByUserId()));
    }

    @Test
    @WithMockUser
    void deleteBid() throws Exception {

        mockMvc.perform(delete("/api/bids/1")).andExpect(status().isOk());
    }
} 