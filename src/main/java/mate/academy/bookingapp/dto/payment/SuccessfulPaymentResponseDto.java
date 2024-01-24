package mate.academy.bookingapp.dto.payment;

import lombok.Data;

@Data
public class SuccessfulPaymentResponseDto {
    private String status;
    private String paymentId;
    private String sessionUrl;
}
