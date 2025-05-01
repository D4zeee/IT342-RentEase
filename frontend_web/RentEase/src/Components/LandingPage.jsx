"use client"

import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { Typography, Button, Card, CardBody } from "@material-tailwind/react"

function LandingPage() {
  const navigate = useNavigate()
  const [mounted, setMounted] = useState(false)

  // Handle hydration issues with Material Tailwind
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
    <div className="fixed h-screen w-full flex justify-start items-center">
      {/* Background Container */}
      <div className="absolute h-full w-full z-[-1]">
        {/* Background image with blur */}
        <div className="absolute inset-0 h-full w-full">
          <img src="/assets/img/images2.jpg" alt="Background" className="h-full w-full object-cover blur-[5px]" />
        </div>
        {/* Dark overlay */}
        <div className="absolute inset-0 h-full w-full bg-black opacity-30"></div>
      </div>

      {/* Content Card */}
      <Card className="ml-[2%] w-4/5 md:w-1/2 z-10 bg-opacity-80" style={{ backgroundColor: "rgba(0,42,58,0.8)" }}>
        <CardBody className="p-6 md:p-8">
          <div className="text-white text-left">
            <Typography variant="h5" className="font-semibold mb-4">
              RentEase
            </Typography>

            <Typography variant="h1" className="text-[2.5rem] md:text-5xl font-bold mb-6 leading-[1.3]">
              Manage your Room
              <br />
              At ease.
            </Typography>

            <Typography className="text-[0.9rem] md:text-base mb-8 opacity-90 leading-[1.6]">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cursus imperdiet sed id elementum. Quam vel
              aliquam sit vulputate. Faucibus nec gravida ipsum pulvinar vel non.
            </Typography>

            <Button
              size="lg"
              className="bg-[#0a8ea8] rounded-[50px] py-3 px-8 text-base font-medium transition-colors hover:bg-[#0a7d94]"
              onClick={handleGetStarted}
            >
              Get Started
            </Button>
          </div>
        </CardBody>
      </Card>
    </div>
  )
}

export default LandingPage
