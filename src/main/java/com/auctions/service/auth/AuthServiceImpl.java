package com.auctions.service.auth;

import com.auctions.mapper.user.UserEntityToUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auctions.persistence.entity.UserEntity;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.security.JwtTokenProvider;
import com.auctions.domain.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

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