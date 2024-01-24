package mate.academy.bookingapp.service.payment;

import java.util.List;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.CreatePaymentRequestDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    List<PaymentDto> getPaymentsForUser(Long userId, Pageable pageable);

    String initiatePaymentSession(CreatePaymentRequestDto createPaymentRequestDto);

    SuccessfulPaymentResponseDto handleSuccessfulPayment(String paymentId);

    CancelledPaymentResponseDto handleCancelledPayment(String paymentId);
}
