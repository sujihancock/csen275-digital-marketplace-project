package handmade_goods.digital_marketplace.repository.user;

import handmade_goods.digital_marketplace.model.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
}
