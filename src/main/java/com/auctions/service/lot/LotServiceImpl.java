package com.auctions.service.lot;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.auctions.persistence.entity.UserEntity;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.repository.LotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final UserRepository userRepository;
    private final LotRepository lotRepository;
    private final FileStorageService fileStorageService;
    private final LotEntityToLotMapper lotEntityToLotMapper;

    public List<Lot> getAllLots() {

        return lotRepository
                .findAll()
                .stream()
                .map(lotEntityToLotMapper::map)
                .toList();
    }

    public Lot getLotById(Integer id) {

        return lotRepository.findById(id)
                .map(lotEntityToLotMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found with id: " + id));
    }

    @Transactional
    public Lot createLot(Lot lot, User currentUser) {

        UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        LotEntity lotEntity = new LotEntity();

        lotEntity.setName(lot.getName());
        lotEntity.setSurname(lot.getSurname());
        lotEntity.setCreatedBy(currentUserEntity);
        lotEntity.setLastModifiedBy(currentUserEntity);

        try {

            LotEntity newLotEntitySaved = lotRepository.save(lotEntity);
            
            return lotEntityToLotMapper.map(newLotEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create lot: " + e.getMessage());
        }
    }

    @Transactional
    public Lot updateLotDetails(Integer id, Lot lot, User currentUser) {

        LotEntity lotEntity = findLotByIdOrThrowException(id);

        Optional.ofNullable(lot.getName()).ifPresent(lotEntity::setName);

        Optional.ofNullable(lot.getSurname()).ifPresent(lotEntity::setSurname);

        UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        lotEntity.setLastModifiedBy(currentUserEntity);

        try {
            
            LotEntity updatedLotEntity = lotRepository.save(lotEntity);

            return lotEntityToLotMapper.map(updatedLotEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to update lot details: " + e.getMessage());
        }
    }

    @Transactional
    public void updateLotPhoto(Integer id, MultipartFile photo, User currentUser) {

        String oldPhotoUrl = null;

        LotEntity lotEntity = findLotByIdOrThrowException(id);

        try {

            oldPhotoUrl = lotEntity.getPhotoUrl();

            if (photo != null && !photo.isEmpty()) {

                String photoUrl = fileStorageService.storeFile(photo);
                lotEntity.setPhotoUrl(photoUrl);
            }

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            lotEntity.setLastModifiedBy(currentUserEntity);

            LotEntity updatedLotEntity = lotRepository.save(lotEntity);

            // Delete old photo if new one was uploaded successfully
            if (oldPhotoUrl != null && !oldPhotoUrl.equals(updatedLotEntity.getPhotoUrl())) {
                fileStorageService.deleteFile(oldPhotoUrl);
            }
        } catch (Exception e) {

            // Rollback photo changes if something went wrong
            if (lotEntity != null && lotEntity.getPhotoUrl() != null && !lotEntity.getPhotoUrl().equals(oldPhotoUrl)) {

                fileStorageService.deleteFile(lotEntity.getPhotoUrl());
                lotEntity.setPhotoUrl(oldPhotoUrl);
            }

            throw new BusinessException("Failed to update lot photo: " + e.getMessage());
        }
    }

    public Path getLotPhotoPath(Integer id) {

        return lotRepository.findById(id)
                .map(LotEntity::getPhotoUrl)
                .map(fileStorageService::getFilePath)
                .orElseThrow(() -> new ResourceNotFoundException("Photo path not found for lot with id: " + id));
    }

    @Transactional
    public void deleteLot(Integer id) {

        LotEntity lotEntity;

        try {

            lotEntity = findLotByIdOrThrowException(id);

        } catch(ResourceNotFoundException e) {

            return;
        }

        try {

            lotRepository.deleteById(id);

            Optional.ofNullable(lotEntity.getPhotoUrl()).ifPresent(fileStorageService::deleteFile);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete lot: " + e.getMessage());
        }
    }

    private UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    private LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find lot with id: " + id));
    }
} 