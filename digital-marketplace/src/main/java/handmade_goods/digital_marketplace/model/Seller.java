package handmade_goods.digital_marketplace.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Seller extends User {

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SellerReview> reviews = new ArrayList<>();

    @Transient
    private final Map<Long, Integer> store = new HashMap<>();

    public Seller() {
    }

    public Seller(String username, String password, String email) {
        super(username, password, email);
    }

    public List<SellerReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<SellerReview> reviews) {
        this.reviews = reviews;
    }

    public void addReview(SellerReview review) {
        this.reviews.add(review);
    }

    public Map<Long, Integer> getProducts() {
        return store;
    }

    public void addProduct(Product product, int quantity) {
        Long productId = product.getId();
        store.put(productId, store.getOrDefault(productId, 0) + quantity);
    }

    public void sellProduct(Product product, int quantity) {
        Long productId = product.getId();
        store.put(productId, store.get(productId) - quantity);
    }
}
