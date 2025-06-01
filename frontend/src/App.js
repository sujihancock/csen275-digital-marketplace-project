import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar';
import Signin from './pages/Signin';
import Signup from './pages/Signup';
import Home from './pages/Home';
import Profile from './pages/Profile';
import AddProduct from './pages/AddProduct';
import ManageProducts from './pages/ManageProducts';
import WriteReview from './pages/WriteReview';
import { UserProvider } from './context/UserContext';
import { CartProvider } from './context/CartContext';
import { Routes, Route } from 'react-router-dom';
import Payment from "./pages/Payment";
import OrderHistory from "./pages/OrderHistory";
import Order from './pages/Order';
import Product from './pages/Product';
import SellerStore from './pages/SellerStore'
import CustomerOrders from "./pages/CustomerOrders";
import ViewReview from "./pages/ViewReview";


function App() {
   return (
    <Router>
      <UserProvider>
        <CartProvider>
          <div className="App">
            <Navbar />
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/signin" element={<Signin />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/add-product" element={<AddProduct />} />
              <Route path="/products/:id" element={<Product />} />
              <Route path= "/seller-store/:id" element={<SellerStore />} />
              <Route path="/customer-orders" element={<CustomerOrders />} />
              <Route path="/manage-products" element={<ManageProducts />} />
              <Route path="/write-review/:productID" element={<WriteReview />} />
              <Route path="/payment" element={<Payment />} />
              <Route path="/order-history" element={<OrderHistory />} />
              <Route path="/orders/:id" element={<Order />} />
              <Route path="/view-review" element={<ViewReview />} />
            </Routes>
          </div>
        </CartProvider>
      </UserProvider>
    </Router>
  );
}

export default App;