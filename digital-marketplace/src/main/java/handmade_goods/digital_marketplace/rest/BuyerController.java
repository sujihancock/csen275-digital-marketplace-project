package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.Buyer;
import handmade_goods.digital_marketplace.model.User;
import handmade_goods.digital_marketplace.service.BuyerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "api/buyers")
public class BuyerController {

    private final BuyerService buyerService;
    private final HttpSession httpSession;

    @Autowired
    public BuyerController(BuyerService buyerService, HttpSession httpSession) {
        this.buyerService = buyerService;
        this.httpSession = httpSession;
    }
}
