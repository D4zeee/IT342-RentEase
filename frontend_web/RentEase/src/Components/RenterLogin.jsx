import { useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";

function RenterLogin() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8080/api/renters/login", { email, password });
            const { jwt } = response.data;
            Cookies.set("renterToken", jwt, { expires: 1 });
    
            // ✅ Immediately fetch the renter info
            const renterInfoResponse = await axios.get("http://localhost:8080/api/renters/current", {
                headers: { Authorization: `Bearer ${jwt}` },
            });
            const { renterId, name } = renterInfoResponse.data;
            console.log("Logged in renterId:", renterId); // 🔥 You can see in console
            Cookies.set("renterId", renterId, { expires: 1 });
            Cookies.set("renterName", name, { expires: 1 });
    
            navigate("/renter-dashboard");
        } catch (error) {
            console.error("Login error:", error.response?.data || error.message);
            alert("Login failed: " + (error.response?.data || error.message));
        }
    };
    

    return (
        <form onSubmit={handleSubmit}>
            <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email"
                required
            />
            <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Password"
                required
            />
            <button type="submit">Login</button>
        </form>
    );
}

export default RenterLogin;