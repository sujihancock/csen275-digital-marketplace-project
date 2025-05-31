import React, { useEffect, useState } from "react";
import { useUser } from "../context/UserContext";
import { useNavigate } from 'react-router-dom';
import {sellers} from "../services/api";


const CustomerOrders = () => {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        const fetchOrders = async () => {
            const response = await sellers.getCustomerOrders();
            try {
                if (response && response.data.status === 'success') {
                    setOrders(response.data.data);
                } else {
                    throw new Error(response.data.message);
                }
            } catch (error) {
                console.log(`Failed to fetch orders: ${error.message}`);
            }
        }
        fetchOrders();
    }, []);

    return (
        <div className="orders-container">
            <h3>Your Customers' Orders</h3>
            {orders && orders.length > 0 ? (
                orders.map((order) => (
                    <div className="customer-order" key={order.id}>
                        <h4>Order #{order.id} - {new Date(order.date).toLocaleDateString()}</h4>
                        <h5>Buyer: {order.buyer.username}</h5>
                        <h5>Total: ${order.amount.toFixed(2)}</h5>
                        <h5>Status:  {order.status}</h5>
                        {order.items.map((item) => (
                            <div key={item.id} className="order-item">
                                <h5>{item.product.name} - ${item.product.price.toFixed(2)}</h5>
                                <p>Quantity: {item.quantity}</p>
                                <p>Subtotal: ${item.subtotal.toFixed(2)}</p>
                            </div>
                        ))}
                    </div>
                )
            )) : (<div>No orders yet</div>)}
        </div>
    );
}
export default CustomerOrders;