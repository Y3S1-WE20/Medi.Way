import React from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { FaUserCircle, FaClipboardList, FaCalendarCheck, FaHome, FaSignInAlt, FaSignOutAlt, FaUserPlus, FaUserMd } from 'react-icons/fa';
import '../index.css';

export default function Layout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();
  const [healthId, setHealthId] = React.useState(localStorage.getItem('healthId') || '');
  const [doctorId, setDoctorId] = React.useState(localStorage.getItem('doctorId') || '');
  const isAdmin = location.pathname.startsWith('/admin');
  const isDoctor = location.pathname.startsWith('/doctor');

  React.useEffect(() => {
    // Sync auth state on route change
    setHealthId(localStorage.getItem('healthId') || '');
    setDoctorId(localStorage.getItem('doctorId') || '');
  }, [location]);

  function logout(){
    localStorage.removeItem('healthId');
    setHealthId('');
    navigate('/login');
  }

  function doctorLogout(){
    localStorage.removeItem('doctorId');
    setDoctorId('');
    navigate('/doctor/login');
  }

  return (
    <div className="app">
      <header className="header">
        <div className="brand">
          <img src="/logo d.png" alt="MediWay logo" style={{height:50, width:'auto'}}/>
          <span></span>
        </div>
        {isDoctor && (
          <div className="header-page-title">Doctor Dashboard</div>
        )}
        <nav>
          <NavLink to="/" className={({isActive})=> isActive? 'active' : undefined}><FaHome/> Home</NavLink>
          <NavLink to="/appointments" className={({isActive})=> isActive? 'active' : undefined}><FaCalendarCheck/> Appointments</NavLink>
          {healthId && (
            <NavLink to="/profile" className={({isActive})=> isActive? 'active' : undefined}><FaUserCircle/> Profile</NavLink>
          )}
          <NavLink to="/admin/appointments" className={({isActive})=> isActive? 'active' : undefined}><FaClipboardList/> Admin</NavLink>
          {!healthId && !doctorId && (
            <>
              <NavLink to="/register" className={({isActive})=> isActive? 'active' : undefined}><FaUserPlus/> Register</NavLink>
              <NavLink to="/login" className={({isActive})=> isActive? 'active' : undefined}><FaSignInAlt/> Login</NavLink>
              <NavLink to="/doctor/login" className={({isActive})=> isActive? 'active' : undefined}><FaUserMd/> Doctor Login</NavLink>
            </>
          )}
          {doctorId && (
            <NavLink to="/doctor/profile" className={({isActive})=> isActive? 'active' : undefined}><FaUserMd/> Doctor</NavLink>
          )}
          {healthId && (
            <button className="btn small icon" onClick={logout} style={{marginLeft:12}}><FaSignOutAlt/> Logout</button>
          )}
          {doctorId && (
            <button className="btn small icon" onClick={doctorLogout} style={{marginLeft:12}}><FaSignOutAlt/> Doctor Logout</button>
          )}
        </nav>
      </header>
  <main className={`main ${(isAdmin || isDoctor) ? 'admin-main' : ''}`}>{children}</main>
      <footer className="footer">© {new Date().getFullYear()} MediWay Smart Healthcare</footer>
    </div>
  );
}
