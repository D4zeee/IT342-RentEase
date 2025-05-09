
"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import {
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  HomeIcon,
  CurrencyDollarIcon,
  CalendarDaysIcon,
  XMarkIcon,
  UserIcon,
  PhoneIcon,
  EnvelopeIcon,
  MapPinIcon,
  CreditCardIcon,
  ClockIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from "@heroicons/react/24/outline";

const Dashboard = () => {
  const [stats, setStats] = useState({ total: 0, available: 0, rented: 0, revenue: 0 });
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [rentedRooms, setRentedRooms] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingRentedRooms, setIsLoadingRentedRooms] = useState(true);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const token = Cookies.get("token");
        const ownerResponse = await axios.get("http://localhost:8080/owners/current-user", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const ownerId = ownerResponse.data.ownerId;

        // Fetch room stats
        const statsResponse = await axios.get(`http://localhost:8080/rooms/owner/${ownerId}/room-stats`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setStats(statsResponse.data);

        // Fetch payment history
        const paymentResponse = await axios.get("http://localhost:8080/api/payment-history", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPaymentHistory(paymentResponse.data);

        // Calculate total revenue from payment history
        const totalRevenue = paymentResponse.data.reduce((sum, payment) => sum + (payment.rentalFee || 0), 0);
        setStats((prevStats) => ({ ...prevStats, revenue: totalRevenue }));

        // Fetch rented rooms
        setIsLoadingRentedRooms(true);
        const rentedRoomsResponse = await axios.get(`http://localhost:8080/rooms/owner/${ownerId}/unavailable`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setRentedRooms(rentedRoomsResponse.data);
      } catch (error) {
        console.error("Failed to fetch dashboard data", error);
      } finally {
        setIsLoading(false);
        setIsLoadingRentedRooms(false);
      }
    };

    fetchData();
  }, []);

  // Data for UI elements
  const mockData = {
    newBookings: stats.total,
    revenue: stats.revenue,
    occupiedRooms: stats.rented,
    availableRooms: stats.available,
  };

  // Placeholder for monthly occupancy data
  const monthlyOccupancy = [75, 82, 65, 90, 85, 88, 92, 78, 83, 70, 88, 95];
  const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

  const openRoomDetails = (room) => {
    setSelectedRoom(room);
    setCurrentImageIndex(0); // Reset to first image when opening modal
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setCurrentImageIndex(0); // Reset image index when closing
  };

  // Close modal when clicking outside
  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      closeModal();
    }
  };

  // Image navigation handlers
  const handleNextImage = () => {
    if (selectedRoom && selectedRoom.imagePaths && selectedRoom.imagePaths.length > 1) {
      setCurrentImageIndex((prevIndex) =>
        prevIndex === selectedRoom.imagePaths.length - 1 ? 0 : prevIndex + 1
      );
    }
  };

  const handlePrevImage = () => {
    if (selectedRoom && selectedRoom.imagePaths && selectedRoom.imagePaths.length > 1) {
      setCurrentImageIndex((prevIndex) =>
        prevIndex === 0 ? selectedRoom.imagePaths.length - 1 : prevIndex - 1
      );
    }
  };

  // Format date function
  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  return (
    <div className="bg-gray-50 min-h-screen p-6">
      <div className="max-w-7xl mx-auto">
        {/* Overview Cards */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Overview</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* New Bookings Card */}
            <div className="bg-gradient-to-br from-blue-50 to-blue-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Number Of Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{mockData.newBookings}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <CalendarDaysIcon className="h-6 w-6 text-blue-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+20%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Available Rooms Card */}
            <div className="bg-gradient-to-br from-green-50 to-green-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Available Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{stats.available}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <HomeIcon className="h-6 w-6 text-green-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+5.2%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Rented Rooms Card */}
            <div className="bg-gradient-to-br from-orange-50 to-orange-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Rented Rooms</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">{stats.rented}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <HomeIcon className="h-6 w-6 text-orange-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingDownIcon className="h-4 w-4 text-red-500 mr-1" />
                <span className="text-red-500 font-medium">-3.1%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>

            {/* Total Revenue Card */}
            <div className="bg-gradient-to-br from-teal-50 to-teal-200 rounded-xl shadow-sm p-6">
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-gray-600 font-medium">Total Revenue</p>
                  <p className="text-3xl font-bold text-gray-800 mt-2">₱{mockData.revenue.toLocaleString()}</p>
                </div>
                <div className="bg-white p-2 rounded-lg shadow-sm">
                  <CurrencyDollarIcon className="h-6 w-6 text-teal-500" />
                </div>
              </div>
              <div className="mt-4 flex items-center text-sm">
                <ArrowTrendingUpIcon className="h-4 w-4 text-green-500 mr-1" />
                <span className="text-green-500 font-medium">+20%</span>
                <span className="text-gray-500 ml-1">from last week</span>
              </div>
            </div>
          </div>
        </div>

        {/* Featured Rented Rooms */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Featured Rented Rooms</h2>
          {isLoadingRentedRooms ? (
            <div className="flex justify-center items-center h-40">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-teal-500"></div>
            </div>
          ) : rentedRooms.length === 0 ? (
            <div className="bg-white rounded-xl shadow-sm p-8 text-center">
              <HomeIcon className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-700 mb-2">No rented rooms available</h3>
              <p className="text-gray-500">When you have rented rooms, they will appear here.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {rentedRooms.map((room) => (
                <div
                  key={room.roomId}
                  className="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1 cursor-pointer group"
                  onClick={() => openRoomDetails(room)}
                >
                  <div className="relative">
                    <img
                      src={room.imagePaths?.[0] || "https://via.placeholder.com/300x200"}
                      alt={room.unitName}
                      className="w-full h-48 object-cover group-hover:opacity-90 transition-opacity"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity flex items-end">
                      <p className="text-white font-medium p-4">Click to view details</p>
                    </div>
                  </div>
                  <div className="p-4">
                    <h3 className="text-lg font-semibold text-gray-800">{room.unitName}</h3>
                    <p className="text-sm text-gray-600">
                      {room.addressLine1}, {room.city}
                    </p>
                    <p className="text-sm text-gray-600 mt-1">
                      Renter: {room.renter ? `${room.renter.firstName} ${room.renter.lastName}` : "N/A"}
                    </p>
                    <p className="text-sm font-medium text-teal-600 mt-2">
                      ₱{room.rentalFee?.toLocaleString() || 0} / month
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Room Details Modal */}
      {isModalOpen && selectedRoom && (
        <div
          className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center z-50 p-4"
          onClick={handleBackdropClick}
        >
          <div className="bg-white rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <div className="relative">
              {/* Image Gallery */}
              <div className="h-64 md:h-80 bg-gray-200 relative">
                {selectedRoom.imagePaths && selectedRoom.imagePaths.length > 0 ? (
                  <div className="relative w-full h-full">
                    <img
                      src={selectedRoom.imagePaths[currentImageIndex] || "/placeholder.svg"}
                      alt={`${selectedRoom.unitName} - Image ${currentImageIndex + 1}`}
                      className="w-full h-full object-cover"
                    />
                    {/* Navigation Buttons */}
                    {selectedRoom.imagePaths.length > 1 && (
                      <>
                        <button
                          onClick={handlePrevImage}
                          className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white rounded-full p-2 shadow-md hover:bg-gray-100"
                        >
                          <ChevronLeftIcon className="h-6 w-6 text-gray-700" />
                        </button>
                        <button
                          onClick={handleNextImage}
                          className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white rounded-full p-2 shadow-md hover:bg-gray-100"
                        >
                          <ChevronRightIcon className="h-6 w-6 text-gray-700" />
                        </button>
                      </>
                    )}
                    {/* Image Counter */}
                    {selectedRoom.imagePaths.length > 1 && (
                      <div className="absolute bottom-4 right-4 bg-black/60 text-white text-sm font-medium px-2 py-1 rounded">
                        {currentImageIndex + 1} / {selectedRoom.imagePaths.length}
                      </div>
                    )}
                  </div>
                ) : (
                  <div className="flex items-center justify-center h-full">
                    <HomeIcon className="h-16 w-16 text-gray-400" />
                  </div>
                )}
                <button
                  onClick={closeModal}
                  className="absolute top-4 right-4 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
                >
                  <XMarkIcon className="h-6 w-6 text-gray-700" />
                </button>
              </div>

              {/* Room Details */}
              <div className="p-6">
                <div className="flex flex-col md:flex-row md:justify-between md:items-start mb-6">
                  <div>
                    <h2 className="text-2xl font-bold text-gray-800">{selectedRoom.unitName}</h2>
                    <p className="flex items-center text-gray-600 mt-1">
                      <MapPinIcon className="h-4 w-4 mr-1" />
                      {selectedRoom.addressLine1}, {selectedRoom.city}
                    </p>
                  </div>
                  <div className="mt-4 md:mt-0 bg-teal-50 px-4 py-2 rounded-lg">
                    <p className="text-teal-700 font-semibold text-lg">
                      ₱{selectedRoom.rentalFee?.toLocaleString() || 0}
                      <span className="text-sm text-teal-600 font-normal"> / month</span>
                    </p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  {/* Room Information */}
                  <div>
                    <h3 className="text-lg font-semibold text-gray-800 mb-4 border-b pb-2">Room Information</h3>
                    <div className="space-y-3">
                      <div className="flex items-start">
                        <HomeIcon className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                        <div>
                          <p className="text-sm font-medium text-gray-700">Room ID</p>
                          <p className="text-gray-600">{selectedRoom.roomId}</p>
                        </div>
                      </div>
                      <div className="flex items-start">
                        <CreditCardIcon className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                        <div>
                          <p className="text-sm font-medium text-gray-700">Rental Fee</p>
                          <p className="text-gray-600">₱{selectedRoom.rentalFee?.toLocaleString() || 0} per month</p>
                        </div>
                      </div>
                      <div className="flex items-start">
                        <ClockIcon className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                        <div>
                          <p className="text-sm font-medium text-gray-700">Rental Status</p>
                          <p className="text-gray-600">
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-orange-100 text-orange-800">
                              Rented
                            </span>
                          </p>
                        </div>
                      </div>
                    
                    
                    </div>
                  </div>

                  {/* Renter Information */}
                  <div>
                    <h3 className="text-lg font-semibold text-gray-800 mb-4 border-b pb-2">Renter Information</h3>
                    {selectedRoom.renter ? (
                      <div className="space-y-3">
                     
                        <div className="flex items-start">
                          <EnvelopeIcon className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                          <div>
                            <p className="text-sm font-medium text-gray-700">Email</p>
                            <p className="text-gray-600">{selectedRoom.renter.email || "N/A"}</p>
                          </div>
                        </div>
                       
                      
                      </div>
                    ) : (
                      <div className="text-gray-500 italic">No renter information available</div>
                    )}
                  </div>
                </div>

               
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;