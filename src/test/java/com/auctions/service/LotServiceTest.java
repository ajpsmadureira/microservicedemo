package com.auctions.service;

import com.auctions.domain.*;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import com.auctions.service.lot.LotServiceImpl;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LotServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LotRepository lotRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private LotEntityToLotMapper lotEntityToLotMapper;

    @InjectMocks
    private LotServiceImpl lotService;

    private Lot testLot;

    private LotEntity testLotEntity;

    private UserEntity testUserEntity;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        testLot = TestDataFactory.createTestLot(testUser);
        testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);
    }

    @Test
    void getAllLots_whenAllConditionsExist_shouldReturnLots() {

        when(lotRepository.findAll()).thenReturn(List.of(testLotEntity));
        when(lotEntityToLotMapper.map(any())).thenReturn(testLot);

        List<Lot> lots = lotService.getAllLots();

        verify(lotEntityToLotMapper).map(testLotEntity);
        assertEquals(1, lots.size());
        assertEquals(testLot, lots.get(0));
    }

    @Test
    void getLotById_whenAllConditionsExist_shouldReturnLot() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(lotEntityToLotMapper.map(any())).thenReturn(testLot);

        assertEquals(testLot, lotService.getLotById(1));

        verify(lotEntityToLotMapper).map(testLotEntity);
    }

    @Test
    void getLotById_whenLotNotFound_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lotService.getLotById(1));

        verify(lotEntityToLotMapper, times(0)).map(any());
    }

    @Test
    void createLot_whenAllConditionsExist_shouldCreateLot() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(any())).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(any())).thenReturn(testLot);

        Lot lot = lotService.createLot(testLot, testUser);

        assertEquals(testLot, lot);

        ArgumentCaptor<LotEntity> lotEntityCaptor = ArgumentCaptor.forClass(LotEntity.class);
        verify(lotRepository).save(lotEntityCaptor.capture());
        LotEntity lotEntityCaptured = lotEntityCaptor.getValue();
        assertEquals(testLot.getName(), lotEntityCaptured.getName());
        assertEquals(testLot.getSurname(), lotEntityCaptured.getSurname());
        assertEquals(LotState.CREATED, lotEntityCaptured.getState());
        assertEquals(testUserEntity, lotEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, lotEntityCaptured.getLastModifiedBy());

        verify(lotEntityToLotMapper).map(testLotEntity);
    }

    @Test
    void createLot_whenUserIsUnknown_shouldThrowException() {

        when(userRepository.findById(any())).thenThrow(new BusinessException());

        assertThrows(BusinessException.class, () -> lotService.createLot(testLot, testUser));

        verify(lotRepository, times(0)).save(any());
        verify(lotEntityToLotMapper, times(0)).map(any());
    }

    @Test
    void createLot_whenSaveThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> lotService.createLot(testLot, testUser));

        verify(lotEntityToLotMapper, times(0)).map(any());
    }

    @Test
    void createLot_whenMapThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(any())).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> lotService.createLot(testLot, testUser));
    }

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

        Lot lot = lotService.updateLotDetails(CREATED_BY, updatedTestLot, newUser);

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

        when(lotRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> lotService.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenUserIsUnknown_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> lotService.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenSaveThrowsException_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> lotService.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotDetails_whenMapThrowsException_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> lotService.updateLotDetails(1, testLot, testUser));
    }

    @Test
    void updateLotPhoto_whenAllConditionsExist_shouldUpdateLotPhoto() {

        final String OLD_PHOTO_URL = testLotEntity.getPhotoUrl();
        final String NEW_PHOTO_URL = "newPhotoUrl";
        final Integer LOT_ID = 1;
        final Integer MODIFIED_BY = 2;
        final MultipartFile FILE = mock(MultipartFile.class);

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(FILE)).thenReturn(NEW_PHOTO_URL);
        User newUser = User.builder().id(MODIFIED_BY).build();
        UserEntity newUserEntity = new UserEntity();
        when(userRepository.findById(MODIFIED_BY)).thenReturn(Optional.of(newUserEntity));
        when(lotRepository.save(testLotEntity)).thenReturn(testLotEntity);

        lotService.updateLotPhoto(LOT_ID, FILE, newUser);

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

        when(lotRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> lotService.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenFileStorageThrowsException_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(BusinessException.class, () -> lotService.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenUserIsUnknown_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenReturn("newPhotoUrl");
        when(userRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        assertThrows(BusinessException.class, () -> lotService.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
    }

    @Test
    void updateLotPhoto_whenSaveThrowsException_shouldThrowException() {

        when(lotRepository.findById(any())).thenReturn(Optional.of(testLotEntity));
        when(fileStorageService.storeFile(any())).thenReturn("newPhotoUrl");
        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> lotService.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));
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

        assertThrows(BusinessException.class, () -> lotService.updateLotPhoto(1, mock(MultipartFile.class), mock(User.class)));

        verify(fileStorageService).deleteFile(NEW_PHOTO_URL);
    }

    @Test
    void deleteLot_whenAllConditionsExist_shouldDeleteLot() {

        final Integer LOT_ID = 1;

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));

        lotService.deleteLot(LOT_ID);

        verify(fileStorageService).deleteFile(testLotEntity.getPhotoUrl());
    }

    @Test
    void deleteLot_whenLotIsUnknown_shouldNotThrowException() {

        when(lotRepository.findById(any())).thenThrow(new ResourceNotFoundException());

        lotService.deleteLot(1);

        verify(lotRepository, times(0)).deleteById(any());
        verify(fileStorageService, times(0)).deleteFile(any());
    }

    @Test
    void deleteLot_whenRepositoryDeletionThrowsException_shouldThrowException() {

        final Integer LOT_ID = 1;

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        doThrow(new RuntimeException()).when(lotRepository).deleteById(LOT_ID);

        assertThrows(BusinessException.class, () -> lotService.deleteLot(LOT_ID));

        verify(fileStorageService, times(0)).deleteFile(any());
    }

    @Test
    void deleteLot_whenFileDeletionThrowsException_shouldThrowException() {

        final Integer LOT_ID = 1;

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        doThrow(new RuntimeException()).when(fileStorageService).deleteFile(testLotEntity.getPhotoUrl());

        assertThrows(BusinessException.class, () -> lotService.deleteLot(LOT_ID));
    }
}
