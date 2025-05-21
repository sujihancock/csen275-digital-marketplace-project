package handmade_goods.digital_marketplace.repository.product;

import handmade_goods.digital_marketplace.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    List<Product> findAllByCategory(Product.Category category);
    List<Product> findAllByNameStartingWith(String name);
}
