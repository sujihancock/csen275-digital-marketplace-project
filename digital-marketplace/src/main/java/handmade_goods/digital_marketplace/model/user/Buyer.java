package handmade_goods.digital_marketplace.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import handmade_goods.digital_marketplace.model.order.Order;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "buyers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Buyer extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @JsonIgnore
    @Transient
    private Cart cart = new Cart();

    public record Dto(Long id, String username, String email, List<Order.Summary> orders) {
    }

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

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setBuyer(this);
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setBuyer(null);
    }

    public void clearCartItems() {
        cartItems.clear();
    }

    public Dto convertToDto() {
        return new Buyer.Dto(getId(), getUsername(), getEmail(), getOrders().stream().map(Order::summarize).collect(Collectors.toList()));
    }
}
