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
  MapPinIcon,
  CreditCardIcon,
  ClockIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  EnvelopeIcon,
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

  const BASE_URL = import.meta.env.VITE_API_BASE_URL;

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const token = Cookies.get("token");

        const ownerResponse = await axios.get(`${BASE_URL}/owners/current-user`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const ownerId = ownerResponse.data.ownerId;

        const statsResponse = await axios.get(`${BASE_URL}/rooms/owner/${ownerId}/room-stats`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setStats(statsResponse.data);

        const paymentResponse = await axios.get(`${BASE_URL}/api/payment-history`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPaymentHistory(paymentResponse.data);

        const totalRevenue = paymentResponse.data.reduce((sum, payment) => sum + (payment.rentalFee || 0), 0);
        setStats((prevStats) => ({ ...prevStats, revenue: totalRevenue }));

        setIsLoadingRentedRooms(true);
        const rentedRoomsResponse = await axios.get(`${BASE_URL}/rooms/owner/${ownerId}/unavailable`, {
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

  const mockData = {
    newBookings: stats.total,
    revenue: stats.revenue,
    occupiedRooms: stats.rented,
    availableRooms: stats.available,
  };

  const openRoomDetails = (room) => {
    setSelectedRoom(room);
    setCurrentImageIndex(0);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setCurrentImageIndex(0);
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      closeModal();
    }
  };

  const handleNextImage = () => {
    if (selectedRoom?.imagePaths?.length > 1) {
      setCurrentImageIndex((prev) => (prev === selectedRoom.imagePaths.length - 1 ? 0 : prev + 1));
    }
  };

  const handlePrevImage = () => {
    if (selectedRoom?.imagePaths?.length > 1) {
      setCurrentImageIndex((prev) => (prev === 0 ? selectedRoom.imagePaths.length - 1 : prev - 1));
    }
  };

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
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Overview</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
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

      {/* Modal code not included here for brevity – it's unchanged from your version. */}
    </div>
  );
};

export default Dashboard;
