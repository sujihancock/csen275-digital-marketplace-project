import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function ManageProducts() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editingProduct, setEditingProduct] = useState(null);
    const [updateData, setUpdateData] = useState({});
    const [isUpdating, setIsUpdating] = useState(false);
    const navigate = useNavigate();

    // Available categories
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

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            const response = await api.get('/sellers/products');
            if (response.data.status === 'success') {
                setProducts(response.data.data || []);
            } else {
                setError(response.data.message || 'Failed to fetch products');
            }
        } catch (error) {
            console.error('Error fetching products:', error);
            if (error.response?.status === 401) {
                setError('Please log in as a seller to manage products');
            } else {
                setError('Failed to load products. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (product) => {
        setEditingProduct(product.id);
        setUpdateData({
            name: product.name,
            description: product.description,
            price: product.price,
            imageUrl: product.imageUrl,
            category: product.category,
            quantity: product.quantity || 0
        });
    };

    const handleCancelEdit = () => {
        setEditingProduct(null);
        setUpdateData({});
    };

    const handleUpdateProduct = async (productId) => {
        try {
            setIsUpdating(true);
            const response = await api.put(`/sellers/products/${productId}`, updateData);
            
            if (response.data.status === 'success') {
                // Update the product in the local state
                setProducts(products.map(product => 
                    product.id === productId 
                        ? { ...product, ...updateData }
                        : product
                ));
                setEditingProduct(null);
                setUpdateData({});
            } else {
                setError(response.data.message || 'Failed to update product');
            }
        } catch (error) {
            console.error('Error updating product:', error);
            setError('Failed to update product. Please try again.');
        } finally {
            setIsUpdating(false);
        }
    };

    const handleDelete = async (productId) => {
        if (!window.confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await api.delete(`/sellers/products/${productId}/remove`);
            
            if (response.data.status === 'success') {
                setProducts(products.filter(product => product.id !== productId));
            } else {
                setError(response.data.message || 'Failed to delete product');
            }
        } catch (error) {
            console.error('Error deleting product:', error);
            setError('Failed to delete product. Please try again.');
        }
    };

    const handleQuantityChange = (field, value) => {
        if (field === 'quantity') {
            const quantity = parseInt(value);
            if (isNaN(quantity) || quantity < 0) return;
            setUpdateData(prev => ({ ...prev, [field]: quantity }));
        } else if (field === 'price') {
            const price = parseFloat(value);
            if (isNaN(price) || price < 0) return;
            setUpdateData(prev => ({ ...prev, [field]: price }));
        } else {
            setUpdateData(prev => ({ ...prev, [field]: value }));
        }
    };

    if (loading) {
        return (
            <div className="manage-products-container">
                <div className="loading-state">Loading your products...</div>
            </div>
        );
    }

    return (
        <div className="manage-products-container">
            <div className="manage-header">
                <h2>Manage Products</h2>
                <button 
                    className="add-new-btn"
                    onClick={() => navigate('/add-product')}
                >
                    + Add New Product
                </button>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                    <button onClick={() => setError('')} className="dismiss-btn">×</button>
                </div>
            )}

            {products.length === 0 ? (
                <div className="empty-products">
                    <h3>No Products Yet</h3>
                    <p>You haven't added any products to your store.</p>
                    <button 
                        className="add-first-product-btn"
                        onClick={() => navigate('/add-product')}
                    >
                        Add Your First Product
                    </button>
                </div>
            ) : (
                <div className="products-grid">
                    {products.map(product => (
                        <div key={product.id} className="product-manage-card">
                            {editingProduct === product.id ? (
                                // Edit Mode
                                <div className="product-edit-form">
                                    <div className="form-group">
                                        <label>Product Name:</label>
                                        <input
                                            type="text"
                                            value={updateData.name}
                                            onChange={(e) => handleQuantityChange('name', e.target.value)}
                                            disabled={isUpdating}
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label>Description:</label>
                                        <textarea
                                            value={updateData.description}
                                            onChange={(e) => handleQuantityChange('description', e.target.value)}
                                            disabled={isUpdating}
                                            rows="3"
                                        />
                                    </div>

                                    <div className="form-row">
                                        <div className="form-group">
                                            <label>Price ($):</label>
                                            <input
                                                type="number"
                                                step="0.01"
                                                min="0"
                                                value={updateData.price}
                                                onChange={(e) => handleQuantityChange('price', e.target.value)}
                                                disabled={isUpdating}
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label>Quantity:</label>
                                            <input
                                                type="number"
                                                min="0"
                                                value={updateData.quantity}
                                                onChange={(e) => handleQuantityChange('quantity', e.target.value)}
                                                disabled={isUpdating}
                                            />
                                        </div>
                                    </div>

                                    <div className="form-group">
                                        <label>Category:</label>
                                        <select
                                            value={updateData.category}
                                            onChange={(e) => handleQuantityChange('category', e.target.value)}
                                            disabled={isUpdating}
                                        >
                                            {categories.map(cat => (
                                                <option key={cat.value} value={cat.value}>
                                                    {cat.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="form-group">
                                        <label>Image URL:</label>
                                        <input
                                            type="url"
                                            value={updateData.imageUrl}
                                            onChange={(e) => handleQuantityChange('imageUrl', e.target.value)}
                                            disabled={isUpdating}
                                        />
                                    </div>

                                    <div className="edit-actions">
                                        <button 
                                            className="save-btn"
                                            onClick={() => handleUpdateProduct(product.id)}
                                            disabled={isUpdating}
                                        >
                                            {isUpdating ? 'Saving...' : 'Save Changes'}
                                        </button>
                                        <button 
                                            className="cancel-btn"
                                            onClick={handleCancelEdit}
                                            disabled={isUpdating}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                // View Mode
                                <div className="product-view">
                                    <img 
                                        src={product.imageUrl} 
                                        alt={product.name}
                                        className="product-image"
                                        onError={(e) => {
                                            e.target.src = 'https://via.placeholder.com/150?text=No+Image';
                                        }}
                                    />
                                    
                                    <div className="product-info">
                                        <h3>{product.name}</h3>
                                        <p className="product-description">{product.description}</p>
                                        
                                        <div className="product-details">
                                            <div className="detail-row">
                                                <span className="label">Price:</span>
                                                <span className="value">${product.price?.toFixed(2)}</span>
                                            </div>
                                            <div className="detail-row">
                                                <span className="label">Quantity:</span>
                                                <span className={`value quantity ${(product.quantity || 0) <= 5 ? 'low-stock' : ''}`}>
                                                    {product.quantity || 0}
                                                    {(product.quantity || 0) <= 5 && (product.quantity || 0) > 0 && (
                                                        <span className="stock-warning"> (Low Stock)</span>
                                                    )}
                                                    {(product.quantity || 0) === 0 && (
                                                        <span className="out-of-stock"> (Out of Stock)</span>
                                                    )}
                                                </span>
                                            </div>
                                            <div className="detail-row">
                                                <span className="label">Category:</span>
                                                <span className="value">{categories.find(c => c.value === product.category)?.label || product.category}</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="product-actions">
                                        <button 
                                            className="edit-btn"
                                            onClick={() => handleEdit(product)}
                                        >
                                            Edit
                                        </button>
                                        <button 
                                            className="delete-btn"
                                            onClick={() => handleDelete(product.id)}
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}