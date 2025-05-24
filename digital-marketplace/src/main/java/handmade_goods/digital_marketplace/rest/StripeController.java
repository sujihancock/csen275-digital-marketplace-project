package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.dto.ProductRequest;
import handmade_goods.digital_marketplace.dto.StripeResponse;
import handmade_goods.digital_marketplace.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "${client.url}")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> createCheckoutSession(@RequestBody ProductRequest request) {
        StripeResponse response = stripeService.checkoutProducts(request);
        return ResponseEntity.ok(response);
    }
}
