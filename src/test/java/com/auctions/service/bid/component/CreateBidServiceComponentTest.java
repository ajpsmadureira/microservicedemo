package com.auctions.service.bid.component;

import com.auctions.domain.auction.AuctionState;
import com.auctions.domain.bid.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.entity.BidEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateBidServiceComponentTest extends BidServiceComponentTest {

    @InjectMocks
    private CreateBidServiceComponent createBidServiceComponent;

    @Test
    void createBid_whenAllConditionsExist_shouldCreateBid() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testAuctionEntity.setState(AuctionState.ONGOING);
        when(auctionRepository.findById(any())).thenReturn(Optional.ofNullable(testAuctionEntity));
        when(bidRepository.save(any())).thenReturn(testBidEntity);
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        var result = createBidServiceComponent.createBid(testBid, testUser);

        ArgumentCaptor<BidEntity> bidEntityCaptor = ArgumentCaptor.forClass(BidEntity.class);
        verify(bidRepository).save(bidEntityCaptor.capture());
        BidEntity bidEntityCaptured = bidEntityCaptor.getValue();
        assertEquals(testBid.getAmount(), testBidEntity.getAmount());
        assertTrue(testBid.getUntil().equals(testBidEntity.getUntil()));
        assertEquals(testAuctionEntity, bidEntityCaptured.getAuction());
        assertEquals(BidState.CREATED, bidEntityCaptured.getState());
        assertEquals(testUserEntity, bidEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, bidEntityCaptured.getLastModifiedBy());

        verify(bidEntityToBidMapper).map(testBidEntity);

        assertEquals(testBid, result);
    }

    @Test
    void createBid_whenUserDoesNotExist_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> createBidServiceComponent.createBid(testBid, testUser));
    }

    @Test
    void createBid_whenAuctionDoesNotExist_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        when(auctionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createBidServiceComponent.createBid(testBid, testUser));
    }

    @Test
    void createBid_whenAuctionIsNotOngoing_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testAuctionEntity.setState(AuctionState.CREATED);
        when(auctionRepository.findById(any())).thenReturn(Optional.ofNullable(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> createBidServiceComponent.createBid(testBid, testUser));
    }
}
