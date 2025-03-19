package com.crm.config;

import com.crm.mapper.lot.LotCreateRequestToLotMapper;
import com.crm.mapper.lot.LotToLotResponseMapper;
import com.crm.mapper.lot.LotUpdateRequestToLotMapper;
import com.crm.mapper.user.UserCreateRequestToUserMapper;
import com.crm.mapper.user.UserToUserResponseMapper;
import com.crm.mapper.user.UserUpdateRequestToUserMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.crm.security.CustomUserDetailsService;
import com.crm.security.JwtAuthenticationFilter;
import com.crm.security.JwtTokenProvider;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Import(SecurityConfig.class)
public class TestConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {

        return mock(JwtTokenProvider.class);
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {

        return mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {

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
    public LotCreateRequestToLotMapper lotCreateRequestToLotMapper() {

        return new LotCreateRequestToLotMapper();
    }

    @Bean
    public LotUpdateRequestToLotMapper lotUpdateRequestToLotMapper() {

        return new LotUpdateRequestToLotMapper();
    }

    @Bean
    public LotToLotResponseMapper lotToLotResponseMapper() {

        return new LotToLotResponseMapper();
    }
} 