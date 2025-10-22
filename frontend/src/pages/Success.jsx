import React from 'react';
import { useSearchParams } from 'react-router-dom';

export default function Success(){
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
