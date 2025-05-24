package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.dto.UserProfileDto;
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

import java.util.Optional;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        Optional<User> userOpt = userService.getByLoginCredentials(loginRequest);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            httpSession.setAttribute("user", user);
            UserProfileDto profile = userService.getUserProfile(user);
            return ResponseEntity.ok(ApiResponse.success(profile, "user logged in"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("incorrect username or password"));
        }
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        return user.<ResponseEntity<ApiResponse<?>>>map(value -> ResponseEntity.ok(ApiResponse.success(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("user not found")));
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }
        
        UserProfileDto profile = userService.getUserProfile(user);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping(path = "/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(@RequestParam String username, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }
        
        try {
            User updatedUser = userService.updateUsername(user.getId(), username);
            httpSession.setAttribute("user", updatedUser); // Update session
            UserProfileDto profile = userService.getUserProfile(updatedUser);
            return ResponseEntity.ok(ApiResponse.success(profile, "username updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("failed to update username: " + e.getMessage()));
        }
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        httpSession.invalidate();
        return ResponseEntity.ok(ApiResponse.success("user logged out"));
    }
}