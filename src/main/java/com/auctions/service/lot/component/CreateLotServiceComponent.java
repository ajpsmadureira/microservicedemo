package com.auctions.service.lot.component;

import com.auctions.domain.*;
import com.auctions.exception.BusinessException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateLotServiceComponent extends LotServiceComponent {

    public CreateLotServiceComponent(UserRepository userRepository, LotRepository lotRepository, FileStorageService fileStorageService, LotEntityToLotMapper lotEntityToLotMapper) {
        super(userRepository, lotRepository, fileStorageService, lotEntityToLotMapper);
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
}
