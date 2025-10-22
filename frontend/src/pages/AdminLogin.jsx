import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function AdminLogin(){
  const navigate = useNavigate();
  const [form, setForm] = React.useState({ username: '', password: ''});
  const [error, setError] = React.useState('');

  // Hard-coded credentials
  const ADMIN_USER = 'admin';
  const ADMIN_PASS = 'admin123';

  function submit(e){
    e.preventDefault();
    setError('');
    const { username, password } = form;
    if (username === ADMIN_USER && password === ADMIN_PASS){
      // store a simple flag so the app can check admin login if needed
      localStorage.setItem('isAdmin', 'true');
      navigate('/admin/appointments');
    } else {
      setError('Invalid username or password');
    }
  }

  return (
    <div className="card" style={{maxWidth:420}}>
      <h2>Admin Login</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={submit} className="form-grid">
        <label>Username<input required value={form.username} onChange={e=>setForm({...form, username: e.target.value})} /></label>
        <label>Password<input type="password" required value={form.password} onChange={e=>setForm({...form, password: e.target.value})} /></label>
        <div style={{display:'flex', gap:8, justifyContent:'flex-end'}}>
          <button type="submit" className="btn primary">Login</button>
        </div>
      </form>
      <div style={{marginTop:12, fontSize:13, color:'#666'}}></div>
    </div>
  );
}
