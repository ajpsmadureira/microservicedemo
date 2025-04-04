package com.auctions.service.bid.component;

import com.auctions.domain.BidState;
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

public class CancelBidServiceComponentTest extends BidServiceComponentTest {

    @InjectMocks
    private CancelBidServiceComponent cancelBidServiceComponent;

    @Test
    void cancelBid_whenBidDoesNotExist_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cancelBidServiceComponent.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenBidIsAlreadyCancelled_shouldReturnWithoutFurtherRepositoryActions() {

        testBidEntity.setState(BidState.CANCELLED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        cancelBidServiceComponent.cancelBid(1);

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenBidIsInCreatedState_shouldUpdateState() {

        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        cancelBidServiceComponent.cancelBid(1);

        verify(bidRepository).save(testBidEntity);
        assertEquals(BidState.CANCELLED, testBidEntity.getState());
    }

    @Test
    void cancelBid_whenBidIsNotInCreatedOrCancelledState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> cancelBidServiceComponent.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenUntilHasAlreadyPassed_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.CREATED);
        testBidEntity.setUntil(now().minus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> cancelBidServiceComponent.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }
}
