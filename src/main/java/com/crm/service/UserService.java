package com.crm.service;

import java.util.List;

import com.crm.domain.User;
import com.crm.persistence.entity.UserEntity;
import com.crm.mapper.user.UserEntityToUserMapper;
import com.crm.persistence.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crm.exception.BusinessException;
import com.crm.exception.ResourceNotFoundException;
import com.crm.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityToUserMapper userEntityToUserMapper;

    public List<User> getAllUsers() {

        return userRepository.findAll()
                .stream().map(userEntityToUserMapper::map)
                .toList();
    }

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .map(userEntityToUserMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User createUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) throw new BusinessException("Username already exists: " + user.getUsername());

        if (userRepository.existsByEmail(user.getEmail())) throw new BusinessException("Email already exists: " + user.getEmail());

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(user.getUsername());
        userEntity.setEmail(user.getEmail());
        userEntity.setAdmin(user.getIsAdmin());
        userEntity.setActive(true);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity createdUserEntity = userRepository.save(userEntity);

        return userEntityToUserMapper.map(createdUserEntity);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {

        UserEntity userEntity = findById(id);

        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            if (!userDetails.getUsername().equals(userEntity.getUsername()) &&
                    userRepository.existsByUsername(userDetails.getUsername())) {
                throw new BusinessException("Username already exists: " + userDetails.getUsername());
            } else {
                userEntity.setUsername(userDetails.getUsername());
            }
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            if (!userDetails.getEmail().equals(userEntity.getEmail()) &&
                    userRepository.existsByEmail(userDetails.getEmail())) {
                throw new BusinessException("Email already exists: " + userDetails.getEmail());
            } else {
                userEntity.setEmail(userDetails.getEmail());
            }
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getIsAdmin() != null) userEntity.setAdmin(userDetails.getIsAdmin());

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        
        return userEntityToUserMapper.map(updatedUserEntity);
    }

    @Transactional
    public void deleteUser(Long id) {

        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!customerRepository.findByCreatedBy(userEntity).isEmpty() || !customerRepository.findByLastModifiedBy(userEntity).isEmpty()) {

            throw new BusinessException("User has customers associated with it; please delete these customers first.");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public User toggleAdminStatus(Long id) {

        UserEntity userEntity = findById(id);
        userEntity.setAdmin(!userEntity.isAdmin());
        UserEntity userEntityUpdated = userRepository.save(userEntity);

        return userEntityToUserMapper.map(userEntityUpdated);
    }

    private UserEntity findById(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot be found with id: " + id));
    }
} 