import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function ProductReviews() {
    const [reviews] = useState([]); 
    return (
        <div className="product-reviewss-container">
            <h2>Product Reviews</h2>
            {reviews.length === 0 && (<p>No reviews yet.</p>)}
        </div>
    );
}