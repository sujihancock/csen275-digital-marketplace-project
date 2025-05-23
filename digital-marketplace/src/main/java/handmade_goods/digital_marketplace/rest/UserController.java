package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.LoginRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        return userService.getByLoginCredentials(loginRequest)
                .map(user -> {
                    httpSession.setAttribute("user", user);
                    return ResponseEntity.ok(ApiResponse.success("user logged in"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("incorrect username or password")));
    }

    @PostMapping(path = "/signup/{type}")
    public ResponseEntity<ApiResponse<String>> signup(@PathVariable String type, @RequestParam String username, @RequestParam String email, @RequestParam String password, HttpSession httpSession) {
        if (userService.isEmailTaken(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("email already in use"));
        }

        if (userService.isUsernameTaken(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("username already in use"));
        }

        User user;
        switch (type) {
            case "buyer":
                user = new Buyer(username, password, email);
                break;
            case "seller":
                user = new Seller(username, password, email);
                break;
            default:
                return ResponseEntity.badRequest().body(ApiResponse.error("page not found"));
        }

        httpSession.setAttribute("user", user);

        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(type + " created"));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        httpSession.invalidate();
        return ResponseEntity.ok(ApiResponse.success("user logged out"));
    }
}