package com.auctions.service.payment.component;

import com.auctions.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

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

        assertEquals(testPayment, getPaymentServiceComponent.getPaymentById(1));

        verify(paymentEntityToPaymentMapper).map(testPaymentEntity);
    }

    @Test
    void getPaymentById_whenPaymentNotFound_shouldThrowException() {

        when(paymentRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getPaymentServiceComponent.getPaymentById(1));

        verify(paymentEntityToPaymentMapper, times(0)).map(any());
    }
}
