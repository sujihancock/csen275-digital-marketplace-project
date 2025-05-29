package handmade_goods.digital_marketplace.rest;

import com.stripe.exception.StripeException;
import handmade_goods.digital_marketplace.dto.UserProfileDto;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.LoginRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.payload.ApiResponse;
import handmade_goods.digital_marketplace.service.BuyerService;
import handmade_goods.digital_marketplace.service.StripeService;
import handmade_goods.digital_marketplace.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;
    private final StripeService stripeService;
    private final BuyerService buyerService;

    @Autowired
    public UserController(UserService userService, StripeService stripeService, BuyerService buyerService) {
        this.userService = userService;
        this.stripeService = stripeService;
        this.buyerService = buyerService;
    }

    /**
     * Signs in the user into the application
     * Now loads persistent cart for buyers
     *
     * @param loginRequest contains username and password
     * @return a status message
     * */
    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) throws StripeException {
        Optional<User> userOpt = userService.getByLoginCredentials(loginRequest);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Load persistent cart for buyers
            if (user instanceof Buyer) {
                Buyer buyer = (Buyer) user;
                buyerService.loadCartFromDatabase(buyer);
            }
            
            httpServletRequest.getSession().setAttribute("user", user);
            UserProfileDto profile = userService.getUserProfile(user);
            return ResponseEntity.ok(ApiResponse.success(profile, "user logged in"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("incorrect username or password"));
        }
    }

    /**
     * Registers the user as either a buyer or a seller
     *
     * @param type is either 'buyer' or 'seller'
     * @param username must be unique to the user
     * @param email must be unique to the user
     * @return a status message
     * */
    @PostMapping(path = "/signup/{type}")
    public ResponseEntity<ApiResponse<String>> signup(@PathVariable String type, @RequestParam String username, @RequestParam String email, @RequestParam String password, HttpSession httpSession) {
        if (userService.isEmailTaken(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("email already in use"));
        }

        if (userService.isUsernameTaken(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("username already in use"));
        }

        User user;
        ApiResponse<String> apiResponse;
        switch (type) {
            case "buyer":
                user = new Buyer(username, password, email);
                apiResponse = ApiResponse.success("buyer created");
                break;
            case "seller":
                try {
                    StripeService.StripeAccount stripeAccount = stripeService.onboardSeller();
                    user = new Seller(username, password, email, stripeAccount.id());
                    apiResponse = ApiResponse.success(stripeAccount.url());
                } catch (StripeException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("error onboarding seller: " + e.getMessage()));
                }
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        httpSession.setAttribute("user", user);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get the profile of the user signed in to the application
     *
     *
     * @return the user's id, username, email, role
     **/
    @GetMapping(path = "/profile")
    public ResponseEntity<ApiResponse<?>> getProfile(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("not logged in"));
        }
        
        UserProfileDto profile = userService.getUserProfile(user);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Update the username of the user signed in the application
     *
     * @return a status message
     **/
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

    /**
     * Signs the user out of the application
     * Now syncs cart to database before logout for buyers
     *
     * @return a status message
     **/
    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        
        // Sync cart to database before logout for buyers
        if (user instanceof Buyer) {
            Buyer buyer = (Buyer) user;
            buyerService.syncCartToDatabase(buyer);
        }
        
        httpSession.removeAttribute("user");
        httpSession.invalidate();
        return ResponseEntity.ok(ApiResponse.success("user logged out"));
    }
}