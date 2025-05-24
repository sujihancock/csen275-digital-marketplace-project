import React, { useState } from 'react';
import { users } from './services/api';

function Signup() {
        const[email, setEmail] = useState('');
        const [username, setUsername] = useState('');
        const [password, setPassword] = useState('');
        const [message, setMessage] = useState('');

        const handleSubmit = async (event) => {
            event.preventDefault();
            try {
                const response = await users.signup('buyer', username, email, password);
                setMessage(`Successfully signed up as ${username}!`);
            } catch (error) {
                setMessage(error.response?.data?.message || 'Signup failed. Please try again.');
            }
        }

        return (
            <form onSubmit={handleSubmit}>
                <label>
                    Email:
                    <input
                    type="email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    /> 
                </label><br/>
                <label>
                    Username:
                    <input
                    type="text"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    />
                </label><br />
                <label>
                    Password:
                    <input type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    />
                </label><br />
                <button type="submit">Sign Up</button>
                {message && <p>{message}</p>}
            </form>
        )
};

export default Signup;