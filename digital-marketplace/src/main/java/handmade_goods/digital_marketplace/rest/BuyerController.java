package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.dto.CartRequest;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/buyers")
public class BuyerController {

    private final BuyerService buyerService;

    @Autowired
    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    private ResponseEntity<ApiResponse<?>> handleExceptions(Exception e) {
        String errorMessage = e.getMessage();
        HttpStatus httpStatus = errorMessage.equals("not logged in") ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(httpStatus).body(ApiResponse.error(errorMessage));
    }

    @GetMapping(path = "/cart")
    public ResponseEntity<ApiResponse<?>> getCart(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            return ResponseEntity.ok(ApiResponse.success(buyerService.getCartItems(buyer.getCart())));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/cart/add")
    public ResponseEntity<ApiResponse<?>> addToCart(@RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            buyerService.addItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/cart/remove")
    public ResponseEntity<ApiResponse<?>> removeFromCart(@RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            buyerService.removeItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product removed cart"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/cart/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            buyerService.clearCart(buyer.getCart());
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }
}
