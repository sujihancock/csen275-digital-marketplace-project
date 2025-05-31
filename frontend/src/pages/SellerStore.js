import React, { useState, useEffect} from "react";
import { useParams } from "react-router-dom";
import { Link } from 'react-router-dom';
import { sellers } from '../services/api'
import EmailLink from "../components/EmailLink";

const SellerStore = () => {
    const { id } = useParams();
    const [seller, setSeller] = useState({
        id: id,
        username: "",
        email: "",
        reviews: [],
        products: []
    });
    const ratingLimit = 5;

    useEffect(() => {
        const fetchSeller = async () => {
            try {
                const response = await sellers.getSellerStore(id);
                if (response && response.data.status === 'success') {
                    setSeller(response.data.data);
                } else {
                    throw new Error(response.data.message);
                }
            } catch (error) {
                console.error(`Failed to fetch seller: ${error.message}`);
            }
        }
        fetchSeller();
    }, [id]);

    if (!seller) {
        return <div className="seller-loading">Loading seller details...</div>;
    }

    return (
        <div className="seller-store">
            <h3>{seller.username} - <EmailLink email={seller.email}/></h3>
            <div className="seller-details">
                <div className="seller-products">
                    <h4>Products:</h4>
                    {seller.products.map((product) => (
                        <div className="product" key={product.id}>
                            <h5>
                                <Link to={`/products/${product.id}`}>{product.name}</Link>
                            </h5>
                            <img src={product.imageUrl} alt={product.name} width={100}/>
                            <h4>${product.price.toFixed(2)}</h4>
                        </div>
                    ))}
                </div>
                <div className="seller-reviews">
                    <h4>Reviews:</h4>
                    {seller.reviews && seller.reviews.length > 0 ? (
                        seller.reviews.map((review) => (
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
        </div>
    )

}

export default SellerStore;