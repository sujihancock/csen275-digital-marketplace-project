package handmade_goods.digital_marketplace.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import handmade_goods.digital_marketplace.dto.ProductRequest;
import handmade_goods.digital_marketplace.dto.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${client.url}")
    private String clientUrl;

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

    public record StripeAccount(String id, String url) { }

    public StripeAccount onboardSeller() throws StripeException {
        Stripe.apiKey = secretKey;
        AccountCreateParams accountParams = AccountCreateParams.builder().setType(AccountCreateParams.Type.EXPRESS).build();

        Account account = Account.create(accountParams);
        AccountLinkCreateParams accountLinkParams = AccountLinkCreateParams.builder()
                .setAccount(account.getId())
                .setRefreshUrl(clientUrl)
                .setReturnUrl(clientUrl + "/profile")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(accountLinkParams);
        return new StripeAccount(account.getId(), accountLink.getUrl());
    }
}
