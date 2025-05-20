package com.auctions.service.lot.component;

import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UpdateLotServiceComponentTest extends LotServiceComponentTest {

    @InjectMocks
    private UpdateLotServiceComponent updateLotServiceComponent;

    @Test
    void updateLotDetails_whenAllConditionsExist_shouldUpdateLotDetails() {

        final Integer CREATED_BY = 1;
        final Integer MODIFIED_BY = 2;

        when(lotRepository.findById(CREATED_BY)).thenReturn(Optional.of(testLotEntity));
        User newUser = User.builder().id(MODIFIED_BY).build();
        UserEntity newUserEntity = new UserEntity();
        when(userRepository.findById(MODIFIED_BY)).thenReturn(Optional.of(newUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(testLotEntity)).thenReturn(testLot);

        Lot.LotBuilder lotBuilder = testLot.toBuilder();
        lotBuilder.name("new name");
        lotBuilder.surname("new surname");
        Lot updatedTestLot = lotBuilder.build();

        Lot lot = updateLotServiceComponent.updateLotDetails(CREATED_BY, updatedTestLot, newUser);

        assertEquals(testLot, lot);

        ArgumentCaptor<LotEntity> lotEntityCaptor = ArgumentCaptor.forClass(LotEntity.class);
        verify(lotRepository).save(lotEntityCaptor.capture());
        LotEntity lotEntityCaptured = lotEntityCaptor.getValue();
        assertEquals(updatedTestLot.getName(), lotEntityCaptured.getName());
        assertEquals(updatedTestLot.getSurname(), lotEntityCaptured.getSurname());
        assertEquals(testLotEntity.getCreatedBy(), lotEntityCaptured.getCreatedBy());
        assertEquals(newUserEntity, lotEntityCaptured.getLastModifiedBy());
    }

    @Test
    void updateLotDetails_whenLotIsUnknown_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> updateLotServiceComponent.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenUserIsUnknown_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(testUser.getId())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> updateLotServiceComponent.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenSaveThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenMapThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotPhoto_whenAllConditionsExist_shouldUpdateLotPhoto() {

        final String OLD_PHOTO_URL = testLotEntity.getPhotoUrl();
        final String NEW_PHOTO_URL = "newPhotoUrl";
        final Integer MODIFIED_BY = 2;
        final MultipartFile FILE = mock(MultipartFile.class);

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(FILE)).thenReturn(NEW_PHOTO_URL);
        User newUser = User.builder().id(MODIFIED_BY).build();
        UserEntity newUserEntity = new UserEntity();
        when(userRepository.findById(MODIFIED_BY)).thenReturn(Optional.of(newUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);

        updateLotServiceComponent.updateLotPhoto(LOT_ID, FILE, newUser);

        ArgumentCaptor<LotEntity> lotEntityCaptor = ArgumentCaptor.forClass(LotEntity.class);
        verify(lotRepository).save(lotEntityCaptor.capture());
        LotEntity lotEntityCaptured = lotEntityCaptor.getValue();
        assertEquals(lotEntityCaptured, testLotEntity);
        assertEquals(NEW_PHOTO_URL, lotEntityCaptured.getPhotoUrl());
        assertEquals(newUserEntity, lotEntityCaptured.getLastModifiedBy());

        verify(fileStorageService).deleteFile(OLD_PHOTO_URL);
    }

    @Test
    void updateLotPhoto_whenLotIsUnknown_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> updateLotServiceComponent.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenFileStorageThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenUserIsUnknown_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenReturn("newPhotoUrl");
        when(userRepository.findById(testUser.getId())).thenThrow(new ResourceNotFoundException());

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenSaveThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenReturn("newPhotoUrl");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenFileDeletionThrowsException_shouldThrowException() {

        final String NEW_PHOTO_URL = "newPhotoUrl";

        when(lotRepository.findById(1)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenReturn(NEW_PHOTO_URL);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);
        doThrow(new RuntimeException()).when(fileStorageService).deleteFile(testLotEntity.getPhotoUrl());
        doNothing().when(fileStorageService).deleteFile(NEW_PHOTO_URL);

        assertThrows(BusinessException.class, () -> updateLotServiceComponent.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));

        verify(fileStorageService).deleteFile(NEW_PHOTO_URL);
    }
}
