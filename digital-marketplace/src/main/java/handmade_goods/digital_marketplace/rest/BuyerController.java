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

    /**
     * View the cart of the buyer signed in to the application
     * Now loads persistent cart items from database
     *
     * @return total amount in cart and an array of cart items (product (id, name, price, image url), quantity,
     * total price)
     **/
    @GetMapping(path = "/cart")
    public ResponseEntity<ApiResponse<?>> getCart(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            
            // Load cart from database and return persistent cart items
            return ResponseEntity.ok(ApiResponse.success(buyerService.getPersistentCartItems(buyer)));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    /**
     * Add product(s) to the cart of the buyer signed in to the application
     * Now saves to database for persistence
     *
     * @param cartRequest contains product's id and request quantity
     * @return a status message
     **/
    @PostMapping(path = "/cart/add")
    public ResponseEntity<ApiResponse<?>> addToCart(@RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            
            // Add to persistent cart (database)
            buyerService.addItemToPersistentCart(buyer, cartRequest);
            
            // Also update in-memory cart for backward compatibility
            buyerService.addItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            
            return ResponseEntity.ok(ApiResponse.success("product added to cart"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    /**
     * Remove product(s) from the cart of the buyer signed in to the application
     * Now removes from database for persistence
     *
     * @param cartRequest contains product's id and request quantity
     * @return a status message
     **/
    @PostMapping(path = "/cart/remove")
    public ResponseEntity<ApiResponse<?>> removeFromCart(@RequestBody CartRequest cartRequest, HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            
            // Remove from persistent cart (database)
            buyerService.removeItemFromPersistentCart(buyer, cartRequest);
            
            // Also update in-memory cart for backward compatibility
            buyerService.removeItem(buyer.getCart(), cartRequest);
            httpSession.setAttribute("user", buyer);
            
            return ResponseEntity.ok(ApiResponse.success("product removed from cart"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    /**
     * Clear all items from the cart of the buyer signed in to the application
     * Now clears database for persistence
     *
     * @return a status message
     **/
    @PostMapping(path = "/cart/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            
            // Clear persistent cart (database)
            buyerService.clearPersistentCart(buyer);
            
            // Also clear in-memory cart for backward compatibility
            buyerService.clearCart(buyer.getCart());
            httpSession.setAttribute("user", buyer);
            
            return ResponseEntity.ok(ApiResponse.success("cart cleared"));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    /**
     * View the total price of all items in the cart of the buyer signed in to the application
     * Now calculates from persistent cart items
     *
     * @return the total price of the cart
     **/
    @GetMapping(path = "/cart/amount")
    public ResponseEntity<ApiResponse<?>> getCartAmount(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            
            // Get amount from persistent cart
            return ResponseEntity.ok(ApiResponse.success(buyerService.getPersistentCartItems(buyer).totalAmount()));
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }
}
