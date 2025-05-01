import { useState } from "react";
import {
    Card,
    Typography,
    List,
    ListItem,
    ListItemPrefix,
    Drawer,
} from "@material-tailwind/react";
import {
    Home,
    UserCircle,
    Bell,
    LogOut, // Replace ArrowLeftOnRectangle with LogOut
} from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import Cookies from "js-cookie";

function RenterLayout({ children }) {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        Cookies.remove("renterToken");
        navigate("/renter-login");
    };

    const openDrawer = () => setIsDrawerOpen(true);
    const closeDrawer = () => setIsDrawerOpen(false);

    return (
        <div className="flex min-h-screen bg-gray-100">
            {/* Sidebar for Desktop */}
            <Card className="hidden lg:block h-screen w-64 p-4 shadow-xl shadow-blue-gray-900/5">
                <div className="mb-2 p-4">
                    <Typography variant="h5" color="blue-gray">
                        Renter Dashboard
                    </Typography>
                </div>
                <List>
                    <Link to="/renter-dashboard">
                        <ListItem>
                            <ListItemPrefix>
                                <Home className="h-5 w-5" />
                            </ListItemPrefix>
                            Dashboard
                        </ListItem>
                    </Link>
                    <Link to="/renter-dashboard/notifications">
                        <ListItem>
                            <ListItemPrefix>
                                <Bell className="h-5 w-5" />
                            </ListItemPrefix>
                            Notifications
                        </ListItem>
                    </Link>
                    <Link to="/renter-dashboard/profile">
                        <ListItem>
                            <ListItemPrefix>
                                <UserCircle className="h-5 w-5" />
                            </ListItemPrefix>
                            Profile
                        </ListItem>
                    </Link>
                    <ListItem onClick={handleLogout}>
                        <ListItemPrefix>
                            <LogOut className="h-5 w-5" /> {/* Updated icon */}
                        </ListItemPrefix>
                        Logout
                    </ListItem>
                </List>
            </Card>

            {/* Drawer for Mobile */}
            <Drawer open={isDrawerOpen} onClose={closeDrawer} className="lg:hidden">
                <Card className="h-full w-64 p-4 shadow-xl shadow-blue-gray-900/5">
                    <div className="mb-2 p-4">
                        <Typography variant="h5" color="blue-gray">
                            Renter Dashboard
                        </Typography>
                    </div>
                    <List>
                        <Link to="/renter-dashboard">
                            <ListItem onClick={closeDrawer}>
                                <ListItemPrefix>
                                    <Home className="h-5 w-5" />
                                </ListItemPrefix>
                                Dashboard
                            </ListItem>
                        </Link>
                        <Link to="/renter-notif">
                            <ListItem onClick={closeDrawer}>
                                <ListItemPrefix>
                                    <Bell className="h-5 w-5" />
                                </ListItemPrefix>
                                Notifications
                            </ListItem>
                        </Link>
                        <Link to="/renter-dashboard/profile">
                            <ListItem onClick={closeDrawer}>
                                <ListItemPrefix>
                                    <UserCircle className="h-5 w-5" />
                                </ListItemPrefix>
                                Profile
                            </ListItem>
                        </Link>
                        <ListItem onClick={() => { handleLogout(); closeDrawer(); }}>
                            <ListItemPrefix>
                                <LogOut className="h-5 w-5" /> {/* Updated icon */}
                            </ListItemPrefix>
                            Logout
                        </ListItem>
                    </List>
                </Card>
            </Drawer>

            {/* Main Content Area */}
            <div className="flex-1 p-6">
                {/* Mobile Header with Drawer Toggle */}
                <div className="lg:hidden flex justify-between items-center mb-4">
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

                {/* Render child components (e.g., RenterNotifications) */}
                <div className="max-w-7xl mx-auto">
                    {children}
                </div>
            </div>
        </div>
    );
}

export default RenterLayout;