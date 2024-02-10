package mate.academy.bookingapp.service.payment;

import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;

public interface PaymentProcessor {
    SuccessfulPaymentResponseDto processSuccessfulPayment(String paymentId);

    CancelledPaymentResponseDto processCancelledPayment(String paymentId);
}
