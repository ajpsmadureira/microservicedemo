package com.auctions.service.auction.component;

import com.auctions.domain.AuctionState;
import com.auctions.domain.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.entity.AuctionEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CancelAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private CancelAuctionServiceComponent cancelAuctionServiceComponent;

    @Test
    void cancelAuction_whenAllConditionsExist_shouldCancelAuction() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setId(AUCTION_ID);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        cancelAuctionServiceComponent.cancelAuction(AUCTION_ID);

        ArgumentCaptor<AuctionEntity> auctionEntityCaptor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository).save(auctionEntityCaptor.capture());
        AuctionEntity auctionEntityCaptured = auctionEntityCaptor.getValue();
        assertNotNull(auctionEntityCaptured.getStartTime());
        assertEquals(AuctionState.CANCELLED, auctionEntityCaptured.getState());

        verify(auctionRepository).updateAuctionCreatedBidsState(BidState.CANCELLED, AUCTION_ID);
    }

    @Test
    void cancelAuction_whenAuctionIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(AUCTION_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> cancelAuctionServiceComponent.cancelAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void cancelAuction_whenAuctionIsAlreadyCancelled_shouldSimplyReturn() {

        testAuctionEntity.setState(AuctionState.CANCELLED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        cancelAuctionServiceComponent.cancelAuction(AUCTION_ID);

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void cancelAuction_whenAuctionIsClosed_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CLOSED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> cancelAuctionServiceComponent.cancelAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void cancelAuction_whenAuctionHasStopped_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.ONGOING);
        testAuctionEntity.setStopTime(now().minus(1, ChronoUnit.MINUTES));
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> cancelAuctionServiceComponent.cancelAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void cancelAuction_whenRepositorySaveThrowsException_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.ONGOING);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        doThrow(new RuntimeException()).when(auctionRepository).save(testAuctionEntity);

        assertThrows(BusinessException.class, () -> cancelAuctionServiceComponent.cancelAuction(AUCTION_ID));
    }
}
