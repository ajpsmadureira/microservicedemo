package com.auctions.service.lot;

import java.nio.file.Path;
import java.util.List;

import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.service.lot.component.CreateLotServiceComponent;
import com.auctions.service.lot.component.DeleteLotServiceComponent;
import com.auctions.service.lot.component.GetLotServiceComponent;
import com.auctions.service.lot.component.UpdateLotServiceComponent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final CreateLotServiceComponent createLotServiceComponent;
    private final DeleteLotServiceComponent deleteLotServiceComponent;
    private final GetLotServiceComponent getLotServiceComponent;
    private final UpdateLotServiceComponent updateLotServiceComponent;

    @Override
    public List<Lot> getAllLots() {

        return getLotServiceComponent.getAllLots();
    }

    @Override
    public Lot getLotById(Integer id) {

        return getLotServiceComponent.getLotById(id);
    }

    @Override
    public Path getLotPhotoPath(Integer id) {

        return getLotServiceComponent.getLotPhotoPath(id);
    }

    @Override
    public Lot createLot(Lot lot, User currentUser) {

        return createLotServiceComponent.createLot(lot, currentUser);
    }

    @Override
    public Lot updateLotDetails(Integer id, Lot lot, User currentUser) {

        return updateLotServiceComponent.updateLotDetails(id, lot, currentUser);
    }

    @Override
    public void updateLotPhoto(Integer id, MultipartFile photo, User currentUser) {

        updateLotServiceComponent.updateLotPhoto(id, photo, currentUser);
    }

    @Override
    public void deleteLot(Integer id) {

        deleteLotServiceComponent.deleteLot(id);
    }
} 