"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookies";
import {
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  HomeIcon,
  CurrencyDollarIcon,
  CalendarDaysIcon,
} from "@heroicons/react/24/outline";

const Dashboard = () => {
  const [stats, setStats] = useState({ total: 0, available: 0, rented: 0, revenue: 0 });
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [featuredRooms, setFeaturedRooms] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

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

        // Fetch featured (rented) rooms
        const featuredRoomsResponse = await axios.get(`http://localhost:8080/rooms/owner/${ownerId}/unavailable`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setFeaturedRooms(featuredRoomsResponse.data);
      } catch (error) {
        console.error("Failed to fetch dashboard data", error);
      } finally {
        setIsLoading(false);
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
                  <p className="text-3xl font-bold text-gray-800 mt-2">
                    ₱{mockData.revenue.toLocaleString()}
                  </p>
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

        {/* Featured Rooms Section */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Featured Rooms (Rented)</h2>
          {isLoading ? (
            <p className="text-gray-600">Loading featured rooms...</p>
          ) : featuredRooms.length === 0 ? (
            <p className="text-gray-600">No rented rooms available.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {featuredRooms.map((room) => (
                <div
                  key={room.roomId}
                  className="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="text-lg font-semibold text-gray-800">{room.unitName}</p>
                      <p className="text-sm text-gray-600 mt-1">{room.city}</p>
                      <p className="text-sm text-gray-600 mt-1">
                        Renter: {room.renter ? `${room.renter.firstName} ${room.renter.lastName}` : "N/A"}
                      </p>
                      <p className="text-sm font-medium text-gray-700 mt-2">
                        ₱{room.rentalFee.toLocaleString()} / month
                      </p>
                    </div>
                    {room.imagePaths && room.imagePaths.length > 0 && (
                      <img
                        src={room.imagePaths[0]}
                        alt={room.unitName}
                        className="w-16 h-16 object-cover rounded-lg"
                      />
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;