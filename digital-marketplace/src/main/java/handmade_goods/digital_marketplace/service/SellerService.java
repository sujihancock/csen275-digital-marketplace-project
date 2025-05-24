package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.review.SellerReview;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.review.SellerReviewRepository;
import handmade_goods.digital_marketplace.repository.user.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final SellerReviewRepository sellerReviewRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository, ProductRepository productRepository, SellerReviewRepository sellerReviewRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.sellerReviewRepository = sellerReviewRepository;
    }

    public Optional<Seller> getById(Long id) {
        return sellerRepository.findById(id);
    }

    public Seller.Dto getSellerDtoById(Long id) {
        return sellerRepository.findById(id).map(Seller::convertToDto).orElse(null);
    }

    public void addProduct(Seller seller, AddRequest productRequest) {
        productRepository.save(new Product(productRequest.name(), productRequest.description(), productRequest.price(), seller, productRequest.imageUrl(), productRequest.category()));
    }

    public void addReview(Seller seller, Buyer buyer, ReviewRequest review) {
        sellerReviewRepository.save(new SellerReview(review.comment(), review.rating(), review.date(), buyer, seller));
    }

    public List<SellerReview.Dto> getReviews(Seller seller) {
       return seller.getReviews().stream().map(SellerReview::convertToDto).toList();
    }

    public List<Product.Dto> getProducts(Seller seller) {
        return seller.getProducts().stream().map(Product::convertToDto).toList();
    }

//    public void chargeBuyer(Buyer buyer, Double totalAmount) {
//        // charge payment Stripe logic
//    }
}
