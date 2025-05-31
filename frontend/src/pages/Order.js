import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { orders } from '../services/api';

const Order = () => {
    const { id } = useParams();
    const [order, setOrder] = useState(null);

    useEffect(() => {
        const fetchOrder = async () => {
            try {
                const response = await orders.getOrder(id);
                if (response && response.data.status === 'success') {
                    setOrder(response.data.data);
                } else {
                    throw new Error(response.data.message);
                }
            } catch (error) {
                console.error(`Failed to fetch order: ${error.message}`);
            }
        };

        fetchOrder();
    }, [id]);

    if (!order) {
        return <div className="order-loading">Loading order details...</div>;
    }

    return (
        <div className="order-container">
            <div className="order-details">
                <h3>
                    Order #{order.id} - {new Date(order.date).toLocaleDateString()}
                </h3>
                <h4>
                    Total: ${order.amount.toFixed(2)}
                </h4>
                <h4>
                    Status: {order.status}
                </h4>
                <h4>
                    Items:
                </h4>
                {order.items.map((item) => (
                    <div key={item.id} className="order-item">
                        <img src={item.product.imageUrl} alt={item.product.name} width="100" />
                        <h4>
                            Product: {item.product.name}
                        </h4>
                        <p>
                            Price: ${item.product.price.toFixed(2)}
                        </p>
                        <p>
                            Quantity: {item.quantity}
                        </p>
                        <p>
                            Subtotal: ${item.subtotal.toFixed(2)}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Order;
