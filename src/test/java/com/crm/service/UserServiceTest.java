package com.crm.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crm.domain.User;
import com.crm.exception.BusinessException;
import com.crm.exception.ResourceNotFoundException;
import com.crm.mapper.user.UserEntityToUserMapper;
import com.crm.persistence.entity.UserEntity;
import com.crm.persistence.repository.CustomerRepository;
import com.crm.persistence.repository.UserRepository;
import com.crm.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEntityToUserMapper userEntityToUserMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();
    }

    // TODO: add negative scenarios

    @Test
    void getAllUsers_ShouldReturnAllUsers() {

        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUserEntity));
        when(userEntityToUserMapper.map(any())).thenReturn(testUser);
        
        var result = userService.getAllUsers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userEntityToUserMapper.map(any())).thenReturn(testUser);
        
        var result = userService.getUserById(1L);
        
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void createUser_WhenUsernameAndEmailAreUnique_ShouldCreateUser() {

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(testUserEntity);
        when(userEntityToUserMapper.map(any())).thenReturn(testUser);
        
        var result = userService.createUser(testUser);
        
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(passwordEncoder).encode(testUser.getPassword());
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrowException() {

        when(userRepository.existsByUsername(any())).thenReturn(true);
        
        assertThrows(BusinessException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);
        
        assertThrows(BusinessException.class, () -> userService.createUser(testUser));
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {

        UserEntity existingUserEntity = TestDataFactory.createTestUserEntity();
        existingUserEntity.setId(1L);

        User updatedUser = TestDataFactory.createTestUser();
        ReflectionTestUtils.setField(updatedUser, "username", "newusername");
        ReflectionTestUtils.setField(updatedUser, "email", "newemail@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserEntity));
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userEntityToUserMapper.map(any())).thenReturn(updatedUser);
        
        var result = userService.updateUser(1L, updatedUser);
        
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void deleteUser_WhenUserExistsAndAssociatedCustomersDoNotExist_ShouldDeleteUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(customerRepository.findByCreatedBy(any())).thenReturn(List.of());
        when(customerRepository.findByLastModifiedBy(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void toggleAdminStatus_ShouldToggleAdminStatus() {

        UserEntity userEntity = TestDataFactory.createTestUserEntity();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any())).thenAnswer(invocation -> {
            assertTrue(((UserEntity) invocation.getArgument(0)).isAdmin());
            return invocation.getArgument(0);
        });
        
        userService.toggleAdminStatus(1L);
    }
} 