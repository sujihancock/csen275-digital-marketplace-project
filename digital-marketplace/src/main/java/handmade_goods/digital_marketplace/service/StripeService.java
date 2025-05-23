package handmade_goods.digital_marketplace.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import handmade_goods.digital_marketplace.dto.ProductRequest;
import handmade_goods.digital_marketplace.dto.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String secretKey;

    public StripeResponse checkoutProducts(ProductRequest request) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(request.getQuantity())
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(request.getCurrency() != null ? request.getCurrency() : "usd")
                                .setUnitAmount(request.getAmount())
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(request.getName())
                                                .build()
                                )
                                .build()
                )
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
                .build();

        try {
            Session session = Session.create(params);
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED", "Stripe session creation failed: " + e.getMessage(), null, null);
        }
    }
}
