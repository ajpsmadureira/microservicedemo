package com.auctions.service.lot.component;

import com.auctions.domain.Lot;
import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GetLotServiceComponentTest extends LotServiceComponentTest {

    @InjectMocks
    private GetLotServiceComponent getLotServiceComponent;

    @Test
    void getAllLots_whenAllConditionsExist_shouldReturnLots() {

        when(lotRepository.findAll()).thenReturn(List.of(testLotEntity));
        when(lotEntityToLotMapper.map(testLotEntity)).thenReturn(testLot);

        List<Lot> lots = getLotServiceComponent.getAllLots();

        verify(lotEntityToLotMapper).map(testLotEntity);
        assertEquals(1, lots.size());
        assertEquals(testLot, lots.get(0));
    }

    @Test
    void getLotById_whenAllConditionsExist_shouldReturnLot() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.of(testLotEntity));
        when(lotEntityToLotMapper.map(testLotEntity)).thenReturn(testLot);

        assertEquals(testLot, getLotServiceComponent.getLotById(LOT_ID));

        verify(lotEntityToLotMapper).map(testLotEntity);
    }

    @Test
    void getLotById_whenLotNotFound_shouldThrowException() {

        when(lotRepository.findById(LOT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getLotServiceComponent.getLotById(LOT_ID));

        verify(lotEntityToLotMapper, times(0)).map(testLotEntity);
    }
}
