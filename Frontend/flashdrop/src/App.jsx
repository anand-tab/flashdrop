
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import Register from './pages/Register';
import { Login } from './pages/Login';
import Catalogue from './pages/Catalogue';
import  Product  from './components/Product';
import BuyNow from './components/BuyNow';

function App() {
  
  return (
   <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/catalogue" element={<Catalogue/>} />
        <Route path="/product/:productId" element={<Product/>} />
        <Route path="/buy/:productId" element={<BuyNow />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
