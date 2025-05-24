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

    private Buyer getBuyerFromSession(Long id, HttpSession httpSession) {
        if (!buyerService.exists(id)) {
            throw new RuntimeException("buyer with id: " + id + " not found");
        }
        if (httpSession.getAttribute("user") == null) {
            throw new RuntimeException("not logged in");
        }

        Buyer buyer = (Buyer) httpSession.getAttribute("user");
        if (!buyer.getId().equals(id)) {
            throw new RuntimeException("not logged in");
        }

        return buyer;
    }

    private ResponseEntity<ApiResponse<?>> handleExceptions(RuntimeException e) {
        String errorMessage = e.getMessage();
        HttpStatus httpStatus = errorMessage.equals("not logged in") ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(httpStatus).body(ApiResponse.error(errorMessage));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getBuyer(@PathVariable("id") Long id) {
        Buyer.Dto buyer = buyerService.getBuyerDtoById(id);
        return buyer != null ? ResponseEntity.ok(ApiResponse.success(buyer)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("buyer with id: " + id + " not found"));
    }

    @GetMapping(path = "/{id}/cart")
    public ResponseEntity<ApiResponse<?>> getCart(@PathVariable Long id, HttpSession httpSession) {
        try {
            Buyer buyer = getBuyerFromSession(id, httpSession);
            return ResponseEntity.ok(ApiResponse.success(buyerService.getCartItems(buyer.getCart())));
        } catch (RuntimeException e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/{id}/cart/add")
    public ResponseEntity<ApiResponse<?>> addToCart(@PathVariable Long id, @RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = getBuyerFromSession(id, httpSession);
            buyerService.addItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (RuntimeException e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/{id}/cart/remove")
    public ResponseEntity<ApiResponse<?>> removeFromCart(@PathVariable Long id, @RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = getBuyerFromSession(id, httpSession);
            buyerService.removeItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (RuntimeException e) {
            return handleExceptions(e);
        }
    }

    @PostMapping(path = "/{id}/cart/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(@PathVariable Long id, HttpSession httpSession) {
        try {
            Buyer buyer = getBuyerFromSession(id, httpSession);
            buyerService.clearCart(buyer.getCart());
            httpSession.setAttribute("user", buyer);
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (RuntimeException e) {
            return handleExceptions(e);
        }
    }

//    @PostMapping(path = "/{id}/cart/checkout")
//    public ResponseEntity<ApiResponse<?>> checkout(@PathVariable Long id, HttpSession httpSession) {
//        try {
//            Buyer buyer = getBuyerFromSession(id, httpSession);
//
//            Map<Long, Double> payments = buyerService.getPaymentsToSellers(buyer.getCart());
//            for (Map.Entry<Long, Double> entry : payments.entrySet()) {
//
//            }
//
//
//            return ResponseEntity.ok(ApiResponse.success("cart checked out"));
//        } catch (RuntimeException e) {
//            return handleExceptions(e);
//        }
//    }
}
