package com.auctions.service.auction;

import com.auctions.domain.*;
import com.auctions.service.auction.component.*;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private CancelAuctionServiceComponent cancelAuctionServiceComponent;

    @Mock
    private CreateAuctionServiceComponent createAuctionServiceComponent;

    @Mock
    private DeleteAuctionServiceComponent deleteAuctionServiceComponent;

    @Mock
    private GetAuctionServiceComponent getAuctionServiceComponent;

    @Mock
    private StartAuctionServiceComponent startAuctionServiceComponent;

    @Mock
    private UpdateAuctionServiceComponent updateAuctionServiceComponent;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private Auction testAuction;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        Lot testLot = TestDataFactory.createTestLot(testUser);

        testAuction = TestDataFactory.createTestAuction(testUser, testLot);
    }

    @Test
    void getAllAuctions() {

        when(getAuctionServiceComponent.getAllAuctions()).thenReturn(List.of(testAuction));

        List<Auction> auctions = auctionService.getAllAuctions();

        assertEquals(1, auctions.size());
        assertEquals(testAuction, auctions.get(0));
    }

    @Test
    void getAuctionById() {

        when(getAuctionServiceComponent.getAuctionById(testAuction.getId())).thenReturn(testAuction);

        assertEquals(testAuction, auctionService.getAuctionById(testAuction.getId()));
    }

    @Test
    void createAuction() {

        when(createAuctionServiceComponent.createAuction(testAuction, testUser)).thenReturn(testAuction);

        assertEquals(testAuction, auctionService.createAuction(testAuction, testUser));
    }

    @Test
    void updateAuctionDetails() {

        when(updateAuctionServiceComponent.updateAuctionDetails(testAuction.getId(), testAuction, testUser)).thenReturn(testAuction);

        assertEquals(testAuction, auctionService.updateAuctionDetails(testAuction.getId(), testAuction, testUser));
    }

    @Test
    void deleteAuction() {

        auctionService.deleteAuction(testAuction.getId());

        verify(deleteAuctionServiceComponent).deleteAuction(testAuction.getId());
    }

    @Test
    void startAuction() {

        auctionService.startAuction(testAuction.getId());

        verify(startAuctionServiceComponent).startAuction(testAuction.getId());
    }

    @Test
    void cancelAuction() {

        auctionService.cancelAuction(testAuction.getId());

        verify(cancelAuctionServiceComponent).cancelAuction(testAuction.getId());
    }
}
