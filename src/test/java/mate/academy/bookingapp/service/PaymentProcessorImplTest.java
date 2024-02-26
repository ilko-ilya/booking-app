package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.repository.PaymentRepository;
import mate.academy.bookingapp.service.payment.PaymentProcessorImpl;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @InjectMocks
    private PaymentProcessorImpl paymentProcessorImpl;

    @DisplayName("ProcessSuccessful payment. Should return SuccessfulPaymentResponseDto")
    @Test
    public void processSuccessfulPayment_WithExistingID_ShouldReturnSuccessfulPaymentResponseDto()
            throws MalformedURLException {
        Long paymentID = 123L;
        URL sessionUrl = new URL("https://example.com");
        Payment payment = new Payment();
        payment.setId(paymentID);
        payment.setStatus(Payment.Status.PENDING);
        payment.setSessionUrl(sessionUrl);

        when(paymentRepository.findPaymentById(123L)).thenReturn(Optional.of(payment));

        SuccessfulPaymentResponseDto responseDto =
                paymentProcessorImpl.processSuccessfulPayment(String.valueOf(paymentID));

        verify(paymentRepository, atLeastOnce()).findPaymentById(123L);
        verify(telegramNotificationService, times(1))
                .notifySuccessfulPayment(String.valueOf(paymentID));

        assertEquals("PAID", responseDto.getStatus());
        assertEquals(String.valueOf(paymentID), responseDto.getPaymentId());
        assertNotNull(responseDto.getSessionUrl());
    }

    @DisplayName("Process successful payment with Non existing paymentID."
            + " Should throw EntityNotFoundException")
    @Test
    public void processSuccessfulPayment_WithNonExistingId_ShouldThrowEntityNotFoundException() {
        Long nonExistingID = 999L;

        when(paymentRepository.findPaymentById(nonExistingID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            paymentProcessorImpl.processSuccessfulPayment(String.valueOf(nonExistingID));
        });

        verify(paymentRepository, times(1)).findPaymentById(nonExistingID);
        verify(telegramNotificationService, never()).notifySuccessfulPayment(anyString());
    }

    @DisplayName("Process Canceled payment")
    @Test
    public void processCanceledPayment_ShouldReturnCanceledPaymentResponseDto() {
        Long paymentID = 123L;

        Payment payment = new Payment();
        payment.setId(paymentID);
        payment.setStatus(Payment.Status.PENDING);

        when(paymentRepository.findPaymentById(paymentID)).thenReturn(Optional.of(payment));

        CancelledPaymentResponseDto responseDto =
                paymentProcessorImpl.processCancelledPayment(String.valueOf(paymentID));

        verify(paymentRepository, times(1)).findPaymentById(paymentID);

        assertEquals("CANCELED", responseDto.getStatus());
        assertEquals(String.valueOf(paymentID), responseDto.getPaymentId());

    }
}
