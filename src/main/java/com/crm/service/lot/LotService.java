package com.crm.service.lot;

import com.crm.domain.Lot;
import com.crm.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface LotService {

    List<Lot> getAllLots();
    Lot getLotById(Integer id);
    Lot createLot(Lot lot, User currentUser);
    Lot updateLotDetails(Integer id, Lot lot, User currentUser);
    void updateLotPhoto(Integer id, MultipartFile photo, User currentUser);
    Path getLotPhotoPath(Integer id);
    void deleteLot(Integer id);
}
