import { useState } from "react";
import "./Register.css";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
    address: "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const navigate = useNavigate();
  const handleSubmit = async (e) => {
    e.preventDefault();

    const res = await fetch("http://localhost:3002/api/auth/registerUser", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    if(res.ok){
      navigate("/");
    }
    const data = await res.text();
    alert(data);
  };

  return (
    <div className="register-page">
      <div className="register-card">
        <h1 className="title">FlashDrop</h1>
        <h2 className="subtitle">Create Account</h2>

        <form onSubmit={handleSubmit}>

          
          <input
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            placeholder="First Name"
          />

         
          <input
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            placeholder="Last Name"
          />

          
          <input
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="Email"
          />

         
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="Password"
          />

          
          <input
            name="phoneNumber"
            value={form.phoneNumber}
            onChange={handleChange}
            placeholder="Number"
          />

          
          <input
            name="address"
            value={form.address}
            onChange={handleChange}
            placeholder="Address"
          />

          <button type="submit">Create Account</button>
        </form>

        <p className="signin-text">
          Already have an account? <a href="/">Sign in</a>
        </p>
      </div>
    </div>
  );
}