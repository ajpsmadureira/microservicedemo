package com.auctions.persistence.repository;

import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.PaymentEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrievePaymentInformation() {

        PaymentEntity payment = getTestPaymentEntity();

        PaymentEntity paymentSaved = paymentRepository.save(payment);

        entityManager.clear();

        PaymentEntity paymentRetrieved = paymentRepository.findById(paymentSaved.getId()).orElseThrow();

        assertEquals(paymentSaved.getAmount(), paymentRetrieved.getAmount());
        assertEquals(paymentSaved.getLink(), paymentRetrieved.getLink());
        assertEquals(paymentSaved.getState(), paymentRetrieved.getState());
        assertEquals(paymentSaved.getAuction().getId(), paymentRetrieved.getAuction().getId());
        assertEquals(paymentSaved.getCreatedBy().getId(), paymentRetrieved.getCreatedBy().getId());
        assertEquals(paymentSaved.getLastModifiedBy().getId(), paymentRetrieved.getLastModifiedBy().getId());
        assertNotNull(paymentRetrieved.getCreatedAt());
        assertNotNull(paymentRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLinkIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setLink(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfAuctionIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setAuction(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfAmountIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setAmount(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfStateIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setState(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfCreatedByIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setCreatedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLastModifiedByIsNull() {

        PaymentEntity payment = getTestPaymentEntity();

        payment.setLastModifiedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(payment));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        PaymentEntity payment = getTestPaymentEntity();

        PaymentEntity paymentSaved = paymentRepository.save(payment);

        entityManager.flush();

        PaymentEntity paymentRetrieved = paymentRepository.findById(paymentSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = paymentRetrieved.getUpdatedAt();

        paymentRetrieved.setAmount(BigDecimal.valueOf(20));
        PaymentEntity paymentUpdated = paymentRepository.save(paymentRetrieved);

        entityManager.flush();

        PaymentEntity paymentUpdatedRetrieved = paymentRepository.findById(paymentUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = paymentUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByAuctionId() {

        PaymentEntity payment = getTestPaymentEntity();

        PaymentEntity paymentSaved = paymentRepository.save(payment);

        assertEquals(1, paymentRepository.findByAuctionId(paymentSaved.getAuction().getId()).size());
    }

    private PaymentEntity getTestPaymentEntity() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();
        userEntity.setId(null);
        UserEntity userSaved = userRepository.save(userEntity);

        LotEntity lotEntity = TestDataFactory.createTestLotEntity(userSaved);
        lotEntity.setId(null);
        LotEntity lotSaved = lotRepository.save(lotEntity);

        AuctionEntity auctionEntity = TestDataFactory.createTestAuctionEntity(userSaved, lotSaved);
        auctionEntity.setId(null);
        AuctionEntity auctionSaved = auctionRepository.save(auctionEntity);

        PaymentEntity paymentEntity = TestDataFactory.createTestPaymentEntity(userSaved, auctionSaved);
        paymentEntity.setId(null);

        return paymentEntity;
    }
}
