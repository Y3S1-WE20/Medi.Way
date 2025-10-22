import React from 'react';
import { FaUserMd, FaCalendarAlt, FaClock, FaCheck, FaQrcode, FaPhone, FaUserCircle, FaCheckCircle } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';

export default function Appointments(){
  // Patient info
  const [healthId, setHealthId] = React.useState(localStorage.getItem('healthId') || '');
  const [patient, setPatient] = React.useState(null);
  // Doctor & schedule
  const [doctors, setDoctors] = React.useState([]);
  const [selectedDoctor, setSelectedDoctor] = React.useState(null);
  const [date, setDate] = React.useState('');
  const [slots, setSlots] = React.useState([]);
  const [selectedSlot, setSelectedSlot] = React.useState('');
  const [status, setStatus] = React.useState('');
  const [showSuccess, setShowSuccess] = React.useState(false);
  const navigate = useNavigate();

  // Load patient info when healthId available
  const loadPatient = React.useCallback(async () => {
    if(!healthId) { setPatient(null); return; }
    const res = await fetch(`http://localhost:8080/api/patients/${encodeURIComponent(healthId)}`);
    if(res.ok){ setPatient(await res.json()); }
  }, [healthId]);

  React.useEffect(()=>{ loadPatient(); }, [loadPatient]);

  // Load all doctors
  React.useEffect(() => {
    (async () => {
      const res = await fetch('http://localhost:8080/api/doctors');
      if(res.ok) setDoctors(await res.json());
    })();
  }, []);

  // Load slots when doctor/date changes
  React.useEffect(() => {
    (async () => {
      setSlots([]);
      setSelectedSlot('');
      if(!selectedDoctor || !date) return;
      const res = await fetch(`http://localhost:8080/api/appointments/slots?doctorId=${selectedDoctor}&date=${date}`);
      if(res.ok) setSlots(await res.json());
    })();
  }, [selectedDoctor, date]);

  async function book(){
    setStatus('');
    if(!healthId || !selectedDoctor || !date || !selectedSlot){
      setStatus('Please complete all fields.');
      return;
    }
    const res = await fetch(`http://localhost:8080/api/appointments/book?healthId=${encodeURIComponent(healthId)}&doctorId=${selectedDoctor}&date=${date}&time=${selectedSlot}`, { method:'POST' });
    if(!res.ok) { setStatus('Booking failed'); return; }
    setShowSuccess(true);
  }

  const today = new Date().toISOString().slice(0,10);
  const qrUrl = healthId ? `http://localhost:8080/api/patients/${encodeURIComponent(healthId)}/qr` : '';

  React.useEffect(()=>{
    if(!showSuccess) return;
    const t = setTimeout(()=> navigate('/profile'), 1800);
    return ()=> clearTimeout(t);
  },[showSuccess, navigate]);

  return (
    <>
    <div className="card section">
      <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaCalendarAlt/> Book Appointment</h2>
  {status && <div className="error" style={{background:'#e8f7ee', color:'#065f46', border:'1px solid #c7f0d7'}}>{status}</div>}

      {/* 1. Patient Information */}
      <div className="card" style={{maxWidth:'100%', marginTop:12}}>
        <div style={{fontWeight:700, marginBottom:10}}>1. Patient Information</div>
        <div className="form-grid">
          <label>Health ID<input value={healthId} onChange={e=>setHealthId(e.target.value)} placeholder="Enter your Health ID"/></label>
          <div style={{display:'flex', alignItems:'center', gap:12}}>
            <span className="badge"><FaQrcode/> Health QR</span>
            {qrUrl && <img className="qr" src={qrUrl} alt="Health QR" width={90} height={90}/>}          
          </div>
        </div>
        {patient && (
          <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:12, marginTop:10}}>
            <div className="chip" style={{display:'flex', alignItems:'center', gap:10}}><FaUserCircle/> Full Name: <b>{patient.fullName}</b></div>
            <div className="chip" style={{display:'flex', alignItems:'center', gap:10}}><FaPhone/> Contact: <b>{patient.phone || '-'}</b></div>
          </div>
        )}
      </div>

      {/* 2. Appointment Details */}
      <div className="spacer"/>
      <div className="card" style={{maxWidth:'100%'}}>
        <div style={{fontWeight:700, marginBottom:10}}>2. Appointment Details</div>
        <div style={{marginBottom:8, fontWeight:600}}>Select Doctor</div>
        <div style={{display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(260px,1fr))', gap:14}}>
          {doctors.map(d => (
            <div key={d.id} className={`chip ${selectedDoctor===d.id ? 'active':''}`} onClick={()=>setSelectedDoctor(d.id)} style={{padding:14}}>
              <div style={{display:'flex', alignItems:'center', gap:12}}>
                <img src={`http://localhost:8080/api/doctors/${d.id}/photo`} alt="" width={48} height={48} style={{objectFit:'cover', borderRadius:10, border:'1px solid var(--border)'}} onError={(e)=>{e.currentTarget.style.visibility='hidden'}}/>
                <div>
                  <div style={{fontWeight:700}}>{d.name}</div>
                  <div className="subtle">{d.specialization}</div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="form-grid" style={{marginTop:12}}>
          <label>Appointment Date<input type="date" min={today} value={date} onChange={e=>setDate(e.target.value)} /></label>
          <div>
            <div className="subtle" style={{marginBottom:6}}><FaClock/> Select Time Slot</div>
            <div className="grid-buttons">
              {slots.map(s => (
                <button key={s} className={`btn small icon ${selectedSlot===s? 'soft':''}`} onClick={()=>setSelectedSlot(s)}><FaCheck/> {s}</button>
              ))}
              {(!selectedDoctor || !date) && <div className="subtle">Choose a doctor and date to see available slots</div>}
              {selectedDoctor && date && slots.length===0 && <div className="subtle">No slots available for this day</div>}
            </div>
          </div>
        </div>

        <div style={{display:'flex', justifyContent:'flex-end', marginTop:16}}>
          <button className="btn primary icon" onClick={book} disabled={!healthId || !patient || !selectedDoctor || !date || !selectedSlot}><FaCalendarAlt/> Submit</button>
        </div>
      </div>
    </div>
    {showSuccess && (
      <div className="modal-overlay" role="dialog" aria-modal="true">
        <div className="modal-card">
          <div style={{display:'flex', alignItems:'center', gap:10}}>
            <FaCheckCircle color="#16a34a" size={24}/>
            <div style={{fontWeight:800}}>Appointment booked!</div>
          </div>
          <div className="subtle" style={{marginTop:8}}>We’ll redirect you to your profile to view details.</div>
          <div style={{display:'flex', gap:10, justifyContent:'flex-end', marginTop:16}}>
            <button className="btn" onClick={()=>setShowSuccess(false)}>Stay</button>
            <button className="btn primary" onClick={()=>navigate('/profile')}>Go to Profile</button>
          </div>
        </div>
      </div>
    )}
    </>
  );
}
