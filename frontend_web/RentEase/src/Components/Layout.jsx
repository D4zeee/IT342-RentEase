import React from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "./Sidebar"
import Header from "./Header"

const Layout = () => {
  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      {/* Sidebar - fixed position */}
      <Sidebar />
      
      {/* Main content area with left margin to account for sidebar */}
      <div className="flex flex-col flex-1 ml-[250px] w-[calc(100%-250px)]">
        {/* Header - sticky at top */}
        <Header />
        
        {/* Content area - scrollable */}
        <main className="flex-1 overflow-y-auto p-3">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

export default Layout
