package handmade_goods.digital_marketplace.model.review;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Double rating;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id")
    private Buyer reviewer;

    public record Dto(Long id, String comment, Double rating, LocalDateTime date, User.Summary reviewer) {
    }

    public Review() {
    }

    public Review(String comment, Double rating, LocalDateTime date, Buyer reviewer) {
        this.comment = comment;
        this.rating = rating;
        this.date = date;
        this.reviewer = reviewer;
    }

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Buyer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Buyer reviewer) {
        this.reviewer = reviewer;
    }

    public Dto convertToDto() {
        return new Dto(id, comment, rating, date, reviewer.summarize());
    }
}
