package com.auctions.service.auction.component;

import com.auctions.domain.AuctionState;
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

public class StartAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private StartAuctionServiceComponent startAuctionServiceComponent;

    @Test
    void startAuction_whenAllConditionsExist_shouldStartAuction() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        startAuctionServiceComponent.startAuction(AUCTION_ID);

        ArgumentCaptor<AuctionEntity> auctionEntityCaptor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository).save(auctionEntityCaptor.capture());
        AuctionEntity auctionEntityCaptured = auctionEntityCaptor.getValue();
        assertNotNull(auctionEntityCaptured.getStartTime());
        assertEquals(AuctionState.ONGOING, auctionEntityCaptured.getState());
    }

    @Test
    void startAuction_whenAuctionIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(AUCTION_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> startAuctionServiceComponent.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsAlreadyStarted_shouldSimplyReturn() {

        testAuctionEntity.setState(AuctionState.ONGOING);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        startAuctionServiceComponent.startAuction(AUCTION_ID);

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsCancelled_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CANCELLED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> startAuctionServiceComponent.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsClosed_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CLOSED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> startAuctionServiceComponent.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionHasStopped_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(now().minus(1, ChronoUnit.MINUTES));
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> startAuctionServiceComponent.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenRepositorySaveThrowsException_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        doThrow(new RuntimeException()).when(auctionRepository).save(testAuctionEntity);

        assertThrows(BusinessException.class, () -> startAuctionServiceComponent.startAuction(AUCTION_ID));
    }
}
