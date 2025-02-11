package com.crm.persistence.repository;

import com.crm.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.persistence.entity.CustomerEntity;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    List<CustomerEntity> findByCreatedBy(UserEntity userEntity);
    List<CustomerEntity> findByLastModifiedBy(UserEntity userEntity);
} 