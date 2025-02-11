package com.crm.persistence.repository;

import com.crm.persistence.entity.UserEntity;
import com.crm.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrieveUserInformation() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        UserEntity userRetrieved = userRepository.findById(userSaved.getId()).orElseThrow();

        assertEquals(user.getUsername(), userRetrieved.getUsername());
        assertEquals(user.getPassword(), userRetrieved.getPassword());
        assertEquals(user.getEmail(), userRetrieved.getEmail());
        assertEquals(user.isAdmin(), userRetrieved.isAdmin());
        assertEquals(user.isActive(), userRetrieved.isActive());
        assertNotNull(userRetrieved.getCreatedAt());
        assertNotNull(userRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfEmailIsNull() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);
        user.setEmail(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfPasswordIsNull() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);
        user.setPassword(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfUsernameIsNull() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);
        user.setUsername(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        entityManager.flush();

        UserEntity userRetrieved = userRepository.findById(userSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = userRetrieved.getUpdatedAt();

        userRetrieved.setPassword("new password");
        UserEntity userUpdated = userRepository.save(userRetrieved);

        entityManager.flush();

        UserEntity userUpdatedRetrieved = userRepository.findById(userUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = userUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByUsername() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        assertTrue(userRepository.findByUsername(userSaved.getUsername()).isPresent());
    }

    @Test
    public void shouldCheckExistenceByUsername() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        assertTrue(userRepository.existsByUsername(userSaved.getUsername()));
    }

    @Test
    public void shouldCheckExistenceByEmail() {

        UserEntity user = TestDataFactory.createTestUserEntity();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        assertTrue(userRepository.existsByEmail(userSaved.getEmail()));
    }
}
