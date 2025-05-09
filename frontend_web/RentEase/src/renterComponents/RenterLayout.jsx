import React, { useState } from "react";
import { Outlet } from "react-router-dom";
import RenterSidebar from "./RenterSidebar";
import { Typography } from "@material-tailwind/react";

const RenterLayout = () => {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);

    const openDrawer = () => setIsDrawerOpen(true);
    const closeDrawer = () => setIsDrawerOpen(false);

    return (
        <div className="flex h-screen overflow-hidden bg-gray-50">
            {/* Sidebar - fixed position */}
            <RenterSidebar
                isDrawerOpen={isDrawerOpen}
                openDrawer={openDrawer}
                closeDrawer={closeDrawer}
            />

            {/* Main content area with left margin to account for sidebar */}
            <div className="flex flex-col flex-1 lg:ml-[256px] w-full lg:w-[calc(100%-256px)]">
                {/* Mobile Header with Drawer Toggle */}
                <div className="lg:hidden flex justify-between items-center p-6 bg-white shadow-sm">
                    <Typography variant="h5" color="blue-gray">
                        Renter Dashboard
                    </Typography>
                    <button onClick={openDrawer} className="text-blue-gray-500">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M4 6h16M4 12h16M4 18h16"
                            />
                        </svg>
                    </button>
                </div>

                {/* Content area - scrollable */}
                <main className="flex-1 overflow-y-auto p-6">
                    <div className="max-w-7xl mx-auto">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    );
};

export default RenterLayout;