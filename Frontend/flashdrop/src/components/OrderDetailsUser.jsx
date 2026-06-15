import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './OrderDetailsUser.css';
import { apiFetch } from '../Api';

export const OrderDetailsUser = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedOrder, setSelectedOrder] = useState(null);

    // Pagination Configuration
    const [currentPage, setCurrentPage] = useState(1);
    const ordersPerPage = 5;

    const navigate = useNavigate();
    const email = localStorage.getItem("email");

    useEffect(() => {
        const fetchOrders = async () => {
            if (!email) {
                setError("User email not found. Please log in.");
                setLoading(false);
                return;
            }

            try {
                setLoading(true);
                const response = await apiFetch(`http://localhost:3002/api/order/orders/${email}`, {
                    method: "GET",
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();

                if (Array.isArray(data)) {
                    // Sorts automatically from most recent date to oldest date
                    const sortedData = data.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));
                    setOrders(sortedData);
                } else {
                    setOrders([]);
                }
            } catch (err) {
                console.error("Failed to fetch orders:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, [email]);

    const formatBackendDate = (dateString) => {
        if (!dateString) return "N/A";
        return new Date(dateString).toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        });
    };

    // Pagination Slice Logic
    const indexOfLastOrder = currentPage * ordersPerPage;
    const indexOfFirstOrder = indexOfLastOrder - ordersPerPage;
    const currentOrders = orders.slice(indexOfFirstOrder, indexOfLastOrder);
    const totalPages = Math.ceil(orders.length / ordersPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleProductNavigation = (e, order) => {
        e.stopPropagation(); // Stops the modal from firing open
        
        // SAFE KEY EXTRACTION: Tries matching 'productId' or alternative names like 'id' from your data object
        const idToPass = order.productId || order.id || order.idOfProduct;
        
        if (!idToPass) {
            console.error("Navigation Aborted: The product ID key is null/undefined on this item record:", order);
            alert("Unable to route: Missing a valid Product ID key on this order record.");
            return;
        }

        navigate(`/product/${idToPass}`);
    };

    if (loading) return <div className="orders-container state-message"><div className="spinner"></div><p>Loading your orders...</p></div>;
    if (error) return <div className="orders-container state-message"><p className="error-text">Error: {error}</p></div>;
    if (orders.length === 0) return <div className="orders-container state-message"><h2>No Orders Found</h2><p>Looks like you haven't placed any flash-sale orders yet.</p><button className="btn-primary" onClick={() => navigate('/')}>Explore Products</button></div>;

    return (
        <div className="orders-container">
            <header className="orders-header">
                <h1>Your Orders</h1>
                <p>Manage and track your recent FlashDrop purchases</p>
            </header>

            <div className="orders-list">
                {currentOrders.map((order, index) => {
                    // Extracting ID on render for explicit safety check
                    const safeId = order.productId || order.id || "N/A";

                    return (
                        <div key={order.orderId || index} className="amazon-style-card" onClick={() => setSelectedOrder(order)}>

                            {/* Top Row Bounding Header Metadata */}
                            <div className="amazon-card-header">
                                <div className="header-left-meta">
                                    <div className="meta-group">
                                        <span className="meta-label">ORDER PLACED</span>
                                        <span className="meta-value">{formatBackendDate(order.orderDate)}</span>
                                    </div>
                                    <div className="meta-group">
                                        <span className="meta-label">TOTAL</span>
                                        <span className="meta-value total-value">₹ {order.totalPrice.toFixed(2)}</span>
                                    </div>
                                    <div className="meta-group hide-mobile">
                                        <span className="meta-label">SHIP TO</span>
                                        <span className="meta-value user-email-truncate" title={order.email}>{order.email}</span>
                                    </div>
                                </div>
                                <div className="header-right-meta">
                                    <div className="meta-group text-right">
                                        <span className="meta-label">ORDER # {order.orderId || "N/A"}</span>
                                    </div>
                                </div>
                            </div>

                            {/* Main Content Bounding Body Block */}
                            <div className="amazon-card-body">
                                <div className="status-announcement">
                                    <h2 className={`status-title text-${order.status ? order.status.toLowerCase().replace(/\s+/g, '-') : 'pending'}`}>
                                        {order.status || "Processing"}
                                    </h2>
                                    {order.status?.toLowerCase() === 'cancelled' && (
                                        <p className="status-subtext">If you were charged, a refund will be processed and credited to the original payment method within next 3-5 business days.</p>
                                    )}
                                    {order.status?.toLowerCase() === 'delivered' && (
                                        <p className="status-subtext">Your package was handed directly to the resident or secure drop box.</p>
                                    )}
                                </div>

                                <div className="amazon-item-row">
                                    <img
                                        src={order.imageUrl || 'https://via.placeholder.com/90'}
                                        alt="Product representation"
                                        className="amazon-item-image"
                                        onClick={(e) => handleProductNavigation(e, order)}
                                        title="View item details"
                                    />
                                    <div className="amazon-item-details">
                                        <span className="product-title-link" onClick={(e) => handleProductNavigation(e, order)}>
                                            {order.productDescription || `FlashDrop Exclusive Item (ID: ${safeId})`}
                                        </span>
                                        <p className="amazon-qty">Quantity: <strong>{order.quantity}</strong></p>
                                        <button className="view-details-action-btn" onClick={(e) => handleProductNavigation(e, order)}>
                                            View Product Details
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div className="amazon-card-footer">
                                <span className="click-hint">Click inside order box for localized receipt and tracking metadata</span>
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Pagination Actions Controller */}
            {totalPages > 1 && (
                <div className="pagination-wrapper">
                    <button
                        className="pagination-btn arrow-btn"
                        disabled={currentPage === 1}
                        onClick={() => handlePageChange(currentPage - 1)}
                    >
                        &larr; Previous
                    </button>

                    {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
                        <button
                            key={pageNum}
                            className={`pagination-btn num-btn ${currentPage === pageNum ? 'active-page' : ''}`}
                            onClick={() => handlePageChange(pageNum)}
                        >
                            {pageNum}
                        </button>
                    ))}

                    <button
                        className="pagination-btn arrow-btn"
                        disabled={currentPage === totalPages}
                        onClick={() => handlePageChange(currentPage + 1)}
                    >
                        Next &rarr;
                    </button>
                </div>
            )}

            {/* Complete Popup Sheet Modal */}
            {selectedOrder && (
                <div className="modal-overlay" onClick={() => setSelectedOrder(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="close-modal-btn" onClick={() => setSelectedOrder(null)}>&times;</button>

                        <h2>Order Context Sheet</h2>
                        <hr className="modal-divider" />

                        <div className="modal-grid">
                            <div className="modal-section">
                                <h4>Order Ledger</h4>
                                <p><strong>Order ID:</strong> {selectedOrder.orderId || "N/A"}</p>
                                <p><strong>Registry Date:</strong> {formatBackendDate(selectedOrder.orderDate)}</p>
                                <p><strong>Fulfillment Status:</strong> <span className={`status-text-${selectedOrder.status?.toLowerCase().replace(/\s+/g, '-')}`}>{selectedOrder.status}</span></p>
                            </div>

                            <div className="modal-section">
                                <h4>Shipping Registry</h4>
                                <p><strong>User Destination Account:</strong></p>
                                <p className="address-placeholder">{selectedOrder.email}</p>
                                <p className="address-placeholder sub-info">Standard Domestic Secure Hand-Delivery Node</p>
                            </div>
                        </div>

                        <hr className="modal-divider" />

                        <div className="modal-item-row">
                            <img src={selectedOrder.imageUrl || 'https://via.placeholder.com/90'} alt="Modal focal item" className="modal-item-image" />
                            <div className="modal-item-details">
                                <h3>Product Identifier Token: {selectedOrder.productId || selectedOrder.id || "N/A"}</h3>
                                <p className="modal-item-desc-text">{selectedOrder.productDescription || "No localized contextual summary available from system inventory database."}</p>
                                <p className="modal-qty">Units Bound: <strong>{selectedOrder.quantity}</strong></p>
                            </div>
                            <div className="modal-item-price">
                                <p>Unit Cost: ₹ {(selectedOrder.totalPrice / selectedOrder.quantity).toFixed(2)}</p>
                                <h3 className="final-price">Aggregate: ₹ {selectedOrder.totalPrice.toFixed(2)}</h3>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};