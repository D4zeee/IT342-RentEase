import React from "react";
import styles from "../styles/Dashboard.module.css";

const Dashboard = () => {
  return (
    <div className={styles.dashboard}>
      <p>Welcome to RentEase! This is your dashboard where you can manage your rooms, payments, and more.</p>
      {/* Additional dashboard content can go here */}
    </div>
  );
};

export default Dashboard;
