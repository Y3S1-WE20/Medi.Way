import React from 'react';
import { FaUserCircle, FaCopy, FaTimesCircle, FaSyncAlt, FaQrcode, FaCalendarAlt, FaClock } from 'react-icons/fa';

export default function Profile(){
  const [healthId, setHealthId] = React.useState(localStorage.getItem('healthId') || '');
  const [list, setList] = React.useState([]);
  const [date, setDate] = React.useState('');
  const [time, setTime] = React.useState('');
  const [patient, setPatient] = React.useState(null);
  const [records, setRecords] = React.useState([]);

  const load = React.useCallback(async () => {
    if(!healthId) return;
    const [pRes, aRes, rRes] = await Promise.all([
      fetch(`http://localhost:8080/api/patients/${encodeURIComponent(healthId)}`),
      fetch(`http://localhost:8080/api/appointments/mine?healthId=${encodeURIComponent(healthId)}`),
      fetch(`http://localhost:8080/api/patients/${encodeURIComponent(healthId)}/records`)
    ]);
    if (pRes.ok) setPatient(await pRes.json());
    if (aRes.ok) setList(await aRes.json());
    if (rRes.ok) setRecords(await rRes.json());
  }, [healthId]);

  React.useEffect(()=>{ if(healthId) load(); },[healthId, load]);

  async function reschedule(id){
    const qp = [];
    if(date) qp.push(`date=${date}`);
    if(time) qp.push(`time=${time}`);
    if(qp.length===0) return;
    await fetch(`http://localhost:8080/api/appointments/${id}?${qp.join('&')}`, { method:'PUT' });
    setDate(''); setTime('');
    load();
  }

  async function cancel(id){
    await fetch(`http://localhost:8080/api/appointments/${id}`, { method:'DELETE' });
    load();
  }

  const copyId = async () => {
    if(!patient?.healthId) return;
    try { await navigator.clipboard.writeText(patient.healthId); } catch {}
  }

  const qrUrl = patient?.healthId ? `http://localhost:8080/api/patients/${encodeURIComponent(patient.healthId)}/qr` : '';

  return (
    <div className="card section">
      <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaUserCircle/> My Profile & Appointments</h2>
      {!healthId && (
        <div className="toolbar" style={{marginTop:10}}>
          <div className="field" style={{flex:1}}>
            <span className="subtle">Health ID</span>
            <input className="input" value={healthId} onChange={e=>setHealthId(e.target.value)} placeholder="Enter your Health ID" />
          </div>
          <button className="btn" onClick={load}>Load</button>
        </div>
      )}
      {/* Two-column layout: left = patient preview + reschedule, right = scrollable records and appointments */}
      <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:16, alignItems:'start'}}>
        <div>
          {patient && (
            <div className="card" style={{maxWidth:'100%', marginTop:12}}>
              <div style={{display:'grid', gridTemplateColumns:'1fr auto', gap:20, alignItems:'center'}}>
                <div>
                  <div style={{fontWeight:800, fontSize:18}}>{patient.fullName}</div>
                  <div className="subtle">{patient.email}</div>
                  <div style={{display:'flex', alignItems:'center', gap:8, marginTop:8}}>
                    <span className="badge"><FaUserCircle/> {patient.healthId}</span>
                    <button className="btn small icon" onClick={copyId}><FaCopy/> Copy</button>
                  </div>
                  {patient.phone && <div className="subtle" style={{marginTop:8}}>Phone: {patient.phone}</div>}
                  {patient.address && <div className="subtle">Address: {patient.address}</div>}
                </div>
                {qrUrl && (
                  <div style={{textAlign:'center'}}>
                    <div className="subtle" style={{marginBottom:6}}><FaQrcode/> Health ID QR</div>
                    <img className="qr" src={qrUrl} alt="Health ID QR" width={120} height={120}/>
                  </div>
                )}
              </div>
            </div>
          )}
          <div className="spacer"/>
          <div className="card" style={{maxWidth:'100%'}}>
            <div style={{fontWeight:700, marginBottom:10}}>Reschedule</div>
            <div className="form-grid">
              <label>New Date<input type="date" value={date} onChange={e=>setDate(e.target.value)} /></label>
              <label>New Time<input type="time" value={time} onChange={e=>setTime(e.target.value)} /></label>
            </div>
            <div className="grid-buttons" style={{marginTop:8}}>
              {list.map(a => (
                <button key={`r-${a.id}`} className="btn small icon" onClick={()=>reschedule(a.id)}><FaSyncAlt/> Reschedule #{a.id}</button>
              ))}
            </div>
          </div>
        </div>
        <div>
          <div className="card" style={{maxWidth:'100%'}}>
            <div style={{fontWeight:700, marginBottom:10}}>Medical Records</div>
            {records.length===0 && <div className="subtle">No records yet.</div>}
            <div style={{maxHeight:380, overflowY:'auto', paddingRight:4}}>
              <div style={{display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(300px,1fr))', gap:12}}>
                {records.map(r => (
                  <div key={r.id} className="chip" style={{padding:16}}>
                    <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
                      <div style={{fontWeight:700}}>By Dr. {r.doctor?.name || '-'}</div>
                      <span className="subtle">{new Date(r.createdAt).toLocaleString()}</span>
                    </div>
                    {r.diagnosis && <div style={{marginTop:6}}><b>Diagnosis:</b> {r.diagnosis}</div>}
                    {r.prescriptions && <div style={{marginTop:6}}><b>Prescriptions:</b> {r.prescriptions}</div>}
                    {r.labNotes && <div style={{marginTop:6}}><b>Lab Notes:</b> {r.labNotes}</div>}
                    {r.comments && <div style={{marginTop:6}}><b>Comments:</b> {r.comments}</div>}
                  </div>
                ))}
              </div>
            </div>
          </div>
          <div className="spacer"/>
          <div className="card" style={{maxWidth:'100%'}}>
            <div style={{fontWeight:700, marginBottom:10}}>My Appointments</div>
            {list.length===0 && <div className="subtle">No appointments found.</div>}
            <div style={{maxHeight:380, overflowY:'auto', paddingRight:4}}>
              <div style={{display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(260px,1fr))', gap:12}}>
                {list.map(a => (
                  <div key={a.id} className="chip" style={{padding:16}}>
                    <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
                      <div style={{fontWeight:700}}>{a.doctor?.name || 'Doctor'}</div>
                      <span className={`badge ${
                        a.status==='CONFIRMED' ? 'confirmed' :
                        a.status==='REJECTED' ? 'rejected' :
                        a.status==='CANCELLED' ? 'cancelled' : 'pending'}`}>{a.status}</span>
                    </div>
                    <div className="subtle">{a.doctor?.specialization}</div>
                    <div style={{display:'flex', gap:10, marginTop:8}}>
                      <span className="badge"><FaCalendarAlt/> {a.date}</span>
                      <span className="badge"><FaClock/> {a.time}</span>
                    </div>
                    <div style={{display:'flex', gap:8, marginTop:10}}>
                      <button className="btn small icon" onClick={()=>cancel(a.id)} style={{background:'#fee2e2', borderColor:'#fecaca', color:'#991b1b'}}><FaTimesCircle/> Cancel</button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
