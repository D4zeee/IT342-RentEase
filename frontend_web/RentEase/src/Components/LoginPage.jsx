"use client"

import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Link } from "react-router-dom"
import { Card, CardBody, Typography } from "@material-tailwind/react"
import { UserIcon, KeyIcon } from "@heroicons/react/24/outline"

function LoginPage() {
  const navigate = useNavigate()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSignIn = () => {
    navigate("/dashboard")
  }

  const handleLogin = (e) => {
    e.preventDefault()
    
    setIsSubmitting(true)
    
    // Simulate API call
    setTimeout(() => {
      console.log("Logging in...", { username, password })
      setIsSubmitting(false)
      navigate("/dashboard")
    }, 1000)
  }

  return (
    <div className="h-screen w-full fixed flex justify-center items-center">
      {/* Background with blur and overlay */}
      <div className="fixed inset-0 z-[-1]">
        {/* Background image with blur */}
        <div
          className="absolute inset-0 bg-cover bg-center blur-[5px]"
          style={{ backgroundImage: "url('/assets/img/images2.jpg')" }}
        ></div>
        {/* Dark overlay */}
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
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <UserIcon className="h-5 w-5" />
              </div>
              <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full pl-10 pr-4 py-3 rounded-lg border border-transparent bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all"
              />
            </div>

            <div className="relative">
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                <KeyIcon className="h-5 w-5" />
              </div>
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full pl-10 pr-4 py-3 rounded-lg border border-transparent bg-[#747775]/90 text-white placeholder-gray-300 outline-none focus:ring-2 focus:ring-teal-500/50 transition-all"
              />
            </div>

            <div className="flex justify-between items-center text-sm px-1 mt-1">
              <div className="flex items-center">
                <input 
                  type="checkbox" 
                  id="remember" 
                  className="mr-2 h-4 w-4 rounded border-gray-300 text-teal-500 focus:ring-teal-500/50"
                />
                <label htmlFor="remember" className="text-gray-300 cursor-pointer">Remember me</label>
              </div>
              <a href="#" className="text-teal-300 hover:text-teal-200 transition-colors">Forgot password?</a>
            </div>

            <div className="flex justify-center mt-4">
              <button
                type="submit"
                disabled={isSubmitting}
                className={`bg-[#0a8ea8] text-white border-none rounded-[50px] py-3 px-8 w-[60%] font-medium cursor-pointer transition-all duration-300 hover:bg-[#0a7d94] hover:shadow-md ${
                  isSubmitting ? "opacity-70" : ""
                }`}
              >
                {isSubmitting ? "Logging in..." : "Login"}
              </button>
            </div>
          </form>

          {/* OR Divider */}
          <div className="flex items-center gap-2 my-6">
            <div className="h-px flex-1 bg-gray-600/50"></div>
            <Typography className="font-medium text-gray-300">OR</Typography>
            <div className="h-px flex-1 bg-gray-600/50"></div>
          </div>

          {/* Google Sign-In */}
          <button
            className="bg-white text-[#444] border-none rounded-[50px] py-3 px-6 text-base font-medium cursor-pointer inline-flex items-center justify-center gap-2 mx-auto w-[80%] transition-all hover:bg-gray-100 hover:shadow-md"
            onClick={handleSignIn}
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
