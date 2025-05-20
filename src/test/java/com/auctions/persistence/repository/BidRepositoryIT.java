package com.auctions.persistence.repository;

import com.auctions.domain.bid.BidState;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;

public class BidRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrieveBidInformation() {

        BidEntity bid = getTestBidEntity();

        BidEntity bidSaved = bidRepository.save(bid);

        entityManager.clear();

        BidEntity bidRetrieved = bidRepository.findById(bidSaved.getId()).orElseThrow();

        assertEquals(bid.getAmount(), bidRetrieved.getAmount());
        assertEquals(BidState.CREATED, bidRetrieved.getState());
        assertEquals(bid.getAuction().getId(), bidRetrieved.getAuction().getId());
        assertEquals(bid.getCreatedBy().getId(), bidRetrieved.getCreatedBy().getId());
        assertEquals(bid.getLastModifiedBy().getId(), bidRetrieved.getLastModifiedBy().getId());
        assertNotNull(bidRetrieved.getUntil());
        assertNotNull(bidRetrieved.getCreatedAt());
        assertNotNull(bidRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfAmountIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setAmount(null);

        assertThrows(DataIntegrityViolationException.class, () -> bidRepository.save(bid));
    }

    @Test
    public void shouldNotThrowExceptionIfUntilIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setUntil(null);

        bidRepository.save(bid);
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfAuctionIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setAuction(null);

        assertThrows(DataIntegrityViolationException.class, () -> bidRepository.save(bid));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfStateIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setState(null);

        assertThrows(DataIntegrityViolationException.class, () -> bidRepository.save(bid));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfCreatedByIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setCreatedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> bidRepository.save(bid));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLastModifiedByIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setLastModifiedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> bidRepository.save(bid));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        BidEntity bid = getTestBidEntity();

        BidEntity bidSaved = bidRepository.save(bid);

        entityManager.flush();

        BidEntity bidRetrieved = bidRepository.findById(bidSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = bidRetrieved.getUpdatedAt();

        bidRetrieved.setAmount(BigDecimal.valueOf(101));
        BidEntity bidUpdated = bidRepository.save(bidRetrieved);

        entityManager.flush();

        BidEntity bidUpdatedRetrieved = bidRepository.findById(bidUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = bidUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByCreatedBy() {

        BidEntity bid = getTestBidEntity();

        BidEntity bidSaved = bidRepository.save(bid);

        assertEquals(1, bidRepository.findByCreatedBy(bidSaved.getCreatedBy()).size());
    }

    @Test
    public void shouldFindByLastModifiedBy() {

        BidEntity bid = getTestBidEntity();

        BidEntity bidSaved = bidRepository.save(bid);

        assertEquals(1, bidRepository.findByLastModifiedBy(bidSaved.getLastModifiedBy()).size());
    }

    @Test
    public void shouldUpdateBidsStateToOutdated() {

        Instant now = now();
        Instant instantInThePast = now.minus(1, ChronoUnit.MINUTES);
        Instant instantInTheFuture = now.plus(1, ChronoUnit.MINUTES);

        saveBidWithBidStateAndUntil(BidState.CREATED, null);
        Integer bidIdToBeUpdated = saveBidWithBidStateAndUntil(BidState.CREATED, instantInThePast);
        saveBidWithBidStateAndUntil(BidState.CREATED, instantInTheFuture);
        saveBidWithBidStateAndUntil(BidState.OUTDATED, instantInThePast);
        saveBidWithBidStateAndUntil(BidState.CANCELLED, instantInThePast);
        saveBidWithBidStateAndUntil(BidState.ACCEPTED, instantInThePast);
        saveBidWithBidStateAndUntil(BidState.REJECTED, instantInThePast);

        assertEquals(1, bidRepository.updateBidsStateToOutdated());

        entityManager.clear();

        Optional<BidEntity> bidEntity = bidRepository.findById(bidIdToBeUpdated);
        assertTrue(bidEntity.isPresent());
        assertEquals(BidState.OUTDATED, bidEntity.get().getState());
    }

    private Integer saveBidWithBidStateAndUntil(BidState bidState, Instant until) {

        BidEntity bidWithOutdatedState = getTestBidEntity();
        bidWithOutdatedState.setUntil(until);
        bidWithOutdatedState.setState(bidState);
        return bidRepository.save(bidWithOutdatedState).getId();
    }

    private BidEntity getTestBidEntity() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();

        LotEntity lotEntity = TestDataFactory.createTestLotEntity(userEntity);

        AuctionEntity auctionEntity = TestDataFactory.createTestAuctionEntity(userEntity, lotEntity);

        BidEntity bid = TestDataFactory.createTestBidEntity(userEntity, auctionEntity);

        UserEntity user = bid.getCreatedBy();
        user.setId(null);
        UserEntity userSaved = userRepository.save(user);

        lotEntity.setId(null);
        lotEntity.setCreatedBy(userSaved);
        lotEntity.setLastModifiedBy(userSaved);
        LotEntity lotSaved = lotRepository.save(lotEntity);

        AuctionEntity auction = bid.getAuction();
        auction.setId(null);
        auction.setLot(lotSaved);
        auction.setCreatedBy(userSaved);
        auction.setLastModifiedBy(userSaved);
        AuctionEntity auctionSaved = auctionRepository.save(auction);

        bid.setCreatedBy(userSaved);
        bid.setLastModifiedBy(userSaved);
        bid.setAuction(auctionSaved);
        bid.setId(null);

        return bid;
    }
}
