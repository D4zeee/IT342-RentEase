"use client"

import { useLocation, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"

function PaymentSuccess() {
  const location = useLocation()
  const navigate = useNavigate()
  const query = new URLSearchParams(location.search)
  const paymentIntentId = query.get("payment_intent_id")
  const roomId = query.get("room_id")

  const [status, setStatus] = useState("")
  const [amount, setAmount] = useState(0)
  const [description, setDescription] = useState("")
  const [savedPayment, setSavedPayment] = useState(null)

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const fetchPaymentDetails = async () => {
      const token = Cookies.get("renterToken")
      if (!token) {
        console.error("No renter token found, redirecting to login")
        navigate("/renter-login")
        return
      }

      try {
        const response = await axios.get(
          `${API_BASE_URL}/payments/intent/${paymentIntentId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        )

        const data = response.data.data.attributes
        setStatus(data.status)
        setAmount(data.amount)
        setDescription(data.description)

        if (data.status === "succeeded") {
          savePayment(token)
        }
      } catch (error) {
        console.error("Failed to fetch payment intent:", error.response?.data || error.message)
        setStatus("Unknown")
        if (error.response?.status === 401 || error.response?.status === 403) {
          console.error("Authentication failed, redirecting to login")
          navigate("/renter-login")
          return
        }
        // Attempt to save payment even if fetch fails
        savePayment(token)
      }
    }

    const savePayment = async (token) => {
      try {
        console.log("Saving payment with:", { paymentIntentId, roomId })
        const saveResponse = await axios.post(
          `${API_BASE_URL}/payments/save`,
          {
            paymentIntentId,
            roomId,
          },
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        )
        console.log("Save payment response:", saveResponse.data)
        setSavedPayment(saveResponse.data)
      } catch (error) {
        console.error("Failed to save payment:", error.response?.data || error.message)
        if (error.response?.status === 401 || error.response?.status === 403) {
          console.error("Authentication failed, redirecting to login")
          navigate("/renter-login")
        }
      }
    }

    if (paymentIntentId && roomId) {
      fetchPaymentDetails()
    } else {
      console.error("Missing paymentIntentId or roomId")
      setStatus("Unknown")
    }
  }, [paymentIntentId, roomId, navigate])

  return (
    <div className="p-10 text-center">
      <h1 className="text-2xl font-bold">🎉 Payment {status === "succeeded" ? "Success" : "Status"}</h1>
      {status && (
        <p className="mt-4 text-lg">
          Status: <strong className={status === "succeeded" ? "text-green-600" : "text-orange-600"}>{status}</strong>
        </p>
      )}
      {amount > 0 && (
        <p className="mt-2">Amount Paid: <strong>₱{(amount / 100).toFixed(2)}</strong></p>
      )}
      {description && (
        <p className="mt-1 text-sm text-gray-500">Description: {description}</p>
      )}
      {paymentIntentId && (
        <p className="mt-4 text-sm text-gray-500">
          Payment Intent ID: <code>{paymentIntentId}</code>
        </p>
      )}
      {savedPayment && (
        <div className="mt-4">
          <h3 className="text-lg font-semibold">🧾 Payment Saved in Database</h3>
          <p>Payment ID: <strong>{savedPayment.paymentId}</strong></p>
          <p>Status: <strong>{savedPayment.status}</strong></p>
          <p>Amount: <strong>₱{savedPayment.amount.toFixed(2)}</strong></p>
          <p>Payment Method: <strong>{savedPayment.paymentMethod}</strong></p>
        </div>
      )}
    </div>
  )
}

export default PaymentSuccess