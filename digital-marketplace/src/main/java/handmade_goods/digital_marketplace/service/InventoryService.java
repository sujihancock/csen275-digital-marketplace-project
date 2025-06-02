package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.order.OrderItem;
import handmade_goods.digital_marketplace.model.product.Product;
import handmade_goods.digital_marketplace.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Reduce inventory for all products in an order after successful payment
     */
    public void reduceInventoryForOrder(Order order) {
        System.out.println("=== REDUCING INVENTORY FOR ORDER ===");
        System.out.println("Order ID: " + order.getId());
        
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int quantityPurchased = orderItem.getQuantity();
            
            System.out.println("Processing product: " + product.getName() + 
                             ", current stock: " + product.getQuantity() + 
                             ", purchased: " + quantityPurchased);
            
            // Reduce the product quantity
            int newQuantity = product.getQuantity() - quantityPurchased;
            if (newQuantity < 0) {
                newQuantity = 0; // Prevent negative inventory
                System.out.println("WARNING: Inventory went negative for product: " + product.getName());
            }
            
            product.setQuantity(newQuantity);
            productRepository.save(product);
            
            System.out.println("Updated stock for " + product.getName() + 
                             " from " + (newQuantity + quantityPurchased) + 
                             " to " + newQuantity);
        }
        
        System.out.println("Inventory reduction completed for order: " + order.getId());
    }

    /**
     * Validate that all products in an order have sufficient stock before checkout
     */
    public void validateStockForOrder(Order order) {
        System.out.println("=== VALIDATING STOCK FOR ORDER ===");
        System.out.println("Order ID: " + order.getId());
        
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int requestedQuantity = orderItem.getQuantity();
            
            // Refresh product from database to get latest stock
            Optional<Product> latestProductOpt = productRepository.findById(product.getId());
            if (latestProductOpt.isEmpty()) {
                throw new RuntimeException("Product no longer exists: " + product.getName());
            }
            
            Product latestProduct = latestProductOpt.get();
            System.out.println("Checking product: " + latestProduct.getName() + 
                             ", available: " + latestProduct.getQuantity() + 
                             ", requested: " + requestedQuantity);
            
            if (latestProduct.getQuantity() == null || latestProduct.getQuantity() < requestedQuantity) {
                throw new RuntimeException("Insufficient stock for product: " + latestProduct.getName() + 
                                         ". Available: " + (latestProduct.getQuantity() != null ? latestProduct.getQuantity() : 0) + 
                                         ", Requested: " + requestedQuantity);
            }
        }
        
        System.out.println("Stock validation completed successfully for order: " + order.getId());
    }

    /**
     * Check if a product is available for purchase
     */
    public boolean isProductAvailable(Long productId, int requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }
        
        Product product = productOpt.get();
        return product.getQuantity() != null && product.getQuantity() >= requestedQuantity;
    }

    /**
     * Get available quantity for a product
     */
    public int getAvailableQuantity(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return 0;
        }
        
        Product product = productOpt.get();
        return product.getQuantity() != null ? product.getQuantity() : 0;
    }
} 