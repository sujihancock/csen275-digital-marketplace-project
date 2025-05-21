import React, { useState, useEffect } from 'react';
import { users } from './services/api';

function Signin() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await users.login(username, password);
            setMessage(`Signed in as ${username}`);
        } catch (error) {
            setMessage(error.response && error.response.status === 401 ? error.response.data["message"] : "unexpected error during login");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
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
                <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                />
            </label><br />
            <button type="submit">Sign In</button>
            {message && <p>{message}</p>}
        </form>
    )
}

export default Signin;