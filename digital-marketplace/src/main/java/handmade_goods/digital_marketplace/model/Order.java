package handmade_goods.digital_marketplace.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private double amount = 0.0;

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
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private Buyer buyer;


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

    private void calculateamount() {
        amount = 0;
        if (products != null) {
            for (Product product : products) {
                amount += product.getPrice();
            }
        }
    }

    public double getamount() {
        return amount;
    }

    public void setamount(double amount) {
        this.amount = amount;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        calculateamount();
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
        amount = 0;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
