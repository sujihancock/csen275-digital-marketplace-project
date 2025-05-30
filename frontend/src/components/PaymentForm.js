import React, { useState } from "react";
import { CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { useNavigate } from 'react-router-dom';
import {useCart} from "../context/CartContext";

export default function PaymentForm({ clientSecrets }) {
    const stripe = useStripe();
    const elements = useElements();
    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [isProcessing, setIsProcessing] = useState(false);
    const { totalAmount, clearCart } = useCart();

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
            const { error: pmError, paymentMethod } = await stripe.createPaymentMethod({
                type: "card",
                card: cardElement,
            });

            if (pmError) {
                throw new Error(`Payment Method Error: ${pmError.message}`);
            }

            for (const { clientSecret, sellerStripeId } of clientSecrets) {
                setMessage(`Processing payment for seller ${sellerStripeId}...`);

                const { error: confirmError } = await stripe.confirmCardPayment(clientSecret, {
                    payment_method: paymentMethod.id,
                });

                if (confirmError) {
                    throw new Error(`Payment failed for seller ${sellerStripeId}: ${confirmError.message}`);
                }
            }
            setMessage("✅ Payment successful! Thank you for your purchase.");
            await clearCart();
            setTimeout(() => navigate('/order-history'), 1500);
        } catch (err) {
            setMessage(`❌ ${err.message}`);
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
