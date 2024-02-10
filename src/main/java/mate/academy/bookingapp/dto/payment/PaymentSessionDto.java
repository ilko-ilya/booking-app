package mate.academy.bookingapp.dto.payment;

import lombok.Data;

@Data
public class PaymentSessionDto {
    private String sessionId;
    private String sessionUrl;

    public PaymentSessionDto(String clientSecret, String url) {
    }

}

