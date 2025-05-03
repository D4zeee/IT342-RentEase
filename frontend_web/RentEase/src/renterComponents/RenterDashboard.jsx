"use client"

import { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import axios from "axios"
import Cookies from "js-cookie"
import { jwtDecode } from "jwt-decode"

function RenterDashboard() {
  const location = useLocation()
  const navigate = useNavigate()
  const [rooms, setRooms] = useState([])
  const [renterId, setRenterId] = useState(null)
  const [renterName, setRenterName] = useState("")
  const [isLoading, setIsLoading] = useState(true)
  const [isRoomsLoading, setIsRoomsLoading] = useState(true)

  // Modal states
  const [showModal, setShowModal] = useState(false)
  const [selectedRoomId, setSelectedRoomId] = useState(null)
  const [startDate, setStartDate] = useState("")
  const [endDate, setEndDate] = useState("")

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const token = Cookies.get("renterToken")
    if (!token) {
      navigate("/renter-login")
      return
    }

    try {
      const decodedToken = jwtDecode(token)
      setRenterId(decodedToken.renterId)
      setRenterName(decodedToken.renterName)
    } catch (error) {
      console.error("Error decoding token:", error)
      navigate("/renter-login")
      return
    } finally {
      setIsLoading(false)
    }

    const fetchRooms = async () => {
      try {
        const response = await axios.get(`${API_BASE_URL}/rooms`, {
          headers: { Authorization: `Bearer ${Cookies.get("renterToken")}` },
        })
        setRooms(response.data)
      } catch (error) {
        console.error("Error fetching rooms:", error.response?.data || error.message)
        navigate("/renter-login")
      } finally {
        setIsRoomsLoading(false)
      }
    }

    if (location.state?.rooms) {
      setRooms(location.state.rooms)
      setIsRoomsLoading(false)
    } else {
      fetchRooms()
    }
  }, [location, navigate])

  const handleConfirmBooking = async () => {
    const token = Cookies.get("renterToken")

    if (!startDate || !endDate || !selectedRoomId) {
      alert("Please select start and end dates.")
      return
    }

    const rentedUnitData = {
      renter: { renterId: parseInt(renterId) },
      room: { roomId: parseInt(selectedRoomId) },
      startDate,
      endDate,
    }

    try {
      await axios.post(`${API_BASE_URL}/rented_units`, rentedUnitData, {
        headers: { Authorization: `Bearer ${token}` },
      })

      alert("Room booking submitted successfully! Please wait for approval.")
      setShowModal(false)
      setStartDate("")
      setEndDate("")
      setSelectedRoomId(null)

      // Refresh room list to reflect updated status
      const updatedRooms = await axios.get(`${API_BASE_URL}/rooms`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      setRooms(updatedRooms.data)
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.response?.data || error.message
      console.error("Error renting room:", errorMessage)
      alert("Failed to rent room: " + errorMessage)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800">Available Rooms</h1>
        <h2 className="text-lg text-gray-600">
          {isLoading ? "Loading..." : `Renter ID: ${renterId || "Not Available"}`}
        </h2>
        <div className="flex gap-2">
          <button
            onClick={() => navigate("/renter-notif")}
            className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded shadow"
          >
            Notifications
          </button>
          <button
            onClick={() => navigate("/renter-reminder")}
            className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded shadow"
          >
            Reminders
          </button>
          <button
            onClick={() => {
              Cookies.remove("renterToken")
              navigate("/renter-login")
            }}
            className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded shadow"
          >
            Logout
          </button>
        </div>
      </div>

      {isRoomsLoading ? (
        <p className="text-center text-gray-500">Loading rooms...</p>
      ) : rooms.length === 0 ? (
        <p className="text-center text-gray-500">No rooms available at the moment.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {rooms.map((room) => (
            <div key={room.roomId} className="bg-white p-6 rounded-lg shadow-md">
              <h2 className="text-xl font-semibold text-gray-800 mb-2">{room.unitName}</h2>
              <p className="text-gray-600 mb-1">
                <strong>Rental Fee:</strong> ${room.rentalFee}/month
              </p>
              <p className="text-gray-600 mb-1">
                <strong>Location:</strong> {room.city}, {room.addressLine1}
              </p>
              <p className="text-gray-600 mb-1">
                <strong>Status:</strong> {room.status}
              </p>
              <p className="text-gray-500 text-sm mt-2">{room.description}</p>

              {room.status === "available" && (
                <button
                  onClick={() => {
                    setSelectedRoomId(room.roomId)
                    setShowModal(true)
                  }}
                  className="mt-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                >
                  Rent this Room
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-700">Select Dates</h2>

            <div className="mb-4">
              <label className="block text-gray-600 text-sm font-bold mb-2">Start Date</label>
              <input
                type="date"
                className="border border-gray-300 rounded px-3 py-2 w-full"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </div>

            <div className="mb-4">
              <label className="block text-gray-600 text-sm font-bold mb-2">End Date</label>
              <input
                type="date"
                className="border border-gray-300 rounded px-3 py-2 w-full"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </div>

            <div className="flex justify-end gap-2">
              <button
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded"
                onClick={() => setShowModal(false)}
              >
                Cancel
              </button>
              <button
                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
                onClick={handleConfirmBooking}
              >
                Confirm Booking
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default RenterDashboard