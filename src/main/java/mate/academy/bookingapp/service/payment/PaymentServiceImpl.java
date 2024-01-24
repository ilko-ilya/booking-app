package mate.academy.bookingapp.service.payment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.CreatePaymentRequestDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.mapper.payment.PaymentMapper;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.repository.PaymentRepository;
import mate.academy.bookingapp.service.stripe.StripePaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String PAID = "PAID";
    public static final String CANCELED = "CANCELED";

    private final StripePaymentService stripePaymentService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> getPaymentsForUser(Long userId, Pageable pageable) {
        List<Payment> paymentsByUserId = paymentRepository.findPaymentsByUserId(userId);
        return paymentsByUserId.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public String initiatePaymentSession(CreatePaymentRequestDto createPaymentRequestDto) {
        return stripePaymentService.createPaymentSession(
                createPaymentRequestDto.getSuccessUrl(),
                createPaymentRequestDto.getCancelUrl(),
                createPaymentRequestDto.getAmountToPay(),
                createPaymentRequestDto.getCurrency()
        );
    }

    @Override
    public SuccessfulPaymentResponseDto handleSuccessfulPayment(String paymentId) {
        Payment payment = paymentRepository.findPaymentById(Long.parseLong(paymentId)).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the payment by paymentId: " + paymentId));

        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);

        SuccessfulPaymentResponseDto responseDto = new SuccessfulPaymentResponseDto();
        responseDto.setStatus(PaymentServiceImpl.PAID);
        responseDto.setPaymentId(paymentId);
        responseDto.setSessionUrl(payment.getSessionUrl().toString());
        return responseDto;
    }

    @Override
    public CancelledPaymentResponseDto handleCancelledPayment(String paymentId) {
        Payment payment = paymentRepository.findPaymentById(Long.parseLong(paymentId)).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the payment by paymentId: " + paymentId));

        payment.setStatus(Payment.Status.CANCELED);
        paymentRepository.save(payment);

        CancelledPaymentResponseDto responseDto = new CancelledPaymentResponseDto();
        responseDto.setStatus(PaymentServiceImpl.CANCELED);
        responseDto.setPaymentId(paymentId);
        return responseDto;
    }
}
