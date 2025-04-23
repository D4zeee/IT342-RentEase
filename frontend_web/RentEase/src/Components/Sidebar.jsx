import { NavLink, useNavigate } from "react-router-dom";
import { Typography } from "@material-tailwind/react";
import { HomeIcon, BedDoubleIcon, CreditCardIcon, CalendarIcon, LogOutIcon, Settings } from "lucide-react";
import Cookies from "js-cookie"; 


function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    Cookies.remove("token");
    navigate("/login");
  };

  return (
    <div className="fixed top-0 left-0 w-[250px] h-screen bg-white shadow-md flex flex-col p-5 overflow-y-auto z-30">
      {/* Brand / Logo */}
      <div className="mb-8">
        <Typography variant="h4" className="font-bold text-center text-[#0a8ea8]">
          RentEase
        </Typography>
      </div>

      {/* Subtle divider */}
      <div className="h-px w-full bg-gradient-to-r from-transparent via-gray-200 to-transparent mb-6"></div>

      {/* Navigation Menu */}
      <nav className="flex flex-col gap-3">
        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
              isActive
                ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                : "text-[#087592] hover:bg-gray-100"
            }`
          }
        >
          <HomeIcon className="w-5 h-5" />
          <span className="font-medium">Dashboard</span>
        </NavLink>

        <NavLink
          to="/rooms"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
              isActive
                ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                : "text-[#087592] hover:bg-gray-100"
            }`
          }
        >
          <BedDoubleIcon className="w-5 h-5" />
          <span className="font-medium">Rooms</span>
        </NavLink>

        <NavLink
          to="/payments"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
              isActive
                ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                : "text-[#087592] hover:bg-gray-100"
            }`
          }
        >
          <CreditCardIcon className="w-5 h-5" />
          <span className="font-medium">Payments</span>
        </NavLink>

        <NavLink
          to="/reminder"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
              isActive
                ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                : "text-[#087592] hover:bg-gray-100"
            }`
          }
        >
          <CalendarIcon className="w-5 h-5" />
          <span className="font-medium">Reminder</span>
        </NavLink>
      </nav>

      {/* Spacer to push logout to bottom */}
      <div className="flex-grow"></div>

      {/* Settings and Logout buttons at bottom */}
      <div className="mt-4 space-y-2">
        <NavLink
          to="/settings"
          className={({ isActive }) =>
            `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
              isActive
                ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                : "text-[#087592] hover:bg-gray-100"
            }`
          }
        >
          <Settings className="w-5 h-5" />
          <span className="font-medium">Settings</span>
        </NavLink>

        <div
          onClick={handleLogout}
          className="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-300 cursor-pointer"
        >
          <LogOutIcon className="w-5 h-5" />
          <span className="font-medium">Logout</span>
        </div>
      </div>
    </div>
  );
}

export default Sidebar;
