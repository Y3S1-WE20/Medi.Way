import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { FaClipboardList, FaUserNurse, FaChartPie } from 'react-icons/fa';

export default function AdminLayout(){
  const navigate = useNavigate();

  React.useEffect(()=>{
    const isAdmin = localStorage.getItem('isAdmin');
    if (!isAdmin) {
      navigate('/admin/login');
    }
  }, [navigate]);

  return (
    <div style={{display:'grid', gridTemplateColumns:'240px 1fr', gap:16, width:'100%'}}>
      <aside className="card" style={{height:'fit-content', position:'sticky', top:20, alignSelf:'start'}}>
        <div style={{fontWeight:800, marginBottom:12}}>Admin Panel</div>
        <nav style={{display:'grid', gap:8}}>
          <NavLink to="appointments" className={({isActive})=>`chip ${isActive?'active':''}`} style={{padding:12, display:'flex', alignItems:'center', gap:10}}>
            <FaClipboardList/> Appointments
          </NavLink>
          <NavLink to="doctors" className={({isActive})=>`chip ${isActive?'active':''}`} style={{padding:12, display:'flex', alignItems:'center', gap:10}}>
            <FaUserNurse/> Doctors
          </NavLink>
          <NavLink to="reports" className={({isActive})=>`chip ${isActive?'active':''}`} style={{padding:12, display:'flex', alignItems:'center', gap:10}}>
            <FaChartPie/> Reports
          </NavLink>
        </nav>
      </aside>
      <section className="admin-content" style={{minWidth:0}}>
        <Outlet/>
      </section>
    </div>
  );
}
