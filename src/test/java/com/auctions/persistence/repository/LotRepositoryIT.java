package com.auctions.persistence.repository;

import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class LotRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrieveLotInformation() {

        LotEntity lot = getTestLotEntity();

        LotEntity lotSaved = lotRepository.save(lot);

        entityManager.clear();

        LotEntity lotRetrieved = lotRepository.findById(lotSaved.getId()).orElseThrow();

        assertEquals(lotSaved.getName(), lotRetrieved.getName());
        assertEquals(lotSaved.getSurname(), lotRetrieved.getSurname());
        assertEquals(lotSaved.getPhotoUrl(), lotRetrieved.getPhotoUrl());
        assertEquals(lot.getCreatedBy().getId(), lotRetrieved.getCreatedBy().getId());
        assertEquals(lot.getLastModifiedBy().getId(), lotRetrieved.getLastModifiedBy().getId());
        assertNotNull(lotRetrieved.getCreatedAt());
        assertNotNull(lotRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfNameIsNull() {

        LotEntity lot = getTestLotEntity();

        lot.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> lotRepository.save(lot));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfSurnameIsNull() {

        LotEntity lot = getTestLotEntity();

        lot.setSurname(null);

        assertThrows(DataIntegrityViolationException.class, () -> lotRepository.save(lot));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfCreatedByIsNull() {

        LotEntity lot = getTestLotEntity();

        lot.setCreatedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> lotRepository.save(lot));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLastModifiedByIsNull() {

        LotEntity lot = getTestLotEntity();

        lot.setLastModifiedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> lotRepository.save(lot));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        LotEntity lot = getTestLotEntity();

        LotEntity lotSaved = lotRepository.save(lot);

        entityManager.flush();

        LotEntity lotRetrieved = lotRepository.findById(lotSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = lotRetrieved.getUpdatedAt();

        lotRetrieved.setPhotoUrl("new photo url");
        LotEntity lotUpdated = lotRepository.save(lotRetrieved);

        entityManager.flush();

        LotEntity lotUpdatedRetrieved = lotRepository.findById(lotUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = lotUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByCreatedBy() {

        LotEntity lot = getTestLotEntity();

        LotEntity lotSaved = lotRepository.save(lot);

        assertEquals(1, lotRepository.findByCreatedBy(lotSaved.getCreatedBy()).size());
    }

    @Test
    public void shouldFindByLastModifiedBy() {

        LotEntity lot = getTestLotEntity();

        LotEntity lotSaved = lotRepository.save(lot);

        assertEquals(1, lotRepository.findByLastModifiedBy(lotSaved.getLastModifiedBy()).size());
    }

    private LotEntity getTestLotEntity() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();

        LotEntity lotEntity = TestDataFactory.createTestLotEntity(userEntity);

        UserEntity user = lotEntity.getCreatedBy();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        lotEntity.setCreatedBy(userSaved);
        lotEntity.setLastModifiedBy(userSaved);
        lotEntity.setId(null);

        return lotEntity;
    }
}
