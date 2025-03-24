package com.crm.service;

import com.crm.domain.*;
import com.crm.exception.BusinessException;
import com.crm.exception.InvalidParameterException;
import com.crm.exception.ResourceNotFoundException;
import com.crm.mapper.bid.BidEntityToBidMapper;
import com.crm.persistence.entity.BidEntity;
import com.crm.persistence.entity.LotEntity;
import com.crm.persistence.entity.UserEntity;
import com.crm.persistence.repository.BidRepository;
import com.crm.persistence.repository.LotRepository;
import com.crm.persistence.repository.UserRepository;
import com.crm.service.bid.BidServiceImpl;
import com.crm.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.Instant.now;
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
    private BidServiceImpl bidService;

    private User testUser;

    private UserEntity testUserEntity;

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
    void createBid_whenAllConditionsExist_shouldCreateBid() {

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
        assertEquals(BidState.CREATED, bidEntityCaptured.getState());
        assertEquals(testUserEntity, bidEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, bidEntityCaptured.getLastModifiedBy());

        verify(bidEntityToBidMapper).map(testBidEntity);

        assertEquals(testBid, result);
    }

    @Test
    void createBid_whenUserDoesNotExist_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void createBid_whenLotDoesNotExist_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void createBid_whenLotIsNotAuctioned_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testLotEntity.setState(LotState.CREATED);
        when(lotRepository.findById(any())).thenReturn(Optional.ofNullable(testLotEntity));

        assertThrows(InvalidParameterException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void deleteBid_whenNoJpaException_shouldNotThrowBusinessException() {

        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.deleteBid(1);

        verify(bidRepository).deleteById(1);
    }

    @Test
    void deleteBid_whenJpaException_shouldThrowBusinessException() {

        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        doThrow(new RuntimeException()).when(bidRepository).deleteById(1);

        assertThrows(BusinessException.class, () -> bidService.deleteBid(1));
    }

    @Test
    void deleteBid_whenBidIsInAcceptedState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> bidService.deleteBid(1));

        verify(bidRepository, times(0)).deleteById(1);
    }

    @Test
    void cancelBid_whenBidDoesNotExist_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenBidIsInCreatedState_shouldUpdateState() {

        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.cancelBid(1);

        ArgumentCaptor<BidEntity> bidEntityCaptor = ArgumentCaptor.forClass(BidEntity.class);
        verify(bidRepository).save(bidEntityCaptor.capture());
        BidEntity bidEntityCaptured = bidEntityCaptor.getValue();
        assertEquals(testBidEntity.getId(), bidEntityCaptured.getId());
        assertEquals(BidState.CANCELLED, bidEntityCaptured.getState());
    }

    @Test
    void cancelBid_whenBidIsNotInCreatedState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> bidService.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void acceptBid_whenBidDoesNotExist_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void acceptBid_whenBidIsInCreatedStateAndNotExpired_shouldUpdateState() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.acceptBid(1);

        ArgumentCaptor<BidEntity> bidEntityCaptor = ArgumentCaptor.forClass(BidEntity.class);
        verify(bidRepository).save(bidEntityCaptor.capture());
        BidEntity bidEntityCaptured = bidEntityCaptor.getValue();
        assertEquals(testBidEntity.getId(), bidEntityCaptured.getId());
        assertEquals(BidState.ACCEPTED, bidEntityCaptured.getState());
    }

    @Test
    void acceptBid_whenBidIsInCreatedStateAndExpired_shouldThrowException() {

        testBidEntity.setUntil(now().minus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        assertThrows(InvalidParameterException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void acceptBid_whenBidIsNotInCreatedState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
    }
} 