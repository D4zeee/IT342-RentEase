"use client"

import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { Card, CardBody, Typography, Button } from "@material-tailwind/react"
import { ArrowRightIcon, UserIcon, KeyIcon, CalendarIcon, IdentificationIcon } from "@heroicons/react/24/outline"

function RegisterPage() {
  const navigate = useNavigate()
  
  const [formData, setFormData] = useState({
    name: "",
    age: "",
    username: "",
    password: "",
  })
  
  const [errors, setErrors] = useState({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData({ 
      ...formData, 
      [name]: value 
    })
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ""
      })
    }
  }

  const validate = () => {
    const newErrors = {}
    
    if (!formData.name.trim()) newErrors.name = "Name is required"
    if (!formData.age) newErrors.age = "Age is required"
    else if (parseInt(formData.age) < 18) newErrors.age = "Must be 18 or older"
    if (!formData.username.trim()) newErrors.username = "Username is required"
    if (!formData.password) newErrors.password = "Password is required"
    else if (formData.password.length < 6) newErrors.password = "Password must be at least 6 characters"
    
    return newErrors
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    const validationErrors = validate()
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }
    
    setIsSubmitting(true)
    
    // Simulate API call
    setTimeout(() => {
      console.log("Registering user:", formData)
      setIsSubmitting(false)
      navigate("/login")
    }, 1500)
  }

  return (
    <div className="h-screen w-full fixed flex justify-center items-center">
      {/* Background with blur and overlay */}
      <div className="absolute inset-0 z-[-1]">
        <div
          className="absolute inset-0 bg-cover bg-center blur-[5px]"
          style={{ backgroundImage: "url('/assets/img/images2.jpg')" }}
        ></div>
        <div className="absolute inset-0 bg-black/30"></div>
      </div>

      {/* Registration Card */}
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

          <form onSubmit={handleSubmit} className="mx-auto w-[85%] flex flex-col gap-5">
            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <UserIcon className="h-5 w-5" />
              </div>
              <input
                type="text"
                name="name"
                placeholder="Full Name"
                value={formData.name}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.name ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.name && (
                <p className="text-red-400 text-xs mt-1 ml-2">{errors.name}</p>
              )}
            </div>

            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <CalendarIcon className="h-5 w-5" />
              </div>
              <input
                type="number"
                name="age"
                placeholder="Age"
                value={formData.age}
                onChange={handleChange}
                className={`w-full pl-10 pr-4 py-3 rounded-lg border ${
                  errors.age ? 'border-red-500' : 'border-transparent'
                } bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all`}
              />
              {errors.age && (
                <p className="text-red-400 text-xs mt-1 ml-2">{errors.age}</p>
              )}
            </div>

            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
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

            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
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

