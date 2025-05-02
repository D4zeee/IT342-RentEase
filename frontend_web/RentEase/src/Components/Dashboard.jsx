"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import {
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  HomeIcon,
  CurrencyDollarIcon,
  CalendarDaysIcon,
} from "@heroicons/react/24/outline"

const Dashboard = () => {
  const [stats, setStats] = useState({ total: 0, available: 0, rented: 0, revenue: 0 })
  const [isLoading, setIsLoading] = useState(true)

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setIsLoading(true)
        const token = Cookies.get("token")
        const ownerResponse = await axios.get(`${API_BASE_URL}/owners/current-user`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        const ownerId = ownerResponse.data.ownerId

        const statsResponse = await axios.get(`${API_BASE_URL}/rooms/owner/${ownerId}/room-stats`, {
          headers: { Authorization: `Bearer ${token}` },
        })

        setStats(statsResponse.data)
      } catch (error) {
        console.error("Failed to fetch dashboard stats", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchStats()
  }, [])

  // Data for UI elements
  const mockData = {
    newBookings: stats.total,
    revenue: stats.revenue, // Use revenue from API
    occupiedRooms: stats.rented,
    availableRooms: stats.available,
  }

  // Placeholder for monthly occupancy data
  const monthlyOccupancy = [75, 82, 65, 90, 85, 88, 92, 78, 83, 70, 88, 95]
  const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]

  return (
    <div className="bg-gray-50 min-h-screen p-6">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Welcome to RentEase</h1>
          <p className="text-gray-600">Here's a summary of your property management dashboard</p>
        </div>

        {/* Overview Cards */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Overview</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* New Bookings Card */}
            <div className="bg-gradient-to-br from-blue-50 to-blue-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Number Of Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{mockData.newBookings}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <CalendarDaysIcon className="h-6 w-6 text-blue-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+20%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Available Rooms Card */}
            <div className="bg-gradient-to-br from-green-50 to-green-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Available Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{stats.available}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <HomeIcon className="h-6 w-6 text-green-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+5.2%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Rented Rooms Card */}
            <div className="bg-gradient-to-br from-orange-50 to-orange-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Rented Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{stats.rented}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <HomeIcon className="h-6 w-6 text-orange-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingDownIcon className="h-4 w-4 text-red-500 mr-1" />
                <span className="text-red-500 font-medium">-3.1%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Total Revenue Card */}
            <div className="bg-gradient-to-br from-teal-50 to-teal-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Total Revenue</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">
                    ₱{mockData.revenue.toLocaleString()}
                  </p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <CurrencyDollarIcon className="h-6 w-6 text-teal-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+20%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>
          </div>
        </div>

        {/* Occupancy Statistics */}
        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-700">Occupancy Statistics</h2>
            <div className="bg-gray-100 rounded-lg px-3 py-1 text-sm text-gray-600 flex items-center">
              <CalendarDaysIcon className="h-4 w-4 mr-1" />
              Monthly
            </div>
          </div>
          <div className="h-64">
            <div className="flex h-full items-end">
              {monthlyOccupancy.map((value, index) => (
                <div key={index} className="flex-1 flex flex-col items-center">
                  <div className="w-full bg-blue-500 rounded-t-sm mx-1" style={{ height: `${value}%` }}></div>
                  <div className="text-xs text-gray-500 mt-2">{months[index]}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard