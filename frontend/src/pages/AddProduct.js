import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function AddProduct() {
    const [productName, setProductName] = useState('');
    const [productPrice, setProductPrice] = useState('');
    const [productDescription, setProductDescription] = useState('');
    const [productImage, setProductImage] = useState('');
    const [productURL, setProductURL] = useState('');
    const [productCategory, setProductCategory] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const newProduct = {
            name: productName,
            description: productDescription,
            price: productPrice,
            imageUrl: productImage,
            category: productCategory,
        };

    console.log('Would send to backend:', newProduct); // need to setup to send to backend
    navigate('/products');
};

return (
    <div className="add-product-container">
        <h2>Add New Product</h2>
        <form onSubmit={handleSubmit}>
        <div>
            <label>Product Name:</label>
            <input
                type="text"
                value={productName}
                onChange={(e) => setProductName(e.target.value)}
                required
            />
        </div>
        <div>
            <label>Price:</label>
                <input
                    type="number"
                    value={productPrice}
                    onChange={(e) => setProductPrice(e.target.value)}
                    required
                />
        </div>
        <div>
            <label>Description:</label>
            <textarea
                value={productDescription}
                onChange={(e) => setProductDescription(e.target.value)}
                required
            ></textarea>
        </div>
        <div>
            <label>Image URL:</label>
            <input
                type="text"
                value={productImage}
                onChange={(e) => setProductImage(e.target.value)}
                required
            />
        </div>
        <div>
            <label>Product URL:</label>
            <input
                type="text"
                value={productURL}
                onChange={(e) => setProductURL(e.target.value)}
            />
        </div>
        <div>
            <label>Category:</label>
            <input
                type="text"
                value={productCategory}
                onChange={(e) => setProductCategory(e.target.value)}
            />
        </div>
        <button type="submit">Add Product</button>
        </form>
        </div>
    );
}