"use client"

import { useState } from "react"
import { useLocation } from "react-router-dom"
import { Typography, Avatar, Input } from "@material-tailwind/react"
import { Search, Bell, ChevronDown } from "lucide-react"

function Header() {
  const location = useLocation()
  const [searchQuery, setSearchQuery] = useState("")

  // Map pathname to page title
  const titleMap = {
    "/dashboard": "Dashboard",
    "/rooms": "Rooms",
    "/payments": "Payments",
    "/reminder": "Reminder",
  }

  // Fallback title if not found in the map
  const title = titleMap[location.pathname] || "Dashboard"

  const handleSearch = (e) => {
    e.preventDefault()
    console.log("Searching for:", searchQuery)
    // Implement search functionality here
  }

  return (
    <header className="relative flex items-center justify-between bg-white px-8 py-4 border-b border-gray-200 shadow-sm">
      {/* Page Title */}
      <Typography variant="h4" className="font-bold text-gray-800">
        {title}
      </Typography>

      {/* Search Bar */}
      <div className="absolute left-1/2 -translate-x-1/2 w-full max-w-[350px]">
        <form onSubmit={handleSearch} className="relative">
          <Input
            type="text            "         
            placeholder="Search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="!border !border-gray-200 bg-gray-50 text-gray-900 shadow-none focus:!border-gray-300 rounded-full pl-11 pr-4 py-2.5"
            labelProps={{
              className: "hidden",
            }}
            containerProps={{
              className: "min-w-0",
            }}
          />
          <div className="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
            <Search className="h-4 w-4 text-gray-500" />
          </div>
        </form>
      </div>

      {/* Right Section: Profile */}
      <div className="flex items-center gap-6">

        {/* Profile Section */}
        <div className="flex items-center gap-3 cursor-pointer group">
        <Avatar
        src="/assets/img/profile.jpg"
        alt="Administrator"
        className="w-10 h-10 border border-gray-200 shadow-sm"
/>

          <div className="flex flex-col">
            <Typography variant="small" className="font-semibold text-gray-800 group-hover:text-gray-900">
              Administrator
            </Typography>
            <Typography variant="small" className="text-xs text-gray-500">
              ID: 1234567
            </Typography>
          </div>
          <ChevronDown className="h-4 w-4 text-gray-500 transition-transform group-hover:text-gray-700 group-hover:rotate-180" />
        </div>
      </div>
    </header>
  )
}

export default Header
