package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import handmade_goods.digital_marketplace.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final BuyerService buyerService;

    @Autowired
    public SellerController(SellerService sellerService, BuyerService buyerService) {
        this.sellerService = sellerService;
        this.buyerService = buyerService;
    }

    private String notFound(Long id, String userType) {
        return userType + " with id: " + id + " not found";
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getSeller(@PathVariable("id") Long id) {
        Seller.Dto seller = sellerService.getSellerDtoById(id);
        return seller != null ? ResponseEntity.ok(ApiResponse.success(seller)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id, "seller")));
    }

    @GetMapping(path = "/{id}/products")
    public ResponseEntity<ApiResponse<?>> viewProducts(@PathVariable Long id) {
        return sellerService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(seller -> ResponseEntity.ok(ApiResponse.success(sellerService.getProducts(seller))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id, "seller"))));
    }

    @PostMapping(path = "/{id}/products/add")
    public ResponseEntity<ApiResponse<String>> addProduct(@PathVariable Long id, @RequestBody AddRequest product) {
        return sellerService.getById(id)
                .map(seller -> {
                    sellerService.addProduct(seller, product);
                    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product added"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id, "seller"))));
    }

    @GetMapping(path = "/{id}/reviews")
    public ResponseEntity<ApiResponse<?>> getReviews(@PathVariable Long id) {
        return sellerService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(seller -> ResponseEntity.ok(ApiResponse.success(sellerService.getReviews(seller))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id, "seller"))));
    }

    @PostMapping(path = "/{id}/reviews/add")
    public ResponseEntity<ApiResponse<String>> addReview(@PathVariable Long id, @RequestBody ReviewRequest review) {
        Long reviewerId = review.reviewerId();
        return buyerService.getById(reviewerId)
                .map(buyer -> sellerService.getById(id)
                        .map(seller -> {
                            sellerService.addReview(seller, buyer, review);
                            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("review added"));
                        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id, "seller")))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(reviewerId, "buyer"))));
    }
}
