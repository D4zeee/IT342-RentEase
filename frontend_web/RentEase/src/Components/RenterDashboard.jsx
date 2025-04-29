import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";

function RenterDashboard() {
    const location = useLocation();
    const navigate = useNavigate();
    const [rooms, setRooms] = useState([]);
    const [renterId, setRenterId] = useState(null);
    const [renterName, setRenterName] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isRoomsLoading, setIsRoomsLoading] = useState(true);

    useEffect(() => {
        const token = Cookies.get("renterToken");
        console.log("Token from cookie:", token);

        if (!token) {
            console.log("No token found, redirecting to login");
            navigate("/renter-login");
            return;
        }

        try {
            const decodedToken = jwtDecode(token);
            console.log("Decoded token:", decodedToken);
            setRenterId(decodedToken.renterId);
            setRenterName(decodedToken.renterName);
            console.log("Set renterId:", decodedToken.renterId);
        } catch (error) {
            console.error("Error decoding token:", error);
            navigate("/renter-login");
            return;
        } finally {
            setIsLoading(false);
        }

        if (location.state && location.state.rooms) {
            setRooms(location.state.rooms);
            setIsRoomsLoading(false);
        } else {
            setIsRoomsLoading(true);
            axios
                .get("http://localhost:8080/rooms", {
                    headers: { Authorization: `Bearer ${token}` },
                })
                .then((response) => {
                    console.log("Rooms data:", response.data); // Debug: Check the rooms data
                    setRooms(response.data);
                })
                .catch((error) => {
                    console.error("Error fetching rooms:", error.response?.data || error.message);
                    navigate("/renter-login");
                })
                .finally(() => setIsRoomsLoading(false));
        }
    }, [location, navigate]);

    const handleLogout = () => {
        Cookies.remove("renterToken");
        navigate("/renter-login");
    };

    const handleRentRoom = async (roomId) => {
        const token = Cookies.get("renterToken");

        if (!token) {
            alert("You must be logged in to rent a room.");
            return;
        }

        const startDate = new Date().toISOString().split("T")[0];
        const endDate = new Date(new Date().setMonth(new Date().getMonth() + 1))
            .toISOString()
            .split("T")[0];

        const rentedUnitData = {
            renter: { renterId: parseInt(renterId) },
            room: { roomId: parseInt(roomId) },
            startDate,
            endDate,
        };

        console.log("Rented Unit Data:", JSON.stringify(rentedUnitData, null, 2));

        axios
            .post("http://localhost:8080/rented_units", rentedUnitData, {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
                alert("Room rented successfully!");
                return axios.get("http://localhost:8080/rooms", {
                    headers: { Authorization: `Bearer ${token}` },
                });
            })
            .then((response) => {
                console.log("Updated rooms data:", response.data); // Debug: Check updated rooms
                setRooms(response.data);
            })
            .catch((error) => {
                const errorMessage = error.response?.data?.message || error.response?.data || error.message;
                console.error("Error renting room:", errorMessage);
                alert("Failed to rent room: " + errorMessage);
            });
    };

    return (
        <div className="min-h-screen bg-gray-100 p-8">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-gray-800">Available Rooms</h1>
                <h2 className="text-lg text-gray-600">
                    {isLoading ? "Loading..." : `Renter ID: ${renterId || "Not Available"}`}
                </h2>
                <button
                    onClick={handleLogout}
                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded shadow"
                >
                    Logout
                </button>
            </div>

            {isRoomsLoading ? (
                <p className="text-center text-gray-500">Loading rooms...</p>
            ) : rooms.length === 0 ? (
                <p className="text-center text-gray-500">No rooms available at the moment.</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {rooms.map((room) => (
                        <div
                            key={room.roomId}
                            className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition"
                        >
                            <h2 className="text-xl font-semibold text-gray-800 mb-2">{room.unitName}</h2>
                            <p className="text-gray-600 mb-1">
                                <strong>Rental Fee:</strong> ${room.rentalFee}/month
                            </p>
                            <p className="text-gray-600 mb-1">
                                <strong>Location:</strong> {room.city}, {room.addressLine1}
                            </p>
                            <p className="text-gray-600 mb-1">
                                <strong>Status:</strong> {room.status}
                            </p>
                            <p className="text-gray-600 mb-1">
                                <strong>Owner ID:</strong> {room.ownerId || "Unknown"}
                            </p>
                            <p className="text-gray-600 mb-1">
                                <strong>Owner Name:</strong> {room.ownerName || "Unknown"}
                            </p>
                            <p className="text-gray-500 text-sm mt-2">{room.description}</p>

                            {room.status === "available" && (
                                <button
                                    onClick={() => handleRentRoom(room.roomId)}
                                    className="mt-4 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
                                >
                                    Rent this Room
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default RenterDashboard;