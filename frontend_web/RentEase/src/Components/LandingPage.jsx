"use client"

import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"

function LandingPage() {
  const navigate = useNavigate()
  const [mounted, setMounted] = useState(false)

  // Handle hydration issues
  useEffect(() => {
    setMounted(true)
  }, [])

  const handleGetStarted = () => {
    navigate("/login")
  }

  if (!mounted) {
    return null
  }

  return (
    <div className="fixed h-screen w-full flex items-center overflow-hidden">
      {/* Background with improved overlay */}
      <div className="absolute inset-0 z-0">
        <img
          src="/assets/img/images2.jpg"
          alt="Background"
          className="h-full w-full object-cover blur-[3px] scale-105 transform transition-all duration-700"
        />
        <div className="absolute inset-0 bg-gradient-to-r from-black/60 to-black/40"></div>
      </div>

      {/* Content Container */}
      <div className="relative z-10 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="ml-0 md:ml-[5%] w-full md:w-1/2 backdrop-blur-sm bg-[#002a3a]/80 rounded-xl shadow-2xl border border-white/10 p-8 md:p-10 transform transition-all duration-500">
          <div className="text-white">
            <h5 className="font-semibold text-lg text-cyan-300 mb-3">RentEase</h5>

            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-6 leading-tight">
              Manage your Room
              <br />
              <span className="text-cyan-300">At ease.</span>
            </h1>

            <p className="text-base md:text-lg mb-8 text-gray-200 leading-relaxed max-w-xl">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cursus imperdiet sed id elementum. Quam vel
              aliquam sit vulputate. Faucibus nec gravida ipsum pulvinar vel non.
            </p>

            <div className="flex flex-col sm:flex-row gap-4">
              <button
                onClick={handleGetStarted}
                className="bg-gradient-to-r from-cyan-500 to-teal-400 text-white rounded-full py-4 px-8 text-base font-medium transition-all duration-300 hover:shadow-lg hover:shadow-cyan-500/30 hover:translate-y-[-2px] focus:outline-none focus:ring-2 focus:ring-cyan-400 focus:ring-offset-2 focus:ring-offset-[#002a3a]"
              >
                Get Started
              </button>

              <button className="bg-transparent border border-white/30 text-white rounded-full py-4 px-8 text-base font-medium transition-all duration-300 hover:bg-white/10 focus:outline-none">
                Learn More
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Decorative elements */}
      <div className="absolute bottom-0 right-0 w-1/3 h-1/3 bg-gradient-radial from-cyan-500/20 to-transparent rounded-full blur-3xl"></div>
      <div className="absolute top-20 right-20 w-24 h-24 bg-cyan-500/10 rounded-full blur-xl"></div>
    </div>
  )
}

export default LandingPage
