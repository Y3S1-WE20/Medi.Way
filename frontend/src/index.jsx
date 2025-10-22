import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Routes, Route, Link, useNavigate, useSearchParams } from 'react-router-dom';
import './index.css';

function Layout({ children }) {
  return (
    <div className="app">
      <header className="header">
        <div className="brand">MediWay</div>
        <nav>
          <Link to="/">Home</Link>
          <Link to="/register">Register</Link>
          <Link to="/login">Login</Link>
        </nav>
      </header>
      <main className="main">{children}</main>
      <footer className="footer">© {new Date().getFullYear()} MediWay Smart Healthcare</footer>
    </div>
  );
}

function Home() {
  return (
    <div className="card">
      <h1>Welcome to MediWay</h1>
      <p>A modern smart healthcare system for appointment, billing, and health records.</p>
      <div className="actions">
        <Link className="btn" to="/register">Get Started</Link>
      </div>
    </div>
  );
}

function Register() {
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

function Login() {
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
      navigate(`/success?healthId=${encodeURIComponent(data.healthId)}&name=${encodeURIComponent(form.email)}`);
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

function Success() {
  const [params] = useSearchParams();
  const healthId = params.get('healthId');
  const name = params.get('name');

  const qrUrl = `http://localhost:8080/api/patients/${encodeURIComponent(healthId)}/qr`;
  const downloadUrl = `${qrUrl}?download=true`;

  return (
    <div className="card">
      <h2>Welcome {name}</h2>
      <p>Your Health ID:</p>
      <div className="healthId">{healthId}</div>
      <img className="qr" src={qrUrl} alt="Health ID QR" />
      <div className="actions">
        <a className="btn" href={downloadUrl}>Download QR</a>
        <a className="btn outline" href={qrUrl} target="_blank" rel="noreferrer">Open QR</a>
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/success" element={<Success />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

createRoot(document.getElementById('root')).render(<App />);
