package handmade_goods.digital_marketplace.repository.user;

import handmade_goods.digital_marketplace.model.user.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByEmail(String email);
    Optional<Buyer> findByUsername(String username);
}
