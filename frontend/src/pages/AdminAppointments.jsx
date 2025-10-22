import React from 'react';
import { FaClipboardList, FaCheck, FaTimes } from 'react-icons/fa';

export default function AdminAppointments(){
  const [list, setList] = React.useState([]);

  async function load(){
    const res = await fetch('http://localhost:8080/api/admin/appointments');
    setList(await res.json());
  }

  async function confirm(id){ await fetch(`http://localhost:8080/api/admin/appointments/${id}/confirm`, { method:'POST' }); load(); }
  async function reject(id){ await fetch(`http://localhost:8080/api/admin/appointments/${id}/reject`, { method:'POST' }); load(); }

  React.useEffect(()=>{ load(); },[]);

  return (
    <div className="card">
      <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaClipboardList/> Admin: Appointments</h2>
      {/* Table loads automatically on mount; no manual refresh needed */}
      <div style={{marginTop:16, overflowX:'auto'}}>
        <table className="table" style={{tableLayout:'auto'}}>
          <thead>
            <tr style={{textAlign:'left'}}>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>ID</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Patient</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Health ID</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Doctor</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Specialization</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Date</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Time</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Status</th>
              <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {list.map(a => (
              <tr key={a.id}>
                <td style={{padding:'8px 6px'}}>{a.id}</td>
                <td style={{padding:'8px 6px'}}>{a.patient?.fullName || '-'}</td>
                <td style={{padding:'8px 6px'}}>{a.patient?.healthId || '-'}</td>
                <td style={{padding:'8px 6px'}}>{a.doctor?.name || '-'}</td>
                <td style={{padding:'8px 6px'}}>{a.doctor?.specialization || '-'}</td>
                <td style={{padding:'8px 6px'}}>{a.date}</td>
                <td style={{padding:'8px 6px'}}>{a.time}</td>
                <td style={{padding:'8px 6px'}}>
                  <span className={`badge ${
                    a.status==='CONFIRMED' ? 'confirmed' :
                    a.status==='REJECTED' ? 'rejected' :
                    a.status==='CANCELLED' ? 'cancelled' : 'pending'}`}>{a.status}</span>
                </td>
                <td style={{padding:'8px 6px', whiteSpace:'nowrap'}}>
                  <button className="btn small icon" onClick={()=>confirm(a.id)} style={{background:'#e9f5ff', borderColor:'#c7ddff', marginRight:8}}><FaCheck/> Confirm</button>
                  <button className="btn small icon" onClick={()=>reject(a.id)} style={{background:'#fee2e2', borderColor:'#fecaca', color:'#991b1b'}}><FaTimes/> Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
