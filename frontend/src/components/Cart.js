import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useUser } from '../context/UserContext';
import { payment, orders } from '../services/api';
import { useNavigate } from "react-router-dom";

const Cart = ({ isOpen, onClose }) => {
    const { cartItems, totalAmount, removeFromCart, clearCart, loading, addToCart } = useCart();
    const { user } = useUser();
    const [checkingOut, setCheckingOut] = useState(false);
    const navigate = useNavigate();

    const handleRemoveItem = async (productId, quantity) => {
        await removeFromCart(productId, quantity);
    };

    const handleAddOneMore = async (productId) => {
        await addToCart(productId, 1);
    };

    const handleClearCart = async () => {
        if (window.confirm('Are you sure you want to clear your cart?')) {
            await clearCart();
        }
    };

    const handleCheckoutCart = async () => {
        if (cartItems.length === 0) {
            alert('Your cart is empty');
            return;
        }

        setCheckingOut(true);
        try {
            // Create the order FIRST so we have an order ID for payment linking
            const orderResponse = await orders.saveOrder();
            if (orderResponse && orderResponse.data.status === 'error') {
                 throw new Error('Failed to set up order');
            }

            // Then use the backend's Stripe checkout endpoint (which can now link payments to the order)
            const response = await payment.checkout();
            
            if (response.data.status === 'success') {
                const paymentData = response.data.data;

                if (paymentData && Object.keys(paymentData).length > 0) {
                    navigate('/payment', { state: { paymentData } });
                    onClose();
                } else {
                    alert('No payment required');
                    onClose();
                }
            } else {
                alert('Failed to create checkout session.');
                console.error(response.data.message);
            }
        } catch (error) {
            console.error('Error during checkout:', error);
            alert('Checkout failed. Please try again.');
        } finally {
            setCheckingOut(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="cart-overlay" onClick={onClose}>
            <div className="cart-modal" onClick={(e) => e.stopPropagation()}>
                <div className="cart-header">
                    <h2>🛒 Shopping Cart</h2>
                    <button className="close-btn" onClick={onClose}>×</button>
                </div>
                
                <div className="cart-content">
                    {loading ? (
                        <div className="cart-loading">Loading cart...</div>
                    ) : cartItems.length === 0 ? (
                        <div className="empty-cart">
                            <p>Your cart is empty</p>
                            <p>Add some products to get started!</p>
                        </div>
                    ) : (
                        <>
                            <div className="cart-items">
                                {cartItems.map((item) => (
                                    <div key={item.productSummary.id} className="cart-item">
                                        <div className="item-info">
                                            <h4>{item.productSummary.name}</h4>
                                            <p className="item-price">${item.productSummary.price.toFixed(2)} each</p>
                                            <p className="item-category">{item.productSummary.category}</p>
                                        </div>
                                        <div className="item-controls">
                                            <div className="cart-quantity-controls">
                                                <button 
                                                    onClick={() => handleRemoveItem(item.productSummary.id, 1)}
                                                    className="cart-quantity-btn"
                                                    disabled={loading}
                                                    title="Remove one item"
                                                >
                                                    -
                                                </button>
                                                <span className="cart-quantity">{item.quantity}</span>
                                                <button 
                                                    onClick={() => handleAddOneMore(item.productSummary.id)}
                                                    className="cart-quantity-btn"
                                                    disabled={loading}
                                                    title="Add one more"
                                                >
                                                    +
                                                </button>
                                                <button 
                                                    onClick={() => handleRemoveItem(item.productSummary.id, item.quantity)}
                                                    className="remove-all-btn"
                                                    disabled={loading}
                                                    title="Remove all of this item"
                                                >
                                                    Remove All
                                                </button>
                                            </div>
                                            <div className="item-total">
                                                ${item.totalPrice.toFixed(2)}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                            
                            <div className="cart-summary">
                                <div className="total-amount">
                                    <strong>Total: ${totalAmount.toFixed(2)}</strong>
                                </div>
                                <div className="cart-actions">
                                    <button 
                                        onClick={handleClearCart}
                                        className="clear-cart-btn"
                                        disabled={loading}
                                    >
                                        Clear Cart
                                    </button>
                                    <button 
                                        onClick={handleCheckoutCart}
                                        className="checkout-cart-btn"
                                        disabled={loading || checkingOut}
                                    >
                                        {checkingOut ? 'Processing...' : 'Checkout Cart'}
                                    </button>
                                </div>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Cart; 