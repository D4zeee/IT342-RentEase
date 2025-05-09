"use client"

import { useState, useEffect } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import { Typography, Input } from "@material-tailwind/react"
import { Search, ChevronDown, X, UserCircle } from "lucide-react"
import axios from "axios"
import Cookies from "js-cookie"

function Header() {
  const location = useLocation()
  const navigate = useNavigate()
  const [searchQuery, setSearchQuery] = useState("")
  const [username, setUsername] = useState("")
  const [ownerId, setOwnerId] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [isProfileModalOpen, setProfileModalOpen] = useState(false)

  useEffect(() => {
    const token = Cookies.get("token")
    if (token) {
      axios
        .get("http://localhost:8080/owners/current-user", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((response) => {
          setUsername(response.data.username)
          setOwnerId(response.data.ownerId)
        })
        .catch((error) => {
          if (error.response?.status === 403 || error.response?.status === 401) {
            navigate("/login")
          }
        })
    } else {
      navigate("/login")
    }
  }, [navigate])

  const titleMap = {
    "/dashboard": "Dashboard",
    "/rooms": "Rooms",
    "/payments": "Payments",
    "/reminder": "Reminder",
    "/notifications": "Notification",
  }

  const title = titleMap[location.pathname] || "Dashboard"

  const handleSearch = (e) => {
    e.preventDefault()
    console.log("Searching for:", searchQuery)
  }

  const handleUpdateProfile = () => {
    const token = Cookies.get("token")
    axios
      .patch(
        "http://localhost:8080/owners/update-profile",
        { username, password: newPassword },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )
      .then(() => {
        alert("Profile updated successfully")
        setProfileModalOpen(false)
        setNewPassword("")
      })
      .catch((err) => {
        console.error(err)
        alert("Failed to update profile")
      })
  }

  return (
    <>
      <header className="relative flex items-center justify-between bg-white px-8 py-4 border-b border-gray-200 shadow-sm">
        <Typography variant="h4" className="font-bold text-gray-800">
          {title}
        </Typography>

        <div className="absolute left-1/2 -translate-x-1/2 w-full max-w-[350px]">
          <form onSubmit={handleSearch} className="relative">
            <Input
              type="text"
              placeholder="Search"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="!border !border-gray-200 bg-gray-50 text-gray-900 shadow-none focus:!border-gray-300 rounded-full pl-11 pr-4 py-2.5"
              labelProps={{ className: "hidden" }}
              containerProps={{ className: "min-w-0" }}
            />
            <div className="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
              <Search className="h-4 w-4 text-gray-500" />
            </div>
          </form>
        </div>

        <div className="flex items-center gap-6">
          <div
            className="flex items-center gap-3 cursor-pointer group"
            onClick={() => setProfileModalOpen(true)}
          >
            <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center border border-gray-300 shadow-sm">
              <UserCircle className="h-6 w-6 text-gray-600" />
            </div>
            <div className="flex flex-col">
              <Typography
                variant="small"
                className="font-semibold text-gray-800 group-hover:text-gray-900"
              >
                {username || "Guest"}
              </Typography>
              <Typography variant="small" className="text-xs text-gray-500">
                ID: {ownerId || "N/A"}
              </Typography>
            </div>
            <ChevronDown className="h-4 w-4 text-gray-500 transition-transform group-hover:text-gray-700 group-hover:rotate-180" />
          </div>
        </div>
      </header>

      {isProfileModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div
            className="bg-white rounded-2xl shadow-2xl w-full max-w-[450px] p-8"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold text-gray-800">Edit Profile</h3>
              <button
                onClick={() => setProfileModalOpen(false)}
                className="text-gray-500 hover:text-gray-700 transition-colors"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            {/* Default Profile Icon */}
            <div className="flex flex-col items-center mb-6">
              <div className="w-20 h-20 rounded-full bg-gray-200 flex items-center justify-center border border-gray-300 shadow-sm mb-2">
                <UserCircle className="h-10 w-10 text-gray-600" />
              </div>
              <Typography variant="small" className="text-gray-600">
                (Default Profile Icon)
              </Typography>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">New Username</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="mt-1 w-full border border-gray-300 px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-1">New Password</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="mt-1 w-full border border-gray-300 px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div className="flex justify-end gap-3">
              <button
                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-1"
                onClick={() => setProfileModalOpen(false)}
              >
                Cancel
              </button>
              <button
                className="px-4 py-2 bg-gradient-to-r from-[#5885AF] to-[#274472] text-white rounded-md hover:opacity-90 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1"
                onClick={handleUpdateProfile}
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default Header
