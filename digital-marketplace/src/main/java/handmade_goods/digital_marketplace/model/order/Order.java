package handmade_goods.digital_marketplace.model.order;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private Double amount = 0.0;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY ,orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id")
    private Buyer buyer;

    public record Dto(Long id, OrderStatus status, LocalDateTime date, Double amount, Buyer.Summary buyer, List<OrderItem.Dto> items) {
    }

    public record Summary(Long id, OrderStatus status, LocalDateTime date, Double amount) {
    }

    // Constructors
    public Order() {
    }

    public Order(LocalDateTime date, OrderStatus status, Buyer buyer) {
        this.date = date;
        this.status = status;
        this.buyer = buyer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Double getAmount() {
        return amount;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculateAmount();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // Order item management
    public void addOrderItem(Product product, int quantity) {
        OrderItem item = new OrderItem(this, product, quantity);
        orderItems.add(item);
        calculateAmount();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        calculateAmount();
    }

    public void clearOrderItems() {
        orderItems.clear();
        amount = 0.0;
    }

    private void calculateAmount() {
        amount = 0.0;
        for (OrderItem item : orderItems) {
            amount += item.getSubtotal();
        }
    }

    public Summary summarize() {
        return new Summary(id, status, date, amount);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", amount=" + amount +
                ", status=" + status +
                ", buyer=" + buyer +
                '}';
    }

    public Dto convertToDto() {
        return new Dto(id, status, date, amount, buyer.summarize(), orderItems.stream().map(OrderItem::convertToDto).collect(Collectors.toList()));
    }
}
