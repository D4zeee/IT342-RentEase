"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { UserIcon, KeyIcon } from "@heroicons/react/24/outline"
import axios from "axios"
import Cookies from "js-cookie"

function LoginPage() {
  const navigate = useNavigate()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errors, setErrors] = useState({})
  const [mounted, setMounted] = useState(false)

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  // Check if the user is already logged in
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
        [name]: "",
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
      const response = await axios.post(`${API_BASE_URL}/owners/login`, {
        username,
        password,
      })
      const { token } = response.data
      Cookies.set("token", token, { expires: 7 })

      // Add success animation before navigation
      document.querySelector(".login-card").classList.add("success-animation")

      setTimeout(() => {
        navigate("/dashboard")
      }, 800)
    } catch (error) {
      setIsSubmitting(false)
      console.error("Login failed:", error)
      setErrors({ general: "Invalid Credentials" })

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

      {/* Login Container */}
      <div
        className={`login-card w-[90%] max-w-[400px] z-10 shadow-2xl rounded-xl overflow-hidden 
          transition-all duration-700 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-10 opacity-0"}`}
      >
        {/* Animated gradient border */}
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-teal-400 via-blue-500 to-teal-400 background-animate"></div>
        <div className="absolute bottom-0 left-0 w-full h-1 bg-gradient-to-r from-blue-500 via-teal-400 to-blue-500 background-animate-reverse"></div>
        <div className="absolute left-0 top-0 w-1 h-full bg-gradient-to-b from-teal-400 via-blue-500 to-teal-400 background-animate-vertical"></div>
        <div className="absolute right-0 top-0 w-1 h-full bg-gradient-to-b from-blue-500 via-teal-400 to-blue-500 background-animate-vertical-reverse"></div>

        <div className="p-8 md:p-7 text-center text-white bg-gradient-to-br from-[rgba(0,42,58,0.95)] to-[rgba(0,52,68,0.9)]">
          {/* Header with staggered animation */}
          <h1
            className={`text-[2.2rem] font-semibold mb-5 leading-[1.3] transition-all duration-700 delay-100 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            Welcome to
            <br />
            <span className="bg-gradient-to-r from-teal-300 to-blue-300 text-transparent bg-clip-text">RentEase</span>
          </h1>

          <p
            className={`text-base opacity-80 mb-8 leading-[1.6] transition-all duration-700 delay-200 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            The best place to find millions of
            <br />
            homes, apartments, and office spaces.
          </p>

          {/* Login Form with staggered animation */}
          <form onSubmit={handleLogin} className="mx-auto mb-5 flex flex-col gap-4 w-[85%]">
            <div
              className={`relative transition-all duration-700 delay-300 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
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
                  errors.username ? "border-red-500" : "border-transparent"
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all hover:bg-[#747775]/95`}
              />
              {errors.username && (
                <p className="absolute left-3 text-red-400 text-xs mt-1 ml-2 animate-fade-in">{errors.username}</p>
              )}
            </div>
            <div
              className={`relative transition-all duration-700 delay-400 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
              <div className="absolute top-4 left-3 text-gray-400">
                <KeyIcon className="h-5 w-5" />
              </div>
              <input
                type="password"
                name="password"
                placeholder="Password"
                value={password}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.password ? "border-red-500" : "border-transparent"
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all hover:bg-[#747775]/95`}
              />
              {errors.password && (
                <p className="absolute left-3 text-red-400 text-xs mt-1 ml-2 animate-fade-in">{errors.password}</p>
              )}
            </div>

            {/* General Error Message */}
            {errors.general && <p className="text-red-400 text-xs animate-fade-in">{errors.general}</p>}

            {/* Login Button */}
            <div
              className={`flex justify-center transition-all duration-700 delay-500 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
            >
              <button
                type="submit"
                disabled={isSubmitting}
                className={`relative bg-gradient-to-r from-[#0a8ea8] to-[#0a7d94] text-white border-none rounded-[10px] py-3 px-8 w-[100%] 
                  font-medium cursor-pointer transition-all duration-300 
                  hover:shadow-lg hover:shadow-teal-500/20 hover:translate-y-[-1px] active:translate-y-[1px] mt-2 overflow-hidden ${
                    isSubmitting ? "opacity-70" : ""
                  }`}
              >
                <span className="relative z-10">{isSubmitting ? "Logging in..." : "L o g i n"}</span>
                <span className="absolute inset-0 bg-gradient-to-r from-teal-400 to-blue-500 opacity-0 hover:opacity-20 transition-opacity duration-300"></span>
              </button>
            </div>
          </form>

          {/* OR Divider */}
          <div
            className={`flex items-center gap-2 my-6 transition-all duration-700 delay-600 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            <div className="h-px flex-1 bg-gray-600/50"></div>
            <p className="font-medium text-gray-300">OR</p>
            <div className="h-px flex-1 bg-gray-600/50"></div>
          </div>

          {/* Google Sign-In Button */}
          <div
            className={`transition-all duration-700 delay-700 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            <button
              className="bg-white text-[#444] border-none 
              rounded-[50px] py-3 px-6 text-base font-medium cursor-pointer 
              inline-flex items-center justify-center gap-2 
              mx-auto w-[85%] transition-all hover:bg-gray-100 hover:shadow-lg hover:translate-y-[-1px] active:translate-y-[1px]"
              onClick={() => navigate("/dashboard")} // Dummy sign-in function for Google
            >
              <img src="/assets/img/google.png" alt="Google" className="w-5 h-5" />
              Sign in with Google
            </button>
          </div>

          {/* Register Link */}
          <p
            className={`mt-8 text-base text-gray-300 transition-all duration-700 delay-800 transform ${mounted ? "translate-y-0 opacity-100" : "translate-y-5 opacity-0"}`}
          >
            Don't have an account?{" "}
            <Link to="/register" className="text-teal-300 hover:text-teal-200 transition-colors hover:underline">
              Register
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
