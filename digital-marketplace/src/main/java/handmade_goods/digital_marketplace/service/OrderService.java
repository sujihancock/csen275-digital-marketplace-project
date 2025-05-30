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

    public void convertCartToOrder(Buyer buyer) {
        Order order = new Order(LocalDateTime.now(ZoneId.systemDefault()), Order.OrderStatus.PENDING, buyer);

        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);
        for (CartItem cartItem : cartItems) {
            order.addOrderItem(cartItem.getProduct(), cartItem.getQuantity());
        }
        orderRepository.save(order);
    }

    public List<Order.Summary> getOrderHistory(Buyer buyer) {
        return orderRepository.findByBuyer(buyer).stream().map(Order::summarize).collect(Collectors.toList());
    }

    public Order.Dto getOrderById(Long id) {
        return orderRepository.findById(id).map(Order::convertToDto).orElse(null);
    }
}
