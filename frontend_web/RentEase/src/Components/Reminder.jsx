"use client"

import { useState, useEffect } from "react"
import { BellRing, Plus, Calendar, Home, AlertCircle, Clock, User } from 'lucide-react'
import axios from "axios"
import Cookies from "js-cookie"
import { Typography, Button, Input, Textarea, Select, Option } from "@material-tailwind/react"

function Reminder() {
  const [showModal, setShowModal] = useState(false)
  const [room, setRoom] = useState("")
  const [date, setDate] = useState("")
  const [note, setNote] = useState("")
  const [errors, setErrors] = useState({})
  const [rooms, setRooms] = useState([])
  const [reminders, setReminders] = useState([])
  const [ownerId, setOwnerId] = useState(null)
  const [selectedRenterId, setSelectedRenterId] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = Cookies.get("token")
    if (!token) {
      setLoading(false)
      return
    }

    axios
      .get(`${import.meta.env.VITE_API_BASE_URL}/owners/current-user`, {
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
      .get(`${import.meta.env.VITE_API_BASE_URL}/rooms/owner/${ownerId}/unavailable`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setRooms(res.data)
      })
      .catch((err) => {
        console.error("Failed to fetch unavailable rooms", err)
      })
  }, [ownerId])

  useEffect(() => {
    if (!ownerId) return
    const token = Cookies.get("token")

    axios
      .get(`${import.meta.env.VITE_API_BASE_URL}/payment_reminders/owner/${ownerId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const manualReminders = res.data.filter(
          (r) => !r.note?.includes("Booking pending approval") && !r.note?.includes("Payment is due"),
        )
        setReminders(manualReminders)
      })
      .catch((err) => console.error("Failed to fetch reminders", err))
  }, [ownerId])

  const handleAddClick = () => {
    setShowModal(true)
    setRoom("")
    setDate("")
    setNote("")
    setSelectedRenterId(null)
    setErrors({})
  }

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
    if (!selectedRenterId) newErrors.room = "Selected room has no associated renter"
    return newErrors
  }

  const handleDone = () => {
    const formErrors = validateForm()
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors)
      return
    }

    const selectedRoom = rooms.find((r) => r.roomId.toString() === room)
    const payload = {
      room: { roomId: parseInt(room) },
      renter: { renterId: selectedRenterId },
      owner: { ownerId: ownerId },
      dueDate: date,
      note: note,
      rentalFee: selectedRoom?.rentalFee || 0.0,
    }

    axios
      .post(`${import.meta.env.VITE_API_BASE_URL}/payment_reminders`, payload)
      .then((res) => {
        setReminders((prev) => [...prev, res.data])
        setShowModal(false)
        setRoom("")
        setDate("")
        setNote("")
        setSelectedRenterId(null)
        setErrors({})
      })
      .catch((err) => {
        console.error("Error creating reminder:", err)
        alert("Failed to create reminder")
      })
  }

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  const getRenterName = (reminder) => {
    return reminder.renter?.firstName && reminder.renter?.lastName
      ? `${reminder.renter.firstName} ${reminder.renter.lastName}`
      : "No renter assigned"
  }

  return (
    <div className="relative min-h-[calc(100vh-60px)] bg-gradient-to-b from-sky-50 to-white flex flex-col items-center justify-start p-6">
      <div className="w-full max-w-4xl">
        {loading && (
          <div className="flex justify-center items-center h-40">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-600"></div>
          </div>
        )}

        {!loading && reminders.length === 0 ? (
          <div className="text-center bg-white rounded-xl shadow-lg border border-gray-100 p-10 transition-all duration-300 hover:shadow-xl">
            <div className="flex justify-center mb-6">
              <div className="p-6 bg-sky-50 rounded-full">
                <BellRing size={64} className="text-sky-600" />
              </div>
            </div>
            <Typography variant="h5" className="font-semibold text-gray-800 mb-3">
              No Reminders
            </Typography>
            <Typography variant="paragraph" className="text-gray-600 max-w-xs mx-auto">
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
                  className="p-6 border rounded-xl shadow-md bg-white hover:shadow-lg transition-all duration-300"
                >
                  <div className="flex flex-col md:flex-row justify-between gap-4">
                    <div className="flex items-start gap-4">
                      <div className="p-3 bg-sky-50 rounded-xl shrink-0">
                        <Home size={24} className="text-sky-600" />
                      </div>
                      <div>
                        <Typography variant="h6" className="font-semibold text-gray-800">
                          Room #{reminder.room.roomId}
                          {reminder.room.unitName && (
                            <span className="text-sm font-normal text-gray-500 ml-2">{reminder.room.unitName}</span>
                          )}
                        </Typography>
                        <div className="flex items-center gap-1 text-gray-500 text-sm mt-1">
                          <User size={14} />
                          <Typography variant="small">{getRenterName(reminder)}</Typography>
                        </div>
                        {reminder.note && (
                          <Typography variant="small" className="text-gray-600 mt-3">
                            {reminder.note}
                          </Typography>
                        )}
                      </div>
                    </div>

                    <div className="flex items-center gap-3 md:self-start">
                      <div className="p-2 bg-amber-50 rounded-lg">
                        <Calendar size={20} className="text-amber-600" />
                      </div>
                      <div>
                        <Typography variant="paragraph" className="font-medium text-gray-700">
                          {formatDate(reminder.dueDate)}
                        </Typography>
                        <Typography variant="small" className="text-gray-500 flex items-center">
                          <Clock size={12} className="mr-1" />
                          Due date
                        </Typography>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
          </div>
        )}
      </div>

      <div className="fixed bottom-8 right-8 z-10">
        <Button
          size="lg"
          className="bg-sky-600 hover:bg-sky-700 shadow-lg flex items-center gap-2 px-6 py-3 rounded-full transition-all duration-300 hover:shadow-xl hover:scale-105"
          onClick={handleAddClick}
        >
          <Plus size={20} />
          <span>Add Reminder</span>
        </Button>
      </div>

      {showModal && (
        <div
          className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 p-4"
          onClick={handleModalClose}
        >
          <div
            className="bg-white rounded-2xl shadow-2xl w-full max-w-[500px] p-8"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <Typography variant="h4" className="font-bold text-gray-800">
                Add New Reminder
              </Typography>
              <button
                onClick={() => setShowModal(false)}
                className="p-2 rounded-full hover:bg-gray-100 transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  className="text-gray-500"
                >
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>

            <div className="flex flex-col gap-5">
              <div className="space-y-2">
                <Typography variant="small" className="font-medium text-gray-700">
                  Select Room
                </Typography>
                <Select
                  label="Select a room"
                  value={room}
                  onChange={(val) => {
                    setRoom(val)
                    const selectedRoom = rooms.find((r) => r.roomId.toString() === val)
                    setSelectedRenterId(selectedRoom?.renter?.renterId || null)
                  }}
                  className={errors.room ? "border-red-500" : "border-sky-500"}
                  containerProps={{ className: "min-w-full" }}
                  labelProps={{ className: "text-sky-600" }}
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

              <div className="space-y-2">
                <Typography variant="small" className="font-medium text-gray-700">
                  Due Date
                </Typography>
                <Input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className={errors.date ? "border-red-500" : "border-sky-500"}
                  labelProps={{ className: "text-sky-600" }}
                  error={!!errors.date}
                />
                {errors.date && (
                  <div className="flex items-center gap-1 mt-1 text-red-500">
                    <AlertCircle size={14} />
                    <Typography className="text-xs">{errors.date}</Typography>
                  </div>
                )}
              </div>

              <div className="space-y-2">
                <Typography variant="small" className="font-medium text-gray-700">
                  Note (Optional)
                </Typography>
                <Textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  className="border-sky-500 min-h-[100px]"
                  labelProps={{ className: "text-sky-600" }}
                  rows={4}
                  placeholder="Add any additional details here..."
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
                className="bg-sky-600 hover:bg-sky-700 rounded-full px-8 shadow-md hover:shadow-lg transition-all"
              >
                Save Reminder
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Reminder