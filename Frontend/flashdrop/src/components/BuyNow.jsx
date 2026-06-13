import React, { useEffect, useState } from "react";
import "./BuyNow.css";
import { useNavigate, useParams } from "react-router-dom";
import { apiFetch } from "../Api";
import flashdrop from "/Users/sumanshuanand/Documents/flashdrop/Frontend/flashdrop/public/flashdrop.png";


const BuyNow = () => {
    const navigate = useNavigate();
    const { productId } = useParams();

    const [product, setProduct] = useState(null);
    const [formData, setFormData] = useState({
        email: "",
        address: "",
        PhoneNumber: "",
        name: "",

    });
    const [quantity, setQuantity] = useState(1);
    const [couponCode, setCouponCode] = useState("");
    const [discount, setDiscount] = useState(0);
    const [couponMessage, setCouponMessage] = useState("");
    const [orderMessage, setOrderMessage] = useState("");
    const [showPopup, setShowPopup] = useState(false);
    const [popupTitle, setPopupTitle] = useState("");
    const [popupMessage, setPopupMessage] = useState("");
    const [popupClass, setPopupClass] = useState("");
    const navtoCatalogue = () => {
        navigate("/catalogue");
    };

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
                    `http://localhost:3000/api/users/address/${localStorage.getItem("email")}`,
                    { method: "GET" }
                );

                const data = await response.json();

                console.log("Address Response:", data);

                setFormData({
                    email: data.email,
                    address: data.address,
                    PhoneNumber: data.phoneNumber,
                    name: `${data.firstName} ${data.lastName}`
                });

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
                `http://localhost:3002/api/coupon/fetchOff`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        couponCode,
                        productId,
                        quantity,
                        productPrice: product?.productPrice,
                    }),
                }
            );

            const data = await response.json();


            if (response.ok) {
                setDiscount(Number(data));
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



    const email = localStorage.getItem("email");
    console.log("Email from localStorage:", email);
    //Place Order
    const placeOrder = async () => {
        try {
            const response = await apiFetch(
                `http://localhost:3002/api/order`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        productId,
                        quantity,
                        email,
                        totalPrice: total,
                    }),
                }
            );

            const data = await response.json();

            console.log("Order Response:", data);

            if (!response.ok) {
                setOrderMessage(data.message || "Order failed");
                return;
            }

            switch (data.status) {
                case "CONFIRMED":
                    setPopupTitle("✅ Order Confirmed");
                    setPopupMessage(
                        `${data.message}

Order ID: ${data.orderId}

`
                    );
                    setPopupClass("success-popup");
                    setShowPopup(true);

                    //setTimeout(() => {
                    //     navigate("/catalogue");
                    //}, 10000);

                    break;

                case "CANCELLED":
                    setPopupTitle("⚠️ Order Cancelled");
                    setPopupMessage(data.message);
                    setPopupClass("warning-popup");
                    setShowPopup(true);
                    break;

                case "NORMAL":
                    setOrderMessage(data.message);
                    setPopupTitle("✅ Order Placed");
                    setPopupMessage(
                        `${data.message}

Order ID: ${data.orderId}

`
                    );
                    setPopupClass("success-popup");
                    setShowPopup(true);

                    //setTimeout(() => {
                    //     navigate("/catalogue");
                    //}, 10000);
                    break;

                default:
                    setOrderMessage("Unknown response received");
            }

        } catch (error) {
            setPopupTitle("❌ Order Failed");
            setPopupMessage(data.message);
            setPopupClass("error-popup");
            setShowPopup(true);
        }


    }


    if (!product || !formData) return <h2 className="loading">Loading...</h2>;

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
                            <p>{"Name: " + formData.name}</p>
                            <p>{"Address: " + formData.address}</p>
                            <p>{"Phone: " + formData.PhoneNumber}</p>
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

                    <button className="continue-btn" onClick={() => placeOrder()}>
                        Place Order
                    </button>
                </div>
            </div>
            {showPopup && (
                <div className="popup-overlay">
                    <div className={`popup ${popupClass}`}>
                        <h2>{popupTitle}</h2>

                        <p>{popupMessage}</p>

                        <button
                            className="popup-btn"
                            onClick={() => navtoCatalogue()}
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default BuyNow;


