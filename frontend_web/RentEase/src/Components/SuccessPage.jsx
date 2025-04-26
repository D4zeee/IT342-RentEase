// SuccessPage.jsx
import { useLocation, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";

export default function SuccessPage() {
  const location = useLocation();
  const [paymentInfo, setPaymentInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  // Extract payment_intent_id from URL if you pass it (optional improvement)
  const searchParams = new URLSearchParams(location.search);
  const paymentIntentId = searchParams.get("payment_intent_id"); 

  useEffect(() => {
    if (paymentIntentId) {
      axios.get(`http://localhost:8080/payments/intent/${paymentIntentId}`)
        .then(response => {
          setPaymentInfo(response.data.data.attributes);
          setLoading(false);
        })
        .catch(error => {
          console.error("Error fetching payment details:", error);
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, [paymentIntentId]);

  if (loading) {
    return <div className="flex justify-center items-center h-screen text-xl">Loading payment info...</div>;
  }

  return (
    <div className="flex flex-col justify-center items-center min-h-screen p-6">
      <h1 className="text-4xl font-bold mb-6">🎉 Payment Successful!</h1>

      {paymentInfo ? (
        <div className="text-center space-y-4">
          <p className="text-xl">Amount Paid: <strong>₱{(paymentInfo.amount / 100).toFixed(2)}</strong></p>
          <p className="text-lg">Payment Status: <strong>{paymentInfo.status}</strong></p>
          <p className="text-md text-gray-600">Payment Intent ID: {paymentIntentId}</p>
        </div>
      ) : (
        <p className="text-lg text-center">Payment processed successfully!</p>
      )}

      <Link to="/" className="mt-8 px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-lg">
        Go Back Home
      </Link>
    </div>
  );
}
