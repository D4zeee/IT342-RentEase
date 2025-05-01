import React, { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookie"; // ✅ import Cookies to retrieve token

const RoomBookingPage = () => {
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const renterId = 2; // Replace this with the actual logged-in renter ID logic if needed

  useEffect(() => {
    axios.get("http://localhost:8080/rooms")
      .then(res => setRooms(res.data))
      .catch(err => console.error("Failed to fetch rooms", err));
  }, []);

  const handleBookRoom = async () => {
    if (!selectedRoom || !startDate || !endDate) return alert("Please complete all fields.");
  
    const token = localStorage.getItem("token") || Cookies.get("token"); // or however you're storing it
  
    try {
      await axios.post("http://localhost:8080/rented_units", {
        renter: { renterId },
        room: { roomId: selectedRoom.roomId },
        startDate,
        endDate
      }, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
  
      alert("Booking successful!");
      setSelectedRoom(null);
      setStartDate("");
      setEndDate("");
    } catch (error) {
      console.error("Booking failed", error);
      alert("Booking failed. Check console for details.");
    }
  };
  

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Available Rooms</h1>
      <ul className="space-y-4">
        {rooms.map(room => (
          <li
            key={room.roomId}
            className={`border p-4 rounded-lg ${selectedRoom?.roomId === room.roomId ? 'bg-blue-100' : ''}`}
            onClick={() => setSelectedRoom(room)}
          >
            <h2 className="text-lg font-semibold">{room.unitName}</h2>
            <p>{room.description}</p>
            <p>Rental Fee: ₱{room.rentalFee}</p>
            <p>Location: {room.addressLine1}, {room.city}</p>
          </li>
        ))}
      </ul>

      {selectedRoom && (
        <div className="mt-6 p-4 border rounded-lg bg-gray-50">
          <h2 className="text-lg font-bold mb-2">Book Room: {selectedRoom.unitName}</h2>
          <div className="space-y-2">
            <label className="block">
              Start Date:
              <input type="date" className="border p-2 w-full" value={startDate} onChange={e => setStartDate(e.target.value)} />
            </label>
            <label className="block">
              End Date:
              <input type="date" className="border p-2 w-full" value={endDate} onChange={e => setEndDate(e.target.value)} />
            </label>
            <button
              className="bg-green-600 text-white px-4 py-2 rounded mt-2"
              onClick={handleBookRoom}
            >
              Confirm Booking
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomBookingPage;
