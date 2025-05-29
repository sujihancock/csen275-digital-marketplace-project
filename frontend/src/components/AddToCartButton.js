import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useUser } from '../context/UserContext';

const AddToCartButton = ({ product }) => {
    const [isAdding, setIsAdding] = useState(false);
    const [message, setMessage] = useState('');
    const [quantity, setQuantity] = useState(1);
    const { addToCart, error } = useCart();
    const { user, isAuthenticated } = useUser();

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
        setQuantity(Math.max(1, Math.min(99, value)));
    };

    const incrementQuantity = () => {
        setQuantity(prev => Math.min(99, prev + 1));
    };

    const decrementQuantity = () => {
        setQuantity(prev => Math.max(1, prev - 1));
    };

    return (
        <div className="add-to-cart-container">
            <div className="quantity-selector">
                <label htmlFor={`quantity-${product.id}`}>Qty:</label>
                <div className="quantity-input-group">
                    <button 
                        type="button"
                        onClick={decrementQuantity}
                        className="quantity-adjust-btn"
                        disabled={quantity <= 1 || isAdding}
                    >
                        -
                    </button>
                    <input
                        id={`quantity-${product.id}`}
                        type="number"
                        min="1"
                        max="99"
                        value={quantity}
                        onChange={handleQuantityChange}
                        className="quantity-input"
                        disabled={isAdding}
                    />
                    <button 
                        type="button"
                        onClick={incrementQuantity}
                        className="quantity-adjust-btn"
                        disabled={quantity >= 99 || isAdding}
                    >
                        +
                    </button>
                </div>
            </div>
            <button 
                onClick={handleAddToCart} 
                disabled={isAdding}
                className={`add-to-cart-btn ${isAdding ? 'adding' : ''}`}
            >
                {isAdding ? 'Adding...' : `🛒 Add ${quantity > 1 ? `${quantity} ` : ''}to Cart`}
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