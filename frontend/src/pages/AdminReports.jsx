import React, { useEffect, useMemo, useRef, useState } from 'react';
import * as htmlToImage from 'html-to-image';
import {
  ResponsiveContainer,
  LineChart as RLineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  Brush,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
} from 'recharts';

const API = 'http://localhost:8080/api/reports';

function useFetch(url){
  const [data,setData] = useState(null);
  const [loading,setLoading] = useState(true);
  const [error,setError] = useState(null);
  useEffect(()=>{
    let cancelled=false;
    if(!url){ setData(null); setLoading(false); setError(null); return; }
    setLoading(true); setError(null);
    fetch(url)
      .then(r=>{ if(!r.ok) throw new Error('Network error'); return r.json(); })
      .then(d=>{ if(!cancelled){ setData(d); setLoading(false);} })
      .catch(e=>{ if(!cancelled){ setError(e); setLoading(false);} });
    return ()=>{cancelled=true};
  },[url]);
  return {data,loading,error};
}

function Section({title, subtitle, children, actions, chartRef}){
  return (
    <div className="card" style={{padding:16}} ref={chartRef}>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:12}}>
        <div>
          <h3 style={{margin:0}}>{title}</h3>
          {subtitle? <div style={{opacity:0.7, fontSize:12, marginTop:4}}>{subtitle}</div> : null}
        </div>
        <div style={{display:'flex', gap:8}}>{actions}</div>
      </div>
      {children}
    </div>
  );
}

function usePngExport(ref, filename){
  const [downloading,setDownloading] = useState(false);
  const onDownload = async ()=>{
    if(!ref.current) return;
    try{
      setDownloading(true);
      const dataUrl = await htmlToImage.toPng(ref.current, {backgroundColor:'#fff', pixelRatio:2});
      const a = document.createElement('a');
      a.href = dataUrl; a.download = filename; a.click();
    } finally { setDownloading(false); }
  };
  return {onDownload, downloading};
}

export default function AdminReports(){
  // Filters
  const [from,setFrom] = useState('');
  const [to,setTo] = useState('');
  const [period,setPeriod] = useState('daily');
  const [statusFilter,setStatusFilter] = useState({PENDING:true, CONFIRMED:true, REJECTED:true, CANCELLED:true});
  const [doctorQuery,setDoctorQuery] = useState('');
  const [specQuery,setSpecQuery] = useState('');

  // Data
  const reg = useFetch(`${API}/patients/registration${from||to ? `?${new URLSearchParams({from, to}).toString()}`:''}`);
  const demo = useFetch(`${API}/patients/demographics`);
  const load = useFetch(`${API}/doctors/appointment-load`);
  const summary = useFetch(`${API}/appointments/summary?period=${period}`);
  const bySpec = useFetch(`${API}/appointments/by-specialization`);
  const cancellations = useFetch(`${API}/appointments/cancellations`);

  // Transformations
  const regSeries = useMemo(()=>{
    const series = reg.data?.series || {};
    const keys = Object.keys(series).sort();
    return keys.map((k)=>({date:k, count:Number(series[k])||0}));
  },[reg.data]);

  const ageData = useMemo(()=>
    Object.entries(demo.data?.ageBuckets || {}).map(([name,value])=>({name, value:Number(value)||0})),
  [demo.data]);

  const doctorLoadData = useMemo(()=>
    Object.entries(load.data || {})
      .map(([doctor,count])=>({doctor, count:Number(count)||0}))
      .filter(d => !doctorQuery || d.doctor.toLowerCase().includes(doctorQuery.toLowerCase()))
      .sort((a,b)=>b.count-a.count),
  [load.data, doctorQuery]);

  const specData = useMemo(()=>
    Object.entries(bySpec.data || {})
      .map(([spec,count])=>({spec, count:Number(count)||0}))
      .filter(d => !specQuery || d.spec.toLowerCase().includes(specQuery.toLowerCase()))
      .sort((a,b)=>b.count-a.count),
  [bySpec.data, specQuery]);

  const summaryData = useMemo(()=>{
    const buckets = summary.data?.buckets || {};
    const rows = Object.entries(buckets).map(([bucket,counts])=>({
      bucket,
      PENDING: Number(counts.PENDING)||0,
      CONFIRMED: Number(counts.CONFIRMED)||0,
      REJECTED: Number(counts.REJECTED)||0,
      CANCELLED: Number(counts.CANCELLED)||0,
    }));
    return rows;
  },[summary.data]);

  // KPIs
  const kpi = useMemo(()=>{
    const totalRegistrations = regSeries.reduce((a,b)=>a+b.count,0);
    const totalAppointments = summaryData.reduce((a,b)=>a + b.PENDING + b.CONFIRMED + b.REJECTED + b.CANCELLED, 0);
    // cancellations last 30 days
    const now = new Date();
    const d30 = new Date(now.getTime() - 30*24*60*60*1000);
    const cancMap = cancellations.data || {};
    const canc30 = Object.entries(cancMap).reduce((acc,[d,v])=>{
      const dt = new Date(d);
      return dt>=d30 ? acc + (Number(v)||0) : acc;
    },0);
    const topSpec = specData[0]?.spec || '-';
    return {totalRegistrations, totalAppointments, canc30, topSpec};
  },[regSeries, summaryData, cancellations.data, specData]);

  // Refs for PNG export
  const regRef = useRef(null);
  const demoRef = useRef(null);
  const loadRef = useRef(null);
  const summaryRef = useRef(null);
  const specRef = useRef(null);
  const {onDownload: dlReg} = usePngExport(regRef, 'registrations.png');
  const {onDownload: dlDemo} = usePngExport(demoRef, 'age-demographics.png');
  const {onDownload: dlLoad} = usePngExport(loadRef, 'doctor-load.png');
  const {onDownload: dlSummary} = usePngExport(summaryRef, 'appointment-summary.png');
  const {onDownload: dlSpec} = usePngExport(specRef, 'by-specialization.png');

  const colors = {
    PENDING:'#94a3b8',
    CONFIRMED:'#22c55e',
    REJECTED:'#ef4444',
    CANCELLED:'#f59e0b',
  };

  return (
    <div style={{display:'grid', gap:16}}>
      {/* Filters + KPIs */}
      <div className="card" style={{padding:16, display:'grid', gap:12}}>
        <div style={{display:'flex', gap:12, flexWrap:'wrap', alignItems:'end'}}>
          <div>
            <div style={{fontSize:12, opacity:0.8}}>From</div>
            <input type="date" value={from} onChange={e=>setFrom(e.target.value)} />
          </div>
          <div>
            <div style={{fontSize:12, opacity:0.8}}>To</div>
            <input type="date" value={to} onChange={e=>setTo(e.target.value)} />
          </div>
          <div>
            <div style={{fontSize:12, opacity:0.8}}>Summary Period</div>
            <select value={period} onChange={e=>setPeriod(e.target.value)}>
              <option value="daily">Daily</option>
              <option value="weekly">Weekly</option>
              <option value="monthly">Monthly</option>
            </select>
          </div>
          <div style={{display:'flex', gap:8}}>
            {Object.keys(statusFilter).map(k=> (
              <button key={k} className={`chip ${statusFilter[k]?'active':''}`} onClick={()=>setStatusFilter(s=>({...s, [k]:!s[k]}))}>{k}</button>
            ))}
          </div>
          <div style={{marginLeft:'auto', display:'flex', gap:8}}>
            <a className="btn" href={`${API}/export/patients/registration.csv${from||to ? `?${new URLSearchParams({from,to}).toString()}`:''}`}>Export Registrations CSV</a>
            <a className="btn" href={`${API}/export/appointments/summary.csv?period=${period}`}>Export Summary CSV</a>
            <a className="btn" href={`${API}/export/appointments.pdf`} target="_blank" rel="noreferrer">Export Appointments PDF</a>
          </div>
        </div>

        <div style={{display:'grid', gridTemplateColumns:'repeat(4, minmax(160px,1fr))', gap:12}}>
          <div className="card" style={{padding:12}}>
            <div style={{opacity:0.7, fontSize:12}}>Registrations</div>
            <div style={{fontSize:24, fontWeight:800}}>{kpi.totalRegistrations}</div>
          </div>
          <div className="card" style={{padding:12}}>
            <div style={{opacity:0.7, fontSize:12}}>Total Appointments</div>
            <div style={{fontSize:24, fontWeight:800}}>{kpi.totalAppointments}</div>
          </div>
          <div className="card" style={{padding:12}}>
            <div style={{opacity:0.7, fontSize:12}}>Cancellations (30d)</div>
            <div style={{fontSize:24, fontWeight:800}}>{kpi.canc30}</div>
          </div>
          <div className="card" style={{padding:12}}>
            <div style={{opacity:0.7, fontSize:12}}>Top Specialization</div>
            <div style={{fontSize:18, fontWeight:700}}>{kpi.topSpec}</div>
          </div>
        </div>
      </div>

      {/* Registrations trend */}
      <Section
        title="Patient Registrations"
        subtitle={from||to? `Filtered ${from||'…'} to ${to||'…'}`: 'All time'}
        actions={[
          <button key="png" className="btn" onClick={dlReg}>Download PNG</button>,
          <a key="csv" className="btn" href={`${API}/export/patients/registration.csv${from||to ? `?${new URLSearchParams({from,to}).toString()}`:''}`}>Export CSV</a>
        ]}
        chartRef={regRef}
      >
        <div style={{width:'100%', height:280}}>
          <ResponsiveContainer>
            <RLineChart data={regSeries} margin={{top:10,right:10,left:0,bottom:0}}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" minTickGap={24} />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="count" stroke="#0ea5e9" strokeWidth={2} dot={{r:2}} />
              <Brush dataKey="date" height={20} stroke="#8884d8" />
            </RLineChart>
          </ResponsiveContainer>
        </div>
      </Section>

      <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:16}}>
        {/* Age Demographics */}
        <Section
          title="Age Demographics"
          actions={[
            <button key="png" className="btn" onClick={dlDemo}>Download PNG</button>,
            <a key="csv" className="btn" href={`${API}/export/patients/demographics.csv`}>Export CSV</a>,
          ]}
          chartRef={demoRef}
        >
          <div style={{width:'100%', height:280}}>
            <ResponsiveContainer>
              <PieChart>
                <Pie data={ageData} dataKey="value" nameKey="name" outerRadius={100} label>
                  {ageData.map((entry, index) => {
                    const palette = ['#3b82f6','#ef4444','#22c55e','#a855f7','#f59e0b'];
                    return <Cell key={`cell-${index}`} fill={palette[index % palette.length]} />;
                  })}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </Section>

        {/* Doctor Load */}
        <Section
          title="Appointment Load by Doctor"
          actions={[
            <input key="search" placeholder="Search doctor" value={doctorQuery} onChange={e=>setDoctorQuery(e.target.value)} />,
            <button key="png" className="btn" onClick={dlLoad}>Download PNG</button>,
            <a key="csv" className="btn" href={`${API}/export/doctors/appointment-load.csv`}>Export CSV</a>
          ]}
          chartRef={loadRef}
        >
          <div style={{width:'100%', height:280}}>
            <ResponsiveContainer>
              <BarChart data={doctorLoadData} margin={{top:10,right:10,left:0,bottom:0}}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="doctor" hide={doctorLoadData.length>12} interval={0} angle={-30} textAnchor="end" height={60} />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#22c55e" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Section>
      </div>

      {/* Summary */}
      <Section
        title="Appointment Summary"
        subtitle={`Status filter: ${Object.entries(statusFilter).filter(([,v])=>v).map(([k])=>k).join(', ') || 'None'}`}
        actions={[
          <button key="png" className="btn" onClick={dlSummary}>Download PNG</button>,
          <a key="csv" className="btn" href={`${API}/export/appointments/summary.csv?period=${period}`}>Export CSV</a>,
        ]}
        chartRef={summaryRef}
      >
        <div style={{width:'100%', height:320}}>
          <ResponsiveContainer>
            <BarChart data={summaryData} margin={{top:10,right:10,left:0,bottom:0}}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="bucket" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Legend />
              {statusFilter.PENDING && <Bar stackId="a" dataKey="PENDING" fill={colors.PENDING} />}
              {statusFilter.CONFIRMED && <Bar stackId="a" dataKey="CONFIRMED" fill={colors.CONFIRMED} />}
              {statusFilter.REJECTED && <Bar stackId="a" dataKey="REJECTED" fill={colors.REJECTED} />}            
              {statusFilter.CANCELLED && <Bar stackId="a" dataKey="CANCELLED" fill={colors.CANCELLED} />}
            </BarChart>
          </ResponsiveContainer>
        </div>
      </Section>

      {/* By specialization */}
      <Section
        title="Appointments by Specialization"
        actions={[
          <input key="search" placeholder="Search specialization" value={specQuery} onChange={e=>setSpecQuery(e.target.value)} />,
          <button key="png" className="btn" onClick={dlSpec}>Download PNG</button>,
          <a key="csv" className="btn" href={`${API}/export/appointments/by-specialization.csv`}>Export CSV</a>,
        ]}
        chartRef={specRef}
      >
        <div style={{width:'100%', height:280}}>
          <ResponsiveContainer>
            <BarChart data={specData} margin={{top:10,right:10,left:0,bottom:0}}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="spec" hide={specData.length>12} interval={0} angle={-30} textAnchor="end" height={60} />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Legend />
              <Bar dataKey="count" fill="#0ea5e9" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </Section>
    </div>
  );
}
