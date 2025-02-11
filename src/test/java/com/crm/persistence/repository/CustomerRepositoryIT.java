package com.crm.persistence.repository;

import com.crm.persistence.entity.CustomerEntity;
import com.crm.persistence.entity.UserEntity;
import com.crm.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerRepositoryIT extends AbstractRepositoryIT {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAndRetrieveCustomerInformation() {

        CustomerEntity customer = getTestCustomerEntity();

        CustomerEntity customerSaved = customerRepository.save(customer);

        CustomerEntity customerRetrieved = customerRepository.findById(customerSaved.getId()).orElseThrow();

        assertEquals(customerRetrieved.getName(), customerSaved.getName());
        assertEquals(customerRetrieved.getSurname(), customerSaved.getSurname());
        assertEquals(customerRetrieved.getPhotoUrl(), customerSaved.getPhotoUrl());
        assertEquals(customerRetrieved.getCreatedBy(), customer.getCreatedBy());
        assertEquals(customerRetrieved.getLastModifiedBy(), customer.getLastModifiedBy());
        assertNotNull(customerRetrieved.getCreatedAt());
        assertNotNull(customerRetrieved.getUpdatedAt());
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfNameIsNull() {

        CustomerEntity customer = getTestCustomerEntity();

        customer.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfSurnameIsNull() {

        CustomerEntity customer = getTestCustomerEntity();

        customer.setSurname(null);

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfCreatedByIsNull() {

        CustomerEntity customer = getTestCustomerEntity();

        customer.setCreatedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void shouldThrowDataIntegrityViolationExceptionIfLastModifiedByIsNull() {

        CustomerEntity customer = getTestCustomerEntity();

        customer.setLastModifiedBy(null);

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customer));
    }

    @Test
    public void shouldUpdateUpdatedAt() {

        CustomerEntity customer = getTestCustomerEntity();

        CustomerEntity customerSaved = customerRepository.save(customer);

        entityManager.flush();

        CustomerEntity customerRetrieved = customerRepository.findById(customerSaved.getId()).orElseThrow();
        Instant initialUpdatedAt = customerRetrieved.getUpdatedAt();

        customerRetrieved.setPhotoUrl("new photo url");
        CustomerEntity customerUpdated = customerRepository.save(customerRetrieved);

        entityManager.flush();

        CustomerEntity customerUpdatedRetrieved = customerRepository.findById(customerUpdated.getId()).orElseThrow();
        Instant finalUpdatedAt = customerUpdatedRetrieved.getUpdatedAt();

        assertTrue(finalUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    public void shouldFindByCreatedBy() {

        CustomerEntity customer = getTestCustomerEntity();

        CustomerEntity customerSaved = customerRepository.save(customer);

        assertEquals(1, customerRepository.findByCreatedBy(customerSaved.getCreatedBy()).size());
    }

    @Test
    public void shouldFindByLastModifiedBy() {

        CustomerEntity customer = getTestCustomerEntity();

        CustomerEntity customerSaved = customerRepository.save(customer);

        assertEquals(1, customerRepository.findByLastModifiedBy(customerSaved.getLastModifiedBy()).size());
    }

    private CustomerEntity getTestCustomerEntity() {

        CustomerEntity customer = TestDataFactory.createTestCustomerEntity();

        UserEntity user = customer.getCreatedBy();
        user.setId(null);

        UserEntity userSaved = userRepository.save(user);

        customer.setCreatedBy(userSaved);
        customer.setLastModifiedBy(userSaved);
        customer.setId(null);

        return customer;
    }
}
