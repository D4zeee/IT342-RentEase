import { useEffect, useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import {
    Card,
    CardBody,
    Typography,
    Button,
} from "@material-tailwind/react";
import { useNavigate } from "react-router-dom";

function RenterNotifications() {
    const [notifications, setNotifications] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const token = Cookies.get("renterToken");
        if (!token) {
            navigate("/renter-login");
            return;
        }

        const fetchNotifications = async () => {
            try {
                setIsLoading(true);
                const token = Cookies.get("renterToken");
        
                const response = await axios.get("http://localhost:8080/payment_reminders", {
                    headers: { Authorization: `Bearer ${token}` },
                });
        
                const allReminders = Array.isArray(response.data) ? response.data : [];
        
                const approvedReminders = allReminders
                    .filter(r => r.approvalStatus === "approved")
                    .sort((a, b) => {
                        
                        return b.reminderId - a.reminderId;
                      });
        
                const latest = approvedReminders[0];
        
                const notificationsData = latest ? [{
                    id: latest.reminderId,
                    roomId: latest.room.roomId,
                    ownerName: latest.owner?.username || "Unknown Owner",
                    status: "unread",
                    message: `✅ Your booking for Room ${latest.room.unitName} has been approved by ${latest.owner?.username || "the owner"}.`,
                }] : [];
        
                setNotifications(notificationsData);
            } catch (error) {
                console.error("Error fetching latest approved reminder:", error.response?.data || error.message);
                setNotifications([]);
            } finally {
                setIsLoading(false);
            }
        };
        
        fetchNotifications();
    }, [navigate]);

    return (
        <div>
            <Typography variant="h2" color="blue-gray" className="mb-8">
                Notifications
            </Typography>
            {isLoading ? (
                <Typography className="text-center" color="gray">
                    Loading notifications...
                </Typography>
            ) : notifications.length === 0 ? (
                <Typography className="text-center" color="gray">
                    No approved bookings at the moment.
                </Typography>
            ) : (
                <div className="space-y-4">
                    {notifications.map((notification) => (
                        <Card
                            key={notification.id}
                            className={
                                notification.status === "unread"
                                    ? "border-l-4 border-blue-500 bg-blue-50"
                                    : ""
                            }
                        >
                            <CardBody>
                                <Typography color="blue-gray">
                                    {notification.message}
                                </Typography>
                                {notification.status === "unread" && (
                                    <Button
                                        variant="text"
                                        color="blue"
                                        className="mt-2"
                                        onClick={async () => {
                                            const token = Cookies.get("renterToken");
                                        
                                            try {
                                                const response = await axios.post("http://localhost:8080/rented_units/initiate-payment", {
                                                    roomId: notification.roomId,
                                                }, {
                                                    headers: { Authorization: `Bearer ${token}` }
                                                });
                                        
                                                const { paymentIntentId, clientKey, roomId } = response.data;
                                        
                                                navigate("/payment-method", {
                                                    state: { paymentIntentId, clientKey, roomId } // Pass roomId to PaymentMethodPage
                                                });
                                        
                                            } catch (error) {
                                                console.error("Failed to initiate payment:", error.response?.data || error.message);
                                                alert("Payment initiation failed.");
                                            }
                                        }}
                                    >
                                        Proceed to Pay
                                    </Button>
                                )}
                            </CardBody>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    );
}

export default RenterNotifications;