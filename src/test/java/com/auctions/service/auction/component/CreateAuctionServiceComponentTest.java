package com.auctions.service.auction.component;

import com.auctions.domain.auction.Auction;
import com.auctions.exception.BusinessException;
import com.auctions.persistence.entity.AuctionEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private CreateAuctionServiceComponent createAuctionServiceComponent;

    @Test
    void createAuction_whenAllConditionsExist_shouldCreateAuction() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        Auction auction = createAuctionServiceComponent.createAuction(testAuction, testUser);

        assertEquals(testAuction, auction);

        ArgumentCaptor<AuctionEntity> auctionEntityCaptor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository).save(auctionEntityCaptor.capture());
        AuctionEntity auctionEntityCaptured = auctionEntityCaptor.getValue();
        assertEquals(testAuction.getStartTime(), auctionEntityCaptured.getStartTime());
        assertEquals(testAuction.getStopTime(), auctionEntityCaptured.getStopTime());
        assertEquals(testAuction.getLotId(), auctionEntityCaptured.getLot().getId());
        assertEquals(testUserEntity, auctionEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, auctionEntityCaptured.getLastModifiedBy());

        verify(auctionEntityToAuctionMapper).map(testAuctionEntity);
    }

    @Test
    void createAuction_whenUserIsUnknown_shouldThrowException() {

        when(userRepository.findById(any())).thenThrow(new BusinessException());

        assertThrows(BusinessException.class, () -> createAuctionServiceComponent.createAuction(testAuction, testUser));

        verify(auctionRepository, times(0)).save(any());
        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }

    @Test
    void createAuction_whenSaveThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createAuctionServiceComponent.createAuction(testAuction, testUser));

        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }

    @Test
    void createAuction_whenMapThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createAuctionServiceComponent.createAuction(testAuction, testUser));
    }
}
