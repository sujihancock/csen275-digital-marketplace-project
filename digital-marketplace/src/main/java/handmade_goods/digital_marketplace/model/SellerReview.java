package handmade_goods.digital_marketplace.model;

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

    public SellerReview(Long id, String comment, Double rating, LocalDateTime date, Buyer buyer, Seller seller) {
        super(id, comment, rating, date, buyer);
        this.seller = seller;
    }

    public Seller getSeller() {
        return seller;
    }
}
