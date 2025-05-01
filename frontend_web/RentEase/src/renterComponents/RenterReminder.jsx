"use client"

import { useEffect, useState } from "react"
import axios from "axios"
import Cookies from "js-cookie"
import { Typography } from "@material-tailwind/react"
import { BellRing, Calendar, Home } from "lucide-react"

function RenterReminder() {
  const [reminders, setReminders] = useState([])
  const [loading, setLoading] = useState(true)
  const [renterId, setRenterId] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    const token = Cookies.get("renterToken")
    if (!token) {
      setLoading(false)
      setError("Please log in to view reminders.")
      return
    }
  
    axios
      .get("http://localhost:8080/api/renters/current", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setRenterId(res.data.renterId)
      })
      .catch((err) => {
        console.error("Failed to fetch renter:", err)
        let errorMessage = "Failed to fetch user data."
        if (err.response?.status === 403) {
          errorMessage = "Session expired or access denied. Please log in again."
          // Optionally, clear the invalid token
          Cookies.remove("renterToken")
        }
        setError(errorMessage)
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    if (!renterId) return;
    const token = Cookies.get("renterToken");
  
    axios
      .get(`http://localhost:8080/payment_reminders/renter/${renterId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        console.log("Fetched reminders for renterId:", renterId, res.data); // Debug log
        // Filter out system-generated reminders
        const filteredReminders = res.data.filter(
          (r) => !r.note?.includes("Payment is due") && !r.note?.includes("Booking pending approval")
        );
        setReminders(filteredReminders);
      })
      .catch((err) => {
        console.error("Error loading reminders:", err);
        setError("Failed to load reminders.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [renterId]);

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString(undefined, {
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white p-6">
      <div className="max-w-3xl mx-auto">
        <Typography variant="h3" color="blue-gray" className="text-center font-bold mb-4">
          My Payment Reminders
        </Typography>

        {loading ? (
          <div className="text-center mt-20">Loading reminders...</div>
        ) : error ? (
          <div className="text-center mt-20">
            <Typography color="red">{error}</Typography>
          </div>
        ) : reminders.length === 0 ? (
          <div className="text-center mt-20">
            <BellRing size={48} className="text-cyan-600 ⟟mx-auto mb-4" />
            <Typography variant="h6">No reminders at the moment.</Typography>
          </div>
        ) : (
          <div className="space-y-4">
            {reminders.map((reminder) => (
              <div
                key={reminder.reminderId}
                className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 hover:shadow-md transition-all"
              >
                <div className="flex items-center gap-4">
                  <div className="bg-blue-100 p-3 rounded-md">
                    <Home className="text-blue-600" />
                  </div>
                  <div className="flex-1">
                    <Typography className="font-semibold text-lg">
                      Room #{reminder.room.roomId} - {reminder.room.unitName || "No Unit Name"}
                    </Typography>
                    <Typography className="text-gray-600 mt-1">{reminder.note}</Typography>
                    <div className="flex items-center gap-2 mt-2">
                      <Calendar className="text-amber-600" size={18} />
                      <Typography className="text-sm text-gray-700">
                        Due: {formatDate(reminder.dueDate)}
                      </Typography>
                    </div>
                    <Typography className="text-sm text-gray-500 mt-1">
                      Status: <span className="font-medium">{reminder.approvalStatus || "Pending"}</span>
                    </Typography>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default RenterReminder