package com.auctions.service.lot.component;

import com.auctions.domain.Lot;
import com.auctions.exception.BusinessException;
import com.auctions.persistence.entity.LotEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateLotServiceComponentTest extends LotServiceComponentTest {

    @InjectMocks
    private CreateLotServiceComponent createLotServiceComponent;

    @Test
    void createLot_whenAllConditionsExist_shouldCreateLot() {

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(any())).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(testLotEntity)).thenReturn(testLot);

        Lot lot = createLotServiceComponent.createLot(testLot, testUser);

        assertEquals(testLot, lot);

        ArgumentCaptor<LotEntity> lotEntityCaptor = ArgumentCaptor.forClass(LotEntity.class);
        verify(lotRepository).save(lotEntityCaptor.capture());
        LotEntity lotEntityCaptured = lotEntityCaptor.getValue();
        assertEquals(testLot.getName(), lotEntityCaptured.getName());
        assertEquals(testLot.getSurname(), lotEntityCaptured.getSurname());
        assertEquals(testUserEntity, lotEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, lotEntityCaptured.getLastModifiedBy());

        verify(lotEntityToLotMapper).map(testLotEntity);
    }

    @Test
    void createLot_whenUserIsUnknown_shouldThrowException() {

        when(userRepository.findById(testUser.getId())).thenThrow(new BusinessException());

        assertThrows(BusinessException.class, () -> createLotServiceComponent.createLot(testLot, testUser));

        verify(lotRepository, times(0)).save(testLotEntity);
        verify(lotEntityToLotMapper, times(0)).map(testLotEntity);
    }

    @Test
    void createLot_whenSaveThrowsException_shouldThrowException() {

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createLotServiceComponent.createLot(testLot, testUser));

        verify(lotEntityToLotMapper, times(0)).map(testLotEntity);
    }

    @Test
    void createLot_whenMapThrowsException_shouldThrowException() {

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUserEntity));
        when(lotRepository.save(any())).thenReturn(testLotEntity);
        when(lotEntityToLotMapper.map(testLotEntity)).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createLotServiceComponent.createLot(testLot, testUser));
    }
}
