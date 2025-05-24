package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.dto.UserProfileDto;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.LoginRequest;
import handmade_goods.digital_marketplace.model.user.Seller;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.repository.user.BuyerRepository;
import handmade_goods.digital_marketplace.repository.user.SellerRepository;
import handmade_goods.digital_marketplace.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    @Autowired
    public UserService(UserRepository userRepository, BuyerRepository buyerRepository, SellerRepository sellerRepository) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    public Optional<User> getByLoginCredentials(LoginRequest loginRequest) {
        return userRepository.findByUsernameAndPassword(loginRequest.username(), loginRequest.password());
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public UserProfileDto getUserProfile(User user) {
        String role;
        if (buyerRepository.findById(user.getId()).isPresent()) {
            role = "buyer";
        } else if (sellerRepository.findById(user.getId()).isPresent()) {
            role = "seller";
        } else {
            role = "unknown";
        }
        
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), role);
    }

    public User updateUsername(Long userId, String newUsername) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // Check if the new username is the same as current username
        if (user.getUsername().equals(newUsername)) {
            return user; // No change needed
        }
        
        // Check if the new username is already taken by another user
        Optional<User> existingUser = userRepository.findByUsername(newUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            throw new RuntimeException("Username '" + newUsername + "' is already taken. Please choose a different username.");
        }
        
        user.setUsername(newUsername);
        return userRepository.save(user);
    }
}
