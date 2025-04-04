package com.auctions.service.auction.component;

import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private DeleteAuctionServiceComponent deleteAuctionServiceComponent;

    @Test
    void deleteAuction_whenAllConditionsExist_shouldDeleteAuction() {

        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        deleteAuctionServiceComponent.deleteAuction(AUCTION_ID);
    }

    @Test
    void deleteAuction_whenAuctionIsUnknown_shouldNotThrowException() {

        when(auctionRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        deleteAuctionServiceComponent.deleteAuction(1);

        verify(auctionRepository, times(0)).deleteById(any());
    }

    @Test
    void deleteAuction_whenRepositoryDeletionThrowsException_shouldThrowException() {

        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        doThrow(new RuntimeException()).when(auctionRepository).deleteById(AUCTION_ID);

        assertThrows(BusinessException.class, () -> deleteAuctionServiceComponent.deleteAuction(AUCTION_ID));
    }
}
