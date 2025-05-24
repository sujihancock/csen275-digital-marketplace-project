import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import Navbar from './Navbar';
import Signin from './Signin';
import Signup from './Signup';
import Home from './Home';
import Profile from './Profile';
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
          </Routes>
        </div>
      </UserProvider>
    </Router>
  );
}

export default App;
