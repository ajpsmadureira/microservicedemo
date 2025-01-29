package com.crm.config;

import com.crm.mapper.customer.CustomerCreateRequestToCustomerMapper;
import com.crm.mapper.customer.CustomerToCustomerResponseMapper;
import com.crm.mapper.customer.CustomerUpdateRequestToCustomerMapper;
import com.crm.mapper.user.UserCreateRequestToUserMapper;
import com.crm.mapper.user.UserToUserResponseMapper;
import com.crm.mapper.user.UserUpdateRequestToUserMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.crm.security.CustomUserDetailsService;
import com.crm.security.JwtAuthenticationFilter;
import com.crm.security.JwtTokenProvider;

@TestConfiguration
@Import(SecurityConfig.class)
public class TestConfig {
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockBean
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {

        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public UserToUserResponseMapper userToUserResponseMapper() {

        return new UserToUserResponseMapper();
    }

    @Bean
    public UserCreateRequestToUserMapper userCreateRequestToUserMapper() {

        return new UserCreateRequestToUserMapper();
    }

    @Bean
    public UserUpdateRequestToUserMapper userUpdateRequestToUserMapper() {

        return new UserUpdateRequestToUserMapper();
    }

    @Bean
    public CustomerCreateRequestToCustomerMapper customerCreateRequestToCustomerMapper() {

        return new CustomerCreateRequestToCustomerMapper();
    }

    @Bean
    public CustomerUpdateRequestToCustomerMapper customerUpdateRequestToCustomerMapper() {

        return new CustomerUpdateRequestToCustomerMapper();
    }

    @Bean
    public CustomerToCustomerResponseMapper customerToCustomerResponseMapper() {

        return new CustomerToCustomerResponseMapper();
    }
} 