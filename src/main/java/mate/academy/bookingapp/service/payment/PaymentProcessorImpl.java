package mate.academy.bookingapp.service.payment;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.exception.EntityNotFoundException;
import mate.academy.bookingapp.model.Payment;
import mate.academy.bookingapp.repository.PaymentRepository;
import mate.academy.bookingapp.service.telegram.TelegramNotificationService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentProcessorImpl implements PaymentProcessor {
    private final PaymentRepository paymentRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public SuccessfulPaymentResponseDto processSuccessfulPayment(String paymentId) {
        Payment payment = findPaymentById(paymentId);
        markPaymentAsPaid(payment);
        notifySuccessfulPayment(paymentId);

        SuccessfulPaymentResponseDto responseDto = new SuccessfulPaymentResponseDto();
        responseDto.setStatus(PaymentServiceImpl.PAID);
        responseDto.setPaymentId(paymentId);
        responseDto.setSessionUrl(payment.getSessionUrl().toString());
        return responseDto;
    }

    @Override
    public CancelledPaymentResponseDto processCancelledPayment(String paymentId) {
        Payment payment = findPaymentById(paymentId);
        markPaymentAsCancelled(payment);

        CancelledPaymentResponseDto responseDto = new CancelledPaymentResponseDto();
        responseDto.setStatus(PaymentServiceImpl.CANCELED);
        responseDto.setPaymentId(paymentId);
        return responseDto;
    }

    private Payment findPaymentById(String paymentId) {
        return paymentRepository.findPaymentById(Long.parseLong(paymentId)).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find the payment by paymentId: " + paymentId)
        );
    }

    private void markPaymentAsPaid(Payment payment) {
        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);
    }

    private void markPaymentAsCancelled(Payment payment) {
        payment.setStatus(Payment.Status.CANCELED);
        paymentRepository.save(payment);
    }

    private void notifySuccessfulPayment(String paymentId) {
        telegramNotificationService.notifySuccessfulPayment(paymentId);
    }
}
