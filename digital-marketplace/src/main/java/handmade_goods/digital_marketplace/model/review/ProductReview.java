package handmade_goods.digital_marketplace.model.review;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.User;
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

    public ProductReview(Long id, String comment, Double rating, Buyer reviewer, Product product, LocalDateTime date) {
        super(id, comment, rating, date, reviewer);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
