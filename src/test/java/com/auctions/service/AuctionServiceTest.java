package com.auctions.service;

import com.auctions.domain.Auction;
import com.auctions.domain.AuctionState;
import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.auction.AuctionServiceImpl;
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
public class AuctionServiceTest {

    private static final Integer AUCTION_ID = 1;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionEntityToAuctionMapper auctionEntityToAuctionMapper;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private Auction testAuction;

    private AuctionEntity testAuctionEntity;

    private UserEntity testUserEntity;

    private User testUser;

    private LotEntity testLotEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        testAuction = TestDataFactory.createTestAuction(testUser, testLot);
        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);
    }

    @Test
    void getAllAuctions_whenAllConditionsExist_shouldReturnAuctions() {

        when(auctionRepository.findAll()).thenReturn(List.of(testAuctionEntity));
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        List<Auction> auctions = auctionService.getAllAuctions();

        verify(auctionEntityToAuctionMapper).map(testAuctionEntity);
        assertEquals(1, auctions.size());
        assertEquals(testAuction, auctions.get(0));
    }

    @Test
    void getAuctionById_whenAllConditionsExist_shouldReturnAuction() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        assertEquals(testAuction, auctionService.getAuctionById(1));

        verify(auctionEntityToAuctionMapper).map(testAuctionEntity);
    }

    @Test
    void getAuctionById_whenAuctionNotFound_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> auctionService.getAuctionById(1));

        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }

    @Test
    void createAuction_whenAllConditionsExist_shouldCreateAuction() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(any())).thenReturn(testAuction);

        Auction auction = auctionService.createAuction(testAuction, testUser);

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

        assertThrows(BusinessException.class, () -> auctionService.createAuction(testAuction, testUser));

        verify(auctionRepository, times(0)).save(any());
        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }

    @Test
    void createAuction_whenSaveThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> auctionService.createAuction(testAuction, testUser));

        verify(auctionEntityToAuctionMapper, times(0)).map(any());
    }

    @Test
    void createAuction_whenMapThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(auctionRepository.save(any())).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> auctionService.createAuction(testAuction, testUser));
    }

    @Test
    void updateAuctionDetails_whenAllConditionsExist_shouldUpdateAuctionDetails() {

        final Integer CREATED_BY = 1;
        final Integer MODIFIED_BY = 2;

        when(auctionRepository.findById(CREATED_BY)).thenReturn(Optional.of(testAuctionEntity));
        User newUser = User.builder().id(MODIFIED_BY).build();
        UserEntity newUserEntity = new UserEntity();
        when(userRepository.findById(MODIFIED_BY)).thenReturn(Optional.of(newUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(testAuctionEntity)).thenReturn(testAuction);

        Auction.AuctionBuilder actionBuilder = testAuction.toBuilder();
        actionBuilder.startTime(now());
        actionBuilder.stopTime(now());
        Auction updatedTestAuction = actionBuilder.build();

        Auction auction = auctionService.updateAuctionDetails(CREATED_BY, updatedTestAuction, newUser);

        assertEquals(testAuction, auction);

        ArgumentCaptor<AuctionEntity> auctionEntityCaptor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository).save(auctionEntityCaptor.capture());
        AuctionEntity auctionEntityCaptured = auctionEntityCaptor.getValue();
        assertEquals(updatedTestAuction.getStartTime(), auctionEntityCaptured.getStartTime());
        assertEquals(updatedTestAuction.getStopTime(), auctionEntityCaptured.getStopTime());
        assertEquals(testAuctionEntity.getCreatedBy(), auctionEntityCaptured.getCreatedBy());
        assertEquals(newUserEntity, auctionEntityCaptured.getLastModifiedBy());
    }

    @Test
    void updateAuctionDetails_whenAuctionIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> auctionService.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void updateAuctionDetails_whenUserIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> auctionService.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void updateAuctionDetails_whenSaveThrowsException_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> auctionService.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void updateLotDetails_whenMapThrowsException_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(testAuctionEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> auctionService.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void deleteAuction_whenAllConditionsExist_shouldDeleteAuction() {

        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        auctionService.deleteAuction(AUCTION_ID);
    }

    @Test
    void deleteAuction_whenAuctionIsUnknown_shouldNotThrowException() {

        when(auctionRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        auctionService.deleteAuction(1);

        verify(auctionRepository, times(0)).deleteById(any());
    }

    @Test
    void deleteAuction_whenRepositoryDeletionThrowsException_shouldThrowException() {

        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        doThrow(new RuntimeException()).when(auctionRepository).deleteById(AUCTION_ID);

        assertThrows(BusinessException.class, () -> auctionService.deleteAuction(AUCTION_ID));
    }

    @Test
    void startAuction_whenAllConditionsExist_shouldStartAuction() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        auctionService.startAuction(AUCTION_ID);

        ArgumentCaptor<AuctionEntity> auctionEntityCaptor = ArgumentCaptor.forClass(AuctionEntity.class);
        verify(auctionRepository).save(auctionEntityCaptor.capture());
        AuctionEntity auctionEntityCaptured = auctionEntityCaptor.getValue();
        assertNotNull(auctionEntityCaptured.getStartTime());
        assertEquals(AuctionState.ONGOING, auctionEntityCaptured.getState());
    }

    @Test
    void startAuction_whenAuctionIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(AUCTION_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> auctionService.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsAlreadyStarted_shouldSimplyReturn() {

        testAuctionEntity.setState(AuctionState.ONGOING);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        auctionService.startAuction(AUCTION_ID);

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsCancelled_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CANCELLED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> auctionService.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionIsClosed_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CLOSED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> auctionService.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenAuctionHasStopped_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(now().minus(1, ChronoUnit.MINUTES));
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));

        assertThrows(InvalidParameterException.class, () -> auctionService.startAuction(AUCTION_ID));

        verify(auctionRepository, times(0)).save(any());
    }

    @Test
    void startAuction_whenRepositorySaveThrowsException_shouldThrowException() {

        testAuctionEntity.setState(AuctionState.CREATED);
        testAuctionEntity.setStopTime(null);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        doThrow(new RuntimeException()).when(auctionRepository).save(testAuctionEntity);

        assertThrows(BusinessException.class, () -> auctionService.startAuction(AUCTION_ID));
    }
}
