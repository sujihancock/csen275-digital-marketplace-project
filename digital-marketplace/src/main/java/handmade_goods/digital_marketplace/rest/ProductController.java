package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.review.ReviewRequest;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.payload.ApiResponse;
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

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products for home page display
     *
     * @return a list of all products with basic info (id, name, price, image url, quantity)
     **/
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product.Summary>>> getAllProducts() {
        // Create an empty search request to get all products
        SearchRequest emptySearch = new SearchRequest(List.of(), List.of());
        return ResponseEntity.ok(ApiResponse.success(productService.search(emptySearch)));
    }

    /**
     * Get all products based on search by keywords and/or categories
     *
     * @param searchRequest contains an array of keywords and an array of categories
     * @return a list of search results, each containing a product's id, name, price, and image url
     **/
    @PostMapping(path = "/search")
    public ResponseEntity<ApiResponse<List<Product.Summary>>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(ApiResponse.success(productService.search(searchRequest)));
    }

    /**
     * View an individual product
     *
     * @param id identifies the product in the database
     * @return id, name, image, description, price, image url, seller (id, username), and an array of product
     * reviews (id, comment, rating, date, reviewer (id, username)
     **/
    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> view(@PathVariable Long id) {
        Product.Dto productDto = productService.getProductDtoById(id);
        return productDto != null ? ResponseEntity.ok(ApiResponse.success(productDto)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found"));
    }

    /**
     * View the reviews of an individual product
     *
     * @param id identifies a product in the database
     * @return a list of the product's reviews (id, comment, rating, date, reviewer (id, username)
     **/
    @GetMapping(path = "/{id}/reviews")
    public ResponseEntity<ApiResponse<?>> reviews(@PathVariable Long id) {
        return productService.getById(id)
                .<ResponseEntity<ApiResponse<?>>>map(product -> ResponseEntity.ok(ApiResponse.success(productService.getReviews(product))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found")));
    }

    /**
     * Add a review for an individual product as a buyer signed in to the application
     *
     * @param id identifies a product in the database
     * @param reviewRequest includes comment, rating, and date
     * @return a status message
     **/
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

    /**
     * Get all the different categories of products
     *
     * @return a list of all categories
     **/
    @GetMapping(path = "/categories")
    public ResponseEntity<ApiResponse<?>> categories() {
        return ResponseEntity.ok(ApiResponse.success(productService.getCategories()));
    }
}
