package handmade_goods.digital_marketplace.repository.product;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    void deleteByIdAndSeller(Long id, Seller seller);
    Optional<Product> findByIdAndSeller(Long id, Seller seller);
    List<Product> findBySeller(Seller seller);
}
