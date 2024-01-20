package mate.academy.bookingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.CreatePaymentRequestDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.service.payment.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get all payments by userID",
            description = "Retrieve all payments by userID")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<PaymentDto> getPaymentsForUser(@RequestParam Long userId, Pageable pageable) {
        return paymentService.getPaymentsForUser(userId, pageable);
    }

    @Operation(summary = "Initialize session", description = "Initialize a payment session")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String initiatePaymentSession(@RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.initiatePaymentSession(requestDto);
    }

    @Operation(summary = "Success session",
            description = "Confirm successful payment processing through Stripe redirection.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/success")
    public SuccessfulPaymentResponseDto handleSuccessfulPayment(@RequestParam String paymentId) {
        return paymentService.handleSuccessfulPayment(paymentId);
    }

    @Operation(summary = "Cancel session",
            description = "Manage payment cancellation and return "
                    + "messages during Stripe redirection.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/cancel")
    public CancelledPaymentResponseDto handleCancelledPayment(@RequestParam String paymentId) {
        return paymentService.handleCancelledPayment(paymentId);
    }
}
