package com.auctions.service.bid;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.bid.Bid;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.service.bid.component.CancelBidServiceComponent;
import com.auctions.service.bid.component.CreateBidServiceComponent;
import com.auctions.service.bid.component.GetBidServiceComponent;
import com.auctions.service.bid.component.UpdateBidServiceComponent;
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
class BidServiceTest {

    @Mock
    private UpdateBidServiceComponent updateBidServiceComponent;

    @Mock
    private CancelBidServiceComponent cancelBidServiceComponent;

    @Mock
    private CreateBidServiceComponent createBidServiceComponent;

    @Mock
    private GetBidServiceComponent getBidServiceComponent;

    @InjectMocks
    private BidServiceImpl bidService;

    private User testUser;

    private Bid testBid;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        Lot testLot = TestDataFactory.createTestLot(testUser);

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);

        testBid = TestDataFactory.createTestBid(testUser, testAuction);
    }

    @Test
    void getAllBids() {

        when(getBidServiceComponent.getAllBids()).thenReturn(List.of(testBid));

        List<Bid> bids = bidService.getAllBids();

        assertEquals(1, bids.size());
        assertEquals(testBid, bids.get(0));
    }

    @Test
    void getBidById() {

        when(getBidServiceComponent.getBidById(testBid.getId())).thenReturn(testBid);

        assertEquals(testBid, bidService.getBidById(testBid.getId()));
    }

    @Test
    void createBid() {

        when(createBidServiceComponent.createBid(testBid, testUser)).thenReturn(testBid);

        assertEquals(testBid, bidService.createBid(testBid, testUser));
    }

    @Test
    void cancelBid() {

        bidService.cancelBid(testBid.getId());

        verify(cancelBidServiceComponent).cancelBid(testBid.getId());
    }

    @Test
    void acceptBid() {

        bidService.acceptBid(testBid.getId());

        verify(updateBidServiceComponent).acceptBid(testBid.getId());
    }

    @Test
    void updateBidsStateToOutdated() {

        bidService.updateBidsStateToOutdated();

        verify(updateBidServiceComponent).updateBidsStateToOutdated();
    }
} 