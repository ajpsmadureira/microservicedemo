package com.crm.service;

import com.crm.domain.*;
import com.crm.exception.BusinessException;
import com.crm.mapper.bid.BidEntityToBidMapper;
import com.crm.persistence.entity.BidEntity;
import com.crm.persistence.entity.LotEntity;
import com.crm.persistence.entity.UserEntity;
import com.crm.persistence.repository.BidRepository;
import com.crm.persistence.repository.LotRepository;
import com.crm.persistence.repository.UserRepository;
import com.crm.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private BidEntityToBidMapper bidEntityToBidMapper;

    @InjectMocks
    private BidService bidService;

    private User testUser;

    private UserEntity testUserEntity;

    //private Lot testLot;

    private LotEntity testLotEntity;

    private Bid testBid;

    private BidEntity testBidEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        testBid = TestDataFactory.createTestBid(testUser, testLot);
        testBidEntity = TestDataFactory.createTestBidEntity(testUserEntity, testLotEntity);
    }

    @Test
    void createBid_baseCase_ShouldCreateBid() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testLotEntity.setState(LotState.AUCTIONED);
        when(lotRepository.findById(any())).thenReturn(Optional.ofNullable(testLotEntity));
        when(bidRepository.save(any())).thenReturn(testBidEntity);
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        var result = bidService.createBid(testBid, testUser);

        ArgumentCaptor<BidEntity> bidEntityCaptor = ArgumentCaptor.forClass(BidEntity.class);
        verify(bidRepository).save(bidEntityCaptor.capture());
        BidEntity bidEntityCaptured = bidEntityCaptor.getValue();
        assertEquals(testBid.getAmount(), testBidEntity.getAmount());
        assertEquals(testBid.getUntil(), testBidEntity.getUntil());
        assertEquals(testLotEntity, bidEntityCaptured.getLot());
        assertEquals(BidState.OPENED, bidEntityCaptured.getState());
        assertEquals(testUserEntity, bidEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, bidEntityCaptured.getLastModifiedBy());

        verify(bidEntityToBidMapper).map(testBidEntity);

        assertEquals(testBid, result);
    }

    @Test
    void createBid_WhenUserDoesNotExist_ShouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void createBid_WhenLotDoesNotExist_ShouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void createBid_WhenLotIsNotAuctioned_ShouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testLotEntity.setState(LotState.CREATED);
        when(lotRepository.findById(any())).thenReturn(Optional.ofNullable(testLotEntity));

        assertThrows(BusinessException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void deleteBid_noJpaException_ShouldNotThrowBusinessException() {

        assertDoesNotThrow(() -> bidService.deleteBid(1));

        verify(bidRepository).deleteById(1);
    }

    @Test
    void deleteBid_withJpaException_ShouldThrowBusinessException() {

        doThrow(new RuntimeException()).when(bidRepository).deleteById(1);

        assertThrows(BusinessException.class, () -> bidService.deleteBid(1));
    }
} 