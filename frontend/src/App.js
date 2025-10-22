import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './index.css';
import Layout from './components/Layout';
import Home from './pages/Home';
import Register from './pages/Register';
import Login from './pages/Login';
import Success from './pages/Success';
import Appointments from './pages/Appointments';
import Profile from './pages/Profile';
import AdminDoctors from './pages/AdminDoctors';
import AdminAppointments from './pages/AdminAppointments';
import AdminLayout from './pages/AdminLayout';
import AdminReports from './pages/AdminReports';
import DoctorLogin from './pages/DoctorLogin';
import DoctorSignup from './pages/DoctorSignup';
import DoctorProfile from './pages/DoctorProfile';

export default function App(){
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/register" element={<Register/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/doctor/login" element={<DoctorLogin/>} />
          <Route path="/doctor/signup" element={<DoctorSignup/>} />
          <Route path="/doctor/profile" element={<DoctorProfile/>} />
          <Route path="/success" element={<Success/>} />
          <Route path="/appointments" element={<Appointments/>} />
          <Route path="/profile" element={<Profile/>} />
          <Route path="/admin" element={<AdminLayout/>}>
            <Route path="appointments" element={<AdminAppointments/>} />
            <Route path="doctors" element={<AdminDoctors/>} />
            <Route path="reports" element={<AdminReports/>} />
          </Route>
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
