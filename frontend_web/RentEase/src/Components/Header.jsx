"use client"

import { useState, useEffect } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import { Typography, Avatar, Input } from "@material-tailwind/react"
import { Search, ChevronDown } from "lucide-react"
import axios from "axios"
import Cookies from "js-cookie"

function Header() {
  const location = useLocation();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [username, setUsername] = useState("");
  const [ownerId, setOwnerId] = useState("");
  const [loading, setLoading] = useState(true); // Add loading state

  useEffect(() => {
    const token = Cookies.get("token");

    if (token) {
      setLoading(true); // Start loading
      axios
        .get("http://localhost:8080/owners/current-user", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((response) => {
          if (response.data && response.data.username && response.data.ownerId) {
            setUsername(response.data.username);
            setOwnerId(response.data.ownerId);
          }
        })
        .catch((error) => {
          console.error("Error fetching current user:", error.response || error.message);
          if (error.response?.status === 403 || error.response?.status === 401) {
            navigate("/login");
          }
        })
        .finally(() => {
          setLoading(false); // Stop loading
        });
    } else {
      navigate("/login");
    }
  }, [navigate]);

  const titleMap = {
    "/dashboard": "Dashboard",
    "/rooms": "Rooms",
    "/payments": "Payments",
    "/reminder": "Reminder",
    "/notifications": "Notification",
  };

  const title = titleMap[location.pathname] || "Dashboard";

  const handleSearch = (e) => {
    e.preventDefault();
  };

  return (
    <header className="relative flex items-center justify-between bg-white px-8 py-4 border-b border-gray-200 shadow-sm">
      <Typography variant="h4" className="font-bold text-gray-800">
        {title}
      </Typography>

      <div className="absolute left-1/2 -translate-x-1/2 w-full max-w-[350px]">
        <form onSubmit={handleSearch} className="relative">
          <Input
            type="text"
            placeholder="Search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="!border !border-gray-200 bg-gray-50 text-gray-900 shadow-none focus:!border-gray-300 rounded-full pl-11 pr-4 py-2.5"
            labelProps={{
              className: "hidden",
            }}
            containerProps={{
              className: "min-w-0",
            }}
          />
          <div className="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
            <Search className="h-4 w-4 text-gray-500" />
          </div>
        </form>
      </div>

      <div className="flex items-center gap-6">
        <div className="flex items-center gap-3 cursor-pointer group">
          <Avatar
            src="/assets/img/profile.jpg"
            alt={username || "User"}
            className="w-10 h-10 border border-gray-200 shadow-sm"
          />
          <div className="flex flex-col">
            <Typography variant="small" className="font-semibold text-gray-800 group-hover:text-gray-900">
              {loading ? "Loading..." : (username || "Guest")} {/* Show loading state */}
            </Typography>
            <Typography variant="small" className="text-xs text-gray-500">
              ID: {loading ? "Loading..." : (ownerId || "N/A")} {/* Show loading state */}
            </Typography>
          </div>
          <ChevronDown className="h-4 w-4 text-gray-500 transition-transform group-hover:text-gray-700 group-hover:rotate-180" />
        </div>
      </div>
    </header>
  );
}

export default Header
