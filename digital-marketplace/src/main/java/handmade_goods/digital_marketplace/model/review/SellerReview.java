package handmade_goods.digital_marketplace.model.review;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_reviews")
public class SellerReview extends Review {

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private Seller seller;

    public SellerReview() {
    }

    public SellerReview(String comment, Double rating, LocalDateTime date, Buyer reviewer, Seller seller) {
        super(comment, rating, date, reviewer);
        this.seller = seller;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
