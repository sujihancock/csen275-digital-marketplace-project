package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    private final UserService userService;
    private final HttpSession httpSession;

    @Autowired
    public UserController(UserService userService, HttpSession httpSession) {
        this.userService = userService;
        this.httpSession = httpSession;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestParam String username, @RequestParam String password) {
        return userService.getByLoginCredentials(username, password)
                .map(user -> {
                    httpSession.setAttribute("user", user);
                    return ResponseEntity.ok(ApiResponse.success("user logged in"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("incorrect username or password")));
    }

    @PostMapping(path = "/signup/{type}")
    public ResponseEntity<ApiResponse<String>> signup(@PathVariable String type, @RequestParam String username, @RequestParam String email, @RequestParam String password) {
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

        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(type + " created"));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        return user.<ResponseEntity<ApiResponse<?>>>map(value -> ResponseEntity.ok(ApiResponse.success(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("user not found")));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        httpSession.removeAttribute("user");
        return ResponseEntity.ok(ApiResponse.success("user logged out"));
    }
}