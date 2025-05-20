package com.auctions.service.lot;

import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.service.lot.component.CreateLotServiceComponent;
import com.auctions.service.lot.component.DeleteLotServiceComponent;
import com.auctions.service.lot.component.GetLotServiceComponent;
import com.auctions.service.lot.component.UpdateLotServiceComponent;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LotServiceTest {

    @Mock
    private CreateLotServiceComponent createLotServiceComponent;

    @Mock
    private DeleteLotServiceComponent deleteLotServiceComponent;

    @Mock
    private GetLotServiceComponent getLotServiceComponent;

    @Mock
    private UpdateLotServiceComponent updateLotServiceComponent;

    @InjectMocks
    private LotServiceImpl lotService;

    private Lot testLot;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        testLot = TestDataFactory.createTestLot(testUser);
    }

    @Test
    void getAllLots() {

        when(getLotServiceComponent.getAllLots()).thenReturn(List.of(testLot));

        List<Lot> lots = lotService.getAllLots();

        assertEquals(1, lots.size());
        assertEquals(testLot, lots.get(0));
    }

    @Test
    void getLotById() {

        when(getLotServiceComponent.getLotById(testLot.getId())).thenReturn(testLot);

        assertEquals(testLot, lotService.getLotById(testLot.getId()));
    }

    @Test
    void createLot() {

        when(createLotServiceComponent.createLot(testLot, testUser)).thenReturn(testLot);

        assertEquals(testLot, lotService.createLot(testLot, testUser));
    }

    @Test
    void updateLotDetails() {

        lotService.updateLotDetails(testLot.getId(), testLot, testUser);

        verify(updateLotServiceComponent).updateLotDetails(testLot.getId(), testLot, testUser);
    }

    @Test
    void updateLotPhoto() {

        MultipartFile mockMultipartFile = mock(MultipartFile.class);

        lotService.updateLotPhoto(testLot.getId(), mockMultipartFile, testUser);

        verify(updateLotServiceComponent).updateLotPhoto(testLot.getId(), mockMultipartFile, testUser);
    }

    @Test
    void deleteLot() {

        lotService.deleteLot(testLot.getId());

        verify(deleteLotServiceComponent).deleteLot(testLot.getId());
    }
}
