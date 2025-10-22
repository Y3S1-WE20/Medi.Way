import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login(){
  const navigate = useNavigate();
  const [form, setForm] = React.useState({ email: '', password: ''});
  const [error, setError] = React.useState('');

  async function submit(e) {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('http://localhost:8080/api/patients/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      localStorage.setItem('healthId', data.healthId);
      navigate('/profile');
    } catch (err) {
      setError('Login failed. ' + (err?.message || ''));
    }
  }

  return (
    <div className="card">
      <h2>Login</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={submit} className="form-grid">
        <label>Email<input type="email" required value={form.email} onChange={e=>setForm({...form, email:e.target.value})} /></label>
        <label>Password<input type="password" required value={form.password} onChange={e=>setForm({...form, password:e.target.value})} /></label>
        <button className="btn primary" type="submit">Login</button>
      </form>
    </div>
  );
}
