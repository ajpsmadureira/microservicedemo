package com.auctions.web.controller;

import com.auctions.config.SecurityConfig;
import com.auctions.mapper.lot.*;
import com.auctions.mapper.user.UserCreateRequestToUserMapper;
import com.auctions.mapper.user.UserToUserResponseMapper;
import com.auctions.mapper.user.UserUpdateRequestToUserMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import com.auctions.security.CustomUserDetailsService;
import com.auctions.security.JwtAuthenticationFilter;
import com.auctions.security.JwtTokenProvider;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Import(SecurityConfig.class)
public class TestControllerConfig {

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

    @Bean
    public BidCreateRequestToBidMapper bidCreateRequestToBidMapper() {

        return new BidCreateRequestToBidMapper();
    }

    @Bean
    public BidToBidResponseMapper bidToBidResponseMapper() {

        return new BidToBidResponseMapper();
    }
} 