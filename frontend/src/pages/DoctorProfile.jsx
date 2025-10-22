import React from 'react';
import { FaUserMd, FaCalendarCheck } from 'react-icons/fa';

function Badge({ status }){
  const color = {
    PENDING:'#f59e0b',
    CONFIRMED:'#10b981',
    REJECTED:'#ef4444',
    CANCELLED:'#6b7280'
  }[status] || '#6b7280';
  return <span className="badge" style={{background: color, color:'#fff'}}>{status}</span>;
}

export default function DoctorProfile(){
  const doctorId = localStorage.getItem('doctorId');
  const [doctor, setDoctor] = React.useState(null);
  const [appointments, setAppointments] = React.useState([]);
  const [error, setError] = React.useState('');
  const [record, setRecord] = React.useState({ patientHealthId:'', diagnosis:'', prescriptions:'', labNotes:'', comments:'' });
  const [recStatus, setRecStatus] = React.useState('');
  const [appendMode, setAppendMode] = React.useState(true);
  const [diagTpl, setDiagTpl] = React.useState('');
  const [rxTpl, setRxTpl] = React.useState('');
  const [labTpl, setLabTpl] = React.useState('');
  const [viewHealthId, setViewHealthId] = React.useState('');
  const [patientRecords, setPatientRecords] = React.useState([]);
  const [viewError, setViewError] = React.useState('');

  const diagnosisTemplates = [
    { label: 'Select a diagnosis template...', value: '' },
    { label: 'URTI (Upper Respiratory Tract Infection)', value: 'Diagnosis: Upper Respiratory Tract Infection (URTI)\nSymptoms: Sore throat, nasal congestion, cough, low-grade fever\nExam: Mild pharyngeal erythema, lungs clear\nPlan: Symptomatic management; consider antibiotics only if bacterial suspected' },
    { label: 'Type 2 Diabetes Mellitus - Uncontrolled', value: 'Diagnosis: Type 2 Diabetes Mellitus - uncontrolled\nFindings: Elevated fasting glucose, HbA1c above target\nPlan: Optimize medications, reinforce diet/exercise, monitor glucose logs' },
    { label: 'Hypertension - Stage 2', value: 'Diagnosis: Hypertension - Stage 2\nBP persistently elevated above target\nPlan: Initiate/intensify antihypertensive therapy and lifestyle modification' },
    { label: 'Acute Gastroenteritis', value: 'Diagnosis: Acute Gastroenteritis\nSymptoms: Nausea, vomiting, diarrhea, abdominal cramps\nPlan: Oral rehydration, antiemetic PRN, observe warning signs' },
    { label: 'Migraine without aura', value: 'Diagnosis: Migraine without aura\nFeatures: Unilateral throbbing headache, photophobia/phonophobia\nPlan: Trigger avoidance, acute abortive therapy, consider prophylaxis if frequent' }
  ];

  const prescriptionTemplates = [
    { label: 'Select a prescription template...', value: '' },
    { label: 'URTI regimen', value: 'Amoxicillin 500 mg PO TID x5 days (if bacterial suspected)\nCetirizine 10 mg PO HS x5 days\nSteam inhalation BID\nHydration and rest' },
    { label: 'Hypertension regimen', value: 'Amlodipine 5 mg PO OD\nLosartan 50 mg PO OD\nLow-salt DASH diet, exercise 150 min/week' },
    { label: 'T2DM regimen', value: 'Metformin 500 mg PO BID with meals\nGlimepiride 1 mg PO OD before breakfast\nDietary counseling and daily exercise' },
    { label: 'Migraine regimen', value: 'Sumatriptan 50 mg PO at onset, may repeat after 2 hours (max 200 mg/day)\nNaproxen 500 mg PO BID with food PRN pain\nAvoid known triggers' },
    { label: 'GERD regimen', value: 'Omeprazole 20 mg PO OD x14 days\nLifestyle: elevate head-end, avoid late meals, caffeine, spicy foods' }
  ];

  const labNotesTemplates = [
    { label: 'Select a lab-notes template...', value: '' },
    { label: 'Infection workup', value: 'Ordered: CBC, CRP, ESR\nNotes: Monitor trends, correlate clinically\nStatus: Pending' },
    { label: 'Diabetes monitoring', value: 'Ordered: FPG, HbA1c, Urine microalbumin\nGoal: HbA1c < 7% (individualize)\nPlan: Repeat in 3 months' },
    { label: 'Cardiovascular risk', value: 'Ordered: Lipid panel (TC, LDL, HDL, TG)\nAssess ASCVD risk and target LDL < 100 mg/dL (or per guideline)' },
    { label: 'Gastro workup', value: 'Ordered: Stool R/E, Ova & Parasite, Stool culture if indicated\nHydration status monitored' },
    { label: 'Neuro imaging', value: 'Imaging: MRI Brain without/with contrast\nIndication: Recurrent severe headaches\nAwait radiology report' }
  ];

  function applyTemplate(field, templateValue){
    if(!templateValue) return;
    setRecord(prev => ({
      ...prev,
      [field]: appendMode && prev[field] ? (prev[field] + '\n\n' + templateValue) : templateValue
    }));
  }

  React.useEffect(()=>{
    async function load(){
      try{
        setError('');
        if(!doctorId) return;
        const dRes = await fetch(`http://localhost:8080/api/doctors/${doctorId}`);
        if(!dRes.ok) throw new Error(await dRes.text());
        const d = await dRes.json();
        setDoctor(d);
        const aRes = await fetch(`http://localhost:8080/api/doctors/${doctorId}/appointments`);
        if(!aRes.ok) throw new Error(await aRes.text());
        const a = await aRes.json();
        setAppointments(a);
      }catch(err){ setError(err?.message || 'Failed to load'); }
    }
    load();
  }, [doctorId]);

  if(!doctorId){
    return <div className="card" style={{maxWidth:640, margin:'0 auto'}}>Please login as doctor.</div>
  }

  return (
    <>
      <div style={{
        position:'sticky',
        top:0,
        zIndex:5,
        display:'flex',
        width:'100%',
        justifyContent:'center',
        alignItems:'center',
        padding:'15px 12px',
        margin:'0 0 12px',
        background:'#fff',
        borderBottom:'1px solid #e5e7eb'
      }}>
        <h2 style={{display:'flex', alignItems:'center', gap:10, justifyContent:'center', textAlign:'center', margin:0}}></h2>
      </div>
      {error && <div className="error">{error}</div>}
      <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:16, alignItems:'start'}}>
        <div>
          {doctor && (
            <div className="card" style={{display:'flex', gap:16, alignItems:'center'}}>
              <img src={`http://localhost:8080/api/doctors/${doctorId}/photo`} alt="doctor" onError={(e)=>{e.currentTarget.style.display='none'}} style={{width:96,height:96,objectFit:'cover',borderRadius:12,border:'1px solid #e5e7eb'}}/>
              <div>
                <div style={{fontWeight:600, fontSize:18}}>{doctor.name}</div>
                <div style={{color:'#374151'}}>{doctor.email}</div>
                <div className="chip" style={{marginTop:6}}>{doctor.specialization}</div>
              </div>
            </div>
          )}
          <div style={{height:16}}/>
          <div className="card">
            <h3>Add Patient Medical Record</h3>
            {recStatus && <div className="error" style={{background:'#e8f7ee', color:'#065f46', border:'1px solid #c7f0d7'}}>{recStatus}</div>}
            <div className="form-grid">
              <label>Patient Health ID<input value={record.patientHealthId} onChange={e=>setRecord({...record, patientHealthId:e.target.value})} placeholder="Enter Health ID"/></label>
          <label>
            Diagnosis
            <textarea rows={3} placeholder="e.g., URTI; symptomatic management" value={record.diagnosis} onChange={e=>setRecord({...record, diagnosis:e.target.value})}/>
            <div style={{display:'flex', gap:8, marginTop:6, alignItems:'center'}}>
              <select value={diagTpl} onChange={e=>setDiagTpl(e.target.value)}>
                {diagnosisTemplates.map(t => (<option key={t.label} value={t.value}>{t.label}</option>))}
              </select>
              <button type="button" className="btn small" onClick={()=>applyTemplate('diagnosis', diagTpl)}>Apply</button>
            </div>
          </label>
          <label>
            Prescriptions
            <textarea rows={3} placeholder="Medications, dosage, duration" value={record.prescriptions} onChange={e=>setRecord({...record, prescriptions:e.target.value})}/>
            <div style={{display:'flex', gap:8, marginTop:6, alignItems:'center'}}>
              <select value={rxTpl} onChange={e=>setRxTpl(e.target.value)}>
                {prescriptionTemplates.map(t => (<option key={t.label} value={t.value}>{t.label}</option>))}
              </select>
              <button type="button" className="btn small" onClick={()=>applyTemplate('prescriptions', rxTpl)}>Apply</button>
            </div>
          </label>
          <label>
            Lab Notes
            <textarea rows={3} placeholder="Tests ordered, status, next steps" value={record.labNotes} onChange={e=>setRecord({...record, labNotes:e.target.value})}/>
            <div style={{display:'flex', gap:8, marginTop:6, alignItems:'center'}}>
              <select value={labTpl} onChange={e=>setLabTpl(e.target.value)}>
                {labNotesTemplates.map(t => (<option key={t.label} value={t.value}>{t.label}</option>))}
              </select>
              <button type="button" className="btn small" onClick={()=>applyTemplate('labNotes', labTpl)}>Apply</button>
            </div>
          </label>
          <label>Comments<textarea rows={2} value={record.comments} onChange={e=>setRecord({...record, comments:e.target.value})}/></label>
            </div>
            <div style={{marginTop:8, display:'flex', alignItems:'center', gap:12}}>
          <label style={{display:'flex', alignItems:'center', gap:6}}>
            <input type="checkbox" checked={appendMode} onChange={e=>setAppendMode(e.target.checked)} />
            Append templates (instead of replace)
          </label>
            </div>
            <div style={{marginTop:8}}>
          <button className="btn primary" onClick={async()=>{
            setRecStatus('');
            try{
              const res = await fetch('http://localhost:8080/api/records', {
                method:'POST',
                headers:{'Content-Type':'application/json'},
                body: JSON.stringify({
                  doctorId: Number(doctorId),
                  patientHealthId: record.patientHealthId,
                  diagnosis: record.diagnosis,
                  prescriptions: record.prescriptions,
                  labNotes: record.labNotes,
                  comments: record.comments
                })
              });
              if(!res.ok) throw new Error(await res.text());
              setRecStatus('Record saved successfully.');
              setRecord({ patientHealthId:'', diagnosis:'', prescriptions:'', labNotes:'', comments:'' });
            }catch(err){ setRecStatus(err?.message || 'Failed to save'); }
          }}>Save Record</button>
            </div>
          </div>
        </div>
        <div>
          <div className="card">
            <h3>View Patient Medical Records</h3>
            {viewError && <div className="error">{viewError}</div>}
            <div className="toolbar" style={{marginTop:8}}>
              <div className="field" style={{flex:1}}>
                <span className="subtle">Health ID</span>
                <input className="input" value={viewHealthId} onChange={e=>setViewHealthId(e.target.value)} placeholder="Enter Health ID to view"/>
              </div>
              <button className="btn" onClick={async()=>{
                setViewError('');
                setPatientRecords([]);
                if(!viewHealthId) return;
                try{
                  const res = await fetch(`http://localhost:8080/api/patients/${encodeURIComponent(viewHealthId)}/records`);
                  if(!res.ok) throw new Error(await res.text());
                  const data = await res.json();
                  setPatientRecords(data);
                }catch(err){ setViewError(err?.message || 'Failed to load records'); }
              }}>Load</button>
            </div>
            <div style={{marginTop:8, maxHeight: 380, overflowY:'auto', paddingRight:4}}>
              {patientRecords.length===0 && <div className="subtle">No records to show.</div>}
              <div style={{display:'grid', gridTemplateColumns:'1fr', gap:10}}>
                {patientRecords.map(r => (
                  <div key={r.id} className="chip" style={{padding:12}}>
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
          <div style={{height:16}}/>
          <div className="card">
            <h3 style={{display:'flex', alignItems:'center', gap:8}}><FaCalendarCheck/> My Appointments</h3>
            <div style={{maxHeight:380, overflow:'auto'}}>
              <div style={{minWidth: '600px', overflowX:'auto'}}>
                <table className="table">
                  <thead>
                    <tr style={{textAlign:'left'}}>
                      <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Patient ID</th>
                      <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Patient Name</th>
                      <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Date</th>
                      <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Time</th>
                      <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {appointments.map(a => (
                      <tr key={a.id}>
                        <td style={{padding:'8px 6px'}}>{a.patient?.healthId || '-'}</td>
                        <td style={{padding:'8px 6px'}}>{a.patient?.fullName || '-'}</td>
                        <td style={{padding:'8px 6px'}}>{a.date}</td>
                        <td style={{padding:'8px 6px'}}>{a.time}</td>
                        <td style={{padding:'8px 6px'}}><Badge status={a.status}/></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
