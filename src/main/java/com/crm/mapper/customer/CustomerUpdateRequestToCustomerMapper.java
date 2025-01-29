package com.crm.mapper.customer;

import com.crm.domain.Customer;
import com.crm.mapper.Mapper;
import com.crm.web.api.customer.CustomerUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomerUpdateRequestToCustomerMapper implements Mapper<CustomerUpdateRequest, Customer> {

    @Override
    public Customer map(CustomerUpdateRequest customerUpdateRequest) {

        return Customer.builder()
                .name(customerUpdateRequest.getName())
                .surname(customerUpdateRequest.getSurname())
                .build();
    }
}
