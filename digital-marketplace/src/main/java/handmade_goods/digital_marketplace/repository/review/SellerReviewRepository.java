package handmade_goods.digital_marketplace.repository.review;

import handmade_goods.digital_marketplace.model.review.SellerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerReviewRepository extends JpaRepository<SellerReview, Long> {
}
