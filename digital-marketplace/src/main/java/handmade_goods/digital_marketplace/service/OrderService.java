package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.CartItem;
import handmade_goods.digital_marketplace.repository.order.OrderRepository;
import handmade_goods.digital_marketplace.repository.user.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Order convertCartToOrder(Buyer buyer) {
        System.out.println("=== CONVERTING CART TO ORDER ===");
        System.out.println("Buyer ID: " + buyer.getId());
        System.out.println("Buyer username: " + buyer.getUsername());
        
        Order order = new Order(LocalDateTime.now(ZoneId.systemDefault()), Order.OrderStatus.PENDING, buyer);
        System.out.println("Order created with status: " + order.getStatus());

        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);
        System.out.println("Found " + cartItems.size() + " cart items for buyer");
        
        if (cartItems.isEmpty()) {
            System.out.println("ERROR: No cart items found for buyer " + buyer.getId());
            throw new RuntimeException("Cart is empty - no items to order");
        }
        
        for (CartItem cartItem : cartItems) {
            System.out.println("Processing cart item: Product ID " + cartItem.getProduct().getId() + 
                             ", Quantity: " + cartItem.getQuantity() + 
                             ", Product: " + cartItem.getProduct().getName());
            try {
                order.addOrderItem(cartItem.getProduct(), cartItem.getQuantity());
                System.out.println("Added order item successfully");
            } catch (Exception e) {
                System.err.println("ERROR adding order item: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        
        System.out.println("Order total amount: " + order.getAmount());
        System.out.println("Saving order to database...");
        
        try {
            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully with ID: " + savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            System.err.println("ERROR saving order: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save order: " + e.getMessage());
        }
    }

    public List<Order.Summary> getOrderHistory(Buyer buyer) {
        return orderRepository.findByBuyer(buyer).stream().map(Order::summarize).collect(Collectors.toList());
    }

    public Order.Dto getOrderById(Long id) {
        return orderRepository.findById(id).map(Order::convertToDto).orElse(null);
    }
}
