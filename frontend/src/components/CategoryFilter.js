import React, { useEffect, useState } from 'react';

export default function CategoryFilter({ selectedCategories, setSelectedCategories }) {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        fetch('http://localhost:8080/api/products/categories')
            .then(response => response.json())
            .then(data => setCategories(data.data))
            .catch(err => console.log('Error fetching categories:', err));
    }, []);

    const handleCategoryChange = (e) => {
        const value = e.target.value;
        if (e.target.checked) {
            setSelectedCategories(prev => [...prev, value]);
        } else {
            setSelectedCategories(prev => prev.filter(category => category !== value));
        }
    };

    return (
        <div className="category-list">
            {categories.map(category => (
                <label key={category}>
                    <input
                        type="checkbox"
                        value={category}
                        checked={selectedCategories.includes(category)}
                        onChange={handleCategoryChange}
                    />
                    {category.toLowerCase().replace('_', ' ')}
                </label>
            ))}
        </div>
    );
}
