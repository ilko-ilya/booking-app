package mate.academy.bookingapp.service.payment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.CreatePaymentRequestDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.PaymentSessionDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.payment.PaymentMapper;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.repository.BookingRepository;
import mate.academy.bookingapp.repository.PaymentRepository;
import mate.academy.bookingapp.service.stripe.StripePaymentServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String PAID = "PAID";
    public static final String CANCELED = "CANCELED";

    private final BookingRepository bookingRepository;
    private final StripePaymentServiceImpl stripePaymentServiceImpl;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;

    @Override
    public List<PaymentDto> getPaymentsForUser(Long userId, Pageable pageable) {
        List<Payment> paymentsByUserId = paymentRepository.findPaymentsByUserId(userId);
        return paymentsByUserId.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public String initiatePaymentSession(CreatePaymentRequestDto requestDto) {
        Booking booking = getBookingByBookingId(requestDto.getBookingId());

        PaymentSessionDto paymentSession =
                stripePaymentServiceImpl.createPaymentSession(booking.getId());

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setSessionId(paymentSession.getSessionId());
        payment.setAmountToPay(stripePaymentServiceImpl.calculateTotalBookingAmount(
                booking.getId()));
        payment.setStatus(Payment.Status.PENDING);

        paymentRepository.save(payment);
        return paymentSession.getSessionId();
    }

    @Override
    public SuccessfulPaymentResponseDto handleSuccessfulPayment(String paymentId) {
        return paymentProcessor.processSuccessfulPayment(paymentId);
    }

    @Override
    public CancelledPaymentResponseDto handleCancelledPayment(String paymentId) {
        return paymentProcessor.processCancelledPayment(paymentId);
    }

    private Booking getBookingByBookingId(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find a booking by bookingID: " + bookingId));
    }
}

