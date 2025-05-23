package handmade_goods.digital_marketplace.repository.review;

import handmade_goods.digital_marketplace.model.review.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
}
