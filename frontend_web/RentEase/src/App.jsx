import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LandingPage from './Components/LandingPage';
import LoginPage from './Components/LoginPage';
import RegisterPage from './Components/RegisterPage';
import Dashboard from './Components/Dashboard';
import Layout from './Components/Layout';
import Rooms from './Components/Rooms';
import Payments from './Components/Payment';
import Reminder from './Components/Reminder';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/Register" element={<RegisterPage />} />

        <Route path="/" element={<Layout />}>
        <Route path='/dashboard' element={<Dashboard />} />
        <Route path='/rooms' element={<Rooms />} />
        <Route path='/payments' element={<Payments />} />
        <Route path='/reminder' element={<Reminder />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;