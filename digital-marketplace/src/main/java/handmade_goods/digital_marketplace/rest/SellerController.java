package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.SellerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    private String notFound(Long id) {
        return "seller with id: " + id + " not found";
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getSeller(@PathVariable Long id) {
        Seller.Dto seller = sellerService.getSellerDtoById(id);
        return seller != null ? ResponseEntity.ok(ApiResponse.success(seller)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id)));
    }

    @GetMapping(path = "/{id}/products")
    public ResponseEntity<ApiResponse<?>> viewProducts(@PathVariable Long id) {
        return sellerService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(seller -> ResponseEntity.ok(ApiResponse.success(sellerService.getProducts(seller))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id))));
    }

    @PostMapping(path = "/{id}/products/add")
    public ResponseEntity<ApiResponse<String>> addProduct(@PathVariable Long id, @RequestBody AddRequest product, HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null || !Objects.equals(id, seller.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        sellerService.addProduct((Seller) seller, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product added"));
    }

    @DeleteMapping(path = "/{sellerId}/products/{productId}/remove")
    public ResponseEntity<ApiResponse<String>> removeProduct(@PathVariable Long sellerId, @PathVariable Long productId, HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null || !Objects.equals(sellerId, seller.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }
        sellerService.removeProduct((Seller) seller, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product removed"));
    }

    @GetMapping(path = "/{id}/reviews")
    public ResponseEntity<ApiResponse<?>> getReviews(@PathVariable Long id) {
        return sellerService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(seller -> ResponseEntity.ok(ApiResponse.success(sellerService.getReviews(seller))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id))));
    }

    @PostMapping(path = "/{id}/reviews/add")
    public ResponseEntity<ApiResponse<String>> addReview(@PathVariable Long id, @RequestBody ReviewRequest reviewRequest, HttpSession httpSession) {
        User reviewer = (User) httpSession.getAttribute("user");
        if (reviewer == null || id.equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        return sellerService.getById(id)
                .map(seller -> {
                    sellerService.addReview(seller, (Buyer) reviewer, reviewRequest);
                    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("review added"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("seller with id: " + id + " not found")));
    }
}
