package handmade_goods.digital_marketplace.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    private String transactionId;
    private Double amount;
    private LocalDateTime paymentDate;
    

    // Enum for payment status
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    // Enum for payment method
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL
    }

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Constructors
    public Payment() {
    }

    public Payment(Order order, PaymentMethod paymentMethod, PaymentStatus paymentStatus, String transactionId) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    // Getters and Setters
    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", order=" + order +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                '}';
    }
    

    public boolean processPayment(float amount, String buyerId) {
        if (amount <= 0 || buyerId == null || buyerId.isEmpty()) {
            this.paymentStatus = PaymentStatus.FAILED;
            return false;
        }

        this.amount = (double) amount;
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        return true;
    }
}
