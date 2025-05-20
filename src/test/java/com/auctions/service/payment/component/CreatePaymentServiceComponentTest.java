package com.auctions.service.payment.component;

import com.auctions.domain.payment.Payment;
import com.auctions.exception.BusinessException;
import com.auctions.persistence.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreatePaymentServiceComponentTest extends PaymentServiceComponentTest {

    @InjectMocks
    private CreatePaymentServiceComponent createPaymentServiceComponent;

    @Test
    void createPayment_whenAllConditionsExist_shouldCreatePayment() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(paymentGateway.createPaymentLink(any())).thenReturn(testPayment.getLink());
        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(paymentRepository.save(any())).thenReturn(testPaymentEntity);
        when(paymentEntityToPaymentMapper.map(any())).thenReturn(testPayment);
        when(paymentGateway.createPaymentLink(any())).thenReturn(testPayment.getLink());

        Payment payment = createPaymentServiceComponent.createPayment(testPayment, testUser);

        assertEquals(testPayment, payment);

        ArgumentCaptor<PaymentEntity> paymentEntityCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(paymentEntityCaptor.capture());
        PaymentEntity paymentEntityCaptured = paymentEntityCaptor.getValue();
        assertEquals(testPayment.getAuctionId(), paymentEntityCaptured.getAuction().getId());
        assertEquals(testPayment.getLink(), paymentEntityCaptured.getLink());
        assertEquals(testPayment.getState(), paymentEntityCaptured.getState());
        assertEquals(testPayment.getAmount(), paymentEntityCaptured.getAmount());
        assertEquals(testUserEntity, paymentEntityCaptured.getCreatedBy());
        assertEquals(testUserEntity, paymentEntityCaptured.getLastModifiedBy());

        verify(paymentEntityToPaymentMapper).map(testPaymentEntity);
    }

    @Test
    void createPayment_whenUserIsUnknown_shouldThrowException() {

        when(userRepository.findById(any())).thenThrow(new BusinessException());

        assertThrows(BusinessException.class, () -> createPaymentServiceComponent.createPayment(testPayment, testUser));

        verify(auctionRepository, times(0)).save(any());
        verify(paymentEntityToPaymentMapper, times(0)).map(any());
    }

    @Test
    void createPayment_whenLinkCreationFails_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(paymentGateway.createPaymentLink(any())).thenThrow(new BusinessException());

        assertThrows(BusinessException.class, () -> createPaymentServiceComponent.createPayment(testPayment, testUser));

        verify(auctionRepository, times(0)).save(any());
        verify(paymentEntityToPaymentMapper, times(0)).map(any());
    }

    @Test
    void createPayment_whenSaveThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(paymentRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createPaymentServiceComponent.createPayment(testPayment, testUser));

        verify(paymentEntityToPaymentMapper, times(0)).map(any());
    }

    @Test
    void createPayment_whenMapThrowsException_shouldThrowException() {

        when(userRepository.findById(any())).thenReturn(Optional.of(testUserEntity));
        when(auctionRepository.findById(any())).thenReturn(Optional.of(testAuctionEntity));
        when(paymentEntityToPaymentMapper.map(any())).thenThrow(new RuntimeException());

        assertThrows(BusinessException.class, () -> createPaymentServiceComponent.createPayment(testPayment, testUser));
    }
}
