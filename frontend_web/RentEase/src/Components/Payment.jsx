"use client"

import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import Cookies from 'js-cookie';
import styles from "../styles/Payments.module.css";

const Payments = () => {
  const location = useLocation();
  const [payment, setPayment] = useState(null);
  const [rooms, setRooms] = useState([]);

  const query = new URLSearchParams(location.search);
  const paymentIntentId = query.get("payment_intent_id");

  useEffect(() => {
    const token = Cookies.get("jwt_token");

    // Fetch owner and their rooms
    axios.get("http://localhost:8080/owners/current-user", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    .then((res) => {
      const ownerId = res.data.ownerId;
      return axios.get(`http://localhost:8080/rooms/owner/${ownerId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    })
    .then((res) => {
      setRooms(res.data);
    })
    .catch((err) => {
      console.error("Failed to load rooms for owner:", err);
    });

    // Fetch payment details from the database using paymentIntentId
    if (paymentIntentId) {
      axios.get(`http://localhost:8080/payments/by-intent-id/${paymentIntentId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        const data = res.data;
        setPayment({
          status: data.status,
          amount: data.amount,
          description: "RentEase Payment", // You can fetch this from the database if stored
          paymentIntentId: data.paymentIntentId,
          paymentMethod: data.paymentMethod,
        });
      })
      .catch((err) => {
        console.error("Failed to fetch payment from database:", err.response?.data || err.message);
      });
    }
  }, [paymentIntentId]);

  return (
    <div className={styles.payments}>
      <h2>Payments Dashboard</h2>
      <p>Welcome to RentEase! View your payment status below.</p>

      {payment ? (
        <div className={styles.receipt}>
          <h3>🧾 Payment Receipt</h3>
          <p><strong>Status:</strong> <span style={{ color: payment.status === "Paid" ? "green" : "orange" }}>{payment.status}</span></p>
          <p><strong>Amount:</strong> ₱{(payment.amount).toFixed(2)}</p>
          <p><strong>Payment Method:</strong> {payment.paymentMethod}</p>
          <p><strong>Reference ID:</strong> {payment.paymentIntentId}</p>
          <p><strong>Description:</strong> {payment.description}</p>
        </div>
      ) : (
        <p>No payment details available.</p>
      )}
    </div>
  );
};

export default Payments;