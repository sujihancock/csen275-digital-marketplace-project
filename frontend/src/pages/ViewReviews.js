import React, {useState, useEffect} from 'react';
import {sellers} from "../services/api";

function ViewReviews() {
    const [reviews, setReviews] = useState([]);
    const ratingLimit = 5;

    useEffect(() => {
        const fetchReviews = async () => {
            try {
                const response = await sellers.getReviews();
                console.log(response);
                if (response && response.data.status === 'success') {
                    setReviews(response.data.data);
                } else {
                    throw new Error(response.data.message);
                }
            } catch (error) {
                console.log(`Failed to fetch reviews: ${error.message}`);
            }
        }
        fetchReviews();
    }, []);

    return (
        <div>
        <h4>Your Reviews</h4>
        {reviews && reviews.length > 0 ? (
            reviews.map((review) => (
                <div className="review-container" key={review.id}>
                    <h5>
                        {review.reviewer.username} - {new Date(review.date).toLocaleDateString()}
                    </h5>
                    <p>{review.comment}</p>
                    <h5>
                        {'★'.repeat(review.rating)}{'☆'.repeat(ratingLimit - review.rating)}
                    </h5>
                </div>
            ))
        ):(<p>No reviews yet.</p>)
        }
        </div>
    );
}

export default ViewReviews;