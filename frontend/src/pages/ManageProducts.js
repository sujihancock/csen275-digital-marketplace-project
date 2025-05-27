import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function ManageProducts() {
    const [products, setProducts] = useState([]);
    const navigate = useNavigate();

    const handleDelete = id => {
        console.log('🗑️  Would delete product with id:', id); // Need to implement delete functionality
    };
    return (
        <div className="manage-products-container">
            <h2>Manage Products</h2>
            <button onClick={() => navigate('/add-product')}>Add New Product</button>
            <div className="products-list">
                {products.map(product => (
                    <div key={product.id} className="product-card">
                        <img src={product.image} alt={product.title} width="150" />
                        <h3>{product.title}</h3>
                        <p>{product.description}</p>
                        <p>Price: ${product.price.toFixed(2)}</p>
                        <button onClick={() => handleDelete(product.id)}>Delete</button>
                    </div>
                ))}
            </div>
        </div>
    );
}