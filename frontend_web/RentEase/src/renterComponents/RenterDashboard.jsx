import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";
import { Typography, Button, Card, Input } from "@material-tailwind/react";
import { Building, DollarSign, MapPin, Calendar, X, ChevronLeft, ChevronRight, Bed, Bath, Square } from "lucide-react";

const LoadingSpinner = ({ size = "h-12 w-12", className = "" }) => (
  <div className={`flex justify-center items-center ${className}`}>
    <div className={`animate-spin rounded-full ${size} border-t-2 border-b-2 border-cyan-600`}></div>
  </div>
);

function RenterDashboard() {
  const location = useLocation();
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [renterId, setRenterId] = useState(null);
  const [renterName, setRenterName] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isRoomsLoading, setIsRoomsLoading] = useState(true);
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [showPreviewModal, setShowPreviewModal] = useState(false);
  const [selectedRoomId, setSelectedRoomId] = useState(null);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const token = Cookies.get("renterToken");
    if (!token) {
      navigate("/renter-login");
      return;
    }

    try {
      const decodedToken = jwtDecode(token);
      setRenterId(decodedToken.renterId);
      setRenterName(decodedToken.renterName);
    } catch (error) {
      console.error("Error decoding token:", error);
      navigate("/renter-login");
      return;
    } finally {
      setIsLoading(false);
    }

    const fetchRooms = async () => {
      try {
        const response = await axios.get("http://localhost:8080/rooms", {
          headers: { Authorization: `Bearer ${Cookies.get("renterToken")}` },
        });
        setRooms(response.data);
      } catch (error) {
        console.error("Error fetching rooms:", error.response?.data || error.message);
        navigate("/renter-login");
      } finally {
        setIsRoomsLoading(false);
      }
    };

    if (location.state?.rooms) {
      setRooms(location.state.rooms);
      setIsRoomsLoading(false);
    } else {
      fetchRooms();
    }
  }, [location, navigate]);

  const handleConfirmBooking = async () => {
    const token = Cookies.get("renterToken");

    if (!startDate || !endDate || !selectedRoomId) {
      alert("Please select start and end dates.");
      return;
    }

    const rentedUnitData = {
      renter: { renterId: parseInt(renterId) },
      room: { roomId: parseInt(selectedRoomId) },
      startDate,
      endDate,
    };

    try {
      await axios.post("http://localhost:8080/rented_units", rentedUnitData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("Room booking submitted successfully! Please wait for approval.");
      setShowBookingModal(false);
      setStartDate("");
      setEndDate("");
      setSelectedRoomId(null);

      const updatedRooms = await axios.get("http://localhost:8080/rooms", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRooms(updatedRooms.data);
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.response?.data || error.message;
      console.error("Error renting room:", errorMessage);
      alert("Failed to rent room: " + errorMessage);
    }
  };

  const handleCardClick = (room) => {
    setSelectedRoom(room);
    setCurrentImageIndex(0);
    setShowPreviewModal(true);
  };

  const handlePrevImage = () => {
    setCurrentImageIndex((prev) =>
      prev === 0 ? (selectedRoom.imagePaths?.length || 1) - 1 : prev - 1
    );
  };

  const handleNextImage = () => {
    setCurrentImageIndex((prev) =>
      prev === (selectedRoom.imagePaths?.length || 1) - 1 ? 0 : prev + 1
    );
  };

  return (
    <div className="relative p-4 md:p-8 bg-gradient-to-b from-gray-50 to-gray-100 min-h-screen">
      <div className="flex justify-between items-center mb-8">
        <div>
          <Typography variant="h2" className="text-2xl md:text-3xl font-bold text-gray-800">
            Available Properties
          </Typography>
          <Typography variant="small" className="text-gray-600 mt-1">
            Browse and rent available properties
          </Typography>
        </div>
      </div>

      {isLoading ? (
        <div className="flex flex-col items-center justify-center h-[calc(100vh-12rem)] bg-white rounded-xl shadow-sm border border-gray-100 p-10">
          <LoadingSpinner />
          <Typography variant="small" className="text-gray-500 mt-4">
            Loading your account information...
          </Typography>
        </div>
      ) : isRoomsLoading ? (
        <div className="flex flex-col items-center justify-center h-[calc(100vh-12rem)] bg-white rounded-xl shadow-sm border border-gray-100 p-10">
          <LoadingSpinner />
          <Typography variant="small" className="text-gray-500 mt-4">
            Loading available properties...
          </Typography>
        </div>
      ) : rooms.length === 0 ? (
        <div className="flex flex-col items-center justify-center h-[calc(100vh-12rem)] bg-white rounded-xl shadow-sm border border-gray-100 p-10">
          <div className="bg-gray-50 p-6 rounded-full mb-4">
            <Building size={48} className="text-gray-400" />
          </div>
          <Typography variant="h5" className="text-xl font-semibold text-gray-700 mb-2">
            No Properties Available
          </Typography>
          <Typography variant="small" className="text-gray-500 text-center max-w-md">
            There are no properties available for rent at the moment. Please check back later.
          </Typography>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 mb-8">
          {rooms.map((room) => (
            <Card
              key={room.roomId}
              className="overflow-hidden hover:shadow-lg transition-shadow duration-300 border border-gray-100 cursor-pointer"
              onClick={() => handleCardClick(room)}
            >
              <div className="h-32 flex items-center justify-center">
                {room.imagePaths && room.imagePaths.length > 0 ? (
                  <img
                    src={room.imagePaths[0] || "/placeholder.svg"}
                    alt={room.unitName}
                    className="w-full h-full object-cover"
                    onError={(e) => console.error("Error loading image for room " + room.roomId + ":", e)}
                  />
                ) : (
                  <Building size={48} className="text-gray-400" />
                )}
              </div>
              <div className="p-5">
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

                <div className="flex items-center text-gray-600 mb-2">
                  <Typography variant="small" className="font-medium">
                    Status:{" "}
                    <span
                      className={
                        room.status === "rented"
                          ? "text-red-500"
                          : room.status === "unavailable"
                          ? "text-yellow-500"
                          : "text-green-600"
                      }
                    >
                      {room.status === "rented"
                        ? "Unavailable"
                        : room.status === "unavailable"
                        ? "Pending Approval"
                        : "Available"}
                    </span>
                  </Typography>
                </div>

                {room.description && (
                  <div className="mt-3 pt-3 border-t border-gray-100">
                    <Typography variant="small" className="text-gray-600 line-clamp-2">
                      {room.description}
                    </Typography>
                  </div>
                )}

                {room.status === "available" && (
                  <Button
                    className="mt-4 bg-[#0a8ea8] hover:bg-[#0a7d94] w-full py-2 rounded-lg shadow-md transition-colors"
                    onClick={(e) => {
                      e.stopPropagation();
                      setSelectedRoomId(room.roomId);
                      setShowBookingModal(true);
                    }}
                  >
                    Rent this Property
                  </Button>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}

      {showBookingModal && (
        <div
          className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
          onClick={() => setShowBookingModal(false)}
        >
          <Card className="w-full max-w-md bg-white shadow-2xl rounded-xl" onClick={(e) => e.stopPropagation()}>
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">
                <Typography variant="h5" className="font-bold text-gray-800">
                  Book Property
                </Typography>
                <button
                  className="p-1.5 rounded-full hover:bg-gray-100 transition-colors"
                  onClick={() => setShowBookingModal(false)}
                >
                  <X size={20} className="text-gray-500" />
                </button>
              </div>

              <div className="space-y-6">
                <div className="space-y-1.5">
                  <Typography variant="small" className="text-gray-700 font-medium">
                    Start Date
                  </Typography>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Calendar size={18} className="text-gray-500" />
                    </div>
                    <Input
                      type="date"
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                      className="pl-10 border-gray-300 focus:border-cyan-500"
                      labelProps={{ className: "hidden" }}
                    />
                  </div>
                </div>

                <div className="space-y-1.5">
                  <Typography variant="small" className="text-gray-700 font-medium">
                    End Date
                  </Typography>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                      <Calendar size={18} className="text-gray-500" />
                    </div>
                    <Input
                      type="date"
                      value={endDate}
                      onChange={(e) => setEndDate(e.target.value)}
                      className="pl-10 border-gray-300 focus:border-cyan-500"
                      labelProps={{ className: "hidden" }}
                    />
                  </div>
                </div>
              </div>

              <div className="flex justify-end gap-3 mt-8">
                <Button
                  className="bg-gray-200 text-gray-800 hover:bg-gray-300 px-4 py-2 rounded-lg"
                  onClick={() => setShowBookingModal(false)}
                >
                  Cancel
                </Button>
                <Button
                  className="bg-[#0a8ea8] hover:bg-[#0a7d94] px-4 py-2 rounded-lg"
                  onClick={handleConfirmBooking}
                >
                  Confirm Booking
                </Button>
              </div>
            </div>
          </Card>
        </div>
      )}

      {showPreviewModal && selectedRoom && (
        <div
          className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
          onClick={() => setShowPreviewModal(false)}
        >
          <Card className="w-full max-w-4xl bg-white shadow-2xl rounded-xl overflow-y-auto max-h-[90vh]" onClick={(e) => e.stopPropagation()}>
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">
                <Typography variant="h4" className="font-bold text-gray-800">
                  {selectedRoom.unitName}
                </Typography>
                <button
                  className="p-1.5 rounded-full hover:bg-gray-100 transition-colors"
                  onClick={() => setShowPreviewModal(false)}
                >
                  <X size={24} className="text-gray-500" />
                </button>
              </div>

              {/* Image Preview */}
              <div className="mb-6">
                {selectedRoom.imagePaths && selectedRoom.imagePaths.length > 0 ? (
                  <div className="relative">
                    <img
                      src={selectedRoom.imagePaths[currentImageIndex] || "/placeholder.svg"}
                      alt={`${selectedRoom.unitName} - Image ${currentImageIndex + 1}`}
                      className="w-full h-[400px] object-cover rounded-lg"
                      onError={(e) => console.error("Error loading image:", e)}
                    />
                    {selectedRoom.imagePaths.length > 1 && (
                      <>
                        <button
                          className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full"
                          onClick={handlePrevImage}
                        >
                          <ChevronLeft size={24} />
                        </button>
                        <button
                          className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full"
                          onClick={handleNextImage}
                        >
                          <ChevronRight size={24} />
                        </button>
                        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-2">
                          {selectedRoom.imagePaths.map((_, index) => (
                            <button
                              key={index}
                              className={`w-2 h-2 rounded-full ${
                                index === currentImageIndex ? "bg-white" : "bg-white/50"
                              }`}
                              onClick={() => setCurrentImageIndex(index)}
                            />
                          ))}
                        </div>
                      </>
                    )}
                  </div>
                ) : (
                  <div className="flex items-center justify-center h-[400px] bg-gray-100 rounded-lg">
                    <Building size={48} className="text-gray-400" />
                    <Typography variant="small" className="ml-2 text-gray-500">
                      No images available
                    </Typography>
                  </div>
                )}
              </div>

              {/* Room Details */}
              <div className="bg-gray-50 p-6 rounded-lg border border-gray-100">
                <Typography variant="h5" className="font-semibold text-gray-800 mb-4">
                  Room Details
                </Typography>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div className="flex items-center text-gray-700">
                      <DollarSign size={20} className="mr-3 text-cyan-600" />
                      <div>
                        <Typography variant="small" className="font-medium text-gray-600">
                          Rental Fee
                        </Typography>
                        <Typography variant="h6" className="font-bold text-gray-800">
                          ${selectedRoom.rentalFee.toFixed(2)}/month
                        </Typography>
                      </div>
                    </div>
                    <div className="flex items-center text-gray-700">
                      <MapPin size={20} className="mr-3 text-cyan-600" />
                      <div>
                        <Typography variant="small" className="font-medium text-gray-600">
                          Location
                        </Typography>
                        <Typography variant="small" className="text-gray-800">
                          {selectedRoom.addressLine1}
                          {selectedRoom.addressLine2 && `, ${selectedRoom.addressLine2}`}
                          {selectedRoom.city && `, ${selectedRoom.city}`}
                          {selectedRoom.postalCode && ` ${selectedRoom.postalCode}`}
                        </Typography>
                      </div>
                    </div>
                    <div className="flex items-center text-gray-700">
                      <Typography variant="small" className="font-medium">
                        Status:{" "}
                        <span
                          className={
                            selectedRoom.status === "rented"
                              ? "text-red-500"
                              : selectedRoom.status === "unavailable"
                              ? "text-yellow-500"
                              : "text-green-600"
                          }
                        >
                          {selectedRoom.status === "rented"
                            ? "Unavailable"
                            : selectedRoom.status === "unavailable"
                            ? "Pending Approval"
                            : "Available"}
                        </span>
                      </Typography>
                    </div>
                  </div>
                  <div className="space-y-4">
                    <div className="flex items-center text-gray-700">
                      <Bed size={20} className="mr-3 text-cyan-600" />
                      <div>
                        <Typography variant="small" className="font-medium text-gray-600">
                          Bedrooms
                        </Typography>
                        <Typography variant="h6" className="font-bold text-gray-800">
                          {selectedRoom.numberOfRooms} {selectedRoom.numberOfRooms === 1 ? "Room" : "Rooms"}
                        </Typography>
                      </div>
                    </div>
                  </div>
                </div>
                {selectedRoom.description && (
                  <div className="mt-6">
                    <Typography variant="small" className="font-medium text-gray-600">
                      Description
                    </Typography>
                    <Typography variant="paragraph" className="text-gray-700">
                      {selectedRoom.description}
                    </Typography>
                  </div>
                )}
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <Button
                  className="bg-gray-200 text-gray-800 hover:bg-gray-300 px-4 py-2 rounded-lg"
                  onClick={() => setShowPreviewModal(false)}
                >
                  Close
                </Button>
                {selectedRoom.status === "available" && (
                  <Button
                    className="bg-[#0a8ea8] hover:bg-[#0a7d94] px-4 py-2 rounded-lg"
                    onClick={() => {
                      setShowPreviewModal(false);
                      setSelectedRoomId(selectedRoom.roomId);
                      setShowBookingModal(true);
                    }}
                  >
                    Book Now
                  </Button>
                )}
              </div>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
}

export default RenterDashboard;