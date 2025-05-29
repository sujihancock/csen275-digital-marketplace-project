import React, { useState } from 'react';

function SearchBar({ setProducts, selectedCategories }) {
    const [keywords, setKeywords] = useState([]);

    const handleKeywordChange = async (e) => {
        const value = e.target.value;
        const terms = value.toLowerCase().split(' ');
        setKeywords(terms);
    }

    const handleSubmit= (e) => {
        e.preventDefault();
        const searchRequest = {
            keywords: keywords,
            category: selectedCategories,
        };

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

    return (
       <div className="search-container">
           <form onSubmit={handleSubmit}>
               <input
                   type='text'
                   onChange={handleKeywordChange}
               />
               <button type="submit">Search</button>
           </form>
       </div>
    );
}

export default SearchBar;