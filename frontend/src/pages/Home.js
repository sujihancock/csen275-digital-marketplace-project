import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import AddToCartButton from '../components/AddToCartButton';
//import CheckoutButton from '../components/CheckoutButton';
import SearchBar from '../components/SearchBar';
import { useUser } from '../context/UserContext';
import { Link } from 'react-router-dom';
import { products } from '../services/api';


const Home = () => {
    const [productList, setProductList] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { user, isAuthenticated } = useUser();

    // Load all products initially
    useEffect(() => {
        const loadAllProducts = async () => {
            try {
                setLoading(true);
                const response = await products.getAllProducts();
                if (response.data.status === 'success') {
                    setProductList(response.data.data || []);
                } else {
                    setError('Failed to load products');
                }
            } catch (error) {
                console.error('Error loading products:', error);
                setError('Failed to load products');
            } finally {
                setLoading(false);
            }
        };

        // Only load all products if there are no search parameters
        const keywords = searchParams.get("keywords");
        const categories = searchParams.get("categories");
        
        if (!keywords && !categories) {
            loadAllProducts();
        }
    }, [searchParams]);

    // Handle search when there are search parameters
    useEffect(() => {
        const keywords = searchParams.get("keywords")?.split(",") || [];
        const categories = searchParams.get("categories")?.split(",") || [];

        const searchRequest = {
            keywords,
            categories,
        };

        if (keywords.length > 0 || categories.length > 0) {
            setLoading(true);
            fetch('http://localhost:8080/api/products/search', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(searchRequest)
            })
                .then(response => response.json())
                .then(data => {
                    setProductList(data.data || []);
                    setLoading(false);
                })
                .catch(err => {
                    console.log('Error fetching search results:', err);
                    setError('Error fetching search results');
                    setLoading(false);
                });
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

    if (loading) {
        return <div className="loading">Loading products...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

    return (
        <div className="home-container">
            <SearchBar
                onSearch={handleSearch}
                selectedCategories={selectedCategories}
                setSelectedCategories={setSelectedCategories}
            />
            <div className="products-list">
                {productList.length === 0 ? (
                    <div className="no-products">No products found</div>
                ) : (
                    productList.map(product => (
                        <div key={product.id} className="product-card">
                            <img src={product.imageUrl} alt={product.name} width="150"/>
                            <h3>
                                <Link to={`/products/${product.id}`}>{product.name}</Link>
                            </h3>
                            {/*<p>{product.description}</p>*/}
                            <p>Price: ${product.price.toFixed(2)}</p>
                            <div className="product-buttons">
                                {isAuthenticated && user?.role === 'buyer' && (
                                    <AddToCartButton product={product} />
                                )}
                           {/* <CheckoutButton product={product} /> */}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default Home;
