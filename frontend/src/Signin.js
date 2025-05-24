import React, { useState } from 'react';
import { useUser } from './context/UserContext';
import { useNavigate } from 'react-router-dom';

function Signin() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const { login } = useUser();
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        const result = await login(username, password);
        
        if (result.success) {
            setMessage(`Welcome back, ${result.user.username}!`);
            // Redirect to profile page after successful login
            setTimeout(() => navigate('/profile'), 1500);
        } else {
            setMessage(result.error);
        }
    }

    return (
        <div className="signin-container">
            <h2>Sign In</h2>
            <form onSubmit={handleSubmit}>
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
                        <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                        />
                    </label>
                </div>
                <button type="submit" className="signin-btn">Sign In</button>
                {message && <p className={message.includes('Welcome') ? 'success-message' : 'error-message'}>{message}</p>}
            </form>
        </div>
    )
}

export default Signin;