package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.review.Review;
import handmade_goods.digital_marketplace.model.review.SellerReview;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.user.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository, ProductRepository productRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
    }

    public Optional<Seller> getById(Long id) {
        return sellerRepository.findById(id);
    }

    public void addProduct(Seller seller, AddRequest product) {
        productRepository.save(new Product(product.name(), product.description(), product.price(), seller, product.imageUrl(), product.category()));
    }

    public List<Review.Dto> getReviews(Seller seller) {
       return seller.getReviews().stream().map(SellerReview::convertToDto).toList();
    }
}
