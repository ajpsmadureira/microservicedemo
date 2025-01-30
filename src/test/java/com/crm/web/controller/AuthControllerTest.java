package com.crm.web.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crm.domain.User;
import com.crm.security.CustomUserDetailsService;
import com.crm.service.AuthService;
import com.crm.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import com.crm.config.TestConfig;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

@WebMvcTest(AuthController.class)
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            testUser.getUsername(),
            testUser.getPassword(),
            authorities
        );

        when(userDetailsService.loadUserByUsername(testUser.getUsername())).thenReturn(userDetails);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {

        String expectedToken = "test.jwt.token";
        when(authService.login(anyString(), anyString())).thenReturn(expectedToken);

        Map<String, String> loginRequest = Map.of(
            "username", testUser.getUsername(),
            "password", testUser.getPassword()
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }
} 