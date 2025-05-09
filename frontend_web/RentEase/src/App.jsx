import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import LandingPage from './Components/LandingPage';
import LoginPage from './Components/LoginPage';
import RegisterPage from './Components/RegisterPage';
import Dashboard from './Components/Dashboard';
import Layout from './Components/Layout';
import Rooms from './Components/Rooms';
import Payments from './Components/Payment';
import Reminder from './Components/Reminder';
import PrivateRoute from './Components/PrivateRoute';
import PayMongoTest from './Components/PayMongoTest';
import PaymentSuccess from './Components/PaymentSuccess';
import RoomBookingMobile from './Components/RoomBookingMobile';
import Notification from './Components/Notification';
import SuccessPage from './Components/SuccessPage';

import RenterDashboard from './renterComponents/RenterDashboard';
import RenterLogin from './renterComponents/RenterLogin';
import RenterLayout from './renterComponents/RenterLayout';
import RenterNotifications from './renterComponents/RenterNotifications';
import PaymentMethodPage from './renterComponents/PaymentMethodPage';
import PaymentNotifications from './renterComponents/PaymentNotifications';
import RenterReminder from './renterComponents/RenterReminder';

function App() {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<LandingPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/Register" element={<RegisterPage />} />
                <Route path="/test-payment" element={<PayMongoTest />} />
                <Route path="/payment-success" element={<PaymentSuccess />} />
                <Route path="/rent-room" element={<RoomBookingMobile />} />
                <Route path="/success" element={<SuccessPage />} />
                <Route path="/renter-login" element={<RenterLogin />} />
                <Route path="/payment-method" element={<PaymentMethodPage />} />
                <Route path="/payment-notif" element={<PaymentNotifications />} />

                {/* Renter Protected Routes (Wrapped with RenterLayout) */}
                <Route path="/" element={<RenterLayout />}>
                    <Route
                        path="/renter-dashboard"
                        element={
                            <PrivateRoute>
                                <RenterDashboard />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/renter-notif"
                        element={
                            <PrivateRoute>
                                <RenterNotifications />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/renter-reminder"
                        element={
                            <PrivateRoute>
                                <RenterReminder />
                            </PrivateRoute>
                        }
                    />
                </Route>

                {/* Owner Protected Routes (Wrapped with Layout) */}
                <Route path="/" element={<Layout />}>
                    <Route
                        path="/dashboard"
                        element={
                            <PrivateRoute>
                                <Dashboard />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/rooms"
                        element={
                            <PrivateRoute>
                                <Rooms />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/payments"
                        element={
                            <PrivateRoute>
                                <Payments />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/reminder"
                        element={
                            <PrivateRoute>
                                <Reminder />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/notifications"
                        element={
                            <PrivateRoute>
                                <Notification />
                            </PrivateRoute>
                        }
                    />
                </Route>
            </Routes>
        </Router>
    );
}

export default App;