"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { BellRing, Calendar, Home, CheckCircle, XCircle } from "lucide-react"
import { Typography } from "@material-tailwind/react"

function Notification() {
  const [ownerId, setOwnerId] = useState(null)
  const [reminders, setReminders] = useState([])
  const [loading, setLoading] = useState(true)

  // Fallback for API base URL
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

  useEffect(() => {
    const token = Cookies.get("token")
    if (!token) {
      setLoading(false)
      return
    }

    axios
      .get(`${API_BASE_URL}/owners/current-user`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setOwnerId(res.data.ownerId)
      })
      .catch((err) => {
        console.error("Error fetching current owner:", err)
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    if (!ownerId) return
    const token = Cookies.get("token")

    axios
      .get(`${API_BASE_URL}/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const bookingReminders = res.data
          .filter((r) =>
            (r.note?.startsWith("Booking pending approval") || r.note?.startsWith("Payment is due")) &&
            (r.approvalStatus === "pending" || r.approvalStatus === "approved" || r.approvalStatus === "denied")
          )
          .sort((a, b) => b.reminderId - a.reminderId)
        setReminders(bookingReminders)
      })
      .catch((err) => console.error("Error fetching automatic reminders:", err))
      .finally(() => setLoading(false))
  }, [ownerId])

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  const handleApprove = (reminderId) => {
    const token = Cookies.get("token")
    axios
      .patch(`${API_BASE_URL}/payment_reminders/${reminderId}/approve`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => window.location.reload())
      .catch((err) => console.error("Error approving reminder:", err))
  }

  const handleDeny = (reminderId) => {
    const token = Cookies.get("token")
    axios
      .patch(`${API_BASE_URL}/payment_reminders/${reminderId}/deny`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => window.location.reload())
      .catch((err) => console.error("Error denying reminder:", err))
  }

  return (
    <div className="p-4">
      {loading ? (
        <div className="flex justify-center items-center h-40">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-cyan-600"></div>
        </div>
      ) : (
        <>
          <Typography variant="h5" className="mb-4 text-cyan-700 font-bold">
            Application
          </Typography>
          {reminders.length === 0 ? (
            <div className="text-gray-500 text-sm">No system reminders at the moment.</div>
          ) : (
            <div className="space-y-3">
              {reminders.map((reminder) => (
                <div
                  key={reminder.reminderId}
                  className="p-4 bg-white rounded-xl shadow-sm border border-gray-200"
                >
                  <div className="flex justify-between items-center">
                    <div className="flex items-center gap-2">
                      <BellRing className="text-cyan-600 w-5 h-5" />
                      <Typography className="font-semibold text-gray-800">
                        Room #{reminder.room.roomId} - {reminder.room.unitName}
                      </Typography>
                    </div>
                    <div className="flex items-center gap-2">
                      <Calendar className="text-amber-600 w-4 h-4" />
                      <Typography className="text-gray-600 text-sm">
                        {formatDate(reminder.dueDate)}
                      </Typography>
                    </div>
                  </div>

                  <Typography className="text-gray-600 mt-2 text-sm">
                    {reminder.note}
                  </Typography>

                  {reminder.approvalStatus === "pending" && (
                    <div className="flex gap-2 mt-3">
                      <button
                        className="bg-green-500 hover:bg-green-600 text-white px-4 py-1 rounded-md text-sm flex items-center gap-1"
                        onClick={() => handleApprove(reminder.reminderId)}
                      >
                        <CheckCircle size={16} /> Approve
                      </button>
                      <button
                        className="bg-red-500 hover:bg-red-600 text-white px-4 py-1 rounded-md text-sm flex items-center gap-1"
                        onClick={() => handleDeny(reminder.reminderId)}
                      >
                        <XCircle size={16} /> Deny
                      </button>
                    </div>
                  )}
                  {reminder.approvalStatus !== "pending" && (
                    <p className="mt-3 text-sm text-gray-500">Status: {reminder.approvalStatus}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default Notification