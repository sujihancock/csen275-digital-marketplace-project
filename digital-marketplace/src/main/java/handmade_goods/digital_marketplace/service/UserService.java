package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.user.LoginRequest;
import handmade_goods.digital_marketplace.model.user.User;
import handmade_goods.digital_marketplace.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
