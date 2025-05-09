"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { BellRing, Calendar, CheckCircle, XCircle, Clock, AlertCircle, CheckCheck, X } from "lucide-react"
import { Card, CardBody, CardFooter, CardHeader, Typography, Button, Chip, Spinner } from "@material-tailwind/react"

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

  const getStatusChip = (status) => {
    switch (status) {
      case "pending":
        return (
          <Chip
            value="Pending"
            variant="outlined"
            color="amber"
            icon={<Clock className="h-3 w-3" />}
            className="flex items-center gap-1 text-amber-700 bg-amber-50"
          />
        )
      case "approved":
        return (
          <Chip
            value="Approved"
            variant="outlined"
            color="green"
            icon={<CheckCheck className="h-3 w-3" />}
            className="flex items-center gap-1 text-green-700 bg-green-50"
          />
        )
      case "denied":
        return (
          <Chip
            value="Denied"
            variant="outlined"
            color="red"
            icon={<X className="h-3 w-3" />}
            className="flex items-center gap-1 text-red-700 bg-red-50"
          />
        )
      default:
        return null
    }
  }

  const getBorderColor = (status) => {
    switch (status) {
      case "pending":
        return "border-l-amber-400"
      case "approved":
        return "border-l-green-400"
      case "denied":
        return "border-l-red-400"
      default:
        return ""
    }
  }

  if (loading) {
    return (
      <div className="p-6 max-w-4xl mx-auto">
        <Typography variant="h4" className="font-semibold text-gray-800 mb-6">
          Booking Requests
        </Typography>
        <div className="flex justify-center items-center h-64">
          <Spinner className="h-12 w-12 text-cyan-600" />
        </div>
      </div>
    )
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <Typography variant="h4" className="font-semibold text-gray-800 mb-6">
        Booking Requests
      </Typography>

      {reminders.length === 0 ? (
        <Card className="bg-gray-50 border border-dashed border-gray-200">
          <CardBody className="flex flex-col items-center justify-center py-12">
            <div className="rounded-full bg-gray-100 p-3 mb-4">
              <BellRing className="h-8 w-8 text-gray-400" />
            </div>
            <Typography variant="h5" className="text-gray-700 mb-1">
              No booking requests
            </Typography>
            <Typography variant="paragraph" className="text-gray-500 text-center max-w-md">
              You don't have any pending booking requests at the moment. New requests will appear here when guests book
              your property.
            </Typography>
          </CardBody>
        </Card>
      ) : (
        <div className="space-y-4">
          {reminders.map((reminder) => (
            <Card
              key={reminder.reminderId}
              className={`overflow-hidden transition-all duration-200 hover:shadow-md border-l-4 ${getBorderColor(
                reminder.approvalStatus,
              )}`}
            >
              <CardHeader floated={false} shadow={false} color="transparent" className="pb-2 pt-4 px-6">
                <div className="flex justify-between items-start">
                  <div>
                    <Typography variant="h6" className="text-lg font-medium">
                      Room #{reminder.room.roomId} - {reminder.room.unitName}
                    </Typography>
                    <Typography variant="small" className="flex items-center gap-1 mt-1 text-gray-600">
                      <Calendar className="h-3.5 w-3.5 text-gray-500" />
                      <span>Due by {formatDate(reminder.dueDate)}</span>
                    </Typography>
                  </div>
                  {getStatusChip(reminder.approvalStatus)}
                </div>
              </CardHeader>

              <CardBody className="py-2 px-6">
                <Typography className="text-gray-700">{reminder.note}</Typography>
              </CardBody>

              {reminder.approvalStatus === "pending" && (
                <CardFooter className="flex gap-2 pt-2 pb-4 px-6">
                  <Button
                    onClick={() => handleApprove(reminder.reminderId)}
                    color="green"
                    className="flex items-center gap-2 bg-green-600"
                  >
                    <CheckCircle className="h-4 w-4" /> Approve
                  </Button>
                  <Button
                    onClick={() => handleDeny(reminder.reminderId)}
                    color="red"
                    className="flex items-center gap-2"
                  >
                    <XCircle className="h-4 w-4" /> Deny
                  </Button>
                </CardFooter>
              )}

              {reminder.approvalStatus !== "pending" && (
                <CardFooter className="pt-0 pb-4 px-6">
                  <Typography variant="small" className="flex items-center gap-1 text-gray-500">
                    <AlertCircle className="h-3.5 w-3.5" />
                    <span>
                      {reminder.approvalStatus === "approved"
                        ? "You approved this booking request"
                        : "You denied this booking request"}
                    </span>
                  </Typography>
                </CardFooter>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

export default Notification
