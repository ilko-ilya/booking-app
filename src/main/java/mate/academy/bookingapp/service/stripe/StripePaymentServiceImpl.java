package mate.academy.bookingapp.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.PaymentSessionDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.exception.StripePaymentException;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripePaymentServiceImpl implements StripePaymentService {
    private static final Logger logger = LoggerFactory.getLogger(StripePaymentServiceImpl.class);

    private final BookingRepository bookingRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public PaymentSessionDto createPaymentSession(Long bookingId) {
        Stripe.apiKey = stripeSecretKey;

        BigDecimal totalBookingAmount = calculateTotalBookingAmount(bookingId);
        try {
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("usd")
                    .setAmount(totalBookingAmount.multiply(new BigDecimal("100")).longValue())
                    .build();

            PaymentIntent intent = PaymentIntent.create(createParams);
            return new PaymentSessionDto(
                    intent.getClientSecret(),
                    "https://example.com/redirect");
        } catch (StripeException e) {
            logger.error("Error while interacting with Stripe API", e);
            throw new StripePaymentException("Error while interacting with Stripe API");
        }
    }

    @Override
    public BigDecimal calculateTotalBookingAmount(Long bookingId) {
        Booking booking = getBookingByBookingId(bookingId);
        Accommodation bookingAccommodation = booking.getAccommodation();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();

        long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate) + 1;

        return bookingAccommodation.getDailyRate()
                .multiply(BigDecimal.valueOf(daysBetween));
    }

    private Booking getBookingByBookingId(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't find booking by bookingID: " + bookingId));
    }
}

