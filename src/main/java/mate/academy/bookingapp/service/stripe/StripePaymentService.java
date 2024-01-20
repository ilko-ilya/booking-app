package mate.academy.bookingapp.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StripePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentService.class);
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public String createPaymentSession(
            String successUrl,
            String cancelUrl,
            BigDecimal amount,
            String currency
    ) {
        try {
            Stripe.apiKey = stripeSecretKey;

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(currency)
                    .setUnitAmount(amount.longValueExact() * 100L)
                    .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Booking Payment")
                                    .build()
                    )
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(priceData)
                    .setQuantity(1L)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addLineItem(lineItem)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .build();

            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            logger.error("Error creating Stripe payment session", e);
            throw new PaymentException("Error creating payment session");
        }
    }
}
