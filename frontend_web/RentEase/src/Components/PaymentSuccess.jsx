import { useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";

function PaymentSuccess() {
  const location = useLocation();
  const query = new URLSearchParams(location.search);
  const paymentIntentId = query.get("payment_intent_id");

  const [status, setStatus] = useState("");
  const [amount, setAmount] = useState(0);
  const [description, setDescription] = useState("");

  useEffect(() => {
    const fetchPaymentDetails = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/payments/intent/${paymentIntentId}`
        );

        const data = response.data.data.attributes;
        setStatus(data.status);
        setAmount(data.amount);
        setDescription(data.description);
      } catch (error) {
        console.error("Failed to fetch payment intent:", error.response?.data || error.message);
        setStatus("Unknown");
      }
    };

    if (paymentIntentId) {
      fetchPaymentDetails();
    }
  }, [paymentIntentId]);

  return (
    <div className="p-10 text-center">
      <h1 className="text-2xl font-bold">🎉 Payment {status === "succeeded" ? "Success" : "Status"}</h1>
      {status && (
        <p className="mt-4 text-lg">
          Status: <strong className={status === "succeeded" ? "text-green-600" : "text-orange-600"}>{status}</strong>
        </p>
      )}
      {amount > 0 && (
        <p className="mt-2">Amount Paid: <strong>₱{(amount / 100).toFixed(2)}</strong></p>
      )}
      {description && (
        <p className="mt-1 text-sm text-gray-500">Description: {description}</p>
      )}
      {paymentIntentId && (
        <p className="mt-4 text-sm text-gray-500">
          Payment Intent ID: <code>{paymentIntentId}</code>
        </p>
      )}
    </div>
  );
}

export default PaymentSuccess;
