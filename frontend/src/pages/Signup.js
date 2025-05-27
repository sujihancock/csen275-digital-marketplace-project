import React, { useState } from 'react';
import { users } from '../services/api';
import { useUser } from '../context/UserContext';
import { useNavigate } from 'react-router-dom';

function Signup() {
        const[email, setEmail] = useState('');
        const [username, setUsername] = useState('');
        const [password, setPassword] = useState('');
        const [role, setRole] = useState('buyer'); // Default to buyer
        const [message, setMessage] = useState('');
        const { login } = useUser();
        const navigate = useNavigate();

        const handleSubmit = async (event) => {
            event.preventDefault();
            try {
                const response = await users.signup(role, username, email, password);
                if (role === 'seller') {
                    // Backend returns onboarding URL as `message`
                    window.location.href = response.data.message; // redirect seller to Stripe onboarding and back to profile after completion
                    return;
                }

                setMessage(`Successfully signed up as a ${role}! Logging you in...`);
                
                // Auto-login after successful signup
                setTimeout(async () => {
                    const loginResult = await login(username, password);
                    if (loginResult.success) {
                        navigate('/profile');
                    } else {
                        setMessage('Signup successful! Please log in manually.');
                    }
                }, 1000);
            } catch (error) {
                setMessage(error.response?.data?.message || 'Signup failed. Please try again.');
            }
        }

        return (
            <div className="signup-container">
                <h2>Sign Up</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>
                            Email:
                            <input
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            required
                            /> 
                        </label>
                    </div>
                    <div className="form-group">
                        <label>
                            Username:
                            <input
                            type="text"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            required
                            />
                        </label>
                    </div>
                    <div className="form-group">
                        <label>
                            Password:
                            <input type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            required
                            />
                        </label>
                    </div>
                    <div className="form-group">
                        <label>
                            I want to:
                            <select value={role} onChange={e => setRole(e.target.value)}>
                                <option value="buyer">Buy handmade products</option>
                                <option value="seller">Sell my handmade products</option>
                            </select>
                        </label>
                    </div>
                    <button type="submit" className="signup-btn">Sign Up</button>
                    {message && <p className={message.includes('Successfully') ? 'success-message' : 'error-message'}>{message}</p>}
                </form>
            </div>
        )
}

export default Signup;