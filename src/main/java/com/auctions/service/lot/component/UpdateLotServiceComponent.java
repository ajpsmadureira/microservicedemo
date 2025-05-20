package com.auctions.service.lot.component;

import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.exception.BusinessException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Component
public class UpdateLotServiceComponent extends LotServiceComponent {

    public UpdateLotServiceComponent(UserRepository userRepository, LotRepository lotRepository, FileStorageService fileStorageService, LotEntityToLotMapper lotEntityToLotMapper) {
        super(userRepository, lotRepository, fileStorageService, lotEntityToLotMapper);
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
}
