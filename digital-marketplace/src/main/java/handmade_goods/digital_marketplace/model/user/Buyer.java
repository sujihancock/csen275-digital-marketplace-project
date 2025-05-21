package handmade_goods.digital_marketplace.model.user;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.review.Review;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buyers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Buyer extends User {

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Transient
    private Cart cart = new Cart();

//    public record Dto(Long id, String username, String email, List<Review.Dto> reviews, List<Product.Summary> products) {
//    }

    public Buyer() {
    }

    public Buyer(String username, String password, String email) {
        super(username, password, email);
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
}
