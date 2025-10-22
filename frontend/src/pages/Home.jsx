import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { FaStethoscope, FaUserMd, FaCalendarCheck, FaVial, FaAmbulance, FaPhoneVolume, FaHeartbeat, FaChevronLeft, FaChevronRight, FaArrowRight, FaMicroscope, FaBaby, FaLungs, FaTooth, FaBrain, FaHospital, FaTint, FaRibbon, FaXRay, FaBone, FaChild, FaNotesMedical, FaFileMedical, FaCapsules, FaMoneyCheckAlt, FaCommentMedical, FaFacebook, FaTwitter, FaInstagram } from 'react-icons/fa';

function useInterval(callback, delay){
  const savedRef = useRef();
  useEffect(()=>{ savedRef.current = callback; });
  useEffect(()=>{
    if(delay == null) return;
    const id = setInterval(()=>savedRef.current && savedRef.current(), delay);
    return ()=>clearInterval(id);
  },[delay]);
}

function Dot({active, onClick}){
  return <button onClick={onClick} style={{width:10,height:10,borderRadius:'50%',border:'none',background:active?'#0ea5e9':'#cbd5e1',cursor:'pointer'}} aria-label={active? 'current slide':'go to slide'} />;
}

function Hero(){
  const slides = useMemo(()=>[
    {img:'https://images.pexels.com/photos/3938022/pexels-photo-3938022.jpeg', title:'World‑class Care, Close to You', subtitle:'Advanced diagnostics, experienced specialists, and compassionate nursing—under one roof.'},
    {img:'https://images.pexels.com/photos/6129676/pexels-photo-6129676.jpeg', title:'Book Appointments Instantly', subtitle:'Find the right doctor and get the right slot. No queues. No stress.'},
    {img:'https://images.pexels.com/photos/30797638/pexels-photo-30797638.jpeg', title:'Comprehensive Health Packages', subtitle:'Preventive checks designed for every age and lifestyle.'},
  ],[]);
  const [i,setI] = useState(0);
  useInterval(()=> setI(prev => (prev+1)%slides.length), 6000);
  const cur = slides[i];
  return (
    <section style={{position:'relative', borderRadius:16, overflow:'hidden', height:'80vh', minHeight:520, background:'#000'}}> 
      <img src={cur.img} alt={cur.title} style={{width:'100%', height:'100%', objectFit:'cover', opacity:0.9}}/>
      <div style={{position:'absolute', inset:0, background:'linear-gradient(90deg, rgba(0,0,0,0.55), rgba(0,0,0,0.15) 50%, rgba(0,0,0,0))'}}/>
      <div style={{position:'absolute', inset:0, display:'grid', alignItems:'center'}}>
        <div style={{padding:24}}>
          <div style={{color:'#e2e8f0', display:'flex', alignItems:'center', gap:8, marginBottom:10}}>
            <FaHeartbeat/> Accredited Private Hospital Network
          </div>
          <h1 style={{margin:0, color:'#fff', fontSize:42, lineHeight:1.15}}>{cur.title}</h1>
          <p style={{color:'#e2e8f0', fontSize:16, maxWidth:720}}>{cur.subtitle}</p>
          <div style={{display:'flex', gap:10, marginTop:12}}>
            <Link className="btn primary" to="/appointments">Book Appointment</Link>
            <Link className="btn outline" to="/register">Get Your Health ID</Link>
          </div>
        </div>
      </div>
      <button onClick={()=>setI((i-1+slides.length)%slides.length)} aria-label="previous" style={{position:'absolute', left:12, top:'50%', transform:'translateY(-50%)', background:'#ffffffbb', border:'none', borderRadius:'50%', width:40, height:40, display:'grid', placeItems:'center', cursor:'pointer'}}>
        <FaChevronLeft/>
      </button>
      <button onClick={()=>setI((i+1)%slides.length)} aria-label="next" style={{position:'absolute', right:12, top:'50%', transform:'translateY(-50%)', background:'#ffffffbb', border:'none', borderRadius:'50%', width:40, height:40, display:'grid', placeItems:'center', cursor:'pointer'}}>
        <FaChevronRight/>
      </button>
      <div style={{position:'absolute', bottom:12, left:0, right:0, display:'flex', justifyContent:'center', gap:8}}>
        {slides.map((_,idx)=> <Dot key={idx} active={i===idx} onClick={()=>setI(idx)}/>) }
      </div>
    </section>
  );
}

// Simple reveal-on-scroll wrapper
function Reveal({children, y=16, delay=0}){
  const ref = useRef(null);
  const [visible,setVisible] = useState(false);
  useEffect(()=>{
    const el = ref.current; if(!el) return;
    const io = new IntersectionObserver(([ent])=>{ if(ent.isIntersecting){ setTimeout(()=>setVisible(true), delay); io.disconnect(); } }, {threshold:0.15});
    io.observe(el); return ()=>io.disconnect();
  },[delay]);
  return (
    <div ref={ref} style={{opacity:visible?1:0, transform:visible?'translateY(0)':`translateY(${y}px)`, transition:'all .6s cubic-bezier(.2,.8,.2,1)'}}>
      {children}
    </div>
  );
}

function HealthNetwork(){
  const hospitals = ['MEDIWAY MEDICAL','MEDIWAY SURGICAL','MEDIWAY CENTRAL','MEDIWAY MATARA','MEDIWAY GALLE','MEDIWAY KANDY','MEDIWAY LABORATORIES'];
  const centers = [
    {icon:<FaHeartbeat/>, title:['MEDIWAY HEART CENTRES']},
    {icon:<FaBrain/>, title:['MEDIWAY BRAIN & SPINE','CENTRE']},
    {icon:<FaBone/>, title:['MEDIWAY BONE MARROW','TRANSPLANT CENTRE']},
    {icon:<FaBrain/>, title:['MEDIWAY STROKE CENTRE']},
    {icon:<FaXRay/>, title:['MEDIWAY CENTRE FOR','INTERVENTIONAL','RADIOLOGY']},
    {icon:<FaChild/>, title:['MEDIWAY MOTHER & BABY','CARE']},
    {icon:<FaTint/>, title:['MEDIWAY KIDNEY','TRANSPLANT CENTRE']},
    {icon:<FaStethoscope/>, title:['MEDIWAY UROLOGY SERVICES']},
    {icon:<FaRibbon/>, title:['MEDIWAY CANCER CENTRE']},
    {icon:<FaBaby/>, title:['MEDIWAY IVF & FERTILITY','CENTRE']},
    {icon:<FaNotesMedical/>, title:['VISA MEDICALS – AU, CA,','NZ, US, GULF']},
    {icon:<FaRibbon/>, title:['BREAST CANCER'], accent:'#ef4444'},
  ];
  return (
    <section>
      <div style={{display:'grid', gridTemplateColumns:'360px 1fr', gap:16}}>
        <Reveal>
          <div className="card" style={{padding:16, background:'#2bb5aa', color:'#fff'}}>
            <div style={{fontWeight:800, fontSize:18, letterSpacing:.5}}>MEDIWAY HEALTH
              <br/>NETWORK</div>
            <p style={{opacity:.95}}>Six hospitals in 3 provinces and the largest private laboratory network. The MediWay Port City Hospital opens in 2027.</p>
            <div style={{marginTop:12, display:'grid', gap:8}}>
              {hospitals.map((h,i)=> (
                <div key={i} style={{display:'flex', justifyContent:'space-between', alignItems:'center', padding:'10px 12px', borderRadius:10, background:'rgba(255,255,255,.08)'}}>
                  <span>{h}</span>
                  <FaChevronRight/>
                </div>
              ))}
            </div>
          </div>
        </Reveal>
        <Reveal delay={100}>
          <div style={{display:'grid', gridTemplateColumns:'repeat(4, minmax(180px,1fr))', gap:12}}>
            {centers.map((c,idx)=> (
              <div key={idx} className="card" style={{padding:14, textAlign:'center', background:c.accent?c.accent:'#8090c3', color:'#fff', cursor:'pointer', transition:'transform .25s', transform:'translateZ(0)'}}
                   onMouseEnter={e=>e.currentTarget.style.transform='translateY(-4px)'}
                   onMouseLeave={e=>e.currentTarget.style.transform='translateY(0)'}>
                <div style={{fontSize:28, opacity:.95}}>{c.icon}</div>
                <div style={{marginTop:8, fontWeight:700, fontSize:12, lineHeight:1.2}}>
                  {c.title.map((t,i)=>(<div key={i}>{t}</div>))}
                </div>
              </div>
            ))}
          </div>
        </Reveal>
      </div>
    </section>
  );
}

function QualityData(){
  const metrics = [
    {value:'94.82%', label:'Patient Satisfaction Rate on Services'},
    {value:'97.30%', label:'Compliance to Correct Patient Identification'},
    {value:'88.33%', label:'Hand Hygiene Compliance'},
    {value:'0.09%', label:'Rate of Hospital Acquired Infections'},
    {value:'0.17%', label:'Adverse Drug Reaction'},
    {value:'0.00%', label:'Rate of Hospital Acquired Bed Sores'},
    {value:'0.00%', label:'Rate of Patient Falls'},
  ];
  return (
    <section>
      <Reveal>
        <h2 style={{marginTop:0}}>Quality Data</h2>
        <div style={{display:'grid', gridTemplateColumns:'repeat(3, minmax(220px,1fr))', gap:12}}>
          {metrics.map((m,idx)=> (
            <div key={idx} className="card" style={{padding:16, background:'#0f2d46', color:'#fff', display:'flex', gap:12, alignItems:'center'}}>
              <div style={{fontSize:26, fontWeight:900, letterSpacing:.5, minWidth:90}}>{m.value}</div>
              <div style={{opacity:.95}}>{m.label}</div>
            </div>
          ))}
        </div>
      </Reveal>
    </section>
  );
}

function WhyChooseUs(){
  const stats = [
    {num:'800+', label:'Consultants'},
    {num:'3500+', label:'Consultations Per Day'},
    {num:'4250+', label:'Tests Offered'},
    {num:'14500+', label:'Tests Per Day'},
    {num:'800+', label:'Beds'},
  ];
  return (
    <section>
      <div style={{display:'grid', gridTemplateColumns:'1.2fr 1fr', gap:16, alignItems:'center'}}>
        <Reveal>
          <div>
            <h2 style={{marginTop:0}}>Why Patients Choose MediWay</h2>
            <p style={{color:'#6b7280'}}>MediWay employs skilled, experienced professionals across clinical specialties to deliver outstanding outcomes with evidence‑based care and comprehensive diagnostics.</p>
            <p style={{color:'#6b7280'}}>All MediWay hospitals maintain international accreditation with a strong record for patient safety and quality.</p>
          </div>
        </Reveal>
        <Reveal delay={100}>
          <img src="https://asirihealth.com/public/frontend/asiri_health/images/about.jpg" alt="Care team" style={{width:'100%', borderRadius:16}}/>
        </Reveal>
      </div>
      <Reveal delay={150}>
        <div className="card" style={{padding:16, marginTop:12}}>
          <div style={{display:'grid', gridTemplateColumns:'repeat(5, minmax(120px,1fr))', gap:12}}>
            {stats.map((s,idx)=> (
              <div key={idx} style={{textAlign:'center'}}>
                <div style={{fontSize:24, fontWeight:900, color:'#0f2d46'}}>{s.num}</div>
                <div style={{color:'#6b7280'}}>{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </Reveal>
    </section>
  );
}

function OnlineServices(){
  const items = [
    {icon:<FaFileMedical/>, title:'Download Lab Reports', desc:'Access your laboratory results with ease.'},
    {icon:<FaCalendarCheck/>, title:'Consultation Bookings', desc:'Channel your consultant online.'},
    {icon:<FaNotesMedical/>, title:'Ongoing Number', desc:'Monitor queue numbers and plan your arrival.'},
    {icon:<FaFileMedical/>, title:'Pre‑registration', desc:'Complete forms at home to save time.'},
    {icon:<FaCapsules/>, title:'Online Pharmacy', desc:'Order medication to your doorstep.'},
    {icon:<FaStethoscope/>, title:'Wellness Packages', desc:'Maintain your health with curated checks.'},
    {icon:<FaMoneyCheckAlt/>, title:'Payment Portal', desc:'Secure online payments for bills.'},
    {icon:<FaCommentMedical/>, title:'Patient Feedback', desc:'Share your experience to improve care.'},
  ];
  return (
    <section style={{position:'relative'}}>
      <div style={{position:'absolute', inset:0, background:'url(https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1600&auto=format&fit=crop) center/cover no-repeat', filter:'grayscale(0.1)', opacity:0.12, borderRadius:16}}/>
      <div style={{position:'relative'}}>
        <h2 style={{marginTop:0}}>Use The Convenience Of Our Online Services</h2>
        <Reveal>
          <div style={{display:'grid', gridTemplateColumns:'repeat(4, minmax(220px,1fr))', gap:14}}>
            {items.map((it,idx)=> (
              <div key={idx} className="card" style={{padding:16, background:'#fff', display:'grid', gap:8, cursor:'pointer', transition:'transform .25s, box-shadow .25s'}}
                   onMouseEnter={e=>{e.currentTarget.style.transform='translateY(-4px)'; e.currentTarget.style.boxShadow='0 10px 30px rgba(2,6,23,.12)';}}
                   onMouseLeave={e=>{e.currentTarget.style.transform='translateY(0)'; e.currentTarget.style.boxShadow='';}}>
                <div style={{display:'flex', alignItems:'center', gap:12}}>
                  <div style={{width:44, height:44, borderRadius:12, background:'#e0fbf7', color:'#2bb5aa', display:'grid', placeItems:'center', fontSize:18}}>{it.icon}</div>
                  <div style={{fontWeight:800}}>{it.title}</div>
                </div>
                <div style={{color:'#6b7280'}}>{it.desc}</div>
              </div>
            ))}
          </div>
        </Reveal>
      </div>
    </section>
  );
}

function Services(){
  const items = [
    {icon:<FaCalendarCheck/>, title:'Book an Appointment', desc:'Choose a specialist, pick a time, confirm in seconds.', to:'/appointments', color:'#0ea5e9'},
    {icon:<FaUserMd/>, title:'Find a Doctor', desc:'Search by name or specialization and view profiles.', to:'/doctor/login', color:'#22c55e'},
    {icon:<FaVial/>, title:'Diagnostics', desc:'Lab tests and imaging with quick turnaround.', to:'/appointments', color:'#f59e0b'},
    {icon:<FaStethoscope/>, title:'Health Packages', desc:'Comprehensive checkups tailored for you.', to:'/appointments', color:'#a855f7'},
  ];
  return (
    <section>
      <div style={{display:'grid', gridTemplateColumns:'repeat(4, minmax(180px, 1fr))', gap:16}}>
        {items.map((it,idx)=> (
          <Link key={idx} to={it.to} className="card" style={{padding:16, textDecoration:'none'}}>
            <div style={{display:'flex', alignItems:'center', gap:12}}>
              <div style={{width:42, height:42, borderRadius:12, display:'grid', placeItems:'center', background:`${it.color}22`, color:it.color, fontSize:18}}>
                {it.icon}
              </div>
              <div>
                <div style={{fontWeight:700, color:'#111827'}}>{it.title}</div>
                <div style={{color:'#6b7280', fontSize:13}}>{it.desc}</div>
              </div>
            </div>
            <div style={{marginTop:12, display:'flex', alignItems:'center', gap:6, color:it.color, fontWeight:600}}>Get started <FaArrowRight/></div>
          </Link>
        ))}
      </div>
    </section>
  );
}

function Departments(){
  const items = [
    {icon:<FaMicroscope/>, name:'Pathology'},
    {icon:<FaLungs/>, name:'Pulmonology'},
    {icon:<FaBrain/>, name:'Neurology'},
    {icon:<FaTooth/>, name:'Dental'},
    {icon:<FaBaby/>, name:'Maternity'},
    {icon:<FaStethoscope/>, name:'General Medicine'},
  ];
  return (
    <section>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:8}}>
        <h2 style={{margin:0}}>Centers of Excellence</h2>
        <Link className="btn outline" to="/appointments">Explore</Link>
      </div>
      <div style={{display:'grid', gridTemplateColumns:'repeat(6, minmax(120px,1fr))', gap:12}}>
        {items.map((it,idx)=> (
          <div key={idx} className="card" style={{padding:14, textAlign:'center'}}>
            <div style={{fontSize:20, color:'#0ea5e9'}}>{it.icon}</div>
            <div style={{marginTop:8, fontWeight:700}}>{it.name}</div>
          </div>
        ))}
      </div>
    </section>
  );
}

function Packages(){
  const packs = [
    {img:'https://images.unsplash.com/photo-1622253692010-333f2da6031d?q=80&w=1200&auto=format&fit=crop', name:'Wellness Basic', price:'LKR 7,900', includes:['CBC','FBS','Lipid Profile','Urinalysis']},
    {img:'https://images.unsplash.com/photo-1666214280557-5cb481d2b03e?q=80&w=1200&auto=format&fit=crop', name:'Executive Check', price:'LKR 19,900', includes:['CBC','LFT','KFT','ECG','Chest X-Ray']},
    {img:'https://images.unsplash.com/photo-1580281657527-47ea6b95e526?q=80&w=1200&auto=format&fit=crop', name:'Heart Care', price:'LKR 14,900', includes:['ECG','Echo','TMT','Lipid Profile']},
  ];
  return (
    <section>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:8}}>
        <h2 style={{margin:0}}>Health Packages</h2>
        <Link className="btn" to="/appointments">View all</Link>
      </div>
      <div style={{display:'grid', gridTemplateColumns:'repeat(3, minmax(220px,1fr))', gap:16}}>
        {packs.map((p,idx)=> (
          <div key={idx} className="card" style={{padding:0, overflow:'hidden'}}>
            <img src={p.img} alt={p.name} style={{width:'100%', height:160, objectFit:'cover'}}/>
            <div style={{padding:14}}>
              <div style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
                <div style={{fontWeight:800}}>{p.name}</div>
                <div style={{color:'#0ea5e9', fontWeight:800}}>{p.price}</div>
              </div>
              <div style={{marginTop:8, color:'#6b7280', fontSize:13}}>Includes: {p.includes.join(', ')}</div>
              <div style={{marginTop:12}}>
                <Link className="btn primary" to="/appointments">Book now</Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

function Testimonials(){
  const items = [
    {name:'Tharindu', text:'Seamless booking and caring staff. The entire process was smooth and efficient.'},
    {name:'Ishara', text:'Doctors were very attentive. Results and prescriptions were available quickly.'},
    {name:'Nadeesha', text:'Loved the health packages. Great value and very comprehensive diagnostics.'},
  ];
  const [i,setI] = useState(0);
  useInterval(()=> setI(prev => (prev+1)%items.length), 7000);
  return (
    <section className="card" style={{padding:20, display:'grid', gridTemplateColumns:'1fr 1fr', gap:18, alignItems:'center'}}>
      <div>
        <h2 style={{marginTop:0}}>Patient Stories</h2>
        <p style={{color:'#6b7280'}}>Real experiences from our patients—because your trust matters.</p>
        <div style={{display:'flex', gap:8, alignItems:'center', marginTop:8}}>
          <FaAmbulance style={{color:'#ef4444'}}/> 24x7 Emergency &nbsp; • &nbsp; <FaPhoneVolume style={{color:'#0ea5e9'}}/> 0117 123 123
        </div>
      </div>
      <div>
        <div className="card" style={{padding:16}}>
          <div style={{fontSize:18, lineHeight:1.5}}>“{items[i].text}”</div>
          <div style={{marginTop:10, fontWeight:700}}>— {items[i].name}</div>
        </div>
        <div style={{display:'flex', gap:8, marginTop:10}}>
          {items.map((_,idx)=> <Dot key={idx} active={i===idx} onClick={()=>setI(idx)}/>) }
        </div>
      </div>
    </section>
  );
}

function News(){
  const items = [
    {img:'https://images.unsplash.com/photo-1584824486516-0555a07fc511?q=80&w=1200&auto=format&fit=crop', title:'New MRI Suite Launched', date:'Oct 2025'},
    {img:'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1200&auto=format&fit=crop', title:'Cardiac Camp for Seniors', date:'Sep 2025'},
    {img:'https://images.unsplash.com/photo-1589156280159-27698a70f29e?q=80&w=1200&auto=format&fit=crop', title:'Flu Vaccination Drive', date:'Aug 2025'},
  ];
  return (
    <section>
      <div style={{display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:8}}>
        <h2 style={{margin:0}}>News & Updates</h2>
        <Link className="btn outline" to="/appointments">See all</Link>
      </div>
      <div style={{display:'grid', gridTemplateColumns:'repeat(3, minmax(220px,1fr))', gap:16}}>
        {items.map((n,idx)=> (
          <div key={idx} className="card" style={{padding:0, overflow:'hidden'}}>
            <img src={n.img} alt={n.title} style={{width:'100%', height:140, objectFit:'cover'}}/>
            <div style={{padding:12}}>
              <div style={{fontWeight:700}}>{n.title}</div>
              <div style={{color:'#6b7280', fontSize:12}}>{n.date}</div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

function ContactStrip(){
  return (
    <section className="card" style={{padding:16, display:'grid', gridTemplateColumns:'1fr 1fr 1fr', gap:16, alignItems:'center'}}>
      <div style={{display:'flex', alignItems:'center', gap:10}}>
        <div style={{width:36,height:36,borderRadius:10,background:'#fee2e2',display:'grid',placeItems:'center',color:'#ef4444'}}><FaAmbulance/></div>
        <div>
          <div style={{fontWeight:700}}>Emergency</div>
          <div style={{color:'#6b7280'}}>0117 123 123</div>
        </div>
      </div>
      <div style={{display:'flex', alignItems:'center', gap:10}}>
        <div style={{width:36,height:36,borderRadius:10,background:'#dbeafe',display:'grid',placeItems:'center',color:'#0ea5e9'}}><FaPhoneVolume/></div>
        <div>
          <div style={{fontWeight:700}}>General Hotline</div>
          <div style={{color:'#6b7280'}}>0117 456 456</div>
        </div>
      </div>
      <div style={{textAlign:'right'}}>
        <Link className="btn primary" to="/appointments">Book an Appointment</Link>
      </div>
    </section>
  );
}

function Footer(){
  return (
    <footer style={{marginTop:8}}>
      {/* Full-width dark footer strip */}
      <div style={{background:'#0f2d46', color:'#e5edf4', borderRadius:16, overflow:'hidden'}}>
        <div style={{height:4, background:'#22c55e'}}/>
        <div style={{padding:'22px 20px'}}>
          <div style={{display:'grid', gridTemplateColumns:'2fr 1fr 1fr', gap:16}}>
            <div>
              <div style={{fontSize:20, fontWeight:900}}>MediWay</div>
              <p style={{opacity:0.9, margin:'6px 0 10px'}}>Private healthcare with advanced diagnostics, trusted specialists, and compassionate care across Sri Lanka.</p>
              <div style={{display:'flex', gap:12}}>
                <a href="#" aria-label="facebook" style={{color:'#93c5fd'}}><FaFacebook/></a>
                <a href="#" aria-label="twitter" style={{color:'#93c5fd'}}><FaTwitter/></a>
                <a href="#" aria-label="instagram" style={{color:'#93c5fd'}}><FaInstagram/></a>
              </div>
            </div>
            <div>
              <div style={{fontWeight:800, color:'#ffffff'}}>Quick Links</div>
              <ul style={{listStyle:'none', padding:0, margin:8, lineHeight:1.9}}>
                <li><Link to="/appointments" style={{color:'#e5edf4', textDecoration:'none'}}>Book Appointment</Link></li>
                <li><Link to="/doctor/login" style={{color:'#e5edf4', textDecoration:'none'}}>Find a Doctor</Link></li>
                <li><Link to="/register" style={{color:'#e5edf4', textDecoration:'none'}}>Get Health ID</Link></li>
              </ul>
            </div>
            <div>
              <div style={{fontWeight:800, color:'#ffffff'}}>Contact</div>
              <div style={{marginTop:8}}>Hotline: 0117 456 456<br/>Emergency: 0117 123 123<br/>Email: support@mediway.lk</div>
            </div>
          </div>
          <div style={{marginTop:14, borderTop:'1px solid rgba(255,255,255,.1)', paddingTop:12, opacity:0.8, fontSize:12}}>© {new Date().getFullYear()} MediWay Health. All rights reserved.</div>
        </div>
      </div>
    </footer>
  );
}

export default function Home(){
  return (
    <div style={{display:'grid', gap:22}}>
      <Hero/>
      <HealthNetwork/>
      <QualityData/>
      <WhyChooseUs/>
      <OnlineServices/>
      <Services/>
      <Testimonials/>
      <ContactStrip/>
      <Footer/>
    </div>
  );
}
