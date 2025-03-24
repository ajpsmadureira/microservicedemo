package com.crm.persistence.repository;

import com.crm.domain.BidState;
import com.crm.persistence.entity.BidEntity;
import com.crm.persistence.entity.LotEntity;
import com.crm.persistence.entity.UserEntity;
import com.crm.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class BidRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private LotRepository lotRepository;

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
        assertEquals(bid.getLot().getId(), bidRetrieved.getLot().getId());
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
    public void shouldThrowDataIntegrityViolationExceptionIfLotIsNull() {

        BidEntity bid = getTestBidEntity();

        bid.setLot(null);

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

    private BidEntity getTestBidEntity() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();

        LotEntity lotEntity = TestDataFactory.createTestLotEntity(userEntity);

        BidEntity bid = TestDataFactory.createTestBidEntity(userEntity, lotEntity);

        UserEntity user = bid.getCreatedBy();
        user.setId(null);
        UserEntity userSaved = userRepository.save(user);

        LotEntity lot = bid.getLot();
        lot.setId(null);
        lot.setCreatedBy(userSaved);
        lot.setLastModifiedBy(userSaved);
        LotEntity lotSaved = lotRepository.save(lot);

        bid.setCreatedBy(userSaved);
        bid.setLastModifiedBy(userSaved);
        bid.setLot(lotSaved);
        bid.setId(null);

        return bid;
    }
}
