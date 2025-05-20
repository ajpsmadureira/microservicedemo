package com.auctions.service.auction.component;

import com.auctions.domain.auction.Auction;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private GetAuctionServiceComponent getAuctionServiceComponent;

    @Test
    void getAllAuctions_whenAllConditionsExist_shouldReturnAuctions() {

        when(auctionRepository.findAll()).thenReturn(List.of(testAuctionEntity));
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        List<Auction> auctions = getAuctionServiceComponent.getAllAuctions();

        verify(auctionEntityToAuctionMapper).map(testAuctionEntity);
        assertEquals(1, auctions.size());
        assertEquals(testAuction, auctions.get(0));
    }

    @Test
    void getAuctionById_whenAllConditionsExist_shouldReturnAuction() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        assertEquals(testAuction, getAuctionServiceComponent.getAuctionById(1));

        verify(auctionEntityToAuctionMapper).map(testAuctionEntity);
    }

    @Test
    void getAuctionById_whenAuctionNotFound_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getAuctionServiceComponent.getAuctionById(1));

        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }
}
