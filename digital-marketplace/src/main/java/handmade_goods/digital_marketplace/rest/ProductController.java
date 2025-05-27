package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import handmade_goods.digital_marketplace.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {

    private final ProductService productService;
    private final BuyerService buyerService;

    @Autowired
    public ProductController(ProductService productService, BuyerService buyerService) {
        this.productService = productService;
        this.buyerService = buyerService;
    }

    @GetMapping(path = "/search")
    public ResponseEntity<ApiResponse<List<Product.Summary>>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(ApiResponse.success(productService.search(searchRequest)));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> view(@PathVariable Long id) {
        Product.Dto productDto = productService.getProductDtoById(id);
        return productDto != null ? ResponseEntity.ok(ApiResponse.success(productDto)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found"));
    }

    @GetMapping(path = "/{id}/reviews")
    public ResponseEntity<ApiResponse<?>> reviews(@PathVariable Long id) {
        return productService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(product -> ResponseEntity.ok(ApiResponse.success(productService.getReviews(product))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found")));
    }

    @PostMapping(path = "/{id}/reviews/add")
    public ResponseEntity<ApiResponse<String>> addReviews(@PathVariable Long id, @RequestBody ReviewRequest reviewRequest, HttpSession httpSession) {
        Buyer reviewer = (Buyer) httpSession.getAttribute("user");
        if (reviewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        return productService.getById(id)
                .map(product -> {
                    productService.addReview(product, reviewer, reviewRequest);
                    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("review added"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found")));
    }
}
