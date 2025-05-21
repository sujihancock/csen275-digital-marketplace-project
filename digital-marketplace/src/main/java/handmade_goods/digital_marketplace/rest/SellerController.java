package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/seller")
public class SellerController {

    private final SellerService sellerService;


    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    private String notFound(Long id) {
        return "seller with id: " + id + " not found";
    }

    @PostMapping(path = "/{id}/add")
    public ResponseEntity<ApiResponse<String>> add(@PathVariable Long id, @RequestBody AddRequest product) {
        return sellerService.getById(id)
                .map(seller -> {
                    sellerService.addProduct(seller, product);
                    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product added"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id))));
    }

    @GetMapping(path = "/{id}/reviews")
    public ResponseEntity<ApiResponse<?>>getReviews(@PathVariable Long id) {
        Optional<Seller> seller = sellerService.getById(id);
        return seller.<ResponseEntity<ApiResponse<?>>>map(value -> ResponseEntity.ok().body(ApiResponse.success(sellerService.getReviews(value)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(notFound(id))));
    }
}
