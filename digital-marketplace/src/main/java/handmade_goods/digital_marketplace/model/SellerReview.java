package handmade_goods.digital_marketplace.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_reviews")
@PrimaryKeyJoinColumn(name = "review_id")
public class SellerReview extends Review {

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private Seller seller;

    public SellerReview() {
    }

    public SellerReview(Long id, String comment, Double rating, Buyer buyer, Seller seller, LocalDateTime date) {
        super(id, comment, rating, buyer, date);
        this.seller = seller;
    }

    public Seller getSeller() {
        return seller;
    }
}
