import React, { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import styles from "../styles/Dashboard.module.css";

const Dashboard = () => {
  const [stats, setStats] = useState({ total: 0, available: 0, rented: 0 });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const token = Cookies.get("token");
        const ownerResponse = await axios.get("http://localhost:8080/owners/current-user", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const ownerId = ownerResponse.data.ownerId;

        const statsResponse = await axios.get(`http://localhost:8080/rooms/owner/${ownerId}/room-stats`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        setStats(statsResponse.data);
      } catch (error) {
        console.error("Failed to fetch dashboard stats", error);
      }
    };

    fetchStats();
  }, []);

  return (
    <div className={styles.dashboard}>
      <p>Welcome to RentEase! Here's a summary of your rooms:</p>
      <ul>
        <li>Total Rooms: {stats.total}</li>
        <li>Available Rooms: {stats.available}</li>
        <li>Rented/Unavailable Rooms: {stats.rented}</li>
      </ul>
    </div>
  );
};

export default Dashboard;
