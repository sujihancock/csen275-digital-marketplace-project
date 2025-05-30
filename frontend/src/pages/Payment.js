import React from "react";
import { useLocation } from "react-router-dom";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import PaymentForm from "../components/PaymentForm";

const stripePromise = loadStripe("pk_test_51RSQlJPdMCn0FqeXQIGoZ0gUAAtsLuk74QTOhWiyqgxZYccHpxm2A9oQj7cze9Ke12RO86dqjZtmL5dbhpvkXmD200gj2BVSFG");

export default function Payment() {
    const location = useLocation();
    const clientSecrets = location.state?.paymentData;

    if (!clientSecrets || !Array.isArray(clientSecrets) || clientSecrets.length === 0) {
        return <div>Error: No payment data found. Please return to checkout.</div>;
    }

    return (
        <Elements stripe={stripePromise}>
            <PaymentForm clientSecrets={clientSecrets} />
        </Elements>
    );
}