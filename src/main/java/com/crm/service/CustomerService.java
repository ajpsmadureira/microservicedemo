package com.crm.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.crm.domain.Customer;
import com.crm.domain.User;
import com.crm.persistence.entity.CustomerEntity;
import com.crm.mapper.customer.CustomerEntityToCustomerMapper;
import com.crm.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crm.persistence.entity.UserEntity;
import com.crm.exception.BusinessException;
import com.crm.exception.ResourceNotFoundException;
import com.crm.persistence.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;
    private final CustomerEntityToCustomerMapper customerEntityToCustomerMapper;

    public List<Customer> getAllCustomers() {

        return customerRepository
                .findAll()
                .stream()
                .map(customerEntityToCustomerMapper::map)
                .toList();
    }

    public Customer getCustomerById(Integer id) {

        return customerRepository.findById(id)
                .map(customerEntityToCustomerMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer createCustomer(Customer customer, User currentUser) {

        try {

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            CustomerEntity customerEntity = new CustomerEntity();

            customerEntity.setName(customer.getName());
            customerEntity.setSurname(customer.getSurname());
            customerEntity.setCreatedBy(currentUserEntity);
            customerEntity.setLastModifiedBy(currentUserEntity);

            CustomerEntity newCustomerEntitySaved = customerRepository.save(customerEntity);
            
            return customerEntityToCustomerMapper.map(newCustomerEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create customer: " + e.getMessage());
        }
    }

    @Transactional
    public Customer updateCustomerDetails(Integer id, Customer customer, User currentUser) {

        try {

            CustomerEntity customerEntity = findCustomerByIdOrThrowException(id);

            Optional.ofNullable(customer.getName()).ifPresent(customerEntity::setName);

            Optional.ofNullable(customer.getSurname()).ifPresent(customerEntity::setSurname);

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            customerEntity.setLastModifiedBy(currentUserEntity);
            
            CustomerEntity updatedCustomerEntity = customerRepository.save(customerEntity);

            return customerEntityToCustomerMapper.map(updatedCustomerEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to update customer details: " + e.getMessage());
        }
    }

    @Transactional
    public void updateCustomerPhoto(Integer id, MultipartFile photo, User currentUser) {

        String oldPhotoUrl = null;
        CustomerEntity customerEntity = null;

        try {

            customerEntity = findCustomerByIdOrThrowException(id);

            oldPhotoUrl = customerEntity.getPhotoUrl();

            if (photo != null && !photo.isEmpty()) {

                String photoUrl = fileStorageService.storeFile(photo);
                customerEntity.setPhotoUrl(photoUrl);
            }

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            customerEntity.setLastModifiedBy(currentUserEntity);

            CustomerEntity updatedCustomerEntity = customerRepository.save(customerEntity);

            // Delete old photo if new one was uploaded successfully
            if (oldPhotoUrl != null && !oldPhotoUrl.equals(updatedCustomerEntity.getPhotoUrl())) {
                fileStorageService.deleteFile(oldPhotoUrl);
            }
        } catch (Exception e) {

            // Rollback photo changes if something went wrong
            if (customerEntity != null && customerEntity.getPhotoUrl() != null && !customerEntity.getPhotoUrl().equals(oldPhotoUrl)) {

                fileStorageService.deleteFile(customerEntity.getPhotoUrl());
                customerEntity.setPhotoUrl(oldPhotoUrl);
            }

            throw new BusinessException("Failed to update customer photo: " + e.getMessage());
        }
    }

    public Path getCustomerPhotoPath(Integer id) {

        return customerRepository.findById(id)
                .map(CustomerEntity::getPhotoUrl)
                .map(fileStorageService::getFilePath)
                .orElseThrow(() -> new ResourceNotFoundException("Photo path not found for customer with id: " + id));
    }

    @Transactional
    public void deleteCustomer(Integer id) {

        try {

            CustomerEntity customerEntity = findCustomerByIdOrThrowException(id);

            customerRepository.deleteById(id);

            Optional.ofNullable(customerEntity.getPhotoUrl()).ifPresent(fileStorageService::deleteFile);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete customer: " + e.getMessage());
        }
    }

    private UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    private CustomerEntity findCustomerByIdOrThrowException(Integer id) {

        return customerRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find customer with id: " + id));
    }
} 