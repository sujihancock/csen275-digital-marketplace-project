import React, {useState} from 'react';
import { useParams, useNavigate } from 'react-router-dom';

function WriteSellerReview() {
    const { id } = useParams();
    const [comment, setComment] = useState('');
    const [rating, setRating] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            comment: comment.trim(),
            rating: parseInt(rating, 10),
            date: new Date().toISOString()
        };

        try {
            const res = await fetch(
                `http://localhost:8080/api/sellers/${id}/reviews/add`,
                {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    credentials: "include",
                    body: JSON.stringify(payload),
                }
            );

            if (!res.ok) {
                throw new Error(`Error: ${res.status}`)
            }

            setMessage("Review submitted");
            setComment("");
            setRating("");
            setTimeout(() => navigate(`/seller-store/${id}`), 1000);
        } catch (err) {
            setMessage("Failed to submit seller review");
        }
    }
    return (
        <div className="add-review">
            <h2>Add a New Review</h2>
            <form onSubmit={handleSubmit}>
                <textarea required
                          placeholder="Your comment"
                          value={comment}
                          onChange={(e) => setComment(e.target.value)}>
                </textarea>
                <label>Rating:</label>
                <select
                    required
                    value={rating}
                    onChange={(e) => setRating(e.target.value)}>
                    <option value="">Select rating</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
                <button>Submit Review</button>
                <div>{message}</div>
            </form>
        </div>
    );
}

export default WriteSellerReview;