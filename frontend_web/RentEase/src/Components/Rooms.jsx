"use client"

import { useState, useEffect } from "react"
import { Typography, Input, Button, Card } from "@material-tailwind/react"
import { Plus, Home, DollarSign, FileText, X, Trash, Building, MapPin, Hash } from "lucide-react"
import axios from "axios"
import Cookies from "js-cookie"
import { useNavigate } from "react-router-dom"

function Rooms() {
    const navigate = useNavigate()
    const [showModal, setShowModal] = useState(false)
    const [unitName, setUnitName] = useState("")
    const [numberOfRooms, setNumberOfRooms] = useState("")
    const [description, setDescription] = useState("")
    const [rentalFee, setRentalFee] = useState("")
    const [addressLine1, setAddressLine1] = useState("")
    const [addressLine2, setAddressLine2] = useState("")
    const [city, setCity] = useState("")
    const [postalCode, setPostalCode] = useState("")
    const [rooms, setRooms] = useState([])
    const [owner, setOwner] = useState(null)
    const [error, setError] = useState("")
    const [ownerId, setOwnerId] = useState("")
    const [roomToEdit, setRoomToEdit] = useState(null)
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
    const [showSaveConfirm, setShowSaveConfirm] = useState(false)
    const [images, setImages] = useState([]) // Store the File objects
    const [imagePreviews, setImagePreviews] = useState([]) // Store the preview URLs

    // Fetch current owner
    useEffect(() => {
        const token = Cookies.get("token")
        if (token) {
            axios
                .get("http://localhost:8080/owners/current-user", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                .then((response) => {
                    setOwner(response.data)
                    setOwnerId(response.data.ownerId)
                })
                .catch((error) => {
                    console.error("Error fetching current user:", error.response?.data || error.message)
                    if (error.response?.status === 403 || error.response?.status === 401) {
                        navigate("/login")
                    }
                })
        } else {
            navigate("/login")
        }
    }, [navigate])

    // Fetch rooms by ownerId
    useEffect(() => {
        if (ownerId) {
            axios
                .get(`http://localhost:8080/rooms/owner/${ownerId}`, {
                    headers: {
                        Authorization: `Bearer ${Cookies.get("token")}`,
                    },
                })
                .then((response) => {
                    setRooms(response.data)
                    console.log("Fetched rooms:", response.data)
                })
                .catch((error) => {
                    console.error("Error fetching rooms:", error.response?.data || error.message)
                })
        }
    }, [ownerId])

    // Handle image selection
    const handleImageChange = (e) => {
        const files = Array.from(e.target.files)
        if (files.length > 0) {
            const newImages = [...images, ...files]
            setImages(newImages)

            // Generate previews for the selected images
            const previews = files.map((file) => URL.createObjectURL(file))
            setImagePreviews([...imagePreviews, ...previews])
        }
    }

    // Handle image removal
    const handleRemoveImage = (index) => {
        const newImages = images.filter((_, i) => i !== index)
        const newPreviews = imagePreviews.filter((_, i) => i !== index)
        setImages(newImages)
        setImagePreviews(newPreviews)
    }

    const handleAddRoom = () => {
        setShowModal(true)
        setRoomToEdit(null)
        clearForm()
    }

    const handleEditRoom = (room) => {
        setRoomToEdit(room)
        setShowModal(true)
        setUnitName(room.unitName)
        setNumberOfRooms(room.numberOfRooms)
        setDescription(room.description)
        setRentalFee(room.rentalFee)
        setAddressLine1(room.addressLine1)
        setAddressLine2(room.addressLine2)
        setCity(room.city)
        setPostalCode(room.postalCode)

        // Load existing images if editing
        if (room.imagePaths && room.imagePaths.length > 0) {
            setImagePreviews(room.imagePaths) // Use Supabase public URLs directly
        } else {
            setImagePreviews([])
        }
        setImages([]) // Reset new images to upload
    }

    const handleCloseModal = (e) => {
        if (e.target === e.currentTarget) {
            setShowModal(false)
            setError("")
        }
    }

    const handleDone = () => {
        // Validate required fields
        if (!unitName || !numberOfRooms || !rentalFee || !addressLine1 || !city || !postalCode) {
            setError(
                "Please fill in all required fields: Unit Name, Number of Rooms, Rental Fee, Address Line 1, City, and Postal Code.",
            )
            return
        }

        if (!owner || !owner.ownerId) {
            setError("Owner not loaded or invalid. Please try again.")
            return
        }

        const roomData = {
            unitName,
            numberOfRooms: Number.parseInt(numberOfRooms) || 1,
            description,
            rentalFee: Number.parseFloat(rentalFee) || 0,
            addressLine1,
            addressLine2,
            city,
            postalCode,
            owner: { ownerId },
        }

        // Prepare form data to include images
        const formData = new FormData()
        formData.append("room", new Blob([JSON.stringify(roomData)], { type: "application/json" }))
        images.forEach((image, index) => {
            formData.append("images", image)
        })

        if (roomToEdit) {
            setShowSaveConfirm(true)
        } else {
            // Add a new room
            axios
                .post("http://localhost:8080/rooms", formData, {
                    headers: {
                        Authorization: `Bearer ${Cookies.get("token")}`,
                        "Content-Type": "multipart/form-data",
                    },
                })
                .then((response) => {
                    setRooms((prev) => [...prev, response.data])
                    setShowModal(false)
                    clearForm()
                })
                .catch((error) => {
                    console.error("Error adding room:", error.response?.data || error.message)
                    setError("Failed to add room: " + (error.response?.data?.message || error.message))
                })
        }
    }

    const confirmSave = () => {
        const roomData = {
            unitName,
            numberOfRooms: Number.parseInt(numberOfRooms) || 1,
            description,
            rentalFee: Number.parseFloat(rentalFee) || 0,
            addressLine1,
            addressLine2,
            city,
            postalCode,
            owner: { ownerId },
        }

        const formData = new FormData()
        formData.append("room", new Blob([JSON.stringify(roomData)], { type: "application/json" }))
        images.forEach((image, index) => {
            formData.append("images", image)
        })

        axios
            .put(`http://localhost:8080/rooms/${roomToEdit.roomId}`, formData, {
                headers: {
                    Authorization: `Bearer ${Cookies.get("token")}`,
                    "Content-Type": "multipart/form-data",
                },
            })
            .then((response) => {
                setRooms((prev) => prev.map((room) => (room.roomId === response.data.roomId ? response.data : room)))
                setShowModal(false)
                clearForm()
                setShowSaveConfirm(false)
            })
            .catch((error) => {
                console.error("Error updating room:", error.response?.data || error.message)
                setError("Failed to update room: " + (error.response?.data?.message || error.message))
                setShowSaveConfirm(false)
            })
    }

    const handleDelete = () => {
        if (roomToEdit) {
            setShowDeleteConfirm(true)
        }
    }

    const confirmDelete = () => {
        axios
            .delete(`http://localhost:8080/rooms/${roomToEdit.roomId}`, {
                headers: {
                    Authorization: `Bearer ${Cookies.get("token")}`,
                },
            })
            .then(() => {
                setRooms((prev) => prev.filter((room) => room.roomId !== roomToEdit.roomId))
                setShowModal(false)
                setRoomToEdit(null)
                setShowDeleteConfirm(false)
            })
            .catch((error) => {
                console.error("Error deleting room:", error.response?.data || error.message)
                setShowDeleteConfirm(false)
            })
    }

    const clearForm = () => {
        setUnitName("")
        setNumberOfRooms("")
        setDescription("")
        setRentalFee("")
        setAddressLine1("")
        setAddressLine2("")
        setCity("")
        setPostalCode("")
        setImages([])
        setImagePreviews([])
        setError("")
    }

    return (
        <div className="relative p-4 md:p-8 bg-gradient-to-b from-gray-50 to-gray-100 min-h-screen">
            <div className="mb-8">
                <h1 className="text-2xl md:text-3xl font-bold text-gray-800">My Properties</h1>
                <p className="text-gray-600 mt-1">Manage your rental units and rooms</p>
            </div>

            {rooms.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-[calc(100vh-12rem)] bg-white rounded-xl shadow-sm border border-gray-100 p-10">
                    <div className="bg-gray-50 p-6 rounded-full mb-4">
                        <Building size={48} className="text-gray-400" />
                    </div>
                    <h2 className="text-xl font-semibold text-gray-700 mb-2">No Properties Yet</h2>
                    <p className="text-gray-500 text-center max-w-md mb-6">
                        You haven't added any properties to your account. Add your first property to get started.
                    </p>
                    <button
                        className="px-6 py-3 bg-[#0a8ea8] hover:bg-[#0a7d94] text-white rounded-lg flex items-center justify-center shadow-lg transition-all duration-300 hover:shadow-xl transform hover:scale-105"
                        onClick={handleAddRoom}
                    >
                        <Plus size={20} className="mr-2" />
                        Add Your First Property
                    </button>
                </div>
            ) : (
                <>
                    <div className="grid grid-cols-1 gap-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 mb-8">
                        {rooms.map((room) => (
                            <Card
                                key={room.roomId}
                                className="overflow-hidden hover:shadow-lg transition-shadow duration-300 border border-gray-100"
                                onClick={() => handleEditRoom(room)}
                            >
                                <div className="h-32 flex items-center justify-center">
                                    {room.imagePaths && room.imagePaths.length > 0 ? (
                                        <img
                                            src={room.imagePaths[0]}
                                            alt={room.unitName}
                                            className="w-full h-full object-cover"
                                            onError={(e) => console.error("Error loading image for room " + room.roomId + ":", e)}
                                        />
                                    ) : (
                                        <Building size={48} className="text-gray-400" />
                                    )}
                                </div>
                                <div className="p-5">
                                    <p className="text-sm font-medium text-gray-500 mb-2">
                                        Status: <span className={room.status === "rented" ? "text-red-500" : "text-green-600"}>
                                            {room.status === "rented" ? "Unavailable" : "Available"}
                                        </span>
                                    </p>

                                    <div className="flex justify-between items-center mb-3">
                                        <Typography variant="h5" className="font-bold text-gray-800 truncate">
                                            {room.unitName}
                                        </Typography>
                                        <div className="bg-cyan-50 text-cyan-700 px-2 py-1 rounded-full text-xs font-medium">
                                            {room.numberOfRooms} {room.numberOfRooms === 1 ? "Room" : "Rooms"}
                                        </div>
                                    </div>

                                    <div className="flex items-center text-gray-700 mb-2">
                                        <DollarSign size={16} className="mr-2 text-cyan-600" />
                                        <Typography variant="small" className="font-semibold">
                                            ${room.rentalFee.toFixed(2)}/month
                                        </Typography>
                                    </div>

                                    <div className="flex items-start text-gray-600 mb-2">
                                        <MapPin size={16} className="mr-2 mt-0.5 flex-shrink-0 text-gray-500" />
                                        <Typography variant="small" className="line-clamp-2">
                                            {room.addressLine1}
                                            {room.addressLine2 && `, ${room.addressLine2}`}
                                            {room.city && `, ${room.city}`}
                                            {room.postalCode && ` ${room.postalCode}`}
                                        </Typography>
                                    </div>

                                    {room.description && (
                                        <div className="mt-3 pt-3 border-t border-gray-100">
                                            <Typography variant="small" className="text-gray-600 line-clamp-2">
                                                {room.description}
                                            </Typography>
                                        </div>
                                    )}
                                </div>
                            </Card>
                        ))}
                    </div>
                    <div className="fixed bottom-8 right-8">
                        <button
                            className="w-16 h-16 bg-[#0a8ea8] hover:bg-[#0a7d94] text-white rounded-full flex items-center justify-center shadow-lg transition-all duration-300 hover:shadow-xl transform hover:scale-105"
                            onClick={handleAddRoom}
                        >
                            <Plus size={32} />
                        </button>
                    </div>
                </>
            )}

            {showModal && (
                <div
                    className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
                    onClick={handleCloseModal}
                >
                    <Card className="w-full max-w-md bg-white shadow-2xl rounded-xl" onClick={(e) => e.stopPropagation()}>
                        <div className="p-6 max-h-[80vh] overflow-y-auto">
                            <div className="flex justify-between items-center mb-6">
                                <div>
                                    <Typography variant="h5" className="font-bold text-gray-800">
                                        {roomToEdit ? "Edit Property" : "Add New Property"}
                                    </Typography>
                                    <Typography variant="small" className="text-gray-500">
                                        {roomToEdit ? "Update your property details" : "Fill in the details of your new property"}
                                    </Typography>
                                </div>
                                <button
                                    className="p-1.5 rounded-full hover:bg-gray-100 transition-colors"
                                    onClick={() => setShowModal(false)}
                                >
                                    <X size={20} className="text-gray-500" />
                                </button>
                            </div>

                            {error && (
                                <div className="mb-6 p-3 bg-red-50 border border-red-100 rounded-lg">
                                    <Typography variant="small" className="text-red-600">
                                        {error}
                                    </Typography>
                                </div>
                            )}

                            <div className="space-y-6">
                                <div className="space-y-4">
                                    <Typography variant="small" className="text-gray-800 font-semibold uppercase tracking-wider">
                                        Property Details
                                    </Typography>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Unit Name <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <Building size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={unitName}
                                                onChange={(e) => setUnitName(e.target.value)}
                                                placeholder="e.g. Sunset Apartment 3B"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Number of Rooms <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <Hash size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="number"
                                                value={numberOfRooms}
                                                onChange={(e) => setNumberOfRooms(e.target.value)}
                                                placeholder="e.g. 2"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Rental Fee ($/month) <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <DollarSign size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="number"
                                                value={rentalFee}
                                                onChange={(e) => setRentalFee(e.target.value)}
                                                placeholder="e.g. 1200"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Description
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <FileText size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={description}
                                                onChange={(e) => setDescription(e.target.value)}
                                                placeholder="Brief description of the property"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Property Images
                                        </Typography>
                                        <div className="flex flex-wrap gap-2 mb-2">
                                            {imagePreviews.length > 0 ? (
                                                imagePreviews.map((preview, index) => (
                                                    <div key={index} className="relative">
                                                        <img
                                                            src={preview}
                                                            alt={`Preview ${index}`}
                                                            className="w-16 h-16 object-cover rounded"
                                                            onError={(e) => console.error("Error loading preview image:", e)}
                                                        />
                                                        <button
                                                            className="absolute top-0 right-0 p-1 bg-red-500 text-white rounded-full"
                                                            onClick={() => handleRemoveImage(index)}
                                                        >
                                                            <X size={12} />
                                                        </button>
                                                    </div>
                                                ))
                                            ) : (
                                                <Typography variant="small" className="text-gray-500">
                                                    No images selected
                                                </Typography>
                                            )}
                                        </div>
                                        <label className="cursor-pointer">
                                            <div className="w-10 h-10 bg-[#0a8ea8] hover:bg-[#0a7d94] text-white rounded-full flex items-center justify-center">
                                                <Plus size={20} />
                                            </div>
                                            <input
                                                type="file"
                                                accept="image/*"
                                                multiple
                                                onChange={handleImageChange}
                                                className="hidden"
                                            />
                                        </label>
                                    </div>
                                </div>

                                <div className="space-y-4 pt-2">
                                    <Typography variant="small" className="text-gray-800 font-semibold uppercase tracking-wider">
                                        Property Address
                                    </Typography>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Address Line 1 <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <Home size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={addressLine1}
                                                onChange={(e) => setAddressLine1(e.target.value)}
                                                placeholder="Street address"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Address Line 2
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <Home size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={addressLine2}
                                                onChange={(e) => setAddressLine2(e.target.value)}
                                                placeholder="Apt, suite, unit, etc. (optional)"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            City <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <MapPin size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={city}
                                                onChange={(e) => setCity(e.target.value)}
                                                placeholder="e.g. San Francisco"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>

                                    <div className="space-y-1.5">
                                        <Typography variant="small" className="text-gray-700 font-medium">
                                            Postal Code <span className="text-red-500">*</span>
                                        </Typography>
                                        <div className="relative">
                                            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                                <MapPin size={18} className="text-gray-500" />
                                            </div>
                                            <Input
                                                type="text"
                                                value={postalCode}
                                                onChange={(e) => setPostalCode(e.target.value)}
                                                placeholder="e.g. 94103"
                                                className="pl-10 border-gray-300 focus:border-cyan-500"
                                                labelProps={{ className: "hidden" }}
                                            />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="flex flex-col sm:flex-row justify-center gap-3 mt-8">
                                <Button
                                    className="bg-[#0a8ea8] hover:bg-[#0a7d94] px-8 py-2.5 rounded-lg shadow-md transition-colors flex-1"
                                    onClick={handleDone}
                                >
                                    {roomToEdit ? "Save Changes" : "Add Property"}
                                </Button>

                                {roomToEdit && (
                                    <Button
                                        className="bg-red-500 hover:bg-red-600 px-5 py-2.5 rounded-lg shadow-md transition-colors flex items-center justify-center gap-2 flex-1"
                                        onClick={handleDelete}
                                    >
                                        <Trash size={18} className="text-white" />
                                        <span className="text-white">Delete Property</span>
                                    </Button>
                                )}
                            </div>
                        </div>
                    </Card>
                </div>
            )}

            {showSaveConfirm && (
                <div
                    className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
                    onClick={() => setShowSaveConfirm(false)}
                >
                    <Card className="w fullscreen max-w-sm bg-white shadow-2xl rounded-xl" onClick={(e) => e.stopPropagation()}>
                        <div className="p-6">
                            <Typography variant="h5" className="font-bold text-gray-800 mb-4">
                                Confirm Changes
                            </Typography>
                            <Typography variant="small" className="text-gray-600 mb-6">
                                Are you sure you want to save the changes to this property?
                            </Typography>
                            <div className="flex justify-end gap-3">
                                <Button
                                    className="bg-gray-200 text-gray-800 hover:bg-gray-300 px-4 py-2 rounded-lg"
                                    onClick={() => setShowSaveConfirm(false)}
                                >
                                    Cancel
                                </Button>
                                <Button className="bg-[#0a8ea8] hover:bg-[#0a7d94] px-4 py-2 rounded-lg" onClick={confirmSave}>
                                    Save Changes
                                </Button>
                            </div>
                        </div>
                    </Card>
                </div>
            )}

            {showDeleteConfirm && (
                <div
                    className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
                    onClick={() => setShowDeleteConfirm(false)}
                >
                    <Card className="w-full max-w-sm bg-white shadow-2xl rounded-xl" onClick={(e) => e.stopPropagation()}>
                        <div className="p-6">
                            <Typography variant="h5" className="font-bold text-red-600 mb-4">
                                Confirm Deletion
                            </Typography>
                            <Typography variant="small" className="text-gray-600 mb-6">
                                Are you sure you want to delete this property? This action cannot be undone.
                            </Typography>
                            <div className="flex justify-end gap-3">
                                <Button
                                    className="bg-gray-200 text-gray-800 hover:bg-gray-300 px-4 py-2 rounded-lg"
                                    onClick={() => setShowDeleteConfirm(false)}
                                >
                                    Cancel
                                </Button>
                                <Button className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded-lg text-white" onClick={confirmDelete}>
                                    Delete
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