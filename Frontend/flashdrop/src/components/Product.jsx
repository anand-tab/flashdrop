import React, { useEffect, useState } from "react";
import "./Product.css";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../Api";
import flashdrop from "/Users/sumanshuanand/Documents/flashdrop/Frontend/flashdrop/public/flashdrop.png";

const Product = () => {
  const navigate = useNavigate();
  const { productId } = useParams();

  const [product, setProduct] = useState(null);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await apiFetch(
          `http://localhost:3002/api/product/${productId}`,
          {
            method: "GET",
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch product");
        }

        const data = await response.json();
        setProduct(data);
      } catch (error) {
        console.error("Error fetching product:", error);
      }
    };

    fetchProduct();
  }, [productId]);

  useEffect(() => {
    const closeDropdown = () => setOpen(false);
    document.addEventListener("click", closeDropdown);

    return () => document.removeEventListener("click", closeDropdown);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const handleAddToCart = () => {
    alert(`${product.productName} added to cart`);
  };

  const handleBuyNow = (productId) => {
    navigate(`/buy/${productId}`)
  };

  if (!product) return <h2 className="loading">Loading product...</h2>;

  return (
    <div className="product-page">

      {/* HEADER */}
      <header className="product-header">
  <img
    src={flashdrop}
    alt="FlashDrop Logo"
    className="logo"
    onClick={() => navigate("/catalogue")}
  />

  <div className="user-menu">
    <div
      className="user-info"
      onClick={(e) => {
        e.stopPropagation();
        setOpen(!open);
      }}
    >
      <span className="user-icon">👤</span>
      <span className="username">Admin</span>
    </div>

    {open && (
      <div className="dropdown">
        <div onClick={() => navigate("/profile")}>Profile</div>
        <div onClick={() => navigate("/orders")}>Orders</div>
        <div onClick={handleLogout}>Logout</div>
      </div>
    )}
  </div>
</header>

      {/* PRODUCT DETAILS */}
      <div className="product-container">
        <div className="product-image-section">
          <img src={product.productImageUrl} alt={product.productName} />
        </div>

        <div className="product-info-section">
          <h2>{product.productName}</h2>
          <p className="description">{product.productDescription}</p>
          <p className="rating">⭐ {product.rating}</p>
          <p className="price">₹ {product.productPrice}</p>

          <div className="actions">
            <button className="cart-btn" onClick={handleAddToCart}>
              Add to Cart
            </button>
            <button className="buy-btn" onClick={()=>handleBuyNow(product.productId)}>
              Buy Now
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Product;