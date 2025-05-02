"use client"

import { useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"

function PayMongoTest() {
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState("")

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
  console.log("API_BASE_URL:", API_BASE_URL); // Debug log
  console.log("VITE_FRONTEND_URL:", import.meta.env.VITE_FRONTEND_URL); // Debug log

  const handlePayment = async () => {
    const token = Cookies.get("renterToken");
    if (!token) {
      setMessage("Please log in as a renter first.");
      return;
    }
    setLoading(true);
    try {
      const intentRes = await axios.post(
        `${API_BASE_URL}/payments/intent`,
        { amount: "10000" },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const intentId = intentRes.data.data.id;
      const clientKey = intentRes.data.data.attributes.client_key;

      const methodRes = await axios.post(
        `${API_BASE_URL}/payments/method`,
        {
          name: "Juan Dela Cruz",
          email: "juan@example.com",
          phone: "09171234567",
          type: "gcash"
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const methodId = methodRes.data.data.id;

      const returnUrl = `${
        import.meta.env.VITE_FRONTEND_URL || "http://localhost:5173"
      }/payment-success`;
      console.log("Constructed return_url:", returnUrl); // Debug log

      const attachRes = await axios.post(
        `${API_BASE_URL}/payments/intent/attach/${intentId}`,
        {
          payment_method: methodId,
          client_key: clientKey,
          return_url: returnUrl
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const redirectUrl = attachRes.data.data.attributes.next_action.redirect.url;
      setMessage("Redirecting to GCash...");
      window.location.href = redirectUrl;
    } catch (error) {
      console.error("Payment error:", error.response?.data || error.message);
      setMessage("Something went wrong. Check console.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-10 max-w-lg mx-auto">
      <h1 className="text-2xl font-bold mb-4">PayMongo GCash Test</h1>
      <button
        onClick={handlePayment}
        disabled={loading}
        className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
      >
        {loading ? "Processing..." : "Pay ₱100.00 via GCash"}
      </button>
      <p className="mt-4 text-sm text-gray-600">{message}</p>
    </div>
  )
}

export default PayMongoTest