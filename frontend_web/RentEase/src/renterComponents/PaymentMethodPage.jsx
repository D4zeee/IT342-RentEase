"use client"

import { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import axios from "axios"
import Cookies from "js-cookie"

function PaymentMethodPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { paymentIntentId, clientKey, roomId } = location.state || {} // Receive roomId from state

  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [phone, setPhone] = useState("")

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const token = Cookies.get("renterToken")
    if (!token) return

    axios
      .get(`${API_BASE_URL}/api/renters/current`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setName(res.data.name)
        setEmail(res.data.email)
      })
      .catch((err) => {
        console.error("Failed to fetch renter info:", err.message)
      })
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!paymentIntentId || !clientKey) {
      alert("Missing payment intent information.")
      return
    }

    try {
      const methodResponse = await axios.post(`${API_BASE_URL}/payments/method`, {
        name,
        email,
        phone,
        type: "gcash",
      })

      const paymentMethodId = methodResponse.data.data.id

      const attachResponse = await axios.post(
        `${API_BASE_URL}/payments/intent/attach/${paymentIntentId}`,
        {
          payment_method: paymentMethodId,
          client_key: clientKey,
          return_url: `http://localhost:5173/payment-success?payment_intent_id=${paymentIntentId}&room_id=${roomId}`, // Pass roomId in URL
        }
      )

      const checkoutUrl = attachResponse.data.data.attributes.next_action.redirect.url
      window.location.href = checkoutUrl
    } catch (error) {
      console.error("Payment method or attach error:", error.response?.data || error.message)
      alert("Failed to proceed with payment.")
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center items-center">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <h1 className="text-2xl font-bold mb-6 text-gray-800">Enter Billing Info for GCash</h1>

        <label className="block mb-4">
          <span className="text-gray-700">Name</span>
          <input
            type="text"
            value={name}
            readOnly
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm bg-gray-100 cursor-not-allowed"
          />
        </label>

        <label className="block mb-4">
          <span className="text-gray-700">Email</span>
          <input
            type="email"
            value={email}
            readOnly
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm bg-gray-100 cursor-not-allowed"
          />
        </label>

        <label className="block mb-6">
          <span className="text-gray-700">Phone</span>
          <input
            type="text"
            required
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
            placeholder="+63..."
          />
        </label>

        <button type="submit" className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700">
          Proceed to GCash
        </button>
      </form>
    </div>
  )
}

export default PaymentMethodPage