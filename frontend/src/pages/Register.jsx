import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Register(){
  const navigate = useNavigate();
  const [form, setForm] = React.useState({
    fullName: '', email: '', password: '', phone: '', address: '', dateOfBirth: ''
  });
  const [error, setError] = React.useState('');

  async function submit(e) {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('http://localhost:8080/api/patients/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...form,
          dateOfBirth: form.dateOfBirth || null
        })
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      navigate(`/success?healthId=${encodeURIComponent(data.healthId)}&name=${encodeURIComponent(data.fullName)}`);
    } catch (err) {
      setError('Registration failed. ' + (err?.message || ''));
    }
  }

  return (
    <div className="card">
      <h2>Create your patient account</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={submit} className="form-grid">
        <label>Full Name<input required value={form.fullName} onChange={e=>setForm({...form, fullName:e.target.value})} /></label>
        <label>Email<input type="email" required value={form.email} onChange={e=>setForm({...form, email:e.target.value})} /></label>
        <label>Password<input type="password" required value={form.password} onChange={e=>setForm({...form, password:e.target.value})} /></label>
        <label>Phone<input value={form.phone} onChange={e=>setForm({...form, phone:e.target.value})} /></label>
        <label>Address<input value={form.address} onChange={e=>setForm({...form, address:e.target.value})} /></label>
        <label>Date of Birth<input type="date" value={form.dateOfBirth} onChange={e=>setForm({...form, dateOfBirth:e.target.value})} /></label>
        <button className="btn primary" type="submit">Register</button>
      </form>
    </div>
  );
}
