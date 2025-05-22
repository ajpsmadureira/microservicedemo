package com.auctions.service.payment.component;

import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetPaymentServiceComponentTest extends PaymentServiceComponentTest {

    @InjectMocks
    private GetPaymentServiceComponent getPaymentServiceComponent;

    @Test
    void getPaymentById_whenAllConditionsExist_shouldReturnPayment() {

        when(paymentRepository.findById(any())).thenReturn(Optional.of(testPaymentEntity));
        when(paymentEntityToPaymentMapper.map(any())).thenReturn(testPayment);

        assertEquals(testPayment, getPaymentServiceComponent.getPaymentById(testPaymentEntity.getId()));

        verify(paymentEntityToPaymentMapper).map(testPaymentEntity);
    }

    @Test
    void getPaymentById_whenPaymentNotFound_shouldThrowException() {

        when(paymentRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getPaymentServiceComponent.getPaymentById(testPaymentEntity.getId()));

        verify(paymentEntityToPaymentMapper, times(0)).map(any());
    }

    @Test
    void getPaymentsByAuctionId() {

        when(paymentRepository.findByAuctionId(any())).thenReturn(List.of(testPaymentEntity));
        when(paymentEntityToPaymentMapper.map(any())).thenReturn(testPayment);

        assertEquals(testPayment, getPaymentServiceComponent.getPaymentsByAuctionId(testPaymentEntity.getAuction().getId()).get(0));

        verify(paymentEntityToPaymentMapper).map(testPaymentEntity);
    }
}
