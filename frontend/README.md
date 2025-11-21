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

## 🧱 Technical Architecture
| Layer | Technology |
|-------|------------|
| Frontend | React 19, React Router, Testing Library |
| Backend | Spring Boot 3 (Java 17) |
| Persistence | MySQL (prod) / H2 (test profile) |
| Build | Maven, JaCoCo coverage |
| Security | Spring Security Crypto (password hashing) |
| Reports | OpenPDF (PDF), native CSV assembly |
| QR Codes | ZXing |
| Containerization | Docker & Docker Compose |
| CI/CD | GitHub Actions (frontend & backend jobs) |

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

## 📈 Future Enhancements
- Optimistic locking on appointment rows (`@Version` field)
- Full audit trail for record modifications
- Role-based API gateway / OAuth2 integration
- Container healthchecks & observability (Prometheus/Grafana)

## 🤝 Contributing
1. Fork & branch: `feat/<description>`
2. Run tests & ensure coverage stays ≥ target
3. Open PR with summary & rationale

## 📝 License
Project license can be added here (e.g. MIT) if required.

---
_Generated & maintained with assistance from GitHub Copilot._
