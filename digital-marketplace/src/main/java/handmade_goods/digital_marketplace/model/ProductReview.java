package handmade_goods.digital_marketplace.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews")
public class ProductReview extends Review {

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public ProductReview() {
    }

    public ProductReview(Long id, String comment, Double rating, Buyer buyer, Product product, LocalDateTime date) {
        super(id, comment, rating, date, buyer);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
