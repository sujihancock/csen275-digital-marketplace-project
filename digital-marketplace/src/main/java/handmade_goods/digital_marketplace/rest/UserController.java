package handmade_goods.digital_marketplace.rest;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<String> login(@RequestParam String username, @RequestParam String password) {
        return userService.getByLoginCredentials(username, password)
                .map(user -> {
                    httpSession.setAttribute("user", user);
                    return ApiResponse.success("user logged in");
                }).orElseGet(() -> ApiResponse.error("incorrect username or password"));
    }

    @PostMapping(path = "/signup/{userType}")
    public ApiResponse<String> signup(@PathVariable String userType, @RequestParam String username, @RequestParam String email, @RequestParam String password) {
        if (userService.isEmailTaken(email)) {
            return ApiResponse.error("email already in use");
        }

        if (userService.isUsernameTaken(username)) {
            return ApiResponse.error("username already in use");
        }

        User user;
        switch (userType) {
            case "buyer":
                user = new Buyer(username, password, email);
                break;
            case "seller":
                user = new Seller(username, password, email);
                break;
            default:
                return ApiResponse.error("page not found");
        }

        userService.save(user);
        return ApiResponse.success(userType + " created");
    }

    @GetMapping(path = "/{id}")
    public ApiResponse<?> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        return user.isPresent() ? ApiResponse.success(user.get()) : ApiResponse.error("user not found");
    }

    @PostMapping(path = "/logout")
    public ApiResponse<String> logout() {
        httpSession.removeAttribute("user");
        return ApiResponse.success("logged out");
    }
}