package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.order.OrderItem;
import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.UpdateRequest;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.review.SellerReview;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.repository.order.OrderRepository;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.review.SellerReviewRepository;
import handmade_goods.digital_marketplace.repository.user.SellerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final SellerReviewRepository sellerReviewRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository, ProductRepository productRepository, SellerReviewRepository sellerReviewRepository,OrderRepository orderRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.sellerReviewRepository = sellerReviewRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<Seller> getById(Long id) {
        return sellerRepository.findById(id);
    }

    public Seller.Dto getSellerDtoById(Long id) {
        return sellerRepository.findById(id).map(Seller::convertToDto).orElse(null);
    }

    public void addProduct(Seller seller, AddRequest productRequest) {
        Product product = new Product(
            productRequest.name(), 
            productRequest.description(), 
            productRequest.price(), 
            seller, 
            productRequest.imageUrl(), 
            productRequest.category(), 
            productRequest.quantity() != null ? productRequest.quantity() : 0
        );
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(Seller seller, Long productId, UpdateRequest updateRequest) {
        Optional<Product> productOpt = productRepository.findByIdAndSeller(productId, seller);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (updateRequest.name() != null) product.setName(updateRequest.name());
            if (updateRequest.description() != null) product.setDescription(updateRequest.description());
            if (updateRequest.price() != null) product.setPrice(updateRequest.price());
            if (updateRequest.imageUrl() != null) product.setImageUrl(updateRequest.imageUrl());
            if (updateRequest.category() != null) product.setCategory(updateRequest.category());
            if (updateRequest.quantity() != null) product.setQuantity(updateRequest.quantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public void removeProduct(Seller seller, Long productId) {
        productRepository.deleteByIdAndSeller(productId, seller);
    }

    public void addReview(Seller seller, Buyer buyer, ReviewRequest review) {
        sellerReviewRepository.save(new SellerReview(review.comment(), review.rating(), review.date(), buyer, seller));
    }

    @Transactional
    public List<SellerReview.Dto> getReviews(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return seller.getReviews().stream()
                .map(SellerReview::convertToDto)
                .toList();
    }

    public List<Product.Dto> getProducts(Seller seller) {
        return productRepository.findBySeller(seller).stream().map(Product::convertToDto).toList();
    }

    public List<Order.Dto> getCustomerOrders(Seller seller) {
        List<Order> orders = orderRepository.findOrdersWithItemsBySeller(seller);

        for (Order order : orders) {
            List<OrderItem> filteredItems = order.getOrderItems().stream()
                    .filter(orderItem -> orderItem.getProduct().getSeller().getId().equals(seller.getId()))
                    .toList();
            order.setOrderItems(filteredItems);
        }

        return orders.stream().map(Order::convertToDto).toList();
    }

}
