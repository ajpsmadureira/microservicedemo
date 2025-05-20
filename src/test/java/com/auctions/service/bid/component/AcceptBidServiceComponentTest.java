package com.auctions.service.bid.component;

import com.auctions.domain.auction.AuctionState;
import com.auctions.domain.bid.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AcceptBidServiceComponentTest extends BidServiceComponentTest {

    @InjectMocks
    private AcceptBidServiceComponent acceptBidServiceComponent;

    @Test
    void acceptBid_whenBidDoesNotExist_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsAlreadyAccepted_shouldReturnWithoutFurtherRepositoryActions() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        acceptBidServiceComponent.acceptBid(1);

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsInCreatedStateAndNotExpired_shouldUpdateState() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        acceptBidServiceComponent.acceptBid(1);

        verify(bidRepository).save(testBidEntity);
        assertEquals(BidState.ACCEPTED, testBidEntity.getState());

        verify(auctionRepository).save(testAuctionEntity);

        assertEquals(AuctionState.CLOSED, testAuctionEntity.getState());

        verify(auctionRepository).updateAuctionCreatedBidsState(BidState.REJECTED, testAuctionEntity.getId());
    }

    @Test
    void acceptBid_whenBidIsInCreatedStateAndExpired_shouldThrowException() {

        testBidEntity.setUntil(now().minus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        assertThrows(InvalidParameterException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsNotInCreatedOrAcceptedState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.CANCELLED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenAuctionIsNotOngoing_shouldThrowBusinessException() {

        testBidEntity.getAuction().setState(AuctionState.CREATED);
        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        assertThrows(InvalidParameterException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenLotRepositorySaveThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        when(auctionRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenLotRepositoryRejectLotCreatedBidsThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        doThrow(new RuntimeException()).when(auctionRepository).updateAuctionCreatedBidsState(any(), any());

        assertThrows(BusinessException.class, () -> acceptBidServiceComponent.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void acceptBid_whenBidRepositoryThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        when(bidRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> acceptBidServiceComponent.acceptBid(1));
    }
}
