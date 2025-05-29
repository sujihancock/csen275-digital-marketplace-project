package handmade_goods.digital_marketplace.repository.user;

import handmade_goods.digital_marketplace.model.user.CartItem;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByBuyer(Buyer buyer);
    
    Optional<CartItem> findByBuyerAndProduct(Buyer buyer, Product product);
    
    void deleteByBuyer(Buyer buyer);
    
    void deleteByBuyerAndProduct(Buyer buyer, Product product);
    
    long countByBuyer(Buyer buyer);
} 