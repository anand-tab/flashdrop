
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import Register from './pages/Register';
import { Login } from './pages/Login';
import Catalogue from './pages/Catalogue';
import  Product  from './components/Product';
import BuyNow from './components/BuyNow';
import { Order } from './pages/Order';
import { Profile } from './components/Profile';
import { OrderDetailsUser } from './components/OrderDetailsUser';

function App() {
  
  return (
   <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/catalogue" element={<Catalogue/>} />
        <Route path="/product/:productId" element={<Product/>} />
        <Route path="/buy/:productId" element={<BuyNow />} />
        <Route path="/order/:productId" element={<Order />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/orders" element={<OrderDetailsUser />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
