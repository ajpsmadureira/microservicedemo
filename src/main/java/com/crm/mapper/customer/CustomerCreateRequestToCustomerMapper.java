package com.crm.mapper.customer;

import com.crm.domain.Customer;
import com.crm.web.api.customer.CustomerCreateRequest;
import com.crm.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreateRequestToCustomerMapper implements Mapper<CustomerCreateRequest, Customer> {

    @Override
    public Customer map(CustomerCreateRequest customerCreateRequest) {

        return Customer.builder()
                .name(customerCreateRequest.getName())
                .surname(customerCreateRequest.getSurname())
                .build();
    }
}
