import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import CheckoutButton from '../components/CheckoutButton';
import AddToCartButton from '../components/AddToCartButton';
import SearchBar from '../components/SearchBar';
import { useUser } from '../context/UserContext';

const Home = () => {
    const [products, setProducts] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { user, isAuthenticated } = useUser();

    useEffect(() => {
        const keywords = searchParams.get("keywords")?.split(",") || [];
        const categories = searchParams.get("categories")?.split(",") || [];

        const searchRequest = {
            keywords,
            categories,
        };

        if (keywords.length > 0 || categories.length > 0) {
            fetch('http://localhost:8080/api/products/search', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(searchRequest)
            })
                .then(response => response.json())
                .then(data => setProducts(data.data))
                .catch(err => console.log('Error fetching search results:', err));
        }
    }, [searchParams]);

    const handleSearch = (keywords, categories) => {
        const params = new URLSearchParams();
        if (keywords.length > 0) {
            params.set("keywords", keywords.join(","));
        }
        if (categories.length > 0) {
            params.set("categories", categories.join(","));
        }
        navigate(`?${params.toString()}`);
    };

    return (
        <div className="home-container">
            <SearchBar
                onSearch={handleSearch}
                selectedCategories={selectedCategories}
                setSelectedCategories={setSelectedCategories}
            />
            <div className="products-list">
                {products.map(product => (
                    <div key={product.id} className="product-card">
                        <img src={product.imageUrl} alt={product.name} width="150"/>
                        <h3>{product.name}</h3>
                        {/*<p>{product.description}</p>*/}
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
};

export default Home;
