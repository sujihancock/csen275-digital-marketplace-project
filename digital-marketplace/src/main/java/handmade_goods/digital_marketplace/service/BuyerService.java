package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.repository.user.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepository;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    public Optional<Buyer> getById(Long userId) {
        return buyerRepository.findById(userId);
    }

    public Optional<Buyer> getByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }
}
