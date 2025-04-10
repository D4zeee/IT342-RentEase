"use client"

import { useState } from "react"
import { Typography, Button, Input, Textarea } from "@material-tailwind/react"
import { BellRing, Calendar, Home, FileText, Plus } from "lucide-react"

function Reminder() {
  // Control modal visibility
  const [showModal, setShowModal] = useState(false)

  // States for form inputs
  const [room, setRoom] = useState("")
  const [date, setDate] = useState("")
  const [note, setNote] = useState("")

  // Form validation
  const [errors, setErrors] = useState({})

  const handleAddClick = () => {
    setShowModal(true)
  }

  // Closes the modal
  const handleModalClose = (e) => {
    if (e.target === e.currentTarget) {
      setShowModal(false)
      // Clear errors when closing
      setErrors({})
    }
  }

  // Validate form
  const validateForm = () => {
    const newErrors = {}
    if (!room.trim()) newErrors.room = "Room number is required"
    if (!date) newErrors.date = "Date is required"
    return newErrors
  }

  // Handles the "Done" action
  const handleDone = () => {
    // Validate form
    const formErrors = validateForm()
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors)
      return
    }

    // For demonstration, we'll simply log the values
    console.log("New Reminder Added:")
    console.log("Room:", room)
    console.log("Date:", date)
    console.log("Note:", note)

    // Close the modal after processing
    setShowModal(false)

    // Clear input fields
    setRoom("")
    setDate("")
    setNote("")

    // Clear errors
    setErrors({})
  }

  return (
    <div className="relative min-h-[calc(100vh-60px)] bg-gray-50 flex flex-col justify-center items-center p-6">
      {/* Empty state */}
      <div className="text-center">
        <div className="flex justify-center mb-4">
          <div className="p-6 bg-blue-50 rounded-full">
            <BellRing size={64} className="text-blue-400" />
          </div>
        </div>
        <Typography variant="h5" color="blue-gray" className="mb-2">
          No Reminders
        </Typography>
        <Typography color="gray" className="max-w-xs">
          You don't have any reminders set. Click the button below to add a new reminder.
        </Typography>
      </div>

      {/* ADD button - Using absolute instead of fixed to prevent shifting */}
      <div className="absolute bottom-8 right-8 z-10">
        <Button
          size="lg"
          className="bg-[#0a8ea8] hover:bg-[#0a7d94] shadow-lg flex items-center gap-2 px-6 py-3 rounded-full transition-all duration-300 hover:shadow-xl"
          onClick={handleAddClick}
        >
          <Plus size={20} />
          <span>ADD</span>
        </Button>
      </div>

      {/* Modal - Using a custom modal instead of Material Tailwind Dialog */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 p-4" onClick={handleModalClose}>
          <div
            className="bg-gradient-to-br from-[#ffffff] to-[#ffffff] rounded-xl shadow-xl w-full max-w-[400px] p-6"
            onClick={(e) => e.stopPropagation()}
          >
            <Typography variant="h4" color="black" className="text-center mb-6">
              Add New Reminder
            </Typography>

            <div className="flex flex-col gap-10">
              {/* Room input */}
              <div className="relative ">
                <Input
                  type="text"
                  label="Room Number"
                  placeholder="ROOM #"
                  value={room}
                  onChange={(e) => setRoom(e.target.value)}
                  className="pl-4 !border-t-blue-gray-200 focus:!border-t-black placeholder-black"
                  labelProps={{
                    className: "!text-black absolute right-0 -top-6 text-sm before:content-none after:content-none",
                  }}                  
                  containerProps={{
                    className: "text-black",
                  }}
                  error={!!errors.room}
                />
                {errors.room && (
                  <Typography className="mt-1 text-xs text-red-500">
                    {errors.room}
                  </Typography>
                )}
              </div>

              {/* Date input */}
              <div className="relative">
                <Input
                  type="date"
                  label="Date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className=" pl-3 !border-t-blue-gray-200 focus:!border-t-black pr-3"
                  labelProps={{
                    className: "!text-black absolute right-0 -top-6 text-sm before:content-none after:content-none",
                  }}
                  containerProps={{
                    className: "text-black",
                  }}
                  error={!!errors.date}
                />
              {errors.date && (
                <Typography className="mt-1 text-xs text-red-500">
                  {errors.date}
                </Typography>
              )}
              </div>

              {/* Note textarea */}
              <div className="relative">
                <Textarea
                  label="Note"
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  className="pl-10 !border-t-blue-gray-200 focus:!border-t-black"
                  labelProps={{
                    className: " absolute right-0 -top-6 text-black  text-sm before:content-none after:content-none",
                  }}
                  containerProps={{
                    className: "text-black min-h-[100px]",
                  }}
                />
              </div>
            </div>

            <div className="flex justify-center mt-6">
              <Button onClick={handleDone} className="bg-[#0a8ea8] hover:bg-[#0a7d94] rounded-full px-8">
                Done
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Reminder
