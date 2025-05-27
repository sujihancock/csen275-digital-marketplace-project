package handmade_goods.digital_marketplace.model.order;

import handmade_goods.digital_marketplace.dto.CartItemDto;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "products_in_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id")
    private Buyer buyer;

    public record Dto(Long id, OrderStatus status, LocalDateTime date, Double amount, List<CartItemDto> items, User.Summary buyer) {
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

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", buyer=" + buyer +
                '}';
    }

    private void calculateAmount() {
        amount = 0.0;
        if (products != null) {
            for (Product product : products) {
                amount += product.getPrice();
            }
        }
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        calculateAmount();
    }

    public void addProduct(Product product) {
        products.add(product);
        amount += product.getPrice();
    }

    public void removeProduct(Product product) {
        products.remove(product);
        amount -= product.getPrice();
    }

    public void clearProducts() {
        products.clear();
        amount = 0.0;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Summary summarize() {
        return new Summary(id, status, date, amount);
    }

    public Dto convertToDto(List<CartItemDto> items) {
        return new Dto(id, status, date, amount, items, buyer.summarize());
    }
}
