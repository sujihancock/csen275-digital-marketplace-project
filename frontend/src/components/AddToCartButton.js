import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useUser } from '../context/UserContext';

const AddToCartButton = ({ product }) => {
    const [isAdding, setIsAdding] = useState(false);
    const [message, setMessage] = useState('');
    const [quantity, setQuantity] = useState(1);
    const { addToCart, error } = useCart();
    const { user, isAuthenticated } = useUser();

    // Check if product is out of stock
    const isOutOfStock = !product.quantity || product.quantity === 0;
    const availableStock = product.quantity || 0;
    
    const handleAddToCart = async () => {
        // Check if user is logged in and is a buyer
        if (!isAuthenticated) {
            setMessage('Please log in to add items to cart');
            setTimeout(() => setMessage(''), 3000);
            return;
        }

        if (user?.role !== 'buyer') {
            setMessage('Only buyers can add items to cart');
            setTimeout(() => setMessage(''), 3000);
            return;
        }

        // NEW: Check if product is out of stock
        if (isOutOfStock) {
            setMessage('This product is out of stock');
            setTimeout(() => setMessage(''), 3000);
            return;
        }

        // NEW: Validate quantity against available stock
        if (quantity > availableStock) {
            setMessage(`Only ${availableStock} item(s) available in stock`);
            setTimeout(() => setMessage(''), 3000);
            return;
        }

        // Validate quantity
        if (quantity < 1 || quantity > 99) {
            setMessage('Quantity must be between 1 and 99');
            setTimeout(() => setMessage(''), 3000);
            return;
        }

        setIsAdding(true);
        setMessage('');

        const success = await addToCart(product.id, quantity);
        
        if (success) {
            setMessage(`Added ${quantity} item${quantity > 1 ? 's' : ''} to cart!`);
            setTimeout(() => setMessage(''), 2000);
            // Reset quantity to 1 after successful add
            setQuantity(1);
        } else {
            setMessage(error || 'Failed to add to cart');
            setTimeout(() => setMessage(''), 3000);
        }

        setIsAdding(false);
    };

    const handleQuantityChange = (e) => {
        const value = parseInt(e.target.value) || 1;
        // NEW: Limit quantity to available stock
        const maxQuantity = Math.min(99, availableStock);
        setQuantity(Math.max(1, Math.min(maxQuantity, value)));
    };

    const incrementQuantity = () => {
        // NEW: Limit quantity to available stock
        const maxQuantity = Math.min(99, availableStock);
        setQuantity(prev => Math.min(maxQuantity, prev + 1));
    };

    const decrementQuantity = () => {
        setQuantity(prev => Math.max(1, prev - 1));
    };

    return (
        <div className="add-to-cart-container">
            {/* NEW: Stock information display */}
            <div className="stock-info">
                {isOutOfStock ? (
                    <span className="out-of-stock-text">❌ Out of Stock</span>
                ) : availableStock <= 5 ? (
                    <span className="low-stock-text">⚠️ Only {availableStock} left in stock</span>
                ) : (
                    <span className="in-stock-text">✅ {availableStock} in stock</span>
                )}
            </div>
            
            <div className="quantity-selector">
                <label htmlFor={`quantity-${product.id}`}>Qty:</label>
                <div className="quantity-input-group">
                    <button 
                        type="button"
                        onClick={decrementQuantity}
                        className="quantity-adjust-btn"
                        disabled={quantity <= 1 || isAdding || isOutOfStock}
                    >
                        -
                    </button>
                    <input
                        id={`quantity-${product.id}`}
                        type="number"
                        min="1"
                        max={Math.min(99, availableStock)}
                        value={quantity}
                        onChange={handleQuantityChange}
                        className="quantity-input"
                        disabled={isAdding || isOutOfStock}
                    />
                    <button 
                        type="button"
                        onClick={incrementQuantity}
                        className="quantity-adjust-btn"
                        disabled={quantity >= Math.min(99, availableStock) || isAdding || isOutOfStock}
                    >
                        +
                    </button>
                </div>
            </div>
            <button 
                onClick={handleAddToCart} 
                disabled={isAdding || isOutOfStock}
                className={`add-to-cart-btn ${isAdding ? 'adding' : ''} ${isOutOfStock ? 'out-of-stock' : ''}`}
            >
                {isOutOfStock 
                    ? '❌ Out of Stock' 
                    : isAdding 
                    ? 'Adding...' 
                    : `🛒 Add ${quantity > 1 ? `${quantity} ` : ''}to Cart`
                }
            </button>
            {message && (
                <div className={`cart-message ${message.includes('Added') ? 'success' : 'error'}`}>
                    {message}
                </div>
            )}
        </div>
    );
};

export default AddToCartButton; 