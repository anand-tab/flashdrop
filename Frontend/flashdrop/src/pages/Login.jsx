import React, { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";

export const Login = () => {
  const navigate  = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    try {
      const response = await fetch(`http://localhost:3002/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });
      const data = await response.json();

      if (response.ok) {
        console.log("it entered here");
        localStorage.setItem("token", data.token);
        navigate("/catalogue");
      } else {
        alert(data.message || "Login failed");
      }

    } catch (error) {
      console.error("Error while logging in", error);
    }
  };

  return (
    <div className="login-container">
      <div className="login-header">
        <h1 className="brand-logo">FlashDrop</h1>
        <h2 className="login-title">LogIn</h2>
      </div>

      <div className="login-card">
        <div className="form-group">
          <label className="form-label">Email</label>
          <input
            type="email"
            name="email"
            className="form-input"
            placeholder="john@example.com"
            value={formData.email}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label className="form-label">Password</label>
          <input
            type="password"
            name="password"
            className="form-input"
            placeholder="••••••••"
            value={formData.password}
            onChange={handleChange}
          />
        </div>

        <button className="login-btn" onClick={handleSubmit}>
          Sign In
        </button>

        <p className="register-text">
          Don't have an account?{" "}

          <a className="register-link" href="./register">Create one</a>
        </p>
      </div>
    </div>
  );
};

export default Login;