package handmade_goods.digital_marketplace.service;

import handmade_goods.digital_marketplace.model.order.Order;
import handmade_goods.digital_marketplace.model.order.Payment;
import handmade_goods.digital_marketplace.repository.order.OrderRepository;
import handmade_goods.digital_marketplace.repository.order.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, InventoryService inventoryService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Payment createPaymentForOrder(Order order, String stripePaymentIntentId, Double amount) {
        System.out.println("=== CREATING PAYMENT RECORD ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Transaction ID: " + stripePaymentIntentId);
        System.out.println("Amount: " + amount);
        
        // Check if a payment with this transaction ID already exists
        Optional<Payment> existingPayment = paymentRepository.findByTransactionId(stripePaymentIntentId);
        if (existingPayment.isPresent()) {
            System.out.println("Payment with transaction ID already exists: " + stripePaymentIntentId);
            return existingPayment.get();
        }
        
        // Check existing payments for this order
        List<Payment> existingPaymentsForOrder = paymentRepository.findAllByOrderId(order.getId());
        System.out.println("Existing payments for order " + order.getId() + ": " + existingPaymentsForOrder.size());
        for (Payment p : existingPaymentsForOrder) {
            System.out.println("  - Payment ID: " + p.getid() + ", Transaction: " + p.getTransactionId() + ", Amount: " + p.getAmount());
        }
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTransactionId(stripePaymentIntentId);
        payment.setAmount(amount);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setPaymentDate(LocalDateTime.now());
        
        try {
            Payment savedPayment = paymentRepository.save(payment);
            System.out.println("Payment record created successfully with ID: " + savedPayment.getid());
            return savedPayment;
        } catch (Exception e) {
            System.err.println("ERROR creating payment record: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create payment record: " + e.getMessage());
        }
    }

    /**
     * Check if all payments for an order are completed
     * @param order the order to check
     * @return true if all payments for this order are completed
     */
    private boolean areAllPaymentsCompletedForOrder(Order order) {
        List<Payment> allPaymentsForOrder = paymentRepository.findAllByOrderId(order.getId());
        
        // If no payments exist, return false
        if (allPaymentsForOrder.isEmpty()) {
            return false;
        }
        
        // Check if all payments are completed
        return allPaymentsForOrder.stream()
                .allMatch(payment -> payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED);
    }

    /**
     * Mark a payment as completed
     * @param transactionId the Stripe payment intent ID
     */
    @Transactional
    public void markPaymentAsCompleted(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction ID: " + transactionId));
        
        if (payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
            System.out.println("Payment already completed for transaction: " + transactionId);
            return;
        }
        
        System.out.println("Updating payment status from " + payment.getPaymentStatus() + " to COMPLETED");
        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        System.out.println("Payment status updated to COMPLETED");
        
        try {
            Order order = payment.getOrder();
            if (order != null) {
                System.out.println("Checking if all payments are completed for order: " + order.getId());
                
                // Only update order status and reduce inventory when ALL payments for this order are completed
                if (areAllPaymentsCompletedForOrder(order)) {
                    System.out.println("All payments completed for order " + order.getId() + ". Updating order status...");
                    
                    if (order.getStatus() == Order.OrderStatus.PENDING) {
                        System.out.println("Updating order status from PENDING to SUCCESS");
                        order.setStatus(Order.OrderStatus.SUCCESS);
                        orderRepository.save(order);
                        System.out.println("Order status updated to SUCCESS");
                        
                        // Reduce inventory after successful payment completion of entire order
                        try {
                            inventoryService.reduceInventoryForOrder(order);
                            System.out.println("Inventory successfully reduced for order: " + order.getId());
                        } catch (Exception e) {
                            System.err.println("ERROR: Failed to reduce inventory for order " + order.getId() + ": " + e.getMessage());
                            // Log the error but don't fail the payment - inventory reduction is separate concern
                        }
                    } else {
                        System.out.println("Order is not in PENDING status. Current status: " + order.getStatus());
                    }
                } else {
                    System.out.println("Not all payments completed yet for order " + order.getId() + ". Order status remains PENDING.");
                }
            } else {
                System.out.println("Order is null for payment: " + transactionId);
            }
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            // Don't fail the payment completion if order update fails
        }
    }

    /**
     * Mark a payment as failed
     * @param transactionId the Stripe payment intent ID
     */
    @Transactional
    public void markPaymentAsFailed(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction ID: " + transactionId));
        
        if (payment.getPaymentStatus() == Payment.PaymentStatus.FAILED) {
            System.out.println("Payment already marked as failed for transaction: " + transactionId);
            return;
        }
        
        System.out.println("Updating payment status from " + payment.getPaymentStatus() + " to FAILED");
        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(payment);
        System.out.println("Payment status updated to FAILED");
        
        try {
            // Also update the order status to FAILED
            Order order = payment.getOrder();
            if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
                System.out.println("Updating order status from PENDING to FAILED");
                order.setStatus(Order.OrderStatus.FAILED);
                orderRepository.save(order);
                System.out.println("Order status updated to FAILED");
            }
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            // Don't fail the payment failure processing if order update fails
        }
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId);
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
} 