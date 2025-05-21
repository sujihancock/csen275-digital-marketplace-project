import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Products API
export const products = {
    getAll: () => api.get('/products'),
    getById: (id) => api.get(`/products/${id}`),
};

// Users API
export const users = {
    login: (username, password) => api.post('/users/login', { username, password }),
    signup: (type) => api.post(`/users/signup/${type}`),
    getById: (id) => api.get(`/users/${id}`),
    logout: () => api.post(`/users/logout`),
};

export default api; 