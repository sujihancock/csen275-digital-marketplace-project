package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.dto.CartDto;
import handmade_goods.digital_marketplace.dto.CartItemDto;
import handmade_goods.digital_marketplace.dto.CartRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Cart;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.user.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository, ProductRepository productRepository) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
    }

    public boolean exists(Long id) {
        return buyerRepository.existsById(id);
    }

    public Optional<Buyer> getById(Long id) {
        return buyerRepository.findById(id);
    }

    public Buyer.Dto getBuyerDtoById(Long id) {
        return buyerRepository.findById(id).map(Buyer::convertToDto).orElse(null);
    }

    public Optional<Buyer> getByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }

    public CartDto getCartItems(Cart cart) {
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        Map<Long, Integer> cartItems = cart.getProducts();
        for (Long id : cartItems.keySet()) {
            Optional<Product> product = productRepository.findById(id);
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found");
            }

            cartItemDtos.add(new CartItemDto(product.get().summarize(), cartItems.get(id)));
        }

        return new CartDto(cartItemDtos, cart.getAmount());
    }

    public void addItem(Cart cart, CartRequest cartRequest) {
        Optional<Product> product = productRepository.findById(cartRequest.id());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        cart.addToCart(product.get(), cartRequest.quantity());
    }

    public void removeItem(Cart cart, CartRequest cartRequest) {
        Optional<Product> product = productRepository.findById(cartRequest.id());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        cart.removeFromCart(product.get(), cartRequest.quantity());
    }

    public void clearCart(Cart cart) {
        cart.clearCart();
    }

    public Map<Long, Double> getPaymentsToSellers(Cart cart) {
        Map<Long, Double> payments = new HashMap<>();

        Map<Long, Integer> cartItems = cart.getProducts();
        for (Long id : cartItems.keySet()) {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found");
            }

            Product product = productOpt.get();
            Long sellerId = product.getSeller().getId();
            payments.put(sellerId, payments.getOrDefault(sellerId, 0.0) + cartItems.get(id) * product.getPrice());
        }

        return payments;
    }
}
