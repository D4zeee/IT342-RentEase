import { useLocation } from 'react-router-dom'

function PaymentSuccess() {
  const location = useLocation()
  const query = new URLSearchParams(location.search)
  const paymentIntentId = query.get("payment_intent_id")

  return (
    <div className="p-10 text-center">
      <h1 className="text-2xl font-bold">🎉 Payment Success!</h1>
      <p className="mt-4">Your payment has been processed.</p>
      {paymentIntentId && (
        <p className="mt-2 text-sm text-gray-600">
          Payment Intent ID: <code>{paymentIntentId}</code>
        </p>
      )}
    </div>
  )
}

export default PaymentSuccess
