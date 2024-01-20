package mate.academy.bookingapp.dto.payment;

import lombok.Data;

@Data
public class CancelledPaymentResponseDto {
    private String status;
    private String paymentId;
}
