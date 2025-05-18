package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.Buyer;
import handmade_goods.digital_marketplace.model.Seller;
import handmade_goods.digital_marketplace.model.User;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        return userService.getByLoginCredentials(username, password)
                .map(user -> {
                    httpSession.setAttribute("user", user);
                    return ResponseEntity.ok(Map.of("status", "success",
                                                        "message", "user logged in"));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(Map.of("status", "error",
                                                        "message", "user not found"))
                );
    }

    private ResponseEntity<Map<String, String>> getSignUpResponse(String username, String email, String password, boolean isBuyer) {
        if (userService.getByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "error",
                            "message", "email already in use"));
        }

        if (userService.getByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "error",
                            "message", "username already exists"));
        }

        User user = isBuyer ? new Buyer(username, password, email) : new Seller(username, password, email);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("status", "success",
                        "message",  (isBuyer ? "buyer" : "seller") + " created"));
    }

    @PostMapping(path = "/signup/buyer")
    public ResponseEntity<Map<String, String>> registerBuyer(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        return getSignUpResponse(username, email, password, true);
    }

    @PostMapping(path = "/signup/seller")
    public ResponseEntity<Map<String, String>> registerSeller(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        return getSignUpResponse(username, email, password, false);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> searchResult = userService.getById(id);
        if (searchResult.isPresent()) {
            return ResponseEntity.ok(searchResult.get());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "error",
                            "message", "user not found"));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Map<String, String>> logout() {
        httpSession.removeAttribute("user");
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "logged out"));
    }
}