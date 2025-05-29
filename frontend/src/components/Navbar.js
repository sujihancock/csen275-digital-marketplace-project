import { Link, useNavigate, useLocation } from 'react-router-dom';
import React, { useState } from 'react';
import { useUser } from '../context/UserContext';
import { useCart } from '../context/CartContext';
import Cart from './Cart';

const Navbar = () => {
    const { user, logout, isAuthenticated } = useUser();
    const { getCartItemCount } = useCart();
    const [isCartOpen, setIsCartOpen] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = async () => {
        await logout();
        // If user is on manage-products page, redirect to profile page
        if (location.pathname === '/manage-products') {
            navigate('/profile');
        }
    };

    const handleCartClick = () => {
        setIsCartOpen(true);
    };

    const handleCartClose = () => {
        setIsCartOpen(false);
    };

    const cartItemCount = getCartItemCount();

    return (
        <>
            <nav className="navbar">
            <h1>Digital Marketplace</h1>
            <div className="links">
                <Link to="/">Home</Link>
                {isAuthenticated ? (
                    <>
                        <Link to="/profile" className="profile-link">
                            Profile ({user?.role === 'buyer' ? '🛒' : '🏪'} {user?.username})
                        </Link>
                        {user?.role === 'buyer' && (
                            <button onClick={handleCartClick} className="cart-nav-btn">
                                🛒 Cart
                                {cartItemCount > 0 && (
                                    <span className="cart-badge">{cartItemCount}</span>
                                )}
                            </button>
                        )}
                        <button onClick={handleLogout} className="logout-nav-btn">
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/signin">Sign In</Link>
                        <Link to="/signup">Sign Up</Link>
                    </>
                )}
            </div>
            </nav>
            <Cart isOpen={isCartOpen} onClose={handleCartClose} />
        </>
    );
}

export default Navbar;