package mate.academy.bookingapp.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private String status;
    private Long bookingId;
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
