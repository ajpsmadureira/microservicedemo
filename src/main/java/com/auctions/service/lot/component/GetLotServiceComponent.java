package com.auctions.service.lot.component;

import com.auctions.domain.Lot;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class GetLotServiceComponent extends LotServiceComponent {

    public GetLotServiceComponent(UserRepository userRepository, LotRepository lotRepository, FileStorageService fileStorageService, LotEntityToLotMapper lotEntityToLotMapper) {
        super(userRepository, lotRepository, fileStorageService, lotEntityToLotMapper);
    }

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

    public Path getLotPhotoPath(Integer id) {

        return lotRepository.findById(id)
                .map(LotEntity::getPhotoUrl)
                .map(fileStorageService::getFilePath)
                .orElseThrow(() -> new ResourceNotFoundException("Photo path not found for lot with id: " + id));
    }
}
