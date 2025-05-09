"use client"

import { useEffect, useState } from "react"
import { useLocation } from "react-router-dom"
import axios from "axios"
import Cookies from "js-cookie"
import {
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Typography,
  Button,
  Tabs,
  TabsHeader,
  TabsBody,
  Tab,
  TabPanel,
  List,
  ListItem,
  ListItemPrefix,
  Avatar,
} from "@material-tailwind/react"
import {
  CheckCircleIcon,
  ClockIcon,
  CreditCardIcon,
  CalendarIcon,
  HomeIcon,
  ReceiptIcon,
  ArrowLeftIcon as ArrowPathIcon,
} from "lucide-react"

const Payments = () => {
  const location = useLocation()
  const [payment, setPayment] = useState(null)
  const [paymentHistory, setPaymentHistory] = useState([])
  const [rooms, setRooms] = useState([])
  const [activeTab, setActiveTab] = useState("history")
  const [isLoading, setIsLoading] = useState(true)

  const query = new URLSearchParams(location.search)
  const paymentIntentId = query.get("payment_intent_id")

  useEffect(() => {
    const token = Cookies.get("token")
    setIsLoading(true)

    // Fetch owner and their rooms
    axios
      .get(`${import.meta.env.VITE_API_BASE_URL}/owners/current-user`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        const ownerId = res.data.ownerId
        return axios.get(`${import.meta.env.VITE_API_BASE_URL}/rooms/owner/${ownerId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
      })
      .then((res) => {
        setRooms(res.data)
      })
      .catch((err) => {
        console.error("Failed to load rooms for owner:", err)
      })

    // Fetch payment details from the database using paymentIntentId
    if (paymentIntentId) {
      axios
        .get(`${import.meta.env.VITE_API_BASE_URL}/payments/by-intent-id/${paymentIntentId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((res) => {
          const data = res.data
          setPayment({
            paymentId: data.paymentId,
            status: data.status,
            amount: data.amount,
            description: "RentEase Payment",
            paymentIntentId: data.paymentIntentId,
            paymentMethod: data.paymentMethod,
          })
          setActiveTab("receipt")
        })
        .catch((err) => {
          console.error("Failed to fetch payment from database:", err.response?.data || err.message)
        })
    }

    // Fetch payment history from the backend
    axios
      .get(`${import.meta.env.VITE_API_BASE_URL}/api/payment-history`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setPaymentHistory(res.data)
        setIsLoading(false)
      })
      .catch((err) => {
        console.error("Failed to fetch payment history:", err.response?.data || err.message)
        setIsLoading(false)
      })
  }, [paymentIntentId])

  // Format date to be more readable
  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <Tabs value={activeTab} onChange={(value) => setActiveTab(value)}>
        <TabsBody>
          <TabPanel value="history">
            <Card className="overflow-hidden shadow-lg border border-blue-gray-100">
              <CardHeader
                floated={false}
                shadow={false}
                color="transparent"
                className="bg-gradient-to-r from-[#5885AF] to-[#274472] m-0 p-6 rounded-b-none"
              >
                <div className="flex items-center justify-between">
                  <Typography variant="h5" color="white">
                    Payment History
                  </Typography>
                  <Button
                    size="sm"
                    variant="text"
                    color="white"
                    className="flex items-center gap-2"
                    onClick={() => window.location.reload()}
                  >
                    <ArrowPathIcon className="h-4 w-4" />
                    Refresh
                  </Button>
                </div>
              </CardHeader>
              <CardBody className="p-0">
                {isLoading ? (
                  <div className="flex justify-center items-center h-64">
                    <Typography variant="h6" color="blue-gray">
                      Loading payment history...
                    </Typography>
                  </div>
                ) : paymentHistory.length > 0 ? (
                  <div className="overflow-x-auto">
                    <table className="w-full min-w-max table-auto text-left">
                      <thead>
                        <tr>
                          {["ID", "Rental Fee", "Room ID", "Date", "Unit Name", "Status"].map((head) => (
                            <th key={head} className="border-b border-blue-gray-100 bg-blue-gray-50 p-4">
                              <Typography
                                variant="small"
                                color="blue-gray"
                                className="font-bold leading-none opacity-70"
                              >
                                {head}
                              </Typography>
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {paymentHistory.map((history, index) => {
                          const isLast = index === paymentHistory.length - 1
                          const classes = isLast ? "p-4" : "p-4 border-b border-blue-gray-50"

                          return (
                            <tr key={history.payment_history_id} className="hover:bg-blue-gray-50/50">
                              <td className={classes}>
                                <Typography variant="small" color="blue-gray" className="font-normal">
                                  {history.payment_history_id}
                                </Typography>
                              </td>
                              <td className={classes}>
                                <Typography variant="small" color="blue-gray" className="font-normal">
                                  ₱{history.rentalFee.toFixed(2)}
                                </Typography>
                              </td>
                              <td className={classes}>
                                <Typography variant="small" color="blue-gray" className="font-normal">
                                  {history.roomId}
                                </Typography>
                              </td>
                              <td className={classes}>
                                <Typography variant="small" color="blue-gray" className="font-normal">
                                  {formatDate(history.startDate)}
                                </Typography>
                              </td>
                              <td className={classes}>
                                <div className="flex items-center gap-3">
                                  <HomeIcon className="h-4 w-4 text-blue-gray-500" />
                                  <Typography variant="small" color="blue-gray" className="font-normal">
                                    {history.unitName}
                                  </Typography>
                                </div>
                              </td>
                              <td className={classes}>
                                <Typography variant="small" className="font-medium text-green-500">
                                  Successful
                                </Typography>
                              </td>
                            </tr>
                          )
                        })}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="flex justify-center items-center h-64">
                    <Typography variant="h6" color="blue-gray">
                      No payment history available.
                    </Typography>
                  </div>
                )}
              </CardBody>
              <CardFooter className="flex items-center justify-between border-t border-blue-gray-50 p-4">
                <Typography variant="small" color="blue-gray" className="font-normal">
                  Showing {paymentHistory.length} payments
                </Typography>
              </CardFooter>
            </Card>
          </TabPanel>

          {payment && (
            <TabPanel value="receipt">
              <Card className="overflow-hidden shadow-lg border border-blue-gray-100">
                <CardHeader
                  floated={false}
                  shadow={false}
                  color="transparent"
                  className="bg-gradient-to-r from-[#5885AF] to-[#274472] m-0 p-6 rounded-b-none"
                >
                  <Typography variant="h5" color="white" className="flex items-center gap-2">
                    <ReceiptIcon className="h-6 w-6" />
                    Payment Receipt
                  </Typography>
                </CardHeader>
                <CardBody className="p-6">
                  <List>
                    <ListItem className="p-0 py-2">
                      <ListItemPrefix>
                        <Avatar variant="circular" className="p-1 bg-green-100">
                          <CheckCircleIcon
                            className={`h-5 w-5 ${payment.status === "Paid" || payment.status === "Successful" ? "text-green-500" : "text-orange-500"}`}
                          />
                        </Avatar>
                      </ListItemPrefix>
                      <div>
                        <Typography variant="h6" color="blue-gray">
                          Status
                        </Typography>
                        <Typography
                          variant="small"
                          color={payment.status === "Paid" || payment.status === "Successful" ? "green" : "orange"}
                          className="font-medium"
                        >
                          {payment.status === "Paid" ? "Successful" : payment.status}
                        </Typography>
                      </div>
                    </ListItem>

                    <ListItem className="p-0 py-2">
                      <ListItemPrefix>
                        <Avatar variant="circular" className="p-1 bg-blue-100">
                          <CreditCardIcon className="h-5 w-5 text-blue-500" />
                        </Avatar>
                      </ListItemPrefix>
                      <div>
                        <Typography variant="h6" color="blue-gray">
                          Amount
                        </Typography>
                        <Typography variant="small" color="blue-gray" className="font-medium">
                          ₱{payment.amount.toFixed(2)}
                        </Typography>
                      </div>
                    </ListItem>

                    <ListItem className="p-0 py-2">
                      <ListItemPrefix>
                        <Avatar variant="circular" className="p-1 bg-blue-gray-100">
                          <ClockIcon className="h-5 w-5 text-blue-gray-500" />
                        </Avatar>
                      </ListItemPrefix>
                      <div>
                        <Typography variant="h6" color="blue-gray">
                          Payment Method
                        </Typography>
                        <Typography variant="small" color="blue-gray" className="font-medium">
                          {payment.paymentMethod}
                        </Typography>
                      </div>
                    </ListItem>

                    <ListItem className="p-0 py-2">
                      <ListItemPrefix>
                        <Avatar variant="circular" className="p-1 bg-purple-100">
                          <CalendarIcon className="h-5 w-5 text-purple-500" />
                        </Avatar>
                      </ListItemPrefix>
                      <div>
                        <Typography variant="h6" color="blue-gray">
                          Description
                        </Typography>
                        <Typography variant="small" color="blue-gray" className="font-medium">
                          {payment.description}
                        </Typography>
                      </div>
                    </ListItem>
                  </List>

                  <div className="mt-4 p-4 bg-blue-gray-50 rounded-lg">
                    <Typography variant="small" color="blue-gray" className="font-medium">
                      Payment ID: {payment.paymentId}
                    </Typography>
                    <Typography variant="small" color="blue-gray" className="font-medium mt-1">
                      Reference ID: {payment.paymentIntentId}
                    </Typography>
                  </div>
                </CardBody>
                <CardFooter className="pt-0 border-t border-blue-gray-50 p-4">
                  <Button
                    fullWidth
                    className="bg-gradient-to-r from-[#5885AF] to-[#274472]"
                    onClick={() => window.print()}
                  >
                    Print Receipt
                  </Button>
                </CardFooter>
              </Card>
            </TabPanel>
          )}
        </TabsBody>
      </Tabs>
    </div>
  )
}

export default Payments