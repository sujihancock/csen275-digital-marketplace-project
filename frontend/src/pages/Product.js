import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { products } from "../services/api";
import { Link } from 'react-router-dom';
import AddToCartButton from "../components/AddToCartButton";
import { useUser } from '../context/UserContext';

const Product = () => {
    const { id } = useParams();
    const [product, setProduct] = useState({
        id: id,
        name: "",
        price: 0,
        seller: { id: 0, username: ""},
        quantity: 0,
        description: "",
        reviews: []
    });
    const { user, isAuthenticated } = useUser();
    const ratingLimit = 5;

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await products.getProduct(id);
                if (response && response.data.status === 'success') {
                    setProduct(response.data.data);
                } else {
                    throw new Error(response.data.message);
                }
            } catch (error) {
                console.log(`Failed to fetch product: ${error.message}`);
            }
        }
        fetchProduct();
    }, [id]);

    if (!product) {
        return <div className="product-loading">Loading product details...</div>;
    }

    return (
        <div className="product-container">
            <div key={product.id} className="product-card">
                <h3>{product.name}</h3>
                <img src={product.imageUrl} alt={product.name} width="150" />
                <h4>${product.price.toFixed(2)}</h4>
                <h4>
                    <Link to={`/seller-store/${product.seller.id}`}>
                        {product.seller.username}
                    </Link>
                </h4>
                <h4>In Stock: {product.quantity}</h4>
                <div className="product-buttons">
                    {isAuthenticated && user?.role === 'buyer' && (
                        <AddToCartButton product={product} />
                    )}
                </div>
                <p>{product.description}</p>
                <h4>Reviews:</h4>
                <Link to={`/products/${id}/add-review`}>
                    <button className="action-btn">Leave a Review</button>
                </Link>
                {product.reviews && product.reviews.length > 0 ? (
                    product.reviews.map((review) => (
                        <div className="review-container" key={review.id}>
                            <h5>{review.reviewer.username} - {new Date(review.date).toLocaleDateString()}</h5>
                            <p>{review.comment}</p>
                            <h5>{'★'.repeat(review.rating)}{'☆'.repeat(ratingLimit - review.rating)}</h5>
                        </div>
                    ))
                ) : (<p>No reviews yet.</p>)
                }
            </div>
        </div>
    );
}

export default Product;