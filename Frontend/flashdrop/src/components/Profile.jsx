import React, { useState, useEffect } from 'react';
import './Profile.css';
import { apiFetch } from '../Api';

export const Profile = () => {
  const [profileData, setProfileData] = useState({
    userId: '',
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: ''
  });
  
  // Track separate edit states so we don't overwrite current data prematurely
  const [editForm, setEditForm] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    address: ''
  });

  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');

  const emailFromStorage = localStorage.getItem("email");

  useEffect(() => {
    const fetchProfile = async () => {
      if (!emailFromStorage) {
        setError("User session context not found. Please log in.");
        setLoading(false);
        return;
      }
      try {
        setLoading(true);
        const response = await apiFetch(`http://localhost:3000/api/users/address/${emailFromStorage}`, {
          method: "GET",
        });

        if (!response.ok) {
          throw new Error(`Failed to load profile data: ${response.status}`);
        }

        const data = await response.json();
        
        const fetchedData = {
          userId: data.userId || 'N/A',
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          email: data.email || emailFromStorage,
          phoneNumber: data.phoneNumber || '',
          address: data.address || ''
        };

        setProfileData(fetchedData);
        // Pre-populate our edit form values right away
        setEditForm({
          firstName: fetchedData.firstName,
          lastName: fetchedData.lastName,
          phoneNumber: fetchedData.phoneNumber,
          address: fetchedData.address
        });

      } catch (err) {
        console.error("Profile load error:", err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [emailFromStorage]);

  // Handle inputs inside the editable fields
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditForm((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  // Open edit mode and copy the latest profile data into the fields
  const handleStartEditing = () => {
    setEditForm({
      firstName: profileData.firstName,
      lastName: profileData.lastName,
      phoneNumber: profileData.phoneNumber,
      address: profileData.address
    });
    setIsEditing(true);
  };

  // Submit values back to the backend database
  const handleSave = async (e) => {
    e.preventDefault();
    setSuccessMessage('');
    setError(null);

    try {
      const response = await apiFetch(`http://localhost:3000/api/users/update/${emailFromStorage}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          firstName: editForm.firstName,
          lastName: editForm.lastName,
          email: profileData.email,
          phoneNumber: editForm.phoneNumber,
          address: editForm.address
        })
      });

      if (!response.ok) {
        throw new Error(`Failed to update profile values: ${response.status}`);
      }

      // Update the main view values with our confirmed edits
      setProfileData((prev) => ({
        ...prev,
        firstName: editForm.firstName,
        lastName: editForm.lastName,
        phoneNumber: editForm.phoneNumber,
        address: editForm.address
      }));

      setSuccessMessage("Your profile information has been successfully updated.");
      setIsEditing(false);
      
      setTimeout(() => setSuccessMessage(''), 4000);
    } catch (err) {
      console.error("Profile save error:", err);
      setError(err.message);
    }
  };

  const getDisplayName = () => {
    if (profileData.firstName || profileData.lastName) {
      return `${profileData.firstName} ${profileData.lastName}`.trim();
    }
    return "FlashDrop Shopper";
  };

  if (loading) return <div className="profile-container state-message"><div className="spinner"></div><p>Loading account configurations...</p></div>;
  if (error) return <div className="profile-container state-message"><p className="error-text">Configuration Error: {error}</p></div>;

  return (
    <div className="profile-container">
      <div className="profile-box">
        
        {/* Profile Header Block */}
        <header className="profile-header">
          <div className="profile-avatar-circle">
            {profileData.firstName ? profileData.firstName.charAt(0).toUpperCase() : 'U'}
          </div>
          <div className="profile-meta-title">
            <h2>{getDisplayName()}</h2>
            <p className="id-badge">ID Token: {profileData.userId}</p>
          </div>
        </header>

        {successMessage && <div className="profile-alert success">{successMessage}</div>}

        <form onSubmit={handleSave} className="profile-form">
          
          <div className="form-group-row">
            {/* Email - Always Read Only */}
            <div className="form-input-block static-display">
              <label>Email Address</label>
              <div className="read-only-text-box">{profileData.email}</div>
            </div>

            {/* Phone Number */}
            <div className="form-input-block">
              <label>Phone Number</label>
              {isEditing ? (
                <input 
                  type="tel" 
                  name="phoneNumber"
                  value={editForm.phoneNumber} 
                  onChange={handleInputChange}
                  placeholder="Enter mobile number"
                />
              ) : (
                <div className="read-only-text-box placeholder-fallback">
                  {profileData.phoneNumber || "No phone number added yet"}
                </div>
              )}
            </div>
          </div>

          <div className="form-group-row">
            {/* First Name */}
            <div className="form-input-block">
              <label>First Name</label>
              {isEditing ? (
                <input 
                  type="text" 
                  name="firstName"
                  value={editForm.firstName} 
                  onChange={handleInputChange}
                  placeholder="First name"
                  required
                />
              ) : (
                <div className="read-only-text-box placeholder-fallback">
                  {profileData.firstName || "No first name added"}
                </div>
              )}
            </div>

            {/* Last Name */}
            <div className="form-input-block">
              <label>Last Name</label>
              {isEditing ? (
                <input 
                  type="text" 
                  name="lastName"
                  value={editForm.lastName} 
                  onChange={handleInputChange}
                  placeholder="Last name"
                  required
                />
              ) : (
                <div className="read-only-text-box placeholder-fallback">
                  {profileData.lastName || "No last name added"}
                </div>
              )}
            </div>
          </div>

          {/* Default Shipping Address */}
          <div className="form-input-block full-width">
            <label>Default Shipping Address</label>
            {isEditing ? (
              <textarea 
                name="address"
                value={editForm.address} 
                onChange={handleInputChange}
                placeholder="Provide your building details, street name, city, and pincode"
                rows="4"
              />
            ) : (
              <div className="read-only-text-box textarea-style placeholder-fallback">
                {profileData.address || "No shipping destination address listed under this account profile node."}
              </div>
            )}
          </div>

          {/* Action Control Panel Footer */}
          <div className="profile-actions-footer">
            {!isEditing ? (
              <button 
                type="button" 
                className="profile-btn edit-trigger" 
                onClick={handleStartEditing}
              >
                Edit Account Details
              </button>
            ) : (
              <div className="editing-buttons-group">
                <button 
                  type="button" 
                  className="profile-btn cancel-trigger" 
                  onClick={() => setIsEditing(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="profile-btn save-trigger">
                  Save Changes
                </button>
              </div>
            )}
          </div>

        </form>
      </div>
    </div>
  );
};