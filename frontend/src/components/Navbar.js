import { Link } from 'react-router-dom';
import React from 'react';
import { useUser } from '../context/UserContext';

const Navbar = () => {
    const { user, logout, isAuthenticated } = useUser();

    const handleLogout = async () => {
        await logout();
    };

    return (
        <nav className="navbar">
        <h1>Digital Marketplace</h1>
        <div className="links">
            <Link to="/">Home</Link>
            {isAuthenticated ? (
                <>
                    <Link to="/profile" className="profile-link">
                        Profile ({user?.role === 'buyer' ? '🛒' : '🏪'} {user?.username})
                    </Link>
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
    );
}

export default Navbar;