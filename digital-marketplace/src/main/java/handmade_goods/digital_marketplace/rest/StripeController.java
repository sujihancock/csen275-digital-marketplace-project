package handmade_goods.digital_marketplace.rest;

import com.stripe.exception.StripeException;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import handmade_goods.digital_marketplace.service.StripeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
//@CrossOrigin(origins = "${client.url}")
public class StripeController {

    private final StripeService stripeService;
    private final BuyerService buyerService;

    @Autowired
    public StripeController(StripeService stripeService, BuyerService buyerService) {
        this.stripeService = stripeService;
        this.buyerService = buyerService;
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
            Map<String, Double> paymentsBySeller = buyerService.calculatePaymentsToSellers(buyer.getCart());
            return ResponseEntity.ok(ApiResponse.success(stripeService.handleCheckOut(paymentsBySeller, buyer)));
        } catch (RuntimeException | StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}
