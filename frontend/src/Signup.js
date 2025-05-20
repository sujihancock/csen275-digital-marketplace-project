import React, { useState } from 'react';

function Signup() {
        const[email, setEmail] = useState('');
        const [username, setUsername] = useState('');
        const [password, setPassword] = useState('');
        const [message, setMessage] = useState('');

        const handleSubmit = (event) => {
            event.preventDefault();
            setMessage(`Signed up as ${username}`);
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