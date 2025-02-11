package com.crm.web.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crm.domain.Customer;
import com.crm.domain.User;
import com.crm.service.AuthService;
import com.crm.service.CustomerService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

@WebMvcTest(CustomerController.class)
@Import(TestConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AuthService authService;

    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        
        testCustomer = TestDataFactory.createTestCustomer(testUser);
    }

    @Test
    @WithMockUser
    void getAllCustomers_ShouldReturnCustomers() throws Exception {

        when(customerService.getAllCustomers()).thenReturn(Collections.singletonList(testCustomer));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testCustomer.getName()))
                .andExpect(jsonPath("$[0].surname").value(testCustomer.getSurname()));
    }

    @Test
    void getAllCustomers_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() throws Exception {

        when(customerService.getCustomerById(1)).thenReturn(testCustomer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testCustomer.getName()))
                .andExpect(jsonPath("$.surname").value(testCustomer.getSurname()));
    }

    @Test
    @WithMockUser
    void createCustomer_WithValidData_ShouldCreateCustomer() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(customerService.createCustomer(any(Customer.class), any(User.class)))
                .thenReturn(testCustomer);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(testCustomer.getName()))
                .andExpect(jsonPath("$.surname").value(testCustomer.getSurname()));
    }

    @Test
    @WithMockUser
    void createCustomer_WithInvalidData_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateCustomer_WithValidData_ShouldUpdateCustomer() throws Exception {

        when(authService.getCurrentUser()).thenReturn(testUser);
        when(customerService.updateCustomerDetails(eq(1), any(Customer.class), any(User.class)))
                .thenReturn(testCustomer);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testCustomer.getName()))
                .andExpect(jsonPath("$.surname").value(testCustomer.getSurname()));
    }

    @Test
    @WithMockUser
    void updateCustomerPhoto_WithValidData_ShouldUpdateCustomerPhoto() throws Exception {

        MockMultipartFile photoFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(authService.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(multipart("/api/customers/1/photo")
                        .file(photoFile))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteCustomer_ShouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk());
    }
} 