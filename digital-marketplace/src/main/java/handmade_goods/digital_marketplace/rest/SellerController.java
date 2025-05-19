package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.product.AddRequest;
import handmade_goods.digital_marketplace.model.review.Review;
import handmade_goods.digital_marketplace.model.review.SellerReview;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/seller")
public class SellerController {

    private final SellerService sellerService;

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping(path = "/{id}/add")
    public ApiResponse<String> add(@PathVariable Long id, @RequestBody AddRequest product) {
        return sellerService.getById(id)
                .map(seller -> {
                    sellerService.addProduct(seller, product);
                    return ApiResponse.success("product added");
                }).orElseGet(() -> ApiResponse.error("page not found"));
    }

    @GetMapping(path = "/{id}/reviews")
    public ApiResponse<?> getReviews(@PathVariable Long id) {
        Optional<Seller> seller = sellerService.getById(id);
        return seller.isPresent() ? ApiResponse.success(sellerService.getReviews(seller.get())) : ApiResponse.error("page not found");
    }
}
