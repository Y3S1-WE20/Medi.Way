import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FaUserMd, FaSignInAlt } from 'react-icons/fa';

export default function DoctorLogin(){
  const [form, setForm] = React.useState({ email:'', password:'' });
  const [error, setError] = React.useState('');
  const navigate = useNavigate();

  async function submit(e){
    e.preventDefault(); setError('');
    try{
      const res = await fetch('http://localhost:8080/api/doctors/login', {
        method: 'POST',
        headers: { 'Content-Type':'application/json' },
        body: JSON.stringify(form)
      });
      if(!res.ok){
        const text = await res.text();
        throw new Error(text || 'Login failed');
      }
      const doc = await res.json();
      localStorage.setItem('doctorId', String(doc.id));
      localStorage.setItem('doctorName', doc.name || 'Doctor');
      navigate('/doctor/profile');
    }catch(err){
      setError(err?.message || 'Login failed');
    }
  }

  return (
    <div className="card" style={{maxWidth:480, margin:'0 auto'}}>
      <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaUserMd/> Doctor Login</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={submit} className="form-grid">
        <label>Email<input type="email" required value={form.email} onChange={e=>setForm({...form, email:e.target.value})}/></label>
        <label>Password<input type="password" required value={form.password} onChange={e=>setForm({...form, password:e.target.value})}/></label>
        <button className="btn primary" type="submit"><FaSignInAlt/> Login</button>
      </form>
      <div style={{marginTop:12, fontSize:14}}>
        First time? Ask admin to create your profile, then set a password in Doctor Signup.
      </div>
      <div style={{marginTop:8}}>
        <Link to="/doctor/signup">Go to Doctor Signup</Link>
      </div>
    </div>
  );
}
