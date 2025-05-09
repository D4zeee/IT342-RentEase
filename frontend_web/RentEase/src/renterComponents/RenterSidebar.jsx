"use client"
import { Typography, Drawer } from "@material-tailwind/react"
import { Home, UserCircle, Bell, LogOut, Calendar } from "lucide-react"
import { NavLink, useNavigate } from "react-router-dom"
import Cookies from "js-cookie"

const RenterSidebar = ({ isDrawerOpen, openDrawer, closeDrawer }) => {
  const navigate = useNavigate()

  const handleLogout = () => {
    Cookies.remove("renterToken")
    navigate("/renter-login")
  }

  // Common navigation items for both desktop and mobile
  const navigationItems = [
    {
      to: "/renter-dashboard",
      icon: <Home className="w-5 h-5" />,
      label: "Dashboard",
    },
    {
      to: "/renter-notif",
      icon: <Bell className="w-5 h-5" />,
      label: "Notifications",
    },
    {
      to: "/renter-reminder",
      icon: <Calendar className="w-5 h-5" />,
      label: "Reminders",
    },
    {
      to: "/renter-dashboard/profile",
      icon: <UserCircle className="w-5 h-5" />,
      label: "Profile",
    },
  ]

  // Desktop sidebar
  const DesktopSidebar = () => (
    <div className="fixed top-0 left-0 w-[250px] h-screen bg-white shadow-md flex flex-col p-5 overflow-y-auto z-30 hidden lg:flex">
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
        {navigationItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
                isActive
                  ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                  : "text-[#087592] hover:bg-gray-100"
              }`
            }
          >
            {item.icon}
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Spacer to push logout to bottom */}
      <div className="flex-grow"></div>

      {/* Logout button at bottom */}
      <div className="mt-4">
        <div
          onClick={handleLogout}
          className="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-300 cursor-pointer"
        >
          <LogOut className="w-5 h-5" />
          <span className="font-medium">Logout</span>
        </div>
      </div>
    </div>
  )

  // Mobile drawer content
  const MobileDrawerContent = () => (
    <div className="h-full w-[250px] bg-white flex flex-col p-5 overflow-y-auto">
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
        {navigationItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={closeDrawer}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 ${
                isActive
                  ? "bg-gradient-to-r from-[#5885AF] to-[#274472] text-white shadow-sm"
                  : "text-[#087592] hover:bg-gray-100"
              }`
            }
          >
            {item.icon}
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Spacer to push logout to bottom */}
      <div className="flex-grow"></div>

      {/* Logout button at bottom */}
      <div className="mt-4">
        <div
          onClick={() => {
            handleLogout()
            closeDrawer()
          }}
          className="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-100 transition-all duration-300 cursor-pointer"
        >
          <LogOut className="w-5 h-5" />
          <span className="font-medium">Logout</span>
        </div>
      </div>
    </div>
  )

  return (
    <>
      {/* Desktop Sidebar */}
      <DesktopSidebar />

      {/* Mobile Drawer */}
      <Drawer open={isDrawerOpen} onClose={closeDrawer} className="lg:hidden">
        <MobileDrawerContent />
      </Drawer>
    </>
  )
}

export default RenterSidebar
