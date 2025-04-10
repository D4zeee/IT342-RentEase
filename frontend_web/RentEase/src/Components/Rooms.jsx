"use client"

import { useState, useRef } from "react"
import { Typography, Input, Button, Card } from "@material-tailwind/react"
import { Plus, Home, DollarSign, FileText, X, ImageIcon } from 'lucide-react'

function Rooms() {
  // Modal visibility and form field states
  const [showModal, setShowModal] = useState(false)
  const [roomName, setRoomName] = useState("")
  const [description, setDescription] = useState("")
  const [price, setPrice] = useState("")
  // Store selected images as an array of base64 strings
  const [images, setImages] = useState([])

  // useRef for the hidden file input
  const fileInputRef = useRef(null)

  const handleAddRoom = () => {
    setShowModal(true)
  }

  const handleCloseModal = (e) => {
    if (e.target === e.currentTarget) {
      setShowModal(false)
    }
  }

  const handleDone = () => {
    console.log("Room Name:", roomName)
    console.log("Description:", description)
    console.log("Price:", price)
    console.log("Images:", images)
    // Process the collected data as needed...

    // Close modal and reset fields if desired
    setShowModal(false)
    setRoomName("")
    setDescription("")
    setPrice("")
    // Optionally, clear images: setImages([]);
  }

  // Trigger file input click when plus button is clicked
  const handlePlusClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click()
    }
  }

  // Read the chosen file and add it to the images array
  const handleImageChange = (e) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0]
      const reader = new FileReader()
      reader.onloadend = () => {
        setImages((prevImages) => [...prevImages, reader.result])
      }
      reader.readAsDataURL(file)
    }
  }

  // Remove an image from the array
  const handleRemoveImage = (index) => {
    setImages((prevImages) => prevImages.filter((_, i) => i !== index))
  }

  return (
    <div className="relative p-8 bg-gray-50 min-h-screen">
      <div className="flex flex-col items-center justify-center h-[calc(100vh-4rem)]">
        <button
          className="w-20 h-20 bg-[#0a8ea8] hover:bg-[#0a7d94] text-white rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:shadow-xl transform hover:scale-105"
          onClick={handleAddRoom}
        >
          <Plus size={36} />
        </button>
        <Typography className="mt-4 text-gray-600 font-medium">ADD ROOMS</Typography>
      </div>

      {/* Modal Section */}
      {showModal && (
        <div
          className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
          onClick={handleCloseModal}
        >
          <Card
            className="w-full max-w-md bg-white shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6">
              {/* Header with close button */}
              <div className="flex justify-between items-center mb-4">
                <Typography variant="h5" color="blue-gray" className="font-bold">
                  Add New Room
                </Typography>
                <button
                  className="p-1 rounded-full hover:bg-gray-100 transition-colors"
                  onClick={() => setShowModal(false)}
                >
                  <X size={20} className="text-gray-500" />
                </button>
              </div>

              {/* Room Name input */}
              <Typography variant="small" className="text-gray-700 font-medium mb-1">
                Room #
              </Typography>
              <div className="relative mb-4">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <Home size={18} className="text-gray-500" />
                </div>
                <Input
                  type="text"
                  value={roomName}
                  onChange={(e) => setRoomName(e.target.value)}
                  placeholder="Enter Room #"
                  className="pl-10"
                  containerProps={{ className: "min-w-0" }}
                />
              </div>


              <div className="h-px w-full bg-gray-200 my-4"></div>

              <Typography variant="paragraph" className="text-left font-semibold mb-3">
                Details:
              </Typography>

              {/* Image Upload Section */}
              <div className="mb-4">
                <Typography variant="small" className="text-left text-gray-600 mb-2">
                  Room Images
                </Typography>
                <div className="flex flex-wrap gap-3 items-center">
                  {images.map((img, index) => (
                    <div key={index} className="relative group">
                      <img
                        src={img || "/placeholder.svg"}
                        alt={`Room ${index}`}
                        className="w-20 h-20 object-cover rounded-lg border-2 border-gray-300"
                      />
                      <button
                        className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                        onClick={() => handleRemoveImage(index)}
                      >
                        <X size={14} />
                      </button>
                    </div>
                  ))}
                  <button
                    className="w-20 h-20 bg-gray-100 hover:bg-gray-200 rounded-lg border-2 border-dashed border-gray-300 flex items-center justify-center transition-colors"
                    onClick={handlePlusClick}
                  >
                    <ImageIcon size={24} className="text-gray-500 mr-1" />
                    <Plus size={16} className="text-gray-500" />
                  </button>
                </div>

                {/* Hidden file input */}
                <input
                  type="file"
                  accept="image/*"
                  ref={fileInputRef}
                  className="hidden"
                  onChange={handleImageChange}
                />
              </div>

              <Typography variant="small" className="text-gray-700 font-medium mb-1">
                Description
              </Typography>
              <div className="relative mb-5">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <FileText size={18} className="text-gray-500" />
                </div>
                <Input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Enter description"
                  className="pl-10"
                  containerProps={{ className: "min-w-0" }}
                />
              </div>


              <Typography variant="small" className="text-gray-700 font-medium mb-1">
                Price
              </Typography>
              <div className="relative mb-6">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <DollarSign size={18} className="text-gray-500" />
                </div>
                <Input
                  type="number"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  placeholder="Enter price"
                  className="pl-10"
                  containerProps={{ className: "min-w-0" }}
                />
              </div>


              {/* DONE Button */}
              <div className="flex justify-center">
                <Button
                  className="bg-[#0a8ea8] hover:bg-[#0a7d94] px-8 py-2.5 rounded-lg shadow-md transition-colors"
                  onClick={handleDone}
                >
                  DONE
                </Button>
              </div>
            </div>
          </Card>
        </div>
      )}
    </div>
  )
}

export default Rooms

