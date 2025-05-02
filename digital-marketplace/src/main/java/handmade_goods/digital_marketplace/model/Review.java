package handmade_goods.digital_marketplace.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Inheritance(strategy = InheritanceType.JOINED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Double rating;
    private LocalDateTime date;

//    @ManyToOne
//    @JoinColumn(name = "product_id", referencedColumnName = "id")
//    private Product product;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    private Buyer buyer;

    // Constructors
    public Review() {
    }

    public Review(Long id, String comment, Double rating, Buyer buyer, LocalDateTime date) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.buyer = buyer;
        this.date = date;
    }


    //    public Review(Product product, Buyer buyer, String comment, Double rating) {
//        this.product = product;
//        this.buyer = buyer;
//        this.comment = comment;
//        this.rating = rating;
//    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", date=" + date +
                ", buyer=" + buyer +
                '}';
    }
}
