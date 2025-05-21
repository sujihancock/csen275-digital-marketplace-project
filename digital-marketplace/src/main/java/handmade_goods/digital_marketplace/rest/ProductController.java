package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.ProductService;
import handmade_goods.digital_marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping(path = "/search")
    public ResponseEntity<ApiResponse<List<Product.Summary>>> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(ApiResponse.success(productService.search(searchRequest)));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> view(@PathVariable Long id) {
        Product.Dto productDto = productService.findById(id);
        return productDto != null ? ResponseEntity.ok(ApiResponse.success(productDto)) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("product with id: " + id + " not found"));
    }
}
