package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.review.ProductReview;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.review.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductReviewRepository productReviewRepository) {
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Product.Dto getProductDtoById(Long id) {
        return productRepository.findById(id).map(Product::convertToDto).orElse(null);
    }

    public List<Product.Summary> search(SearchRequest searchRequest) {
        return productRepository.findByCategoryAndKeywords(searchRequest).stream().map(Product::summarize).toList();
    }

    public void addReview(Product product, Buyer buyer, ReviewRequest reviewRequest) {
        productReviewRepository.save(new ProductReview(reviewRequest.comment(), reviewRequest.rating(), buyer, product, reviewRequest.date()));
    }

    public List<ProductReview.Dto> getReviews(Product product) {
        return product.getReviews().stream().map(ProductReview::convertToDto).toList();
    }

    public List<Product.Category> getCategories() {
        return List.of(Product.Category.values());
    }
}
