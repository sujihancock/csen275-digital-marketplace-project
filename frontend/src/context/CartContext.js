import React, { createContext, useContext, useState, useEffect } from 'react';
import { cart as cartApi } from '../services/api';
import { useUser } from './UserContext';

const CartContext = createContext();

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
};

export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const { user, isAuthenticated } = useUser();

    // Load cart when user logs in as a buyer
    useEffect(() => {
        if (isAuthenticated && user?.role === 'buyer') {
            loadCart();
        } else {
            // Clear cart when user logs out or is not a buyer
            setCartItems([]);
            setTotalAmount(0);
        }
    }, [isAuthenticated, user]);

    const loadCart = async () => {
        if (!user || user.role !== 'buyer') return;
        
        setLoading(true);
        setError(null);
        try {
            const response = await cartApi.getCart();
            const cartData = response.data.data;
            setCartItems(cartData.cartItems || []);
            setTotalAmount(cartData.totalAmount || 0);
        } catch (err) {
            setError('Failed to load cart');
            console.error('Error loading cart:', err);
        } finally {
            setLoading(false);
        }
    };

    const addToCart = async (productId, quantity = 1) => {
        if (!user || user.role !== 'buyer') {
            setError('Please log in as a buyer to add items to cart');
            return false;
        }

        setLoading(true);
        setError(null);
        try {
            await cartApi.addToCart(productId, quantity);
            await loadCart(); // Reload cart to get updated data
            return true;
        } catch (err) {
            setError('Failed to add item to cart');
            console.error('Error adding to cart:', err);
            return false;
        } finally {
            setLoading(false);
        }
    };

    const removeFromCart = async (productId, quantity = 1) => {
        if (!user || user.role !== 'buyer') return false;

        setLoading(true);
        setError(null);
        try {
            await cartApi.removeFromCart(productId, quantity);
            await loadCart(); // Reload cart to get updated data
            return true;
        } catch (err) {
            setError('Failed to remove item from cart');
            console.error('Error removing from cart:', err);
            return false;
        } finally {
            setLoading(false);
        }
    };

    const clearCart = async () => {
        if (!user || user.role !== 'buyer') return false;

        setLoading(true);
        setError(null);
        try {
            await cartApi.clearCart();
            setCartItems([]);
            setTotalAmount(0);
            return true;
        } catch (err) {
            setError('Failed to clear cart');
            console.error('Error clearing cart:', err);
            return false;
        } finally {
            setLoading(false);
        }
    };

    const getCartItemCount = () => {
        return cartItems.reduce((total, item) => total + item.quantity, 0);
    };

    const value = {
        cartItems,
        totalAmount,
        loading,
        error,
        addToCart,
        removeFromCart,
        clearCart,
        loadCart,
        getCartItemCount,
        setError, // Allow components to clear errors
    };

    return (
        <CartContext.Provider value={value}>
            {children}
        </CartContext.Provider>
    );
}; 