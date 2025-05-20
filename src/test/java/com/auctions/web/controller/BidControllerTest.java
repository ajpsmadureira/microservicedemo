package com.auctions.web.controller;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.bid.Bid;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.service.auth.AuthService;
import com.auctions.service.bid.BidService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);

        testBid = TestDataFactory.createTestBid(testUser, testAuction);
    }

    @Test
    @WithMockUser
    void getAllBids_whenAuthenticated_shouldReturnLots() throws Exception {

        when(bidService.getAllBids()).thenReturn(Collections.singletonList(testBid));

        mockMvc.perform(get("/api/bids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBid.getId()))
                .andExpect(jsonPath("$[0].amount").value(testBid.getAmount()))
                .andExpect(jsonPath("$[0].until").value(testBid.getUntil().toString()))
                .andExpect(jsonPath("$[0].auctionId").value(testBid.getAuctionId()))
                .andExpect(jsonPath("$[0].createdByUserId").value(testBid.getCreatedByUserId()))
                .andExpect(jsonPath("$[0].lastModifiedByUserId").value(testBid.getLastModifiedByUserId()));
    }

    @Test
    void getAllBids_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/bids"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getBidById_whenBidExists_shouldReturnBid() throws Exception {

        when(bidService.getBidById(1)).thenReturn(testBid);

        mockMvc.perform(get("/api/bids/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBid.getId()))
                .andExpect(jsonPath("$.amount").value(testBid.getAmount()))
                .andExpect(jsonPath("$.until").value(testBid.getUntil().toString()))
                .andExpect(jsonPath("$.auctionId").value(testBid.getAuctionId()))
                .andExpect(jsonPath("$.createdByUserId").value(testBid.getCreatedByUserId()))
                .andExpect(jsonPath("$.lastModifiedByUserId").value(testBid.getLastModifiedByUserId()));
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
                .andExpect(jsonPath("$.auctionId").value(testBid.getAuctionId()))
                .andExpect(jsonPath("$.createdByUserId").value(testBid.getCreatedByUserId()))
                .andExpect(jsonPath("$.lastModifiedByUserId").value(testBid.getLastModifiedByUserId()));
    }

    @Test
    @WithMockUser
    void acceptBid() throws Exception {

        mockMvc.perform(post("/api/bids/1/accept")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void cancelBid() throws Exception {

        mockMvc.perform(post("/api/bids/1/cancel")).andExpect(status().isOk());
    }
} 