package com.auctions.service.payment.component;

import com.auctions.domain.payment.PaymentState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.persistence.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CancelPaymentServiceComponentTest extends PaymentServiceComponentTest {

    @InjectMocks
    private CancelPaymentServiceComponent cancelPaymentServiceComponent;

    @Test
    void cancelPayment_whenAllConditionsExist_shouldCancelPayment() {

        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(testPaymentEntity));

        cancelPaymentServiceComponent.cancelPayment(PAYMENT_ID);

        ArgumentCaptor<PaymentEntity> paymentEntityCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(paymentEntityCaptor.capture());
        PaymentEntity paymentEntityCaptured = paymentEntityCaptor.getValue();
        assertEquals(PaymentState.CANCELLED, paymentEntityCaptured.getState());

        verify(paymentGateway).cancelPayment(testPaymentEntity.getLink());
    }

    @Test
    void cancelPayment_whenPaymentIsUnknown_shouldThrowException() {

        when(paymentRepository.findById(PAYMENT_ID)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> cancelPaymentServiceComponent.cancelPayment(PAYMENT_ID));

        verify(paymentGateway, times(0)).cancelPayment(any());
    }

    @Test
    void cancelPayment_whenPaymentIsAlreadyCancelled_shouldSimplyReturn() {

        testPaymentEntity.setState(PaymentState.CANCELLED);
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(testPaymentEntity));

        cancelPaymentServiceComponent.cancelPayment(PAYMENT_ID);

        verify(paymentRepository, times(0)).save(any());
        verify(paymentGateway, times(0)).cancelPayment(any());
    }

    @Test
    void cancelPayment_whenPaymentGatewayThrowsException_shouldThrowException() {

        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(testPaymentEntity));
        doThrow(new RuntimeException()).when(paymentGateway).cancelPayment(any());

        assertThrows(BusinessException.class, () -> cancelPaymentServiceComponent.cancelPayment(PAYMENT_ID));

        verify(paymentRepository, times(0)).save(any());
    }

    @Test
    void cancelPayment_whenRepositorySaveThrowsException_shouldThrowException() {

        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(testPaymentEntity));
        doThrow(new RuntimeException()).when(paymentRepository).save(testPaymentEntity);

        assertThrows(BusinessException.class, () -> cancelPaymentServiceComponent.cancelPayment(PAYMENT_ID));
    }
}
