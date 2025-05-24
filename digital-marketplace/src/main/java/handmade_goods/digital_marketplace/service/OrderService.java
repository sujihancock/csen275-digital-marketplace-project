package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.repository.order.OrderRepository;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public Order create(LocalDateTime date, Buyer buyer) {
        Order order = new Order(date, Order.OrderStatus.PENDING, buyer);
        Map<Long, Integer> cartItems = buyer.getCart().getProducts();
        for (Long id : cartItems.keySet()) {
            Optional<Product> product = productRepository.findById(id);
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found");
            }
            order.addProduct(product.get());
        }

        return order;
    }

    public void updateStatus(Long id, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }

        Order order = orderOpt.get();
        order.setStatus(status);
        orderRepository.save(order);
    }
}
