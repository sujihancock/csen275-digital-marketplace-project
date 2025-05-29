import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function AddProduct() {
    const [productName, setProductName] = useState('');
    const [productPrice, setProductPrice] = useState('');
    const [productDescription, setProductDescription] = useState('');
    const [productImage, setProductImage] = useState('');
    const [productCategory, setProductCategory] = useState('');
    const [productQuantity, setProductQuantity] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    // Available categories from backend enum
    const categories = [
        { value: 'JEWELRY', label: 'Jewelry' },
        { value: 'ART', label: 'Art' },
        { value: 'CLOTHING', label: 'Clothing' },
        { value: 'HOME_DECOR', label: 'Home Decor' },
        { value: 'ACCESSORIES', label: 'Accessories' },
        { value: 'STATIONARY', label: 'Stationary' },
        { value: 'ELECTRONICS', label: 'Electronics' },
        { value: 'BOOKS', label: 'Books' },
        { value: 'BEAUTY', label: 'Beauty' },
        { value: 'CUSTOM_GIFTS', label: 'Custom Gifts' },
        { value: 'PET_SUPPLIES', label: 'Pet Supplies' },
        { value: 'OTHER', label: 'Other' }
    ];

    const handleCancel = () => {
        navigate('/manage-products');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        setError('');
        setSuccess('');

        // Validate inputs
        if (!productName.trim()) {
            setError('Product name is required');
            setIsSubmitting(false);
            return;
        }

        const price = parseFloat(productPrice);
        if (isNaN(price) || price <= 0) {
            setError('Please enter a valid price greater than 0');
            setIsSubmitting(false);
            return;
        }

        if (!productDescription.trim()) {
            setError('Product description is required');
            setIsSubmitting(false);
            return;
        }

        if (!productImage.trim()) {
            setError('Product image URL is required');
            setIsSubmitting(false);
            return;
        }

        if (!productCategory) {
            setError('Please select a category');
            setIsSubmitting(false);
            return;
        }

        const quantity = parseInt(productQuantity);
        if (isNaN(quantity) || quantity < 0) {
            setError('Please enter a valid quantity (0 or more)');
            setIsSubmitting(false);
            return;
        }

        const newProduct = {
            name: productName.trim(),
            description: productDescription.trim(),
            price: price,
            imageUrl: productImage.trim(),
            category: productCategory,
            quantity: quantity
        };

        try {
            // Call the backend API to add product
            const response = await api.post('/sellers/products/add', newProduct);
            
            if (response.data.status === 'success') {
                setSuccess('Product added successfully!');
                // Clear form
                setProductName('');
                setProductPrice('');
                setProductDescription('');
                setProductImage('');
                setProductCategory('');
                setProductQuantity('');
                
                // Redirect to seller dashboard or products page after a short delay
                setTimeout(() => {
                    navigate('/manage-products');
                }, 1500);
            } else {
                setError(response.data.message || 'Failed to add product');
            }
        } catch (error) {
            console.error('Error adding product:', error);
            if (error.response) {
                // Server responded with error status
                setError(error.response.data?.message || `Server error: ${error.response.status}`);
            } else if (error.request) {
                // Request was made but no response received
                setError('Unable to connect to server. Please check your connection.');
            } else {
                // Something else happened
                setError('An unexpected error occurred. Please try again.');
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="add-product-container">
            <h2>Add New Product</h2>
            
            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}
            
            {success && (
                <div className="success-message">
                    {success}
                </div>
            )}
            
            <form onSubmit={handleSubmit} className="add-product-form">
                <div className="form-group">
                    <label htmlFor="productName">Product Name:</label>
                    <input
                        id="productName"
                        type="text"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        required
                        disabled={isSubmitting}
                        placeholder="Enter product name"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="productPrice">Price ($):</label>
                    <input
                        id="productPrice"
                        type="number"
                        step="0.01"
                        min="0.01"
                        value={productPrice}
                        onChange={(e) => setProductPrice(e.target.value)}
                        required
                        disabled={isSubmitting}
                        placeholder="0.00"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="productDescription">Description:</label>
                    <textarea
                        id="productDescription"
                        value={productDescription}
                        onChange={(e) => setProductDescription(e.target.value)}
                        required
                        disabled={isSubmitting}
                        placeholder="Describe your product..."
                        rows="4"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="productCategory">Category:</label>
                    <select
                        id="productCategory"
                        value={productCategory}
                        onChange={(e) => setProductCategory(e.target.value)}
                        required
                        disabled={isSubmitting}
                    >
                        <option value="">Select a category</option>
                        {categories.map((cat) => (
                            <option key={cat.value} value={cat.value}>
                                {cat.label}
                            </option>
                        ))}
                    </select>
                </div>
                
                <div className="form-group">
                    <label htmlFor="productQuantity">Initial Stock Quantity:</label>
                    <input
                        id="productQuantity"
                        type="number"
                        min="0"
                        value={productQuantity}
                        onChange={(e) => setProductQuantity(e.target.value)}
                        required
                        disabled={isSubmitting}
                        placeholder="0"
                    />
                </div>
                
                <div className="form-group">
                    <label htmlFor="productImage">Image URL:</label>
                    <input
                        id="productImage"
                        type="url"
                        value={productImage}
                        onChange={(e) => setProductImage(e.target.value)}
                        required
                        disabled={isSubmitting}
                        placeholder="https://example.com/image.jpg"
                    />
                </div>
                
                <div className="form-buttons">
                    <button 
                        type="button"
                        onClick={handleCancel}
                        className="cancel-btn"
                        disabled={isSubmitting}
                    >
                        Cancel
                    </button>
                    <button 
                        type="submit" 
                        disabled={isSubmitting}
                        className={`add-product-btn ${isSubmitting ? 'submitting' : ''}`}
                    >
                        {isSubmitting ? 'Adding Product...' : 'Add Product'}
                    </button>
                </div>
            </form>
        </div>
    );
}