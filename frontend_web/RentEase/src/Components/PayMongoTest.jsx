"use client"

import { useState } from "react"
import axios from "axios"

function PayMongoTest() {
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState("")

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  const handlePayment = async () => {
    setLoading(true)
    try {
      // Step 1: Create payment intent
      const intentRes = await axios.post(`${API_BASE_URL}/payments/intent`, {
        amount: "10000" // ₱100.00
      })
      const intentId = intentRes.data.data.id
      const clientKey = intentRes.data.data.attributes.client_key

      // Step 2: Create payment method
      const methodRes = await axios.post(`${API_BASE_URL}/payments/method`, {
        name: "Juan Dela Cruz",
        email: "juan@example.com",
        phone: "09171234567",
        type: "gcash"
      })
      const methodId = methodRes.data.data.id

      // Step 3: Attach method to intent
      const attachRes = await axios.post(`${API_BASE_URL}/payments/intent/attach/${intentId}`, {
        payment_method: methodId,
        client_key: clientKey,
        return_url: "http://localhost:5173/payment-success"
      })

      const redirectUrl = attachRes.data.data.attributes.next_action.redirect.url

      setMessage("Redirecting to GCash...")
      window.location.href = redirectUrl // Go to GCash payment page

    } catch (error) {
      console.error(error)
      setMessage("Something went wrong. Check console.")
    } finally {
      setLoading(false)
    }
  }

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