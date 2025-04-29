package handmade_goods.digital_marketplace.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderDate;
    private Long buyerId;
    private Long sellerId;
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    public enum OrderStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToMany
    @JoinTable(
            name = "products_in_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();


    // Constructors
    public Order() {
    }

    public Order(String orderDate, OrderStatus status, Long buyerId, Long sellerId) {
        this.orderDate = orderDate;
        this.status = status;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                '}';
    }

    public void calculateTotalPrice() {
        totalPrice = 0;
        if (products != null) {
            for (Product product : products) {
                totalPrice += product.getPrice();
            }
        }
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
        calculateTotalPrice();
    }

    public void removeProduct(Product product) {
        products.remove(product);
        calculateTotalPrice();
    }

    public void clearProducts() {
        products.clear();
        totalPrice = 0;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
