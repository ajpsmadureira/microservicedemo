package com.auctions.service.lot.component;

import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteLotServiceComponentTest extends LotServiceComponentTest {

    @InjectMocks
    private DeleteLotServiceComponent deleteLotServiceComponent;

    @Test
    void deleteLot_whenAllConditionsExist_shouldDeleteLot() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));

        deleteLotServiceComponent.deleteLot(LOT_ID);

        verify(fileStorageService).deleteFile(testLotEntity.getPhotoUrl());
    }

    @Test
    void deleteLot_whenLotIsUnknown_shouldNotThrowException() {

        when(lotRepository.findById(LOT_ID)).thenThrow(new ResourceNotFoundException());

        deleteLotServiceComponent.deleteLot(1);

        verify(lotRepository, times(0)).deleteById(LOT_ID);
        verify(fileStorageService, times(0)).deleteFile(any());
    }

    @Test
    void deleteLot_whenLotHasAuctions_shouldNotThrowException() {

        testLotEntity.setAuctions(List.of(testAuctionEntity));
        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));

        assertThrows(InvalidParameterException.class, () -> deleteLotServiceComponent.deleteLot(LOT_ID));

        verify(fileStorageService, times(0)).deleteFile(any());
    }

    @Test
    void deleteLot_whenRepositoryDeletionThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        doThrow(new RuntimeException()).when(lotRepository).deleteById(LOT_ID);

        assertThrows(BusinessException.class, () -> deleteLotServiceComponent.deleteLot(LOT_ID));

        verify(fileStorageService, times(0)).deleteFile(any());
    }

    @Test
    void deleteLot_whenFileDeletionThrowsException_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        doThrow(new RuntimeException()).when(fileStorageService).deleteFile(testLotEntity.getPhotoUrl());

        assertThrows(BusinessException.class, () -> deleteLotServiceComponent.deleteLot(LOT_ID));
    }
}
