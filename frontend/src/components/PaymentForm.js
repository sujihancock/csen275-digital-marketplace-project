import React, { useState } from "react";
import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { useNavigate } from 'react-router-dom';
import { useCart } from "../context/CartContext";

export default function PaymentForm({ clientSecrets }) {
    const stripe = useStripe();
    const elements = useElements();
    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [isProcessing, setIsProcessing] = useState(false);
    const { totalAmount, clearCart } = useCart();

    const confirmPaymentInBackend = async (paymentIntentIds) => {
        try {
            console.log('=== CONFIRMING PAYMENT IN BACKEND ===');
            console.log('Payment Intent IDs to confirm:', paymentIntentIds);
            
            const response = await fetch('/api/payment/confirm-payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(paymentIntentIds),
            });

            console.log('Response status:', response.status);
            console.log('Response OK:', response.ok);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Error response:', errorText);
                throw new Error('Failed to confirm payment in backend: ' + errorText);
            }

            const result = await response.json();
            console.log('Payment confirmed in backend successfully:', result);
        } catch (error) {
            console.error('Error confirming payment in backend:', error);
            throw error; // Re-throw to handle in the main flow
        }
    };

    const notifyPaymentFailureInBackend = async (paymentIntentIds) => {
        try {
            console.log('=== NOTIFYING PAYMENT FAILURE IN BACKEND ===');
            console.log('Payment Intent IDs to mark as failed:', paymentIntentIds);
            
            const response = await fetch('/api/payment/fail-payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(paymentIntentIds),
            });

            console.log('Failure notification response status:', response.status);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Error notifying payment failure:', errorText);
                // Don't throw error here - we still want to show the original payment error to user
            } else {
                const result = await response.json();
                console.log('Payment failure notified successfully:', result);
            }
        } catch (error) {
            console.error('Error notifying payment failure in backend:', error);
            // Don't throw error here - we still want to show the original payment error to user
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setIsProcessing(true);

        if (!stripe || !elements) {
            setMessage("Stripe.js has not loaded.");
            setIsProcessing(false);
            return;
        }

        const cardElement = elements.getElement(CardElement);
        if (!cardElement) {
            setMessage("Card element not found.");
            setIsProcessing(false);
            return;
        }

        try {
            console.log('=== STARTING PAYMENT PROCESSING ===');
            console.log('Client secrets received:', clientSecrets);
            
            const { error: pmError, paymentMethod } = await stripe.createPaymentMethod({
                type: "card",
                card: cardElement,
            });

            if (pmError) {
                throw new Error(`Payment Method Error: ${pmError.message}`);
            }

            console.log('Payment method created successfully:', paymentMethod.id);
            const paymentIntentIds = [];
            const failedPaymentIntentIds = [];
            
            for (const { clientSecret, sellerStripeId, paymentIntentId } of clientSecrets) {
                console.log(`Processing payment for seller ${sellerStripeId}, PaymentIntent ID: ${paymentIntentId}`);
                setMessage(`Processing payment for seller ${sellerStripeId}...`);

                const { error: confirmError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                    payment_method: paymentMethod.id,
                });

                if (confirmError) {
                    console.error(`Payment failed for seller ${sellerStripeId}:`, confirmError);
                    
                    // Check if this is just a state issue (already succeeded)
                    if (confirmError.code === 'payment_intent_unexpected_state' && 
                        confirmError.payment_intent && 
                        confirmError.payment_intent.status === 'succeeded') {
                        console.log(`Payment already succeeded for seller ${sellerStripeId}, continuing...`);
                        paymentIntentIds.push(paymentIntentId);
                        continue;
                    }
                    
                    // Add to failed payments list for backend notification
                    failedPaymentIntentIds.push(paymentIntentId);
                    
                    // Notify backend about failed payments if any
                    if (failedPaymentIntentIds.length > 0) {
                        await notifyPaymentFailureInBackend(failedPaymentIntentIds);
                    }
                    
                    throw new Error(`Payment failed for seller ${sellerStripeId}: ${confirmError.message}`);
                }
                
                console.log(`Payment confirmed with Stripe for seller ${sellerStripeId}. Status: ${paymentIntent.status}`);
                // Collect payment intent IDs for backend confirmation
                paymentIntentIds.push(paymentIntentId);
            }
            
            console.log('All Stripe payments confirmed. Payment Intent IDs:', paymentIntentIds);
            setMessage("Payment successful! Updating order status...");
            
            // Confirm payment completion in backend
            await confirmPaymentInBackend(paymentIntentIds);
            
            setMessage("Payment successful! Thank you for your purchase.");
            await clearCart();
            setTimeout(() => navigate('/order-history'), 1500);
        } catch (err) {
            console.error('Payment processing error:', err);
            setMessage(`${err.message}`);
        } finally {
            setIsProcessing(false);
        }
    };

    return (
        <form className="payment-container" onSubmit={handleSubmit}>
            <CardElement
                options={{
                    style: {
                        base: {
                            fontSize: "16px",
                            color: "#32325d",
                        },
                    },
                    hidePostalCode: true,
                }}
            />

            <div className="make-purchase">
                <h3>Total: ${totalAmount}</h3>
                <button type="submit" disabled={!stripe || isProcessing}>
                    {isProcessing ? "Processing..." : "Pay"}
                </button>
            </div>

            {message && <p className="message">{message}</p>}
        </form>
    );
}
