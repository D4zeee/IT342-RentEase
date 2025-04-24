"use client"

import { useState, useEffect } from "react"
import { Typography, Button, Input, Textarea, Select, Option } from "@material-tailwind/react"
import { BellRing, Plus, Calendar, Home, X, AlertCircle } from 'lucide-react'
import axios from "axios"
import Cookies from "js-cookie"

function Reminder() {
  const [showModal, setShowModal] = useState(false)
  const [room, setRoom] = useState("")
  const [date, setDate] = useState("")
  const [note, setNote] = useState("")
  const [errors, setErrors] = useState({})
  const [rooms, setRooms] = useState([])
  const [reminders, setReminders] = useState([])
  const [ownerId, setOwnerId] = useState(null)
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
      })
      .finally(() => {
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    if (!ownerId) return
    const token = Cookies.get("token")

    axios
      .get(`http://localhost:8080/rooms/owner/${ownerId}/unavailable`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setRooms(res.data)
        console.log("Fetched unavailable rooms:", res.data)
      })
      .catch((err) => {
        console.error("Failed to fetch unavailable rooms", err)
      })
  }, [ownerId])

  const handleAddClick = () => {
    setShowModal(true)
  }

  useEffect(() => {
    if (!ownerId) return
    const token = Cookies.get("token")
  
    axios
      .get(`http://localhost:8080/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        // only include manually created reminders
        const manualReminders = res.data.filter(
          (reminder) => !reminder.note?.includes("Payment is due")
        )
        setReminders(manualReminders)
      })
      .catch((err) => console.error("Failed to fetch reminders", err))
  }, [ownerId])
  

  const handleModalClose = (e) => {
    if (e.target === e.currentTarget) {
      setShowModal(false)
      setErrors({})
    }
  }

  const validateForm = () => {
    const newErrors = {}
    if (!room) newErrors.room = "Room is required"
    if (!date) newErrors.date = "Date is required"
    return newErrors
  }

  const handleDone = () => {
    const formErrors = validateForm()
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors)
      return
    }

    const payload = {
      room: { roomId: parseInt(room) },
      renter: { renterId: 1 }, // ⚠️ Replace with actual renter ID (or get from backend)
      owner: { ownerId: ownerId },
      dueDate: date,
      note: note,
      rentalFee: 0.0, // or get actual fee from selected room
    }

    axios
      .post("http://localhost:8080/payment_reminders", payload)
      .then((res) => {
        setReminders((prev) => [...prev, res.data]) // ⬅️ add new reminder to list
        setShowModal(false)
        setRoom("")
        setDate("")
        setNote("")
        setErrors({})
      })
      .catch((err) => {
        console.error("Error creating reminder:", err)
        alert("Failed to create reminder")
      })

    // You can submit the reminder here using axios.post
    console.log("Reminder:", { room, date, note })

    setShowModal(false)
    setRoom("")
    setDate("")
    setNote("")
    setErrors({})
  }

  // Format date to be more readable
  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  return (
    <div className="relative min-h-[calc(100vh-60px)] bg-gradient-to-b from-cyan-50 to-white flex flex-col justify-center items-center p-6">
      <div className="w-full max-w-3xl">
        <div className="mb-8">
          <Typography variant="h3" color="blue-gray" className="text-center font-bold">
            Payment Reminders
          </Typography>
          <Typography color="gray" className="text-center mt-2">
            Manage payment reminders for your properties
          </Typography>
        </div>

        {/* Loading state */}
        {loading && (
          <div className="flex justify-center items-center h-40">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-cyan-600"></div>
          </div>
        )}

        {/* Empty state */}
        {!loading && reminders.length === 0 ? (
          <div className="text-center bg-white rounded-xl shadow-sm border border-gray-100 p-10 transition-all duration-300 hover:shadow-md">
            <div className="flex justify-center mb-6">
              <div className="p-6 bg-cyan-50 rounded-full">
                <BellRing size={64} className="text-cyan-600" />
              </div>
            </div>
            <Typography variant="h5" color="blue-gray" className="mb-3 font-semibold">
              No Reminders
            </Typography>
            <Typography color="gray" className="max-w-xs mx-auto">
              You don't have any reminders set. Click the button below to add a new reminder.
            </Typography>
          </div>
        ) : (
          <div className="w-full space-y-4">
            {reminders
              .filter((reminder) => !reminder.note?.includes("Payment is due"))
              .map((reminder) => (
                <div
                  key={reminder.reminderId}
                  className="p-6 border rounded-xl shadow-sm bg-white hover:shadow-md transition-all duration-300"
                >
                  <div className="flex justify-between items-start">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-cyan-50 rounded-lg">
                        <Home size={24} className="text-cyan-600" />
                      </div>
                      <Typography variant="h6" className="text-gray-800 font-semibold">
                        Room #{reminder.room.roomId}
                        {reminder.room.unitName && (
                          <span className="text-sm font-normal text-gray-500 ml-2">{reminder.room.unitName}</span>
                        )}
                      </Typography>
                    </div>
                    <div className="flex items-center gap-2">
                      <div className="p-2 bg-amber-50 rounded-lg">
                        <Calendar size={20} className="text-amber-600" />
                      </div>
                      <Typography className="text-gray-700 font-medium">{formatDate(reminder.dueDate)}</Typography>
                    </div>
                  </div>
                  {reminder.note && <Typography className="text-gray-600 mt-3 pl-12">{reminder.note}</Typography>}
                </div>
              ))}
          </div>
        )}
      </div>

      {/* ADD button */}
      <div className="fixed bottom-8 right-8 z-10">
        <Button
          size="lg"
          className="bg-cyan-600 hover:bg-cyan-700 shadow-lg flex items-center gap-2 px-6 py-3 rounded-full transition-all duration-300 hover:shadow-xl hover:scale-105"
          onClick={handleAddClick}
        >
          <Plus size={20} />
          <span>Add Reminder</span>
        </Button>
      </div>

      {/* Modal */}
      {showModal && (
        <div
          className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 p-4"
          onClick={handleModalClose}
          style={{animation: "fadeIn 0.3s ease-out"}}
        >
          <div
            className="bg-white rounded-2xl shadow-2xl w-full max-w-[450px] p-8"
            onClick={(e) => e.stopPropagation()}
            style={{animation: "scaleIn 0.3s ease-out"}}
          >
            <div className="flex justify-between items-center mb-6">
              <Typography variant="h4" color="blue-gray" className="font-bold">
                Add New Reminder
              </Typography>
              <button
                onClick={() => setShowModal(false)}
                className="p-2 rounded-full hover:bg-gray-100 transition-colors"
              >
                <X size={20} className="text-gray-500" />
              </button>
            </div>

            <div className="flex flex-col gap-5">
              {/* Room dropdown */}
              <div className="relative">
                <Select
                  label="Select Room"
                  value={room}
                  onChange={(val) => setRoom(val)}
                  className="border-cyan-500 focus:border-cyan-600"
                  containerProps={{ className: "min-w-[72px]" }}
                  labelProps={{
                    className: "text-cyan-600",
                  }}
                >
                  {rooms.map((r) => (
                    <Option key={r.roomId} value={r.roomId.toString()}>
                      Room #{r.roomId} - {r.unitName}
                    </Option>
                  ))}
                </Select>
                {errors.room && (
                  <div className="flex items-center gap-1 mt-1 text-red-500">
                    <AlertCircle size={14} />
                    <Typography className="text-xs">{errors.room}</Typography>
                  </div>
                )}
              </div>

              {/* Date input */}
              <div className="relative">
                <Input
                  type="date"
                  label="Due Date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="pl-3 border-cyan-500 focus:border-cyan-600"
                  labelProps={{
                    className: "text-cyan-600",
                  }}
                  error={!!errors.date}
                />
                {errors.date && (
                  <div className="flex items-center gap-1 mt-1 text-red-500">
                    <AlertCircle size={14} />
                    <Typography className="text-xs">{errors.date}</Typography>
                  </div>
                )}
              </div>

              {/* Note input */}
              <div className="relative">
                <Textarea
                  label="Note (Optional)"
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  className="pl-3 border-cyan-500 focus:border-cyan-600"
                  labelProps={{
                    className: "text-cyan-600",
                  }}
                  rows={4}
                />
              </div>
            </div>

            <div className="flex justify-end gap-3 mt-8">
              <Button
                variant="outlined"
                onClick={() => setShowModal(false)}
                className="rounded-full px-6 border-gray-300 text-gray-700"
              >
                Cancel
              </Button>
              <Button
                onClick={handleDone}
                className="bg-cyan-600 hover:bg-cyan-700 rounded-full px-8 shadow-md hover:shadow-lg transition-all"
              >
                Save Reminder
              </Button>
            </div>
          </div>
        </div>
      )}

      <style jsx>{`
        @keyframes fadeIn {
          from {
            opacity: 0;
          }
          to {
            opacity: 1;
          }
        }

        @keyframes scaleIn {
          from {
            transform: scale(0.95);
            opacity: 0;
          }
          to {
            transform: scale(1);
            opacity: 1;
          }
        }
      `}</style>
    </div>
  )
}

export default Reminder