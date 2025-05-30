import React, { useState } from 'react';
import CategoryFilter from './CategoryFilter';

function SearchBar({ onSearch, selectedCategories, setSelectedCategories }) {
    const [input, setInput] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        const keywords = input.toLowerCase().split(" ").filter(word => word.trim() !== "");
        onSearch(keywords, selectedCategories);
    };

    return (
        <div className="search-container">
            <form onSubmit={handleSubmit}>
                <CategoryFilter
                    selectedCategories={selectedCategories}
                    setSelectedCategories={setSelectedCategories}
                />
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    placeholder="Search products..."
                />
                <button type="submit">Search</button>
            </form>
        </div>
    );
}

export default SearchBar;
