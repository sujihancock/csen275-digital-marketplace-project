package handmade_goods.digital_marketplace.repository.product;

import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.product.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findByCategoryAndKeywords(SearchRequest searchRequest);
}
