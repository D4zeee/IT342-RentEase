"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { Card, CardBody, Typography, Button } from "@material-tailwind/react"
import { ArrowRightIcon, KeyIcon, IdentificationIcon } from "@heroicons/react/24/outline"
import axios from "axios"
import Cookies from "js-cookie"

function RegisterPage() {
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  })

  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [successMessage, setSuccessMessage] = useState("")

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  // Check if the user is already logged in by checking for the token in cookies
  useEffect(() => {
    const token = Cookies.get("token")
    if (token) {
      navigate("/dashboard")
    }
  }, [navigate])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value
    })

    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ""
      })
    }
  }

  const validate = () => {
    const newErrors = {}
    if (!formData.username.trim()) newErrors.username = "Username is required"
    if (!formData.password) newErrors.password = "Password is required"
    else if (formData.password.length < 6) newErrors.password = "Password must be at least 6 characters"

    return newErrors
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }
    setIsSubmitting(true)

    try {
      const response = await axios.post(
        `${API_BASE_URL}/owners/register`,
        formData
      )
      console.log(response.data)

      setSuccessMessage("Registration successful! Redirecting to login...")
      setIsSubmitting(false)
      setTimeout(() => {
        navigate("/login")
      }, 1000)
    } catch (error) {
      setIsSubmitting(false)

      if (error.response && error.response.status === 409) {
        setErrors({ username: "Username already taken" })
      } else {
        setErrors({ general: "An error occurred. Please try again." })
      }
    }
  }

  return (
    <div className="h-screen w-full fixed flex justify-center items-center">
      <div className="absolute inset-0 z-[-1]">
        <div
          className="absolute inset-0 bg-cover bg-center blur-[5px]"
          style={{ backgroundImage: "url('/assets/img/images2.jpg')" }}
        ></div>
        <div className="absolute inset-0 bg-black/30"></div>
      </div>

      <Card 
        className="w-[90%] max-w-[450px] z-10 shadow-xl overflow-hidden"
        style={{ 
          background: "linear-gradient(145deg, rgba(0, 42, 58, 0.95), rgba(0, 52, 68, 0.9))" 
        }}
      >
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-teal-400 to-blue-500"></div>
        
        <CardBody className="p-8 md:p-10 text-white">
          <div className="flex items-center justify-between mb-8">
            <Typography variant="h3" className="text-[2.2rem] font-semibold">
              Sign Up
            </Typography>
            <Link 
              to="/login" 
              className="text-teal-300 hover:text-teal-200 transition-colors text-sm flex items-center gap-1"
            >
              <span>Back to Login</span>
            </Link>
          </div>

          {/* Success Message */}
          {successMessage && (
            <div className="bg-green-500 text-white p-2 rounded-lg mb-4">
              <span>{successMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="mx-auto w-[85%] flex flex-col gap-5">
            {/* Username */}
            <div className="relative"> 
              <div className="absolute top-4 left-3 text-gray-400">
                <IdentificationIcon className="h-5 w-5" />
              </div>
              <input
                type="text"
                name="username"
                placeholder="Username"
                value={formData.username}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.username ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.username && (
                <p className="text-red-400 text-xs mt-1 ml-2">{errors.username}</p>
              )}
            </div>

            {/* Password */}
            <div className="relative">
              <div className="absolute top-4 left-3 text-gray-400">
                <KeyIcon className="h-5 w-5" />
              </div>
              <input
                type="password"
                name="password"
                placeholder="Password"
                value={formData.password}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.password ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.password && (
                <p className="text-red-400 text-xs mt-1 ml-2">{errors.password}</p>
              )}
            </div>

            <div className="mt-4 flex justify-center">
              <Button
                type="submit"
                disabled={isSubmitting}
                className={`bg-[#0a8ea8] hover:bg-[#0a7d94] text-white rounded-full py-3 px-8 w-[60%] flex items-center justify-center gap-2 transition-all duration-300 ${
                  isSubmitting ? 'opacity-70' : ''
                }`}
              >
                <span>{isSubmitting ? "SUBMITTING..." : "SUBMIT"}</span>
                {!isSubmitting && <ArrowRightIcon className="h-4 w-4 animate-pulse" />}
              </Button>
            </div>
          </form>

          <div className="mt-8 text-center">
            <Typography variant="small" className="text-gray-300">
              By signing up, you agree to our{" "}
              <a href="#" className="text-teal-300 hover:text-teal-200 transition-colors">
                Terms of Service
              </a>{" "}
              and{" "}
              <a href="#" className="text-teal-300 hover:text-teal-200 transition-colors">
                Privacy Policy
              </a>
            </Typography>
          </div>
        </CardBody>
      </Card>
    </div>
  )
}

export default RegisterPage