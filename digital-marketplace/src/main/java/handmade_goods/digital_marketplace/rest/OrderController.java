package handmade_goods.digital_marketplace.rest;


import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Saves an order from the checked out cart of the buyer signed in to the application
     *
     * @return a status message
     **/
    @PostMapping(path = "/save")
    public ResponseEntity<ApiResponse<?>> saveOrder(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }

            orderService.convertCartToOrder(buyer);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("order saved"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get the order history of the buyer signed in to the application
     *
     * @return an array of orders (id, status, date, amount, buyer (id, username), an array of order items (product (id, name,
     * price image, url), quantity, subtotal)
     */
    @GetMapping(path = "/history")
    public ResponseEntity<ApiResponse<?>> getOrderHistory(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }

            return ResponseEntity.ok(ApiResponse.success(orderService.getOrderHistory(buyer)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get an individual order of products from the seller signed in to the application
     *
     * @return an order's id, status, date, amount, buyer (id, username), an array of order items (product (id, name,
     * price image, url), quantity, subtotal)
     **/
    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable Long id, HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            Order.Dto order = orderService.getOrderById(id);
            if (order == null) {
                throw new RuntimeException("order not found");
            }
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}
