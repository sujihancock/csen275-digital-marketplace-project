import React, { useEffect, useState } from 'react';
import CheckoutButton from './CheckoutButton';

const Home = () => {
    const [products, setProducts] = useState([]);
    
    useEffect(() => {
        fetch('https://fakestoreapi.com/products')
            .then(response => response.json())
            .then(data => {
                setProducts(data);
            })
            .catch(error => {
                console.error('Error fetching products:', error);
            });
    }, []);
            
    return (
        <div className="home-container">
            <div className="products-list">
                {products.map(product => (
                    <div key={product.id} className="product-card">
                        <img src={product.image} alt={product.title} width="150"/>
                        <h3>{product.title}</h3>
                        <p>{product.description}</p>
                        <p>Price: ${product.price.toFixed(2)}</p>
                        <CheckoutButton product={product} />
                    </div>
                ))}
            </div>
        </div>
    );
}
 
export default Home;