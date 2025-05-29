package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.dto.CartDto;
import handmade_goods.digital_marketplace.dto.CartItemDto;
import handmade_goods.digital_marketplace.dto.CartRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.model.user.Buyer;
import handmade_goods.digital_marketplace.model.user.Cart;
import handmade_goods.digital_marketplace.model.user.CartItem;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import handmade_goods.digital_marketplace.repository.user.BuyerRepository;
import handmade_goods.digital_marketplace.repository.user.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
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

    // Legacy method for backward compatibility
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

    // New persistent cart methods
    public CartDto getPersistentCartItems(Buyer buyer) {
        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product != null) {
                cartItemDtos.add(new CartItemDto(product.summarize(), cartItem.getQuantity()));
                totalAmount += cartItem.getTotalPrice();
            }
        }

        return new CartDto(cartItemDtos, totalAmount);
    }

    public void addItemToPersistentCart(Buyer buyer, CartRequest cartRequest) {
        Optional<Product> productOpt = productRepository.findById(cartRequest.id());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        Optional<CartItem> existingCartItem = cartItemRepository.findByBuyerAndProduct(buyer, product);

        if (existingCartItem.isPresent()) {
            // Update existing cart item quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.quantity());
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem newCartItem = new CartItem(buyer, product, cartRequest.quantity());
            cartItemRepository.save(newCartItem);
        }
    }

    public void removeItemFromPersistentCart(Buyer buyer, CartRequest cartRequest) {
        Optional<Product> productOpt = productRepository.findById(cartRequest.id());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        Optional<CartItem> existingCartItem = cartItemRepository.findByBuyerAndProduct(buyer, product);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() - cartRequest.quantity();

            if (newQuantity <= 0) {
                // Remove cart item entirely
                cartItemRepository.delete(cartItem);
            } else {
                // Update quantity
                cartItem.setQuantity(newQuantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    public void clearPersistentCart(Buyer buyer) {
        cartItemRepository.deleteByBuyer(buyer);
    }

    public void syncCartToDatabase(Buyer buyer) {
        // Clear existing persistent cart items
        clearPersistentCart(buyer);
        
        // Convert in-memory cart to persistent cart items
        Cart cart = buyer.getCart();
        Map<Long, Integer> cartItems = cart.getProducts();
        
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                CartItem cartItem = new CartItem(buyer, productOpt.get(), quantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    public void loadCartFromDatabase(Buyer buyer) {
        // Load persistent cart items into in-memory cart
        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);
        Cart cart = new Cart();
        
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product != null) {
                cart.addToCart(product, cartItem.getQuantity());
            }
        }
        
        buyer.setCart(cart);
    }

    // Legacy methods for backward compatibility
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

    public Map<String, Double> calculatePaymentsToSellers(Cart cart) {
        Map<String, Double> payments = new HashMap<>();

        Map<Long, Integer> cartItems = cart.getProducts();
        for (Long id : cartItems.keySet()) {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Product not found");
            }

            Product product = productOpt.get();
            String sellerStripeId = product.getSeller().getStripeAccountId();
            payments.put(sellerStripeId, payments.getOrDefault(sellerStripeId, 0.0) + cartItems.get(id) * product.getPrice());
        }

        return payments;
    }

    // Persistent cart payment calculation
    public Map<String, Double> calculatePaymentsToSellersFromPersistentCart(Buyer buyer) {
        Map<String, Double> payments = new HashMap<>();
        List<CartItem> cartItems = cartItemRepository.findByBuyer(buyer);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product != null) {
                String sellerStripeId = product.getSeller().getStripeAccountId();
                payments.put(sellerStripeId, payments.getOrDefault(sellerStripeId, 0.0) + cartItem.getTotalPrice());
            }
        }

        return payments;
    }
}
