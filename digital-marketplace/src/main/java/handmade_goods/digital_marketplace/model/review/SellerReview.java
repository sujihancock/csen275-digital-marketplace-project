package handmade_goods.digital_marketplace.model.review;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_reviews")
public class SellerReview extends Review {

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private Seller seller;

    public SellerReview() {
    }

    public SellerReview(Long id, String comment, Double rating, LocalDateTime date, User reviewer, Seller seller) {
        super(id, comment, rating, date, reviewer);
        this.seller = seller;
    }

    public Seller getSeller() {
        return seller;
    }
}
