package com.auctions.service.bid.component;

import com.auctions.domain.bid.Bid;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetBidServiceComponentTest extends BidServiceComponentTest {

    @InjectMocks
    private GetBidServiceComponent getBidServiceComponent;

    @Test
    void getAllBids_whenAllConditionsExist_shouldReturnBids() {

        when(bidRepository.findAll()).thenReturn(List.of(testBidEntity));
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        List<Bid> bids = getBidServiceComponent.getAllBids();

        verify(bidEntityToBidMapper).map(testBidEntity);
        assertEquals(1, bids.size());
        assertEquals(testBid, bids.get(0));
    }

    @Test
    void getBidById_whenAllConditionsExist_shouldReturnBid() {

        when(bidRepository.findById(any())).thenReturn(Optional.of(testBidEntity));
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        assertEquals(testBid, getBidServiceComponent.getBidById(1));

        verify(bidEntityToBidMapper).map(testBidEntity);
    }

    @Test
    void getBidById_whenBidNotFound_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getBidServiceComponent.getBidById(1));

        verify(bidEntityToBidMapper, times(0)).map(any());
    }
}
