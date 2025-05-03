"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { ArrowRightIcon, KeyIcon, IdentificationIcon } from "@heroicons/react/24/outline"
import axios from "axios"
import Cookies from "js-cookie"

function RegisterPage() {
  const navigate = useNavigate()
  const [mounted, setMounted] = useState(false)

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

    // Animation setup
    setMounted(true)

    // Add particle animation
    const createParticle = () => {
      const particles = document.querySelector(".particles")
      if (!particles) return

      const particle = document.createElement("div")
      particle.className = "particle"

      // Random position
      const x = Math.random() * window.innerWidth
      const y = Math.random() * window.innerHeight

      // Random size
      const size = Math.random() * 5 + 1

      // Apply styles
      particle.style.left = `${x}px`
      particle.style.top = `${y}px`
      particle.style.width = `${size}px`
      particle.style.height = `${size}px`
      particle.style.opacity = Math.random() * 0.5 + 0.2

      particles.appendChild(particle)

      // Remove after animation completes
      setTimeout(() => {
        if (particle.parentNode === particles) {
          particles.removeChild(particle)
        }
      }, 6000)
    }

    // Create particles at intervals
    const particleInterval = setInterval(createParticle, 300)

    return () => clearInterval(particleInterval)
  }, [navigate])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData({
      ...formData,
      [name]: value,
    })

    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: "",
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

      // Add error shake animation
      const form = document.querySelector("form")
      form.classList.add("error-shake")
      setTimeout(() => form.classList.remove("error-shake"), 500)

      return
    }
    setIsSubmitting(true)

    try {
      const response = await axios.post(`${API_BASE_URL}/owners/register`, formData)
      console.log(response.data)

      setSuccessMessage("Registration successful! Redirecting to login...")
      setIsSubmitting(false)

      // Add success animation before navigation
      document.querySelector(".register-card").classList.add("success-animation")

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

      // Add error shake animation
      const form = document.querySelector("form")
      form.classList.add("error-shake")
      setTimeout(() => form.classList.remove("error-shake"), 500)
    }
  }

  return (
    <div className="h-screen w-full fixed flex justify-center items-center overflow-hidden">
      {/* Animated background */}
      <div className="fixed inset-0 z-[-1]">
        <div
          className="absolute inset-0 bg-cover bg-center blur-[5px] scale-105 transform transition-all duration-10000 animate-slow-zoom"
          style={{ backgroundImage: "url('/assets/img/images2.jpg')" }}
        ></div>
        <div className="absolute inset-0 bg-gradient-to-br from-black/40 to-black/60"></div>

        {/* Particles container */}
        <div className="particles absolute inset-0 overflow-hidden"></div>
      </div>

      {/* Animated glow orbs */}
      <div className="absolute top-1/4 left-1/4 w-64 h-64 bg-teal-500/10 rounded-full blur-3xl animate-float-slow"></div>
      <div className="absolute bottom-1/4 right-1/3 w-80 h-80 bg-blue-500/10 rounded-full blur-3xl animate-float-slow-reverse"></div>

      {/* Register Container */}
      <div
        className={`register-card w-[90%] max-w-[450px] z-10 shadow-2xl rounded-xl overflow-hidden 
          transition-all duration-700 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-10 opacity-0"}`}
      >
        {/* Animated gradient border */}
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-teal-400 via-blue-500 to-teal-400 background-animate"></div>
        <div className="absolute bottom-0 left-0 w-full h-1 bg-gradient-to-r from-blue-500 via-teal-400 to-blue-500 background-animate-reverse"></div>
        <div className="absolute left-0 top-0 w-1 h-full bg-gradient-to-b from-teal-400 via-blue-500 to-teal-400 background-animate-vertical"></div>
        <div className="absolute right-0 top-0 w-1 h-full bg-gradient-to-b from-blue-500 via-teal-400 to-blue-500 background-animate-vertical-reverse"></div>

        <div className="p-8 md:p-10 text-white bg-gradient-to-br from-[rgba(0,42,58,0.95)] to-[rgba(0,52,68,0.9)]">
          <div
            className={`flex items-center justify-between mb-8 transition-all duration-700 delay-100 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            <h3 className="text-[2.2rem] font-semibold">
              <span className="bg-gradient-to-r from-teal-300 to-blue-300 text-transparent bg-clip-text">Sign Up</span>
            </h3>
            <Link
              to="/login"
              className="text-teal-300 hover:text-teal-200 transition-colors text-sm flex items-center gap-1 hover:translate-x-[-2px] transform duration-300"
            >
              <span>Back to Login</span>
            </Link>
          </div>

          {/* Success Message */}
          {successMessage && (
            <div className="bg-green-500/80 backdrop-blur-sm text-white p-3 rounded-lg mb-4 animate-fade-in">
              <span>{successMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="mx-auto w-[85%] flex flex-col gap-5">
            {/* Username */}
            <div
              className={`relative transition-all duration-700 delay-200 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
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
                  errors.username ? "border-red-500" : "border-transparent"
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all hover:bg-[#747775]/95`}
              />
              {errors.username && <p className="text-red-400 text-xs mt-1 ml-2 animate-fade-in">{errors.username}</p>}
            </div>

            {/* Password */}
            <div
              className={`relative transition-all duration-700 delay-300 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
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
                  errors.password ? "border-red-500" : "border-transparent"
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all hover:bg-[#747775]/95`}
              />
              {errors.password && <p className="text-red-400 text-xs mt-1 ml-2 animate-fade-in">{errors.password}</p>}
            </div>

            {/* General Error Message */}
            {errors.general && <p className="text-red-400 text-xs animate-fade-in">{errors.general}</p>}

            <div
              className={`mt-4 flex justify-center transition-all duration-700 delay-400 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
              <button
                type="submit"
                disabled={isSubmitting}
                className={`relative bg-gradient-to-r from-[#0a8ea8] to-[#0a7d94] text-white rounded-full py-3 px-8 w-[60%] 
                  flex items-center justify-center gap-2 transition-all duration-300
                  hover:shadow-lg hover:shadow-teal-500/20 hover:translate-y-[-1px] active:translate-y-[1px] ${
                    isSubmitting ? "opacity-70" : ""
                  }`}
              >
                <span className="relative z-10">{isSubmitting ? "SUBMITTING..." : "SUBMIT"}</span>
                {!isSubmitting && <ArrowRightIcon className="h-4 w-4 animate-pulse" />}
                <span className="absolute inset-0 bg-gradient-to-r from-teal-400 to-blue-500 opacity-0 hover:opacity-20 transition-opacity duration-300 rounded-full"></span>
              </button>
            </div>
          </form>

          <div
            className={`mt-8 text-center transition-all duration-700 delay-500 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            <p className="text-gray-300 text-sm">
              By signing up, you agree to our{" "}
              <a href="#" className="text-teal-300 hover:text-teal-200 transition-colors hover:underline">
                Terms of Service
              </a>{" "}
              and{" "}
              <a href="#" className="text-teal-300 hover:text-teal-200 transition-colors hover:underline">
                Privacy Policy
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage
