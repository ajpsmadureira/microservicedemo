package com.crm.service;

import com.crm.mapper.user.UserEntityToUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.crm.persistence.entity.UserEntity;
import com.crm.exception.ResourceNotFoundException;
import com.crm.persistence.repository.UserRepository;
import com.crm.security.JwtTokenProvider;
import com.crm.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserEntityToUserMapper userEntityToUserMapper;

    public String login(String username, String password) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return tokenProvider.generateToken(authentication);

        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public User getCurrentUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        return userEntityToUserMapper.map(userEntity);
    }
} 