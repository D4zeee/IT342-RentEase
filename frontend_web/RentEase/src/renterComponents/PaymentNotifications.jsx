"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"

function PaymentNotifications() {
  const [payments, setPayments] = useState([])

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const token = Cookies.get("renterToken")
    if (!token) return

    axios
      .get(`${API_BASE_URL}/payments/renter/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setPayments(res.data)
      })
      .catch((err) => {
        console.error("Failed to fetch payments:", err.response?.data || err.message)
      })
  }, [])

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">🧾 Payment Notifications</h2>
      {payments.length > 0 ? (
        payments.map((p) => (
          <div key={p.paymentId} className="border rounded-lg p-4 mb-4 shadow">
            <p>
              <strong>Status:</strong> {p.status}
            </p>
            <p>
              <strong>Amount:</strong> ₱{p.amount.toFixed(2)}
            </p>
            <p>
              <strong>Method:</strong> {p.paymentMethod}
            </p>
            <p>
              <strong>Reference ID:</strong> {p.paymentIntentId}
            </p>
            <p>
              <strong>Paid Date:</strong> {p.paidDate || "N/A"}
            </p>
          </div>
        ))
      ) : (
        <p>No payments found.</p>
      )}
    </div>
  )
}

export default PaymentNotifications