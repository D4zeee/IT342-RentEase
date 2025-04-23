import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";  // Import js-cookie

const PrivateRoute = ({ children }) => {
  // Check if the token exists in cookies
  const token = Cookies.get("token");

  // If there's no token, redirect the user to the login page
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // If the token exists, render the children (protected content)
  return children;
};

export default PrivateRoute;
