"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import {
  BellRing,
  Calendar,
  Home,
  CheckCircle,
  XCircle,
  Clock,
  CheckCircle2,
  CircleXIcon as XCircle2,
} from "lucide-react"
import { Typography, Chip, Button, Avatar } from "@material-tailwind/react"

function Notification() {
  const [ownerId, setOwnerId] = useState(null)
  const [reminders, setReminders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = Cookies.get("token")
    if (!token) {
      setLoading(false)
      return
    }

    axios
      .get("http://localhost:8080/owners/current-user", {
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
      .get(`http://localhost:8080/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const bookingReminders = res.data
          .filter(
            (r) =>
              (r.note?.startsWith("Booking pending approval") || r.note?.startsWith("Payment is due")) &&
              (r.approvalStatus === "pending" || r.approvalStatus === "approved" || r.approvalStatus === "denied"),
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
      .patch(
        `http://localhost:8080/payment_reminders/${reminderId}/approve`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      )
      .then(() => window.location.reload())
      .catch((err) => console.error("Error approving reminder:", err))
  }

  const handleDeny = (reminderId) => {
    const token = Cookies.get("token")
    axios
      .patch(
        `http://localhost:8080/payment_reminders/${reminderId}/deny`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      )
      .then(() => window.location.reload())
      .catch((err) => console.error("Error denying reminder:", err))
  }

  // Helper function to get status badge
  const getStatusBadge = (status) => {
    switch (status) {
      case "pending":
        return (
          <Chip
            size="sm"
            variant="ghost"
            value="Pending"
            color="amber"
            icon={<Clock className="h-3 w-3" />}
            className="rounded-full"
          />
        )
      case "approved":
        return (
          <Chip
            size="sm"
            variant="ghost"
            value="Approved"
            color="green"
            icon={<CheckCircle2 className="h-3 w-3" />}
            className="rounded-full"
          />
        )
      case "denied":
        return (
          <Chip
            size="sm"
            variant="ghost"
            value="Denied"
            color="red"
            icon={<XCircle2 className="h-3 w-3" />}
            className="rounded-full"
          />
        )
      default:
        return null
    }
  }

  return (
    <div className="min-h-[calc(100vh-60px)] bg-gradient-to-b from-cyan-50 to-white p-6">
      <div className="max-w-4xl mx-auto">

        {loading ? (
          <div className="flex flex-col items-center justify-center h-64 bg-white rounded-xl shadow-sm p-8">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-cyan-600 mb-4"></div>
            <Typography color="gray" className="font-medium">
              Loading applications...
            </Typography>
          </div>
        ) : reminders.length === 0 ? (
          <div className="text-center bg-white rounded-xl shadow-sm border border-gray-100 p-10 transition-all duration-300 hover:shadow-md">
            <div className="flex justify-center mb-6">
              <div className="p-6 bg-cyan-50 rounded-full">
                <BellRing size={64} className="text-cyan-600" />
              </div>
            </div>
            <Typography variant="h5" color="blue-gray" className="mb-3 font-semibold">
              No Applications
            </Typography>
            <Typography color="gray" className="max-w-xs mx-auto">
              You don't have any pending applications or payment notifications at the moment.
            </Typography>
          </div>
        ) : (
          <div className="space-y-4">
            {reminders.map((reminder) => (
              <div
                key={reminder.reminderId}
                className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden transition-all duration-300 hover:shadow-md"
              >
                <div className="p-5">
                  <div className="flex justify-between items-start">
                    <div className="flex items-start gap-4">
                      <Avatar variant="circular" size="md" className="bg-cyan-100 p-2" alt="Room">
                        <Home className="h-5 w-5 text-cyan-700" />
                      </Avatar>
                      <div>
                        <div className="flex items-center gap-3">
                          <Typography variant="h6" color="blue-gray" className="font-semibold">
                            Room #{reminder.room.roomId}
                          </Typography>
                          {getStatusBadge(reminder.approvalStatus)}
                        </div>
                        <Typography variant="small" color="gray" className="font-normal">
                          {reminder.room.unitName}
                        </Typography>
                      </div>
                    </div>
                    <div className="flex items-center gap-2 bg-blue-gray-50 px-3 py-1 rounded-full">
                      <Calendar className="text-blue-gray-500 w-4 h-4" />
                      <Typography className="text-blue-gray-700 text-sm font-medium">
                        {formatDate(reminder.dueDate)}
                      </Typography>
                    </div>
                  </div>

                  <div className="mt-4 p-4 bg-blue-gray-50 rounded-lg">
                    <Typography className="text-blue-gray-700">{reminder.note}</Typography>
                  </div>

                  {reminder.approvalStatus === "pending" && (
                    <div className="mt-4 flex gap-3 justify-end">
                      <Button
                        variant="outlined"
                        color="red"
                        size="sm"
                        className="flex items-center gap-2 rounded-lg"
                        onClick={() => handleDeny(reminder.reminderId)}
                      >
                        <XCircle className="h-4 w-4" />
                        Deny
                      </Button>
                      <Button
                        color="green"
                        size="sm"
                        className="flex items-center gap-2 rounded-lg"
                        onClick={() => handleApprove(reminder.reminderId)}
                      >
                        <CheckCircle className="h-4 w-4" />
                        Approve
                      </Button>
                    </div>
                  )}
                </div>

                {reminder.approvalStatus !== "pending" && (
                  <div
                    className={`px-5 py-3 border-t ${
                      reminder.approvalStatus === "approved"
                        ? "bg-green-50 border-green-100"
                        : "bg-red-50 border-red-100"
                    }`}
                  >
                    <Typography
                      className={`text-sm font-medium ${
                        reminder.approvalStatus === "approved" ? "text-green-700" : "text-red-700"
                      }`}
                    >
                      {reminder.approvalStatus === "approved"
                        ? "This application has been approved"
                        : "This application has been denied"}
                    </Typography>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Notification
