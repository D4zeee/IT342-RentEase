"use client"

import { useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { useNavigate } from "react-router-dom"
import { jwtDecode } from "jwt-decode"

function RenterLogin() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const navigate = useNavigate()

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const response = await axios.post(`${API_BASE_URL}/api/renters/login`, { email, password })
      const { jwt } = response.data
      Cookies.set("renterToken", jwt, { expires: 1 })

      // Decode the JWT to verify contents (optional, for logging)
      const decodedToken = jwtDecode(jwt)
      console.log("Decoded JWT:", decodedToken)

      navigate("/renter-dashboard")
    } catch (error) {
      console.error("Login error:", error.response?.data || error.message)
      alert("Login failed: " + (error.response?.data || error.message))
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      <button type="submit">Login</button>
    </form>
  )
}

export default RenterLogin