import React, { useState } from 'react';
import { useUser } from './context/UserContext';
import { users } from './services/api';

const Profile = () => {
    const { user, logout, refetchUser } = useUser();
    const [isEditingUsername, setIsEditingUsername] = useState(false);
    const [newUsername, setNewUsername] = useState(user?.username || '');
    const [updateMessage, setUpdateMessage] = useState('');

    if (!user) {
        return (
            <div className="profile-container">
                <h2>Profile</h2>
                <p>Please log in to view your profile.</p>
            </div>
        );
    }

    const handleLogout = async () => {
        await logout();
    };

    const handleUsernameEdit = () => {
        setIsEditingUsername(true);
        setNewUsername(user.username);
        setUpdateMessage('');
    };

    const handleUsernameSave = async () => {
        // Basic validation
        if (!newUsername || newUsername.trim().length === 0) {
            setUpdateMessage('Username cannot be empty');
            return;
        }
        
        if (newUsername === user.username) {
            setIsEditingUsername(false);
            setUpdateMessage('');
            return; // No change needed
        }
        
        // Clear any previous messages before attempting save
        setUpdateMessage('');
        
        try {
            await users.updateProfile(newUsername.trim());
            await refetchUser(); // Refresh user data
            setIsEditingUsername(false);
            setUpdateMessage('Username updated successfully!');
            setTimeout(() => setUpdateMessage(''), 3000);
        } catch (error) {
            const errorMessage = error.response?.data?.message || 'Failed to update username';
            setUpdateMessage(errorMessage);
            // Don't close the edit mode on error so user can try again
        }
    };

    // Real-time validation feedback
    const getValidationMessage = () => {
        if (!newUsername || newUsername.trim().length === 0) {
            return 'Username cannot be empty';
        }
        if (newUsername === user.username) {
            return 'This is your current username';
        }
        return '';
    };

    const validationMessage = getValidationMessage();
    const isValidInput = !validationMessage || validationMessage === 'This is your current username';

    const handleUsernameCancel = () => {
        setIsEditingUsername(false);
        setNewUsername(user.username);
        setUpdateMessage('');
    };

    return (
        <div className="profile-container">
            <div className="profile-header">
                <h2>User Profile</h2>
                <button onClick={handleLogout} className="logout-btn">Logout</button>
            </div>
            
            <div className="profile-content">
                <div className="profile-info">
                    <h3>Personal Information</h3>
                    <div className="info-item">
                        <label>Username:</label>
                        <div className="username-edit">
                            {isEditingUsername ? (
                                <div className="edit-username">
                                    <input
                                        type="text"
                                        value={newUsername}
                                        onChange={(e) => setNewUsername(e.target.value)}
                                        className={`username-input ${validationMessage && validationMessage !== 'This is your current username' ? 'input-error' : ''}`}
                                        placeholder="Enter new username"
                                        maxLength="50"
                                    />
                                    <button 
                                        onClick={handleUsernameSave} 
                                        className="save-btn"
                                        disabled={!isValidInput}
                                        title={!isValidInput ? validationMessage : 'Save username'}
                                    >
                                        Save
                                    </button>
                                    <button onClick={handleUsernameCancel} className="cancel-btn">Cancel</button>
                                    {validationMessage && validationMessage !== 'This is your current username' && (
                                        <div className="validation-message error">
                                            {validationMessage}
                                        </div>
                                    )}
                                </div>
                            ) : (
                                <div className="display-username">
                                    <span>{user.username}</span>
                                    <button onClick={handleUsernameEdit} className="edit-btn">Edit</button>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="info-item">
                        <label>Email:</label>
                        <span>{user.email}</span>
                    </div>
                    <div className="info-item">
                        <label>Account Type:</label>
                        <span className={`role-badge role-${user.role}`}>
                            {user.role === 'buyer' ? '🛒 Buyer' : '🏪 Seller'}
                        </span>
                    </div>
                    {updateMessage && (
                        <div className={`update-message ${updateMessage.includes('successfully') ? 'success' : 'error'}`}>
                            {updateMessage}
                        </div>
                    )}
                </div>

                <div className="role-specific-content">
                    {user.role === 'buyer' ? (
                        <div className="buyer-section">
                            <h3>🛒 Buyer Dashboard</h3>
                            <p>Welcome to your buyer dashboard! Here you can:</p>
                            <ul>
                                <li>Browse handmade products</li>
                                <li>View your order history</li>
                                <li>Manage your shopping cart</li>
                                <li>Leave reviews for products</li>
                            </ul>
                            <div className="quick-actions">
                                <button className="action-btn">Browse Products</button>
                                <button className="action-btn">Order History</button>
                                <button className="action-btn">Shopping Cart</button>
                            </div>
                        </div>
                    ) : (
                        <div className="seller-section">
                            <h3>🏪 Seller Dashboard</h3>
                            <p>Welcome to your seller dashboard! Here you can:</p>
                            <ul>
                                <li>Add new products to your store</li>
                                <li>Manage your product listings</li>
                                <li>View sales analytics</li>
                                <li>Respond to customer reviews</li>
                            </ul>
                            <div className="quick-actions">
                                <button className="action-btn">Add Product</button>
                                <button className="action-btn">Manage Products</button>
                                <button className="action-btn">Sales Report</button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profile; 