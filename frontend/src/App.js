import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar';
import Signin from './pages/Signin';
import Signup from './pages/Signup';
import Home from './pages/Home';
import Profile from './pages/Profile';
import AddProduct from './pages/AddProduct';
import ManageProducts from './pages/ManageProducts';
import ProductReviews from './pages/ProductReviews';
import { UserProvider } from './context/UserContext';
import { Routes, Route } from 'react-router-dom';


function App() {
   return (
    <Router>
      <UserProvider>
        <div className="App">
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/signin" element={<Signin />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/add-product" element={<AddProduct />} />
            <Route path="/manage-products" element={<ManageProducts />} />
            <Route path="/product-reviews" element={<ProductReviews />} />
          </Routes>
        </div>
      </UserProvider>
    </Router>
  );
}

export default App;