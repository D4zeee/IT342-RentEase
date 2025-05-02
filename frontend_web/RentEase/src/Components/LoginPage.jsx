"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { Card, CardBody, Typography } from "@material-tailwind/react"
import { UserIcon, KeyIcon } from "@heroicons/react/24/outline"
import axios from "axios"
import Cookies from "js-cookie"

function LoginPage() {
  const navigate = useNavigate()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errors, setErrors] = useState({})

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  // Check if the user is already logged in
  useEffect(() => {
    const token = Cookies.get("token")
    if (token) {
      navigate("/dashboard")
    }
  }, [navigate])

  // Validate the login form fields
  const validate = () => {
    const newErrors = {}
    if (!username.trim()) newErrors.username = "Username is required"
    if (!password) newErrors.password = "Password is required"

    return newErrors
  }

  // Handle the change of input fields
  const handleChange = (e) => {
    const { name, value } = e.target
    if (name === "username") {
      setUsername(value)
    } else if (name === "password") {
      setPassword(value)
    }

    // Clear error for the field
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ""
      })
    }
  }

  const handleLogin = async (e) => {
    e.preventDefault()
  
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }
  
    setIsSubmitting(true)
    setErrors({})
  
    try {
      const response = await axios.post(
        `${API_BASE_URL}/owners/login`,
        {
          username,
          password,
        }
      )
      const { token } = response.data
      Cookies.set("token", token, { expires: 7 })
      navigate("/dashboard")
    } catch (error) {
      setIsSubmitting(false)
      console.error("Login failed:", error)
      setErrors({ general: "Invalid Credentials" })
    }
  }

  return (
    <div className="h-screen w-full fixed flex justify-center items-center">
      <div className="fixed inset-0 z-[-1]">
        <div
          className="absolute inset-0 bg-cover bg-center blur-[5px]"
          style={{ backgroundImage: "url('/assets/img/images2.jpg')" }}
        ></div>
        <div className="absolute inset-0 bg-black/30"></div>
      </div>

      {/* Login Container */}
      <Card 
        className="w-[90%] max-w-[400px] z-10 shadow-xl overflow-hidden" 
        style={{ 
          background: "linear-gradient(145deg, rgba(0, 42, 58, 0.95), rgba(0, 52, 68, 0.9))" 
        }}
      >
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-teal-400 to-blue-500"></div>
        
        <CardBody className="p-8 md:p-7 text-center text-white">
          {/* Header */}
          <Typography variant="h1" className="text-[2.2rem] font-semibold mb-5 leading-[1.3]">
            Welcome to
            <br />
            RentEase
          </Typography>

          <Typography className="text-base opacity-80 mb-8 leading-[1.6]">
            The best place to find millions of
            <br />
            homes, apartments, and office spaces.
          </Typography>

          {/* Login Form */}
          <form onSubmit={handleLogin} className="mx-auto mb-5 flex flex-col gap-4 w-[80%]">
            <div className="relative">
              <div className="absolute top-4 left-3 text-gray-400">
                <UserIcon className="h-5 w-5" />
              </div>
              <input
                type="text"
                name="username"
                placeholder="Username"
                value={username}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.username ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.username && (
                <p className="absolute left-3 text-red-400 text-xs mt-1 ml-2">{errors.username}</p>
              )}
            </div>
            <div className="relative">
              <div className="absolute top-6 left-3 text-gray-400">
                <KeyIcon className="h-5 w-5" />
              </div>
              <input
                type="password"
                name="password"
                placeholder="Password"
                value={password}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 mt-2 rounded-lg border ${
                  errors.password ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.password && (
                <p className="absolute left-3 text-red-400 text-xs mt-1 ml-2">{errors.password}</p>
              )}
            </div>

            {/* General Error Message */}
            {errors.general && (
              <p className="text-red-400 text-xs">{errors.general}</p>
            )}
             
            {/* Login Button */}
            <div className="flex justify-center">
              <button
                type="submit"
                disabled={isSubmitting}
                className={`bg-[#0a8ea8] text-white border-none rounded-[10px] py-3 px-8 w-[100%] 
                  font-medium cursor-pointer transition-all duration-300 
                  hover:bg-[#0a7d94] hover:shadow-md mt-2 ${
                  isSubmitting ? "opacity-70" : ""
                }`}
              >
                {isSubmitting ? "Logging in..." : "L o g i n"}
              </button>
            </div>
          </form>

          {/* OR Divider */}
          <div className="flex items-center gap-2 my-6">
            <div className="h-px flex-1 bg-gray-600/50"></div>
            <Typography className="font-medium text-gray-300">OR</Typography>
            <div className="h-px flex-1 bg-gray-600/50"></div>
          </div>

          {/* Google Sign-In Button */}
          <button
            className="bg-white text-[#444] border-none 
            rounded-[50px] py-3 px-6 text-base font-medium cursor-pointer 
            inline-flex items-center justify-center gap-2 
            mx-auto w-[80%] transition-all hover:bg-gray-100 hover:shadow-md"
            onClick={() => navigate("/dashboard")} // Dummy sign-in function for Google
          >
            <img src="/assets/img/google.png" alt="Google" className="w-5 h-5" />
            Sign in with Google
          </button>

          {/* Register Link */}
          <Typography className="mt-8 text-base text-gray-300">
            Don't have an account?{" "}
            <Link to="/register" className="text-teal-300 hover:text-teal-200 transition-colors">
              Register
            </Link>
          </Typography>
        </CardBody>
      </Card>
    </div>
  )
}

export default LoginPage