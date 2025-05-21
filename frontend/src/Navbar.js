import { Link } from 'react-router-dom';
import React from 'react';
import Signin from './Signin';
import Signup from './Signup';

const Navbar = () => {
    return (
        <nav className="navbar">
        <h1>Digital Marketplace</h1>
        <div className="links">
            <Link to="/">Home</Link>
            <Link to="/signin">Sign In</Link>
            <Link to="/signup">Sign Up</Link>
        </div>
        </nav>
    );
}

export default Navbar;