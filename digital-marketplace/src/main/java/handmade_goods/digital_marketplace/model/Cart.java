package handmade_goods.digital_marketplace.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private double amount = 0.0;
    private final Map<Long, Integer> products = new HashMap<>();

    public Cart() {
    }

    public void addToCart(Product product, int quantity) {
        Long productId = product.getId();
        products.put(productId, products.getOrDefault(productId, 0) + quantity);
        amount += quantity * product.getPrice();
    }

    public void removeFromCart(Product product, int quantity) {
        Long productId = product.getId();
        int remainingQuantity = products.get(productId) - quantity;
        if (remainingQuantity <= 0) {
            products.remove(productId);
        } else {
            products.put(productId, remainingQuantity);
        }
        amount -= quantity * product.getPrice();
    }

    public void clearCart() {
        products.clear();
        amount = 0.0;
    }

    public Map<Long, Integer> getProducts() {
        return products;
    }

    public double getAmount() {
        return amount;
    }
}
