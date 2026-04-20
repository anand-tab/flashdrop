import React, { useEffect, useState } from "react";
import "./BuyNow.css";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../Api";
import flashdrop from "/Users/sumanshuanand/Documents/flashdrop/Frontend/flashdrop/public/flashdrop.png";


const BuyNow = () => {
    const navigate = useNavigate();
    const { productId } = useParams();

    const [product, setProduct] = useState(null);
    const [address, setAddress] = useState(null);
    const [quantity, setQuantity] = useState(1);

    const [couponCode, setCouponCode] = useState("");
    const [discount, setDiscount] = useState(0);
    const [couponMessage, setCouponMessage] = useState("");

    // FETCH PRODUCT
    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await apiFetch(
                    `http://localhost:3002/api/product/${productId}`,
                    { method: "GET" }
                );

                const data = await response.json();
                setProduct(data);
            } catch (error) {
                console.error("Error fetching product:", error);
            }
        };

        fetchProduct();
    }, [productId]);

    // FETCH ADDRESS
    useEffect(() => {
        const fetchAddress = async () => {
            try {
                const response = await apiFetch(
                    `http://localhost:3002/api/address/default`,
                    { method: "GET" }
                );

                const data = await response.json();
                setAddress(data);
            } catch (error) {
                console.error("Error fetching address:", error);
            }
        };

        fetchAddress();
    }, []);

    // APPLY COUPON
    const handleApplyCoupon = async () => {
        try {
            const response = await apiFetch(
                `http://localhost:3002/api/coupon/apply`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        couponCode,
                        productId,
                        quantity,
                    }),
                }
            );

            const data = await response.json();

            if (response.ok) {
                setDiscount(data.discountAmount);
                setCouponMessage("Coupon applied successfully!");
            } else {
                setDiscount(0);
                setCouponMessage(data.message || "Invalid coupon");
            }
        } catch (error) {
            console.error("Coupon error:", error);
            setCouponMessage("Coupon validation failed");
        }
    };

    if (!product || !address) return <h2 className="loading">Loading...</h2>;

    const subtotal = quantity * Number(product.productPrice);
    const total = subtotal - discount;

    return (
        <div className="buy-page">
            <header className="buy-header">
                <img
                    src="/flashdrop.png"
                    alt="FlashDrop Logo"
                    className="logo"
                    onClick={() => navigate("/catalogue")}
                />
            </header>

            <div className="checkout-container">
                {/* LEFT SECTION */}
                <div className="checkout-left">

                    {/* ADDRESS */}
                    <div className="address-card">
                        <div>
                            <h3>Delivery Address</h3>
                            <p>{ }</p>
                        </div>

                        <span
                            className="edit-icon"
                            onClick={() => navigate("/address")}
                        >
                            ✏️
                        </span>
                    </div>

                    {/* PRODUCT */}
                    <div className="product-card">
                        <img src={product.productImageUrl} alt={product.productName} />

                        <div className="product-info">
                            <h3>{product.productName}</h3>
                            <p>{product.productDescription}</p>
                            <p className="price">₹ {product.productPrice}</p>

                            <div className="quantity-box">
                                <button onClick={() => setQuantity(quantity > 1 ? quantity - 1 : 1)}>-</button>
                                <span>{quantity}</span>
                                <button onClick={() => setQuantity(quantity + 1)}>+</button>
                            </div>
                        </div>
                    </div>

                    {/* COUPON */}
                    <div className="coupon-card">
                        <h3>Apply Coupon</h3>

                        <div className="coupon-box">
                            <input
                                type="text"
                                placeholder="Enter coupon code"
                                value={couponCode}
                                className=""
                                onChange={(e) => setCouponCode(e.target.value)}
                            />
                            <button className="" onClick={handleApplyCoupon}>Apply</button>
                        </div>

                        {couponMessage && <p className="coupon-message">{couponMessage}</p>}
                    </div>
                </div>

                {/* RIGHT SECTION */}
                <div className="checkout-right">
                    <h3>Price Details</h3>

                    <div className="price-row">
                        <span>Subtotal</span>
                        <span>₹ {subtotal}</span>
                    </div>

                    <div className="price-row">
                        <span>Discount</span>
                        <span className="discount">- ₹ {discount}</span>
                    </div>

                    <div className="price-row">
                        <span>Delivery</span>
                        <span className="free">FREE</span>
                    </div>

                    <hr />

                    <div className="price-row total">
                        <span>Total</span>
                        <span>₹ {total}</span>
                    </div>

                    <button className="continue-btn">
                        Continue
                    </button>
                </div>
            </div>
        </div>
    );
};

export default BuyNow;