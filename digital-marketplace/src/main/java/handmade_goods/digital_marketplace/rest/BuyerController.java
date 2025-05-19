package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/buyers")
public class BuyerController {

    private final BuyerService buyerService;

    @Autowired
    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }


}
