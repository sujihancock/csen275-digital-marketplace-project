import React, { useEffect, useState } from 'react';
import CheckoutButton from '../components/CheckoutButton';
import AddToCartButton from '../components/AddToCartButton';
import SearchBar from '../components/SearchBar';
import CategoryFilter from '../components/CategoryFilter';
import { useUser } from '../context/UserContext';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const { user, isAuthenticated } = useUser();

    return (
        <div className="home-container">
            <CategoryFilter
                selectedCategories={selectedCategories}
                setSelectedCategories={setSelectedCategories}
            />
            <SearchBar
                setProducts={setProducts}
                selectedCategories={selectedCategories}
            />
            <div className="products-list">
                {products.map(product => (
                    <div key={product.id} className="product-card">
                        <img src={product.imageUrl} alt={product.name} width="150"/>
                        <h3>{product.name}</h3>
                        <p>{product.description}</p>
                        <p>Price: ${product.price.toFixed(2)}</p>
                        <div className="product-buttons">
                            {isAuthenticated && user?.role === 'buyer' && (
                                <AddToCartButton product={product} />
                            )}
                            <CheckoutButton product={product} />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
 
export default Home;