package com.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public User getUserById(Integer id) {

        return userRepository.findById(id)
                .map(userEntityToUserMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public User createUser(User user) {

        String username = user.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username already exists: " + username);
        }

        String email = user.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setAdmin(user.getIsAdmin());
        userEntity.setActive(true);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity createdUserEntity = userRepository.save(userEntity);

        return userEntityToUserMapper.map(createdUserEntity);
    }

    @Transactional
    public User updateUser(Integer id, User userDetails) {

        UserEntity userEntity = findByIdOrThrowException(id);

        updateUserEntityUsername(userDetails.getUsername(), userEntity);

        updateUserEntityEmail(userDetails.getEmail(), userEntity);

        updateUserEntityPassword(userDetails.getPassword(), userEntity);

        updateUserEntityIsAdmin(userDetails.getIsAdmin(), userEntity);

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        
        return userEntityToUserMapper.map(updatedUserEntity);
    }

    private void updateUserEntityUsername(String username, UserEntity userEntity) {

        if (isUsernameNewAndValid(username, userEntity.getUsername())) {
            if (doesNewUsernameAlreadyExist(username)) {
                throw new BusinessException("Username already exists: " + username);
            } else {
                userEntity.setUsername(username);
            }
        }
    }

    private boolean isUsernameNewAndValid(String newUsername, String oldUsername) {

        return newUsername != null && !newUsername.isBlank() && !newUsername.equals(oldUsername);
    }

    private boolean doesNewUsernameAlreadyExist(String newUsername) {

        return userRepository.existsByUsername(newUsername);
    }

    private void updateUserEntityEmail(String email, UserEntity userEntity) {

        if (isEmailNewAndValid(email, userEntity.getEmail())) {
            if (doesNewEmailAlreadyExist(email)) {
                throw new BusinessException("Email already exists: " + email);
            } else {
                userEntity.setEmail(email);
            }
        }
    }

    private boolean isEmailNewAndValid(String newEmail, String oldEmail) {

        return newEmail != null && !newEmail.isBlank() && !newEmail.equals(oldEmail);
    }

    private boolean doesNewEmailAlreadyExist(String newEmail) {

        return userRepository.existsByEmail(newEmail);
    }

    private void updateUserEntityPassword(String newPassword, UserEntity userEntity) {

        Optional.ofNullable(newPassword)
                .filter(Predicate.not(String::isBlank))
                .map(passwordEncoder::encode)
                .ifPresent(userEntity::setPassword);
    }

    private void updateUserEntityIsAdmin(Boolean isAdmin, UserEntity userEntity) {

        Optional.ofNullable(isAdmin).ifPresent(userEntity::setAdmin);
    }

    @Transactional
    public void deleteUser(Integer id) {

        UserEntity userEntity = findByIdOrThrowException(id);

        if (!customerRepository.findByCreatedBy(userEntity).isEmpty() || !customerRepository.findByLastModifiedBy(userEntity).isEmpty()) {
            throw new BusinessException("User has customers associated with it; please delete these customers first.");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public User toggleAdminStatus(Integer id) {

        UserEntity userEntity = findByIdOrThrowException(id);
        userEntity.setAdmin(!userEntity.isAdmin());
        UserEntity userEntityUpdated = userRepository.save(userEntity);

        return userEntityToUserMapper.map(userEntityUpdated);
    }

    private UserEntity findByIdOrThrowException(Integer id) {

        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User cannot be found with id: " + id));
    }
} 