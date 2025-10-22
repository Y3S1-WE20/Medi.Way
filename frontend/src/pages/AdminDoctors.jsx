import React from 'react';
import { FaUserMd, FaEdit, FaTrash, FaPlus } from 'react-icons/fa';

export default function AdminDoctors(){
  const [form, setForm] = React.useState({ name:'', email:'', specialization:'' });
  const [photo, setPhoto] = React.useState(null);
  const [preview, setPreview] = React.useState('');
  const [status, setStatus] = React.useState('');
  const [list, setList] = React.useState([]);
  const [editingId, setEditingId] = React.useState(null);

  function onPhotoChange(e){
    const file = e.target.files?.[0];
    setPhoto(file || null);
    if(file){ setPreview(URL.createObjectURL(file)); } else { setPreview(''); }
  }
  async function refresh(){
    const res = await fetch('http://localhost:8080/api/doctors');
    setList(await res.json());
  }
  React.useEffect(()=>{ refresh(); },[]);

  async function submit(e){
    e.preventDefault(); setStatus('');
    const fd = new FormData();
    fd.append('name', form.name);
    fd.append('email', form.email);
    fd.append('specialization', form.specialization);
    if(photo) fd.append('photo', photo);
    try{
      const res = await fetch('http://localhost:8080/api/doctors', { method:'POST', body: fd });
      if(!res.ok) throw new Error(await res.text());
      setStatus('Doctor saved successfully.');
      setForm({name:'',email:'',specialization:''});
      setPhoto(null); setPreview('');
      refresh();
    }catch(err){ setStatus('Failed: ' + (err?.message || '')); }
  }

  async function startEdit(id){
    setEditingId(id);
    const d = list.find(x=>x.id===id);
    if(!d) return;
    setForm({ name:d.name, email:d.email, specialization:d.specialization });
    setPreview(`http://localhost:8080/api/doctors/${id}/photo`);
    setPhoto(null);
  }

  async function saveEdit(e){
    e.preventDefault(); if(!editingId) return;
    const fd = new FormData();
    if(form.name) fd.append('name', form.name);
    if(form.email) fd.append('email', form.email);
    if(form.specialization) fd.append('specialization', form.specialization);
    if(photo!==null) fd.append('photo', photo || new File([], ''));
    try{
      const res = await fetch(`http://localhost:8080/api/doctors/${editingId}`, { method:'PUT', body: fd });
      if(!res.ok) throw new Error(await res.text());
      setStatus('Doctor updated.');
      setEditingId(null);
      setForm({name:'',email:'',specialization:''});
      setPhoto(null); setPreview('');
      refresh();
    }catch(err){ setStatus('Failed: ' + (err?.message || '')); }
  }

  async function remove(id){
    if(!window.confirm('Delete this doctor?')) return;
    await fetch(`http://localhost:8080/api/doctors/${id}`, { method:'DELETE' });
    refresh();
  }

  return (
    <>
      <div className="card">
        <h2 style={{display:'flex', alignItems:'center', gap:10}}><FaUserMd/> Admin: {editingId? 'Edit Doctor' : 'Add Doctor'}</h2>
        {status && <div className="error" style={{background:'#e8f7ee', color:'#065f46', border:'1px solid #c7f0d7'}}>{status}</div>}
        <form onSubmit={editingId? saveEdit : submit} className="form-grid">
          <label>Name<input required value={form.name} onChange={e=>setForm({...form, name:e.target.value})} /></label>
          <label>Email<input type="email" required value={form.email} onChange={e=>setForm({...form, email:e.target.value})} /></label>
          <label>Specialization<input required value={form.specialization} onChange={e=>setForm({...form, specialization:e.target.value})} /></label>
          <label>Profile Photo<input type="file" accept="image/*" onChange={onPhotoChange} /></label>
          {preview && <img src={preview} alt="preview" className="qr" style={{width:120, height:120, objectFit:'cover', borderRadius:12}}/>}
          <button className="btn primary" type="submit">{editingId? 'Update Doctor' : 'Save Doctor'}</button>
        </form>
      </div>
      <div style={{height:16}}/>
      <div className="card">
        <h3>Doctors</h3>
        <div style={{overflowX:'auto'}}>
          <table className="table">
            <thead>
              <tr style={{textAlign:'left'}}>
                <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Photo</th>
                <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Name</th>
                <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Email</th>
                <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}>Specialization</th>
                <th style={{padding:'8px 6px', borderBottom:'1px solid #e5e7eb'}}></th>
              </tr>
            </thead>
            <tbody>
            {list.map(d => (
              <tr key={d.id}>
                <td style={{padding:'8px 6px'}}>
                  <img src={`http://localhost:8080/api/doctors/${d.id}/photo`} alt="" style={{width:48,height:48,objectFit:'cover',borderRadius:10,border:'1px solid #e5e7eb'}} onError={(e)=>{e.currentTarget.style.visibility='hidden'}}/>
                </td>
                <td style={{padding:'8px 6px'}}>{d.name}</td>
                <td style={{padding:'8px 6px'}}>{d.email}</td>
                <td style={{padding:'8px 6px'}}>{d.specialization}</td>
                <td style={{padding:'8px 6px', whiteSpace:'nowrap'}}>
                  <button className="btn small icon" onClick={()=>startEdit(d.id)} style={{marginRight:8}}><FaEdit/> Edit</button>
                  <button className="btn small icon" onClick={()=>remove(d.id)} style={{background:'#fee2e2', borderColor:'#fecaca', color:'#991b1b'}}><FaTrash/> Delete</button>
                </td>
              </tr>
            ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}
