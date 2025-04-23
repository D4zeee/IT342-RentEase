"use client"

import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import Cookies from 'js-cookie';  // Add this line
import styles from "../styles/Payments.module.css";

const Payments = () => {
  const location = useLocation();
  const [status, setStatus] = useState("");
  const [receipt, setReceipt] = useState(null);

  const query = new URLSearchParams(location.search);
  const paymentIntentId = query.get("payment_intent_id");

  useEffect(() => {
    const token = Cookies.get("jwt_token"); // or your actual token name
  
    axios.get("http://localhost:8080/owners/current-user", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    .then((res) => {
      const ownerId = res.data.ownerId;
  
      // Now fetch only rooms that belong to this owner
      return axios.get(`http://localhost:8080/rooms/owner/${ownerId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    })
    .then((res) => {
      setRooms(res.data); // Now your dropdown will only show the correct rooms
    })
    .catch((err) => {
      console.error("Failed to load rooms for owner:", err);
    });
  }, []);

  return (
    <div className={styles.payments}>
      <h2>Payments Dashboard</h2>
      <p>Welcome to RentEase! View your payment status below.</p>

      {receipt && (
        <div className={styles.receipt}>
          <h3>🧾 Payment Receipt</h3>
          <p><strong>Status:</strong> <span style={{ color: status === "Paid" ? "green" : "orange" }}>{status}</span></p>
          <p><strong>Amount:</strong> ₱{(receipt.amount / 100).toFixed(2)}</p>
          <p><strong>Reference ID:</strong> {paymentIntentId}</p>
          <p><strong>Description:</strong> {receipt.description}</p>
        </div>
      )}
    </div>
  );
};

export default Payments;

