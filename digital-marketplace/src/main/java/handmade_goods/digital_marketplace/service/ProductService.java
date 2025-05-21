package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product.Dto findById(Long id) {
        return productRepository.findById(id).map(Product::convertToDto).orElse(null);
    }

    public List<Product.Summary> search(SearchRequest searchRequest) {
        return productRepository.findByCategoryAndKeywords(searchRequest).stream().map(Product::summarize).toList();
    }
}
