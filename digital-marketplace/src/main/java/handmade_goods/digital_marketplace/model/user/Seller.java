package handmade_goods.digital_marketplace.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.review.SellerReview;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Seller extends User {

    @JsonIgnore
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SellerReview> reviews = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public record Dto(Long id, String username, String email, List<SellerReview.Dto> reviews, List<Product.Summary> products) {
    }

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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }

    public Dto convertToDto() {
        return new Dto(getId(), getUsername(), getEmail(), getReviews().stream().map(SellerReview::convertToDto).collect(Collectors.toList()), getProducts().stream().map(Product::summarize).collect(Collectors.toList()));
    }
}
