package com.crm.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crm.domain.User;
import com.crm.exception.InvalidParameterException;
import com.crm.exception.ResourceNotFoundException;
import com.crm.mapper.user.UserEntityToUserMapper;
import com.crm.persistence.entity.UserEntity;
import com.crm.persistence.repository.LotRepository;
import com.crm.persistence.repository.UserRepository;
import com.crm.service.user.UserServiceImpl;
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
    private LotRepository lotRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEntityToUserMapper userEntityToUserMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();
    }

    // TODO: add negative scenarios

    @Test
    void getAllUsers_whenAllConditionsExist_shouldReturnAllUsers() {

        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUserEntity));
        when(userEntityToUserMapper.map(any())).thenReturn(testUser);
        
        var result = userService.getAllUsers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {

        when(userRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(userEntityToUserMapper.map(any())).thenReturn(testUser);
        
        var result = userService.getUserById(1);
        
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowException() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void createUser_whenUsernameAndEmailAreUnique_shouldCreateUser() {

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
    void createUser_whenUsernameExists_shouldThrowException() {

        when(userRepository.existsByUsername(any())).thenReturn(true);
        
        assertThrows(InvalidParameterException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_whenEmailExists_shouldThrowException() {

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);
        
        assertThrows(InvalidParameterException.class, () -> userService.createUser(testUser));
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateUser() {

        UserEntity existingUserEntity = TestDataFactory.createTestUserEntity();
        existingUserEntity.setId(1);

        User updatedUser = TestDataFactory.createTestUser();
        ReflectionTestUtils.setField(updatedUser, "username", "newusername");
        ReflectionTestUtils.setField(updatedUser, "email", "newemail@example.com");
        
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUserEntity));
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userEntityToUserMapper.map(any())).thenReturn(updatedUser);
        
        var result = userService.updateUser(1, updatedUser);
        
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void deleteUser_whenUserExistsAndAssociatedLotsDoNotExist_shouldDeleteUser() {

        when(userRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.findByCreatedBy(any())).thenReturn(List.of());
        when(lotRepository.findByLastModifiedBy(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldThrowException() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1));
    }
} 