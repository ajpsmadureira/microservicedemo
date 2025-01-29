package com.crm.mapper.customer;

import com.crm.domain.Customer;
import com.crm.web.api.customer.CustomerResponse;
import com.crm.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerToCustomerResponseMapper implements Mapper<Customer, CustomerResponse> {

    @Override
    public CustomerResponse map(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .createdById(customer.getCreatedById())
                .lastModifiedById(customer.getLastModifiedById())
                .build();
    }
}
