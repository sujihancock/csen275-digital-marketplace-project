import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Include cookies in requests
});

// Products API
export const products = {
    getAllProducts: () => api.get('/products'),
    getProduct: (id) => api.get(`/products/${id}`),
};

// Users API
export const users = {
    login: (username, password) => api.post('/users/login', { username, password }),
    signup: (type, username, email, password) => api.post(`/users/signup/${type}?username=${encodeURIComponent(username)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`),
    getById: (id) => api.get(`/users/${id}`),
    getProfile: () => api.get('/users/profile'),
    updateProfile: (username) => api.put(`/users/profile?username=${encodeURIComponent(username)}`),
    logout: () => api.post(`/users/logout`),
};

// Cart API - Using existing backend endpoints
export const cart = {
    getCart: () => api.get('/buyers/cart'),
    addToCart: (productId, quantity = 1) => api.post('/buyers/cart/add', { id: productId, quantity }),
    removeFromCart: (productId, quantity = 1) => api.post('/buyers/cart/remove', { id: productId, quantity }),
    clearCart: () => api.post('/buyers/cart/clear'),
    getCartAmount: () => api.get('/buyers/cart/amount'),
};

// Payment API
export const payment = {
    checkout: () => api.get('/payment/checkout'),
    confirmPayment: (paymentIntentIds) => api.post('/payment/confirm-payment', paymentIntentIds),
    failPayment: (paymentIntentIds) => api.post('/payment/fail-payment', paymentIntentIds),
    stripeLogin: () => api.get('/payment/stripe-login')
};

// Orders API
export const orders = {
    saveOrder: () => api.post('/orders/save'),
    getOrderHistory: () => api.get('/orders/history'),
    getOrder: (id) => api.get(`/orders/${id}`),
};

// Seller API
export const sellers = {
    getCustomerOrders: () => api.get('/sellers/customer-orders'),
    getReviews: () => api.get('/sellers/reviews'),
    getSellerStore: (id) => api.get(`/sellers/${id}`),
}

export default api; 