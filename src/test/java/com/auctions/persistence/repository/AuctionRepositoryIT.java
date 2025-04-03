package com.auctions.persistence.repository;

import com.auctions.domain.AuctionState;
import com.auctions.domain.BidState;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrieveAuctionInformation() {

        AuctionEntity auction = getTestAuctionEntity();

        AuctionEntity auctionSaved = auctionRepository.save(auction);

        entityManager.clear();

        AuctionEntity auctionRetrieved = auctionRepository.findById(auctionSaved.getId()).orElseThrow();

        assertEquals(auction.getStartTime(), auctionRetrieved.getStartTime());
        assertEquals(auction.getStopTime(), auctionRetrieved.getStopTime());
        assertEquals(auction.getLot().getId(), auctionRetrieved.getLot().getId());
        assertEquals(AuctionState.ONGOING, auctionRetrieved.getState());
        assertEquals(auction.getCreatedBy().getId(), auctionRetrieved.getCreatedBy().getId());
        assertEquals(auction.getLastModifiedBy().getId(), auctionRetrieved.getLastModifiedBy().getId());
        assertNotNull(auctionRetrieved.getCreatedAt());
        assertNotNull(auctionRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLotIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setLot(null);

        assertThrows(DataIntegrityViolationException.class, () -> auctionRepository.save(auction));
    }

    @Test
    public void shouldNotThrowDataIntegrityViolationExceptionIfStartTimeIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setStartTime(null);

        auctionRepository.save(auction);
    }

    @Test
    public void shouldNotThrowDataIntegrityViolationExceptionIfStopTimeIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setStopTime(null);

        auctionRepository.save(auction);
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfStateIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setState(null);

        assertThrows(DataIntegrityViolationException.class, () -> auctionRepository.save(auction));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfCreatedByIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setCreatedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> auctionRepository.save(auction));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLastModifiedByIsNull() {

        AuctionEntity auction = getTestAuctionEntity();

        auction.setLastModifiedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> auctionRepository.save(auction));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        AuctionEntity auction = getTestAuctionEntity();

        AuctionEntity auctionSaved = auctionRepository.save(auction);

        entityManager.flush();

        AuctionEntity auctionRetrieved = auctionRepository.findById(auctionSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = auctionRetrieved.getUpdatedAt();

        auctionRetrieved.setState(AuctionState.CANCELLED);
        AuctionEntity auctionUpdated = auctionRepository.save(auctionRetrieved);

        entityManager.flush();

        AuctionEntity auctionUpdatedRetrieved = auctionRepository.findById(auctionUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = auctionUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByCreatedBy() {

        AuctionEntity auction = getTestAuctionEntity();

        AuctionEntity auctionSaved = auctionRepository.save(auction);

        assertEquals(1, auctionRepository.findByCreatedBy(auctionSaved.getCreatedBy()).size());
    }

    @Test
    public void shouldFindByLastModifiedBy() {

        AuctionEntity auction = getTestAuctionEntity();

        AuctionEntity auctionSaved = auctionRepository.save(auction);

        assertEquals(1, auctionRepository.findByLastModifiedBy(auctionSaved.getLastModifiedBy()).size());
    }

    @Test
    public void shouldRejectAuctionCreatedBids() {

        AuctionEntity auctionSaved = auctionRepository.save(getTestAuctionEntity());

        BidEntity bidEntityFirst = TestDataFactory.createTestBidEntity(auctionSaved.getCreatedBy(), auctionSaved);
        bidEntityFirst.setState(BidState.CREATED);
        BidEntity savedBidEntityFirst = bidRepository.save(bidEntityFirst);

        BidEntity bidEntitySecond = TestDataFactory.createTestBidEntity(auctionSaved.getCreatedBy(), auctionSaved);
        bidEntitySecond.setState(BidState.CREATED);
        BidEntity savedBidEntitySecond = bidRepository.save(bidEntitySecond);

        auctionRepository.updateAuctionCreatedBidsState(BidState.REJECTED, auctionSaved.getId());

        entityManager.clear();

        bidRepository.findById(savedBidEntityFirst.getId()).map(BidEntity::getState).ifPresent(state -> assertEquals(BidState.REJECTED, state));
        bidRepository.findById(savedBidEntitySecond.getId()).map(BidEntity::getState).ifPresent(state -> assertEquals(BidState.REJECTED, state));
    }

    @Test
    public void shouldNotRejectLotAcceptedAndCancelledBids() {

        AuctionEntity auctionSaved = auctionRepository.save(getTestAuctionEntity());

        BidEntity bidEntityAccepted = TestDataFactory.createTestBidEntity(auctionSaved.getCreatedBy(), auctionSaved);
        bidEntityAccepted.setState(BidState.ACCEPTED);
        BidEntity savedBidEntityAccepted = bidRepository.save(bidEntityAccepted);

        BidEntity bidEntityCancelled = TestDataFactory.createTestBidEntity(auctionSaved.getCreatedBy(), auctionSaved);
        bidEntityCancelled.setState(BidState.CANCELLED);
        BidEntity savedBidEntityCancelled = bidRepository.save(bidEntityCancelled);

        auctionRepository.updateAuctionCreatedBidsState(BidState.REJECTED, auctionSaved.getId());

        entityManager.clear();

        bidRepository.findById(savedBidEntityAccepted.getId()).map(BidEntity::getState).ifPresent(state -> assertEquals(BidState.ACCEPTED, state));
        bidRepository.findById(savedBidEntityCancelled.getId()).map(BidEntity::getState).ifPresent(state -> assertEquals(BidState.CANCELLED, state));
    }

    private AuctionEntity getTestAuctionEntity() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();
        userEntity.setId(null);
        UserEntity userSaved = userRepository.save(userEntity);

        LotEntity lotEntity = TestDataFactory.createTestLotEntity(userEntity);
        lotEntity.setId(null);
        LotEntity lotSaved = lotRepository.save(lotEntity);

        AuctionEntity auctionEntity = TestDataFactory.createTestAuctionEntity(userSaved, lotSaved);
        auctionEntity.setId(null);

        return auctionEntity;
    }
}
