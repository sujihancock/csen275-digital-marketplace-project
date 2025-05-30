import React, {useState, useEffect} from 'react';
import { Link } from 'react-router-dom';

import { orders } from '../services/api';


const OrderHistory = () => {
    const [orderSummaries, setOrderSummaries] = useState([]);

    const fetchOrders = async () => {
        try {
            const response = await orders.getOrderHistory();
            setOrderSummaries(response.data.data);
        } catch (error) {
            console.log(`❌ Failed to load order history: ${error.message}`);
        }
    }

    useEffect(() => {
        fetchOrders();
    }, []);

    return (
        <div className="order-history">
            <h1>Order History</h1>
            {orderSummaries.map(order => (
                <div className="order-summary" key={order.id}>
                    <h3>Order #<Link to={`/orders/${order.id}`}>{order.id}</Link> - {new Date(order.date).toLocaleDateString()} </h3>
                    <h4>Total: ${order.amount.toFixed(2)}</h4>
                    <h4>Status: {order.status}</h4>
                </div>
            ))}
        </div>
    );
}

export default OrderHistory;