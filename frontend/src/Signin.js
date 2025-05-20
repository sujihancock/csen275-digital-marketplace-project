import React, { useState } from 'react';

function Signin() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        setMessage(`Signed in as ${username}`);
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
};

export default Signin;