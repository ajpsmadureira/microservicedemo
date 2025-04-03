package com.auctions.service;

import com.auctions.domain.*;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.bid.BidServiceImpl;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private AuctionRepository auctionRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private BidEntityToBidMapper bidEntityToBidMapper;

    @InjectMocks
    private BidServiceImpl bidService;

    private User testUser;

    private UserEntity testUserEntity;

    private AuctionEntity testAuctionEntity;

    private Bid testBid;

    private BidEntity testBidEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        LotEntity testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);
        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);

        testBid = TestDataFactory.createTestBid(testUser, testAuction);
        testBidEntity = TestDataFactory.createTestBidEntity(testUserEntity, testAuctionEntity);
    }

    @Test
    void getAllBids_whenAllConditionsExist_shouldReturnBids() {

        when(bidRepository.findAll()).thenReturn(List.of(testBidEntity));
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        List<Bid> bids = bidService.getAllBids();

        verify(bidEntityToBidMapper).map(testBidEntity);
        assertEquals(1, bids.size());
        assertEquals(testBid, bids.get(0));
    }

    @Test
    void getBidById_whenAllConditionsExist_shouldReturnBid() {

        when(bidRepository.findById(any())).thenReturn(Optional.of(testBidEntity));
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        assertEquals(testBid, bidService.getBidById(1));

        verify(bidEntityToBidMapper).map(testBidEntity);
    }

    @Test
    void getBidById_whenBidNotFound_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.getBidById(1));

        verify(bidEntityToBidMapper, times(0)).map(any());
    }

    @Test
    void createBid_whenAllConditionsExist_shouldCreateBid() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testAuctionEntity.setState(AuctionState.ONGOING);
        when(auctionRepository.findById(any())).thenReturn(Optional.ofNullable(testAuctionEntity));
        when(bidRepository.save(any())).thenReturn(testBidEntity);
        when(bidEntityToBidMapper.map(any())).thenReturn(testBid);

        var result = bidService.createBid(testBid, testUser);

        ArgumentCaptor<BidEntity> bidEntityCaptor = ArgumentCaptor.forClass(BidEntity.class);
        verify(bidRepository).save(bidEntityCaptor.capture());
        BidEntity bidEntityCaptured = bidEntityCaptor.getValue();
        assertEquals(testBid.getAmount(), testBidEntity.getAmount());
        assertEquals(testBid.getUntil(), testBidEntity.getUntil());
        assertEquals(testAuctionEntity, bidEntityCaptured.getAuction());
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
    void createBid_whenAuctionDoesNotExist_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        when(auctionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void createBid_whenAuctionIsNotOngoing_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));
        testAuctionEntity.setState(AuctionState.CREATED);
        when(auctionRepository.findById(any())).thenReturn(Optional.ofNullable(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> bidService.createBid(testBid, testUser));
    }

    @Test
    void cancelBid_whenBidDoesNotExist_shouldThrowException() {

        when(bidRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bidService.cancelBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenBidIsAlreadyCancelled_shouldReturnWithoutFurtherRepositoryActions() {

        testBidEntity.setState(BidState.CANCELLED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.cancelBid(1);

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void cancelBid_whenBidIsInCreatedState_shouldUpdateState() {

        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.cancelBid(1);

        verify(bidRepository).save(testBidEntity);
        assertEquals(BidState.CANCELLED, testBidEntity.getState());
    }

    @Test
    void cancelBid_whenBidIsNotInCreatedOrCancelledState_shouldThrowBusinessException() {

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
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsAlreadyAccepted_shouldReturnWithoutFurtherRepositoryActions() {

        testBidEntity.setState(BidState.ACCEPTED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.acceptBid(1);

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsInCreatedStateAndNotExpired_shouldUpdateState() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        bidService.acceptBid(1);

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

        assertThrows(InvalidParameterException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenBidIsNotInCreatedOrAcceptedState_shouldThrowBusinessException() {

        testBidEntity.setState(BidState.CANCELLED);
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        assertThrows(InvalidParameterException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenAuctionIsNotOngoing_shouldThrowBusinessException() {

        testBidEntity.getAuction().setState(AuctionState.CREATED);
        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));

        assertThrows(InvalidParameterException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenLotRepositorySaveThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        when(auctionRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
        verify(auctionRepository, times(0)).updateAuctionCreatedBidsState(any(), any());
    }

    @Test
    void acceptBid_whenLotRepositoryRejectLotCreatedBidsThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        doThrow(new RuntimeException()).when(auctionRepository).updateAuctionCreatedBidsState(any(), any());

        assertThrows(BusinessException.class, () -> bidService.acceptBid(1));

        verify(bidRepository, times(0)).save(any());
    }

    @Test
    void acceptBid_whenBidRepositoryThrowsException_shouldThrowBusinessException() {

        testBidEntity.setUntil(now().plus(1, ChronoUnit.MINUTES));
        when(bidRepository.findById(any())).thenReturn(Optional.ofNullable(testBidEntity));
        when(bidRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> bidService.acceptBid(1));
    }
} 