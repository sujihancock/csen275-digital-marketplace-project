package handmade_goods.digital_marketplace.rest;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import handmade_goods.digital_marketplace.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    private final PaymentService paymentService;

    @Autowired
    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            if (webhookSecret != null && !webhookSecret.isEmpty()) {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                // For development/testing without webhook secret
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            System.err.println("Webhook error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

        // Handle the event
        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }
        } catch (Exception e) {
            System.err.println("Error handling webhook event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook handling error");
        }

        return ResponseEntity.ok("Webhook handled successfully");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.err.println("Could not deserialize payment_intent object from webhook");
            return;
        }
        
        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            System.out.println("Payment succeeded for PaymentIntent: " + paymentIntent.getId());
            
            // Update payment status in database
            paymentService.markPaymentAsCompleted(paymentIntent.getId());
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.err.println("Could not deserialize payment_intent object from webhook");
            return;
        }
        
        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            System.out.println("Payment failed for PaymentIntent: " + paymentIntent.getId());
            
            // Update payment status in database
            paymentService.markPaymentAsFailed(paymentIntent.getId());
        }
    }
} 