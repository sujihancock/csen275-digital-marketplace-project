package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.UpdateRequest;
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

import java.util.List;

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

    /**
     * View an individual seller
     *
     * @param id identifies a seller in the database
     * @return id, username, email, an array of seller reviews (id, comment, rating, date, reviewer (id, username), an
     * array of products (id, name, price, image url)
     **/
    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getSeller(@PathVariable Long id) {
        Seller.Dto seller = sellerService.getSellerDtoById(id);
        return seller != null ? ResponseEntity.ok(ApiResponse.success(seller)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id)));
    }

    /**
     * View all the products of the seller signed in to the application
     *
     * @return an array of products (id, name, description, price, image url, seller (id, username), product
     * reviews (id, comment, rating, date, reviewer (id, username)
     **/
    @GetMapping(path = "/products")
    public ResponseEntity<ApiResponse<?>> viewProducts(HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        try {
            System.out.println("DEBUG: Fetching products for seller: " + seller.getId());
            List<Product.Dto> products = sellerService.getProducts((Seller) seller);
            System.out.println("DEBUG: Found " + products.size() + " products");
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            System.err.println("ERROR in viewProducts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error fetching products: " + e.getMessage()));
        }
    }

    /**
     * Add a productRequest to the listing of the seller signed in to the application
     *
     * @param productRequest contains name, description, price, image url, category
     * @return a status message
     **/
    @PostMapping(path = "/products/add")
    public ResponseEntity<ApiResponse<String>> addProduct(@RequestBody AddRequest productRequest, HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        try {
            sellerService.addProduct((Seller) seller, productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product added"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update a product of the seller signed in to the application
     *
     * @param id identifies a product in the database
     * @param updateRequest contains name, description, price, image url, category, quantity
     * @return a status message
     **/
    @PutMapping(path = "/products/{id}")
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable Long id, @RequestBody UpdateRequest updateRequest, HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        try {
            sellerService.updateProduct((Seller) seller, id, updateRequest);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("product updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Remove a product from the listing of the seller signed in to the application
     *
     * @param id identifies a product in the database
     * @return a status message
     **/
    @DeleteMapping(path = "/products/{id}/remove")
    public ResponseEntity<ApiResponse<String>> removeProduct(@PathVariable Long id, HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        try {
            sellerService.removeProduct((Seller) seller, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product removed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get the reviews of the seller signed in to the application
     *
     * @return an array of reviews (id, comment, rating, date, reviewer (id, username)
     **/
    @GetMapping(path = "/reviews")
    public ResponseEntity<ApiResponse<?>> getReviews(HttpSession httpSession) {
        User seller = (User) httpSession.getAttribute("user");
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        try {
            return ResponseEntity.ok(ApiResponse.success(sellerService.getReviews((Seller) seller)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Add a review for a seller as a buyer signed in to the application
     *
     * @param id identifies a seller in the database
     * @param reviewRequest contains comment, rating, date
     * @return a status message
     **/
    @PostMapping(path = "/{id}/reviews/add")
    public ResponseEntity<ApiResponse<String>> addReview(@PathVariable Long id, @RequestBody ReviewRequest reviewRequest, HttpSession httpSession) {
        User reviewer = (User) httpSession.getAttribute("user");
        if (reviewer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }

        return sellerService.getById(id)
                .map(seller -> {
                    sellerService.addReview(seller, (Buyer) reviewer, reviewRequest);
                    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("review added"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id))));
    }
}
