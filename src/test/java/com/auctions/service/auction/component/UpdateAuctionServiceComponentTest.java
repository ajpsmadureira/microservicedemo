package com.auctions.service.auction.component;

import com.auctions.domain.Auction;
import com.auctions.domain.AuctionState;
import com.auctions.domain.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateAuctionServiceComponentTest extends AuctionServiceComponentTest {

    @InjectMocks
    private UpdateAuctionServiceComponent updateAuctionServiceComponent;

    @Test
    void updateAuctionDetails_whenAllConditionsExist_shouldUpdateAuctionDetails() {

        final Integer CREATED_BY = 1;
        final Integer MODIFIED_BY = 2;

        testAuctionEntity.setState(AuctionState.CREATED);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        User newUser = User.builder().id(MODIFIED_BY).build();
        UserEntity newUserEntity = new UserEntity();
        when(userRepository.findById(MODIFIED_BY)).thenReturn(Optional.of(newUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(testAuctionEntity)).thenReturn(testAuction);

        Auction.AuctionBuilder actionBuilder = testAuction.toBuilder();
        Instant now = now();
        actionBuilder.startTime(now.plus(1, ChronoUnit.MINUTES));
        actionBuilder.stopTime(now.plus(2, ChronoUnit.MINUTES));
        Auction updatedTestAuction = actionBuilder.build();

        Auction auction = updateAuctionServiceComponent.updateAuctionDetails(CREATED_BY, updatedTestAuction, newUser);

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

        assertThrows(ResourceNotFoundException.class, () -> updateAuctionServiceComponent.updateAuctionDetails(1, testAuction, testUser));
    }

    static Stream<Arguments> startTimeStopTimeStateScenarios() {

        Instant now = now();

        return Stream.of(
                Arguments.of("updateStartTimeWhenAuctionStateIsNotCreated",
                        now.plus(1, ChronoUnit.MINUTES),
                        now.plus(2, ChronoUnit.MINUTES),
                        AuctionState.ONGOING,
                        String.format("Auction start time cannot be updated because state is: %s", AuctionState.ONGOING)
                ),
                Arguments.of("updateStopTimeWhenAuctionStateIsClosed",
                        now.plus(1, ChronoUnit.MINUTES),
                        now.plus(2, ChronoUnit.MINUTES),
                        AuctionState.CLOSED,
                        String.format("Auction start time cannot be updated because state is: %s", AuctionState.CLOSED)
                ),
                Arguments.of("whenStartTimeIsEqualOrLaterThanStopTime",
                        now.plus(1, ChronoUnit.MINUTES),
                        now.plus(1, ChronoUnit.MINUTES),
                        AuctionState.CREATED,
                        "Auction start time needs to be before stop time."
                ),
                Arguments.of("whenStartTimeIsInThePast",
                        now,
                        now.plus(2, ChronoUnit.MINUTES),
                        AuctionState.CREATED,
                        String.format("Auction desired start time is in the past: %s", now)
                ),
                Arguments.of("whenStopTimeIsInThePast",
                        now.plus(1, ChronoUnit.MINUTES),
                        now,
                        AuctionState.CREATED,
                        String.format("Auction desired stop time is in the past: %s", now)
                ));
    }

    @ParameterizedTest
    @MethodSource("startTimeStopTimeStateScenarios")
    void updateAuctionDetails_startTimeStopTimeStateScenarios_shouldThrowException(
            String scenario,
            Instant startTime,
            Instant stopTime,
            AuctionState auctionState,
            String expectedErrorMessage) {

        final Integer CREATED_BY = 1;
        final Integer MODIFIED_BY = 2;

        testAuctionEntity.setState(auctionState);
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuctionEntity));
        User newUser = User.builder().id(MODIFIED_BY).build();

        Auction.AuctionBuilder actionBuilder = testAuction.toBuilder();
        actionBuilder.startTime(startTime);
        actionBuilder.stopTime(stopTime);
        Auction updatedTestAuction = actionBuilder.build();

        InvalidParameterException invalidParameterException = assertThrows(InvalidParameterException.class, () -> updateAuctionServiceComponent.updateAuctionDetails(CREATED_BY, updatedTestAuction, newUser));

        assertEquals(expectedErrorMessage, invalidParameterException.getMessage());
    }

    @Test
    void updateAuctionDetails_whenUserIsUnknown_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> updateAuctionServiceComponent.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void updateAuctionDetails_whenSaveThrowsException_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> updateAuctionServiceComponent.updateAuctionDetails(1, testAuction, testUser));
    }

    @Test
    void updateLotDetails_whenMapThrowsException_shouldThrowException() {

        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.save(testAuctionEntity)).thenReturn(testAuctionEntity);
        when(auctionEntityToAuctionMapper.map(testAuctionEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> updateAuctionServiceComponent.updateAuctionDetails(1, testAuction, testUser));
    }
}
