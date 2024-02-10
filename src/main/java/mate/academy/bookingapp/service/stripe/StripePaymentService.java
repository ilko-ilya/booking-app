package mate.academy.bookingapp.service.stripe;

import java.math.BigDecimal;
import mate.academy.bookingapp.dto.payment.PaymentSessionDto;

public interface StripePaymentService {
    PaymentSessionDto createPaymentSession(Long bookingId);

    BigDecimal calculateTotalBookingAmount(Long bookingId);
}
