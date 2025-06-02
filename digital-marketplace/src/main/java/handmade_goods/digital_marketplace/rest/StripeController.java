package handmade_goods.digital_marketplace.rest;

import com.stripe.exception.StripeException;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import handmade_goods.digital_marketplace.service.PaymentService;
import handmade_goods.digital_marketplace.service.StripeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
//@CrossOrigin(origins = "${client.url}")
public class StripeController {

    private final StripeService stripeService;
    private final BuyerService buyerService;
    private final PaymentService paymentService;

    @Autowired
    public StripeController(StripeService stripeService, BuyerService buyerService, PaymentService paymentService) {
        this.stripeService = stripeService;
        this.buyerService = buyerService;
        this.paymentService = paymentService;
    }

//    @PostMapping("/create-checkout-session")
//    public ResponseEntity<StripeResponse> createCheckoutSession(@RequestBody ProductRequest request) {
//        StripeResponse response = stripeService.checkoutProducts(request);
//        return ResponseEntity.ok(response);
//    }

    /**
     * Checks out all items in the cart of the user signed in the application and calculates the amount owed to each
     * seller whose product the user buys
     *
     * @return a list of seller stripe account ids with respective client secrets
     **/
    @GetMapping("/checkout")
    public ResponseEntity<ApiResponse<?>> checkout(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
            }
            
            Long currentOrderId = (Long) httpSession.getAttribute("currentOrderId");
            Map<String, Double> paymentsBySeller = buyerService.calculatePaymentsToSellers(buyer.getCart());
            return ResponseEntity.ok(ApiResponse.success(stripeService.handleCheckOut(paymentsBySeller, buyer, currentOrderId)));
        } catch (RuntimeException | StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Confirms payment completion for testing purposes
     * Call this after successful payment in frontend
     **/
    @PostMapping("/confirm-payment")
    public ResponseEntity<ApiResponse<?>> confirmPayment(@RequestBody List<String> paymentIntentIds, HttpSession httpSession) {
        try {
            System.out.println("=== PAYMENT CONFIRMATION ENDPOINT CALLED ===");
            System.out.println("Payment Intent IDs received: " + paymentIntentIds);
            System.out.println("Session ID: " + httpSession.getId());
            
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                System.out.println("ERROR: No buyer in session");
                System.out.println("Available session attributes: " + httpSession.getAttributeNames().asIterator());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in - session lost"));
            }
            
            System.out.println("Buyer found: " + buyer.getUsername());

            for (String paymentIntentId : paymentIntentIds) {
                System.out.println("Processing payment intent: " + paymentIntentId);
                paymentService.markPaymentAsCompleted(paymentIntentId);
            }

            System.out.println("Payment confirmation completed successfully");
            return ResponseEntity.ok(ApiResponse.success("Payments confirmed successfully"));
        } catch (RuntimeException e) {
            System.err.println("ERROR in payment confirmation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Marks payment as failed when Stripe payment fails
     * Call this when payment fails in frontend
     **/
    @PostMapping("/fail-payment")
    public ResponseEntity<ApiResponse<?>> failPayment(@RequestBody List<String> paymentIntentIds, HttpSession httpSession) {
        try {
            System.out.println("=== PAYMENT FAILURE ENDPOINT CALLED ===");
            System.out.println("Payment Intent IDs to mark as failed: " + paymentIntentIds);
            System.out.println("Session ID: " + httpSession.getId());
            
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                System.out.println("ERROR: No buyer in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in - session lost"));
            }
            
            System.out.println("Buyer found: " + buyer.getUsername());

            for (String paymentIntentId : paymentIntentIds) {
                System.out.println("Marking payment as failed: " + paymentIntentId);
                paymentService.markPaymentAsFailed(paymentIntentId);
            }

            System.out.println("Payment failure processing completed successfully");
            return ResponseEntity.ok(ApiResponse.success("Payments marked as failed successfully"));
        } catch (RuntimeException e) {
            System.err.println("ERROR in payment failure processing: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Gets a one-time login Stripe link for the seller signed in to the application
     *
     * @return a one-time login link for Stripe
     **/
    @GetMapping("/stripe-login")
    public ResponseEntity<ApiResponse<?>> stripeLogin(HttpSession httpSession) {
        try {
            Seller seller = (Seller) httpSession.getAttribute("user");
            if (seller == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
            }
            return ResponseEntity.ok(ApiResponse.success(stripeService.stripeLogin(seller.getStripeAccountId())));
        } catch (RuntimeException | StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}
