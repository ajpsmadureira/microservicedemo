package com.auctions.service.lot.component;

import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class LotServiceComponent {

    private final UserRepository userRepository;
    final LotRepository lotRepository;
    final FileStorageService fileStorageService;
    final LotEntityToLotMapper lotEntityToLotMapper;

    UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find user with id: " + id));
    }

    LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find lot with id: " + id));
    }
}
