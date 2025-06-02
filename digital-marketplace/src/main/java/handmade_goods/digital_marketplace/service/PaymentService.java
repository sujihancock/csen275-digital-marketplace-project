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
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTransactionId(stripePaymentIntentId);
        payment.setAmount(amount);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setPaymentDate(LocalDateTime.now());
        
        return paymentRepository.save(payment);
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
            // Also update the order status
            Order order = payment.getOrder();
            if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
                System.out.println("Updating order status from PENDING to SUCCESS");
                order.setStatus(Order.OrderStatus.SUCCESS);
                orderRepository.save(order);
                System.out.println("Order status updated to SUCCESS");
                
                // NEW: Reduce inventory after successful payment
                try {
                    inventoryService.reduceInventoryForOrder(order);
                    System.out.println("Inventory successfully reduced for order: " + order.getId());
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to reduce inventory for order " + order.getId() + ": " + e.getMessage());
                    // Log the error but don't fail the payment - inventory reduction is separate concern
                    // In production, you might want to implement compensation logic or manual intervention
                }
            } else {
                System.out.println("Order is null or not in PENDING status. Current status: " + 
                    (order != null ? order.getStatus() : "null"));
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
        return paymentRepository.findByOrderId(orderId).stream().toList();
    }

    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
} 