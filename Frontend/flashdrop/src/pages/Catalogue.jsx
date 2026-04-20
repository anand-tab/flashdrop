import React, { useState, useEffect } from "react";
import "./Catalogue.css";
import { useNavigate } from "react-router-dom";
import { apiFetch } from "../Api";
import flashdrop from "/Users/sumanshuanand/Documents/flashdrop/Frontend/flashdrop/public/flashdrop.png";



const Catalogue = () => {
    const [open, setOpen] = useState(false);
    const [products, setProducts] = useState([]);
    const navigate = useNavigate();
    const [search, setSearch] = useState("");
    const productFilter = products.filter((product) => {
        const searchTerm = search.trim().toLowerCase()

        return (
            product.productName.toLowerCase().includes(searchTerm) ||
            product.productPrice.toString().includes(searchTerm) ||
            product.rating.toString().includes(searchTerm)
        )
    })


    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/");
    };

    // FETCH PRODUCTS
    useEffect(() => {
        const token = localStorage.getItem("token");

        if (!token) {
            navigate("/");
            return;
        }

        const getProduct = async () => {
            try {
                const response = await apiFetch(`http://localhost:3002/api/product`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.status === 401) {
                    localStorage.removeItem("token");
                    navigate("/");
                    return;
                }

                const data = await response.json();
                setProducts(data);

            } catch (error) {
                console.log("Could not fetch the product " + error);
            }
        };

        getProduct();
    }, [navigate]);

    // CLOSE DROPDOWN OUTSIDE CLICK
    useEffect(() => {
        const close = () => setOpen(false);
        document.addEventListener("click", close);
        return () => document.removeEventListener("click", close);
    }, []);

    const handleClick = (id) => {
        navigate(`/product/${id}`);
    }
    return (
        <div className="catalogue">
            {/* NAVBAR */}
            <header className="navbar">
                <img
                    src={flashdrop}
                    alt="FlashDrop Logo"
                    className="logo"
                  />

                <input
                    type="text"
                    placeholder="Search for products..."
                    className="search-bar"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />

                <div className="nav-icons">
                    <span className="cart">🛒</span>

                    <div className="user-menu">
                        <span
                            className="user-name"
                            onClick={(e) => {
                                e.stopPropagation();
                                setOpen(!open);
                            }}
                        >
                            👤 
                        </span>

                        {open && (
                            <div className="dropdown">
                                <div onClick={() => navigate("/profile")}>Profile</div>
                                <div onClick={() => navigate("/orders")}>Orders</div>
                                <div onClick={handleLogout}>Logout</div>
                            </div>
                        )}
                    </div>
                </div>
            </header>

            {/* CATEGORIES */}
            <div className="categories">
                {[
                    "All",
                    "Laptops",
                    "Audio",
                    "Smartphones",
                    "Cameras",
                    "Wearables",
                    "Gaming",
                    "Tablets",
                ].map((cat) => (
                    <span key={cat} className="category">
                        {cat}
                    </span>
                ))}
            </div>

            {/* HERO */}
            <div className="hero">
                <h1>Welcome to FlashDrop</h1>
                <p>
                    Discover high-demand products during flash sales. Limited stock,
                    lightning fast checkout.
                </p>
                <button>Explore Now</button>
            </div>

            {/* PRODUCTS */}
            <h2 className="section-title">Products</h2>

            <div className="products">
                {productFilter.map((p, index) => (
                    <div key={index} className="card" onClick={() => handleClick(p.productId)}>
                        <img src={p.productImageUrl} alt={p.productName} />
                        <h3>{p.productName}</h3>
                        <p className="rating">⭐ {p.rating}</p>
                        <p className="price">₹ {p.productPrice}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Catalogue;