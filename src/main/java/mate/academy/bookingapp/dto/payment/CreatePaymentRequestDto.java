package mate.academy.bookingapp.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    @NotNull(message = "Booking ID can't be null")
    private Long bookingId;
    @NotNull(message = "Amount to pay can't be null")
    @Positive(message = "Amount to pay must be greater than zero")
    private BigDecimal amountToPay;
    private String successUrl;
    private String cancelUrl;
    private String currency;
}
