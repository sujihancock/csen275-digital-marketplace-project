import React from 'react';

function CheckoutButton({product}) {
  const handleCheckout = async () => {
    try {
      const item = {
        name: product.title,
        amount: Math.round(product.price * 100),
        quantity: 1,
        currency: 'usd',
      };

      const response = await fetch('http://localhost:8080/api/payment/create-checkout-session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },

       body: JSON.stringify(item)
      });

      const data = await response.json();
      console.log('Stripe response:', data);
      if (data.status === 'SUCCESS') {
        window.location.href = data.sessionUrl;
      } else {
        alert('Failed to create checkout session.');
        console.error(data.message);
      }
    } catch (error) {
      console.error('Error during checkout:', error);
    }
  };

  return (
    <button onClick={handleCheckout}>
      Checkout
    </button>
  );
}

export default CheckoutButton;
