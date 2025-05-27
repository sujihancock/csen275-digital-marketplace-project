package handmade_goods.digital_marketplace.rest;


import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/save")
    public ResponseEntity<ApiResponse<?>> saveOrder(HttpSession httpSession) {
        try {
            Buyer buyer = (Buyer) httpSession.getAttribute("user");
            if (buyer == null) {
                throw new RuntimeException("not logged in");
            }
            Order.Dto order = orderService.convertCartToOrder(buyer);
            buyer.getCart().clearCart();

            httpSession.setAttribute("user", buyer);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }
}
