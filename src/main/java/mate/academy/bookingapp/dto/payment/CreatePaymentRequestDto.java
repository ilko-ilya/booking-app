package mate.academy.bookingapp.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequestDto {
    @NotNull(message = "Booking ID can't be null")
    private Long bookingId;
}
