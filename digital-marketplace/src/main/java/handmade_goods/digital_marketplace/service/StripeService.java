package handmade_goods.digital_marketplace.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.repository.user.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String secretKey;

    @Value("${client.url}")
    private String clientUrl;

    private final BuyerRepository buyerRepository;

    @Autowired
    public StripeService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

//    public StripeResponse checkoutProducts(ProductRequest request) {
//        Stripe.apiKey = secretKey;
//
//        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
//                .setQuantity(request.getQuantity())
//                .setPriceData(
//                        SessionCreateParams.LineItem.PriceData.builder()
//                                .setCurrency(request.getCurrency() != null ? request.getCurrency() : "usd")
//                                .setUnitAmount(request.getAmount())
//                                .setProductData(
//                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                .setName(request.getName())
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//
//        SessionCreateParams params = SessionCreateParams.builder()
//                .addLineItem(lineItem)
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl("http://localhost:3000/success")
//                .setCancelUrl("http://localhost:3000/cancel")
//                .build();
//
//        try {
//            Session session = Session.create(params);
//            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
//        } catch (StripeException e) {
//            return new StripeResponse("FAILED", "Stripe session creation failed: " + e.getMessage(), null, null);
//        }
//    }

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

    public String stripeLogin(String stripeAccountId) throws StripeException {
        Stripe.apiKey = secretKey;

        LoginLinkCreateOnAccountParams params = LoginLinkCreateOnAccountParams.builder().build();
        LoginLink loginLink = LoginLink.createOnAccount(stripeAccountId, params);

        return loginLink.getUrl();
    }

    private Customer createCustomer(String email, String name) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name)
                .build();

        return Customer.create(params);
    }

    public record StripeClientSecret(String sellerStripeId, String clientSecret) {

    }

    public List<StripeClientSecret> handleCheckOut(Map<String, Double> paymentsBySeller, Buyer buyer) throws StripeException {
        Stripe.apiKey = secretKey;
        List<StripeClientSecret> clientSecrets = new ArrayList<>();

        String stripeCustomerId = buyer.getStripeCustomerId();
        if (stripeCustomerId == null) {
            Customer customer = createCustomer(buyer.getEmail(), buyer.getUsername());
            stripeCustomerId = customer.getId();
            buyer.setStripeCustomerId(stripeCustomerId);
            buyerRepository.save(buyer);
        }

        for (Map.Entry<String, Double> entry : paymentsBySeller.entrySet()) {
            String sellerStripeId = entry.getKey();
            PaymentIntentCreateParams paymentParams = PaymentIntentCreateParams.builder()
                    .setAmount(Math.round(entry.getValue() * 100))
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .setCustomer(stripeCustomerId)
                    .setTransferData(
                            PaymentIntentCreateParams.TransferData.builder()
                                    .setDestination(sellerStripeId)
                                    .build()
                    )
                    .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                    .build();

            clientSecrets.add(new StripeClientSecret(sellerStripeId, PaymentIntent.create(paymentParams).getClientSecret()));
        }

        return clientSecrets;
    }
}
