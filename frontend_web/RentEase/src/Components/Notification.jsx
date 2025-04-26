"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { BellRing, Calendar, Home, CheckCircle, XCircle } from "lucide-react"
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


  // para sa filtering sa application
  useEffect(() => {
    if (!ownerId) return;
    const token = Cookies.get("token");

    axios
      .get(`http://localhost:8080/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const bookingReminders = res.data
          .filter((r) => 
            (r.note?.startsWith("Booking pending approval") || r.note?.startsWith("Payment is due")) &&
            (r.approvalStatus === "pending" || r.approvalStatus === "approved" ||r.approvalStatus === "denied"  )
          )
          .sort((a, b) => b.reminderId - a.reminderId); // latest on top
        
        setReminders(bookingReminders);
      })
      .catch((err) => console.error("Error fetching automatic reminders:", err));
}, [ownerId]);


  
  
  

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  // 🚀 Approve booking
  const handleApprove = (reminderId) => {
    const token = Cookies.get("token")
    axios
      .patch(`http://localhost:8080/payment_reminders/${reminderId}/approve`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => window.location.reload()) // reload to update list
      .catch((err) => console.error("Error approving reminder:", err))
  }

  // 🚀 Deny booking
  const handleDeny = (reminderId) => {
    const token = Cookies.get("token")
    axios
      .patch(`http://localhost:8080/payment_reminders/${reminderId}/deny`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => window.location.reload()) // reload to update list
      .catch((err) => console.error("Error denying reminder:", err))
  }

  return (
    <div className="p-4">
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

              {/* 🚀 Status and Buttons */}
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
    </div>
  )
}

export default Notification
