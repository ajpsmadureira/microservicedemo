package com.auctions.service.lot.component;

import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DeleteLotServiceComponent extends LotServiceComponent {

    public DeleteLotServiceComponent(UserRepository userRepository, LotRepository lotRepository, FileStorageService fileStorageService, LotEntityToLotMapper lotEntityToLotMapper) {
        super(userRepository, lotRepository, fileStorageService, lotEntityToLotMapper);
    }

    @Transactional
    public void deleteLot(Integer id) {

        LotEntity lotEntity;

        try {

            lotEntity = findLotByIdOrThrowException(id);

        } catch(ResourceNotFoundException e) {

            return;
        }

        if (!lotEntity.getAuctions().isEmpty()) {

            throw new InvalidParameterException("Lot has auctions associated to it so it cannot be deleted.");
        }

        try {

            lotRepository.deleteById(id);

            Optional.ofNullable(lotEntity.getPhotoUrl()).ifPresent(fileStorageService::deleteFile);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete lot: " + e.getMessage());
        }
    }
}
