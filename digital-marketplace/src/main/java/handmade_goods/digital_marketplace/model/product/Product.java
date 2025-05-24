package handmade_goods.digital_marketplace.model.product;

import handmade_goods.digital_marketplace.model.review.ProductReview;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    public enum Category {
        JEWELRY,
        ART,
        CLOTHING,
        HOME_DECOR,
        ACCESSORIES,
        STATIONARY,
        ELECTRONICS,
        BOOKS,
        BEAUTY,
        CUSTOM_GIFTS,
        PET_SUPPLIES,
        OTHER   
    }

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductReview> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private Seller seller;

    public record Dto(Long id, String name, Double price, String imageUrl, User.Summary seller, List<ProductReview.Dto> reviews) {
    }

    public record Summary(Long id, String name, Double price, String imageUrl) {
    }

    // Constructors
    public Product() {
    }

    public Product(String name, String description, Double price, Seller seller, String imageUrl, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.seller = seller;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", seller=" + seller +
                ", imageUrl='" + imageUrl + '\'' +
                ", category=" + category +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public void addReview(ProductReview review) {
        this.reviews.add(review);
    }

    public Summary summarize() {
        return new Summary(id, name, price, imageUrl);
    }

    public Dto convertToDto() {
        return new Dto(id, name, price, imageUrl, seller.summarize(), reviews.stream().map(ProductReview::convertToDto).toList());
    }
}
