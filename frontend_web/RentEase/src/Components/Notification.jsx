// Notification.jsx
"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { BellRing, Calendar, Home } from "lucide-react"
import { Typography } from "@material-tailwind/react"

function Notification() {
  const [ownerId, setOwnerId] = useState(null)
  const [reminders, setReminders] = useState([])

  useEffect(() => {
    const token = Cookies.get("token")
    if (!token) return

    axios
      .get("http://localhost:8080/owners/current-user", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setOwnerId(res.data.ownerId)
      })
      .catch((err) => console.error("Error fetching current owner:", err))
  }, [])

  //status for the notification
  const updateStatus = (id, status) => {
    const token = Cookies.get("token")
  
    axios
      .patch(
        `http://localhost:8080/payment_reminders/${id}/status?status=${status}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`, // ✅ Include token
          },
        }
      )
      .then(() => {
        // Optional UI update
        setReminders((prev) =>
          prev.map((r) =>
            r.reminderId === id ? { ...r, status } : r
          )
        )
      })
      .catch((err) => {
        console.error("Failed to update status:", err)
        alert("Update failed. You might not be logged in.")
      })
  }
  

  useEffect(() => {
    if (!ownerId) return
    const token = Cookies.get("token")

    axios
      .get(`http://localhost:8080/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const autoReminders = res.data.filter((r) => r.note?.includes("Payment is due"))
        setReminders(autoReminders)
      })
      .catch((err) => console.error("Error fetching automatic reminders:", err))
  }, [ownerId])

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  return (
    <div className="p-4">
      <Typography variant="h5" className="mb-4 text-cyan-700 font-bold">
        Notifications
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

              <button
                className="text-green-600 hover:underline text-sm"
                onClick={() => updateStatus(reminder.reminderId, "approved")}
                >
                Approve
                </button>
                <button
                className="text-red-600 hover:underline text-sm ml-2"
                onClick={() => updateStatus(reminder.reminderId, "denied")}
                >
                Deny
                </button>

            </div>
            
          ))}
        </div>
      )}
    </div>
  )
}

export default Notification
