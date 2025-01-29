package com.crm.mapper.customer;

import com.crm.domain.Customer;
import com.crm.domain.User;
import com.crm.persistence.entity.CustomerEntity;
import com.crm.mapper.Mapper;
import com.crm.mapper.user.UserEntityToUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerEntityToCustomerMapper implements Mapper<CustomerEntity, Customer> {

    private final UserEntityToUserMapper userEntityToUserMapper;

    @Override
    public Customer map(CustomerEntity customerEntity) {

        return Customer.builder()
                .id(customerEntity.getId())
                .name(customerEntity.getName())
                .surname(customerEntity.getSurname())
                .createdById(getCreatedById(customerEntity))
                .lastModifiedById(getLastModifiedById(customerEntity))
                .build();
    }

    private Long getCreatedById(CustomerEntity customerEntity) {

        return Optional.ofNullable(customerEntity)
                .map(CustomerEntity::getCreatedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Long getLastModifiedById(CustomerEntity customerEntity) {

        return Optional.ofNullable(customerEntity)
                .map(CustomerEntity::getLastModifiedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }
}
