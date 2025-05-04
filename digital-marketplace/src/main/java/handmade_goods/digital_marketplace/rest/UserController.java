package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.User;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;

    @Autowired
    public UserController(UserService userService, HttpSession httpSession) {
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @GetMapping
    public String getUser() {
        return "User endpoint is working!";
    }
}