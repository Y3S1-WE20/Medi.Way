# MediWay Hospital Management System

> Modern full‑stack healthcare management platform providing appointment scheduling, patient records, administrative analytics, and secure role-based access.

## 🎯 Project Overview
MediWay unifies patient, doctor and admin experiences into a single system:
- Streamlined appointment booking & availability management
- Secure medical record storage and retrieval
- Real-time reporting & operational insights
- Containerized deployment (Docker / Compose)
- CI workflow (GitHub Actions) with backend & frontend test automation + coverage

## 🔑 Key Capabilities
- **Appointment Management**: Intelligent slot validation prevents double booking (application-level conflict detection, extensible to optimistic locking)
- **Patient Records**: Complete visit & diagnosis history with controlled access
- **Administrative Dashboard**: Aggregated KPIs, doctor load, cancellations, specialization stats
- **Role-Based Security**: Distinct flows for patients, doctors, and admins (separate login paths)
- **Exportable Reports**: PDF & CSV generation (OpenPDF)
- **QR Code Integration**: Fast patient identification via ZXing

## ✨ Features
### 🏥 Appointment Scheduling System
- Doctor Availability & 30‑minute time slots
- Conflict Prevention: Checks existing appointments before persistence
- Status Lifecycle: `PENDING → CONFIRMED/REJECTED → CANCELLED`
- Reschedule & Cancellation endpoints
- Extensible for future concurrency strategies (optimistic/pessimistic locking)

### 👥 Patient Management
- Rich Profile Data: Core identity & emergency contacts
- Medical Records CRUD: Diagnoses, treatments, notes
- QR Code Generation for quick retrieval
- Secure Access Boundaries (service layer separation & profile scoping)

### 📊 Administrative Dashboard
- Workload Analytics: Appointments per doctor per day
- Demographics & Registration Trends
- Specialization & cancellation summaries
- Export: One‑click PDF or CSV downloads

## 🧱 Tech Stack Overview

| Layer | Technology Used |
|---------|---------------------|
| **Frontend** <br> <img width="25" height="25" src="https://github.com/user-attachments/assets/6efa2156-1095-4324-9d87-0ec0278ba78f" /> | React 19 • React Router • Testing Library |
|  **Backend** <br> <img width="25" height="25" src="https://github.com/user-attachments/assets/e5a94093-9d46-498f-91ea-47d1dfd59644" /> | Spring Boot 3 • Java 17 |
|  **Database** <br> <img width="25" height="25" src="https://github.com/user-attachments/assets/9b9e7b37-cbcf-4bec-aafc-943bc195e48b" /> | MySQL (Production) • H2 (Test Profile) |
|  **Containerization** <br> <img width="25" height="25" src="https://github.com/user-attachments/assets/95f79439-e768-4792-b470-866e6b9c5009" /> | Docker • Docker Compose |
|  **CI/CD** <br> <img width="25" height="25" src="https://github.com/user-attachments/assets/db30891b-d844-49d7-b28a-ad88ca0be34b" /> | GitHub Actions (Frontend & Backend Pipelines) |
|  **Build System** | Maven • JaCoCo Test Coverage |
|  **Security** | Spring Security Crypto (Password Hashing) |
|  **Reports** | OpenPDF (PDF Generation) • Native CSV Assembly |
|  **QR Codes** | ZXing Library |



### High-Level Flow
```
React SPA (nginx) → REST API (Spring Boot) → JPA/Hibernate → MySQL/H2
					 ↓
				 Reporting Service (PDF/CSV)
```

## 📦 Prerequisites
- Node.js ≥ 18.x
- Java 17 (Temurin recommended)
- Docker (Desktop) for container-based workflow
- Maven wrapper included (`./mvnw`)

## 🚀 Installation & Setup (Local Dev)
### 1. Clone
```bash
git clone https://github.com/Y3S1-WE20/Medi.Way.git
cd Medi.Way
```
### 2. Frontend
```bash
cd frontend
npm install
npm start   # http://localhost:3000
```
### 3. Backend
In another terminal:
```bash
cd backend
./mvnw spring-boot:run   # http://localhost:8080
```
By default expects MySQL; for tests / light dev switch to H2 by exporting:
```bash
export SPRING_PROFILES_ACTIVE=test
```

## 🧪 Testing
### Backend
```bash
cd backend
./mvnw test
```
Generates coverage at `backend/target/site/jacoco/index.html`.

### Frontend
```bash
cd frontend
npm test -- --watchAll=false
```

## 🐳 Docker / Compose
Build & run all services (MySQL + backend + frontend):
```bash
docker compose up --build
```
Access:
- Frontend: http://localhost:3000
- Backend:  http://localhost:8080
- MySQL:    localhost:3306

Tear down:
```bash
docker compose down
```
Reset database:
```bash
docker compose down -v
```

## ⚙️ Configuration
| Variable | Purpose | Example |
|----------|---------|---------|
| `SPRING_PROFILES_ACTIVE` | Choose profile (`test` uses H2) | `test` |
| `SPRING_DATASOURCE_URL` | JDBC URL when using MySQL | `jdbc:mysql://db:3306/mediway` |
| `REACT_APP_API_BASE_URL` | Frontend build-time API base | `http://localhost:8080` |
| `JAVA_OPTS` | JVM tuning flags | `-Xms256m -Xmx512m` |

## 🔄 Continuous Integration
GitHub Actions workflow runs:
- Backend: Maven build + tests + JaCoCo artifact
- Frontend: Install, lint (optional), tests, build artifact
- Combined summary job for status aggregation

## 🛡 Security & Access
- Separate login routes for Admin, Doctor, Patient
- Password hashing via Spring Security Crypto
- Expandable for JWT/session authentication & fine-grained RBAC


