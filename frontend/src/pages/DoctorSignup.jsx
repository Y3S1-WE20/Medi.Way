import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUserMd, FaKey } from 'react-icons/fa';

// Admin must have added the doctor already. Signup here sets a password using email.
export default function DoctorSignup(){
  const [form, setForm] = React.useState({ email:'', password:'' });
  const [status, setStatus] = React.useState('');
  const [error, setError] = React.useState('');
  const navigate = useNavigate();

  async function submit(e){
    e.preventDefault(); setError(''); setStatus('');
    try{
      // find doctor by email
      const listRes = await fetch('http://localhost:8080/api/doctors');
      if(!listRes.ok) throw new Error(await listRes.text());
      const list = await listRes.json();
      const doc = list.find(d => (d.email || '').toLowerCase() === form.email.toLowerCase());
      if(!doc) throw new Error('Doctor email not found. Please contact admin.');
      const params = new URLSearchParams({ password: form.password });
      const res = await fetch(`http://localhost:8080/api/doctors/${doc.id}/password?`+params.toString(), { method: 'POST' });
      if(!res.ok){
        const text = await res.text();
        throw new Error(text || 'Failed to set password');
      }
      setStatus('Password set. You can now login.');
      setTimeout(()=> navigate('/doctor/login'), 1200);
    }catch(err){
      setError(err?.message || 'Failed to set password');
    }
  }

  return (
    <div className="card" style={{maxWidth:480, margin:'0 auto'}}>
      <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaUserMd/> Doctor Signup</h2>
      {status && <div className="error" style={{background:'#e8f7ee', color:'#065f46', border:'1px solid #c7f0d7'}}>{status}</div>}
      {error && <div className="error">{error}</div>}
      <form onSubmit={submit} className="form-grid">
        <label>Email<input type="email" required value={form.email} onChange={e=>setForm({...form, email:e.target.value})}/></label>
        <label>Create Password<input type="password" required value={form.password} onChange={e=>setForm({...form, password:e.target.value})}/></label>
        <button className="btn primary" type="submit"><FaKey/> Set Password</button>
      </form>
      <div style={{marginTop:12, fontSize:14}}>Use the email that admin added for your profile.</div>
    </div>
  );
}
