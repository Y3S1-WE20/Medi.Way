<div align="center">

# 🏥 MediWay Hospital Management System

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge)](CONTRIBUTING.md)

### *Modern full‑stack healthcare management platform providing appointment scheduling, patient records, administrative analytics, and secure role-based access.*

---

</div>

## 📋 Table of Contents
- [🎯 Project Overview](#-project-overview)
- [🔑 Key Capabilities](#-key-capabilities)
- [✨ Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [📦 Prerequisites](#-prerequisites)
- [🚀 Installation & Setup](#-installation--setup)
- [🧪 Testing](#-testing)
- [🐳 Docker Deployment](#-docker-deployment)
- [⚙️ Configuration](#️-configuration)
- [🔄 CI/CD](#-cicd)
- [🛡️ Security](#️-security)

---

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

## 🛠️ Tech Stack

<div align="center">

### Frontend
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![React Router](https://img.shields.io/badge/React_Router-6-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white)
![Testing Library](https://img.shields.io/badge/Testing_Library-Latest-E33332?style=for-the-badge&logo=testinglibrary&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-1.25-009639?style=for-the-badge&logo=nginx&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-Latest-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

### Database
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Testing-0000BB?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### DevOps & Tools
![Docker](https://img.shields.io/badge/Docker-Latest-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker_Compose-Latest-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI%2FCD-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-red?style=for-the-badge)

### Libraries & Integration
![OpenPDF](https://img.shields.io/badge/OpenPDF-PDF_Gen-FF6B6B?style=for-the-badge)
![ZXing](https://img.shields.io/badge/ZXing-QR_Codes-000000?style=for-the-badge)

</div>


---

### 🔄 Architecture Flow
```mermaid
graph LR
    A[React SPA<br/>nginx] -->|REST API| B[Spring Boot<br/>Java 17]
    B -->|JPA/Hibernate| C[(MySQL/H2)]
    B -->|Generate| D[Reports<br/>PDF/CSV]
    B -->|Create| E[QR Codes<br/>ZXing]
```

---

## 📦 Prerequisites

![Node.js](https://img.shields.io/badge/Node.js-%3E%3D18.x-339933?style=flat-square&logo=nodedotjs&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Desktop-2496ED?style=flat-square&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper_Included-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

- **Node.js** ≥ 18.x
- **Java 17** (Temurin recommended)
- **Docker Desktop** for container-based workflow
- **Maven** wrapper included (`./mvnw`)

---

## 🚀 Installation & Setup

### Quick Start (Recommended - Docker)

The fastest way to run the entire application:

```bash
# Clone the repository
git clone https://github.com/Y3S1-WE20/Medi.Way.git
cd Medi.Way

# Start all services with Docker Compose
docker-compose up --build

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
# MySQL: localhost:3306
```

### Manual Setup

#### 1. Clone
```bash
git clone https://github.com/Y3S1-WE20/Medi.Way.git
cd Medi.Way
```

#### 2. Frontend Setup
```bash
cd frontend
npm install
npm start   # http://localhost:3000
```

#### 3. Backend Setup
In another terminal:
```bash
cd backend
./mvnw spring-boot:run   # http://localhost:8080
```

> **Note:** Backend will use H2 in-memory database by default. For MySQL, configure `application.properties` or use Docker setup.
By default expects MySQL; for tests / light dev switch to H2 by exporting:
```bash
export SPRING_PROFILES_ACTIVE=test
```

---

## 🧪 Testing

### 🔧 Backend Testing
```bash
cd backend
./mvnw test
```
📊 Coverage report generated at `backend/target/site/jacoco/index.html`

### ⚛️ Frontend Testing
```bash
cd frontend
npm test -- --watchAll=false
```

---

## 🐳 Docker Deployment

### 🚀 Quick Start
Build & run all services (MySQL + backend + frontend):
```bash
docker compose up --build
```

### 🌐 Access Services
| Service | URL | Port |
|---------|-----|------|
| 🎨 Frontend | http://localhost:3000 | 3000 |
| ⚙️ Backend | http://localhost:8080 | 8080 |
| 🗄️ MySQL | localhost:3306 | 3306 |

### 🛑 Tear Down
```bash
docker compose down
```

### 🔄 Reset Database
```bash
docker compose down -v
```

---

## ⚙️ Configuration

| Variable | Purpose | Example |
|----------|---------|---------|
| 🔧 `SPRING_PROFILES_ACTIVE` | Choose profile (`test` uses H2) | `test` |
| 🗄️ `SPRING_DATASOURCE_URL` | JDBC URL when using MySQL | `jdbc:mysql://db:3306/mediway` |
| 🌐 `REACT_APP_API_BASE_URL` | Frontend build-time API base | `http://localhost:8080` |
| ☕ `JAVA_OPTS` | JVM tuning flags | `-Xms256m -Xmx512m` |

---

## 🔄 CI/CD

![GitHub Actions](https://img.shields.io/badge/Automated-GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

**Automated Pipeline includes:**
- ✅ Backend: Maven build + tests + JaCoCo coverage artifact
- ✅ Frontend: Install, lint (optional), tests, build artifact
- ✅ Combined summary job for status aggregation

---

## 🛡️ Security

![Spring Security](https://img.shields.io/badge/Secured-Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)

**Security Features:**
- 🔐 Separate login routes for Admin, Doctor, and Patient
- 🔒 Password hashing via Spring Security Crypto
- 🎯 Role-based access control (RBAC)
- 🚀 Expandable for JWT/session authentication

---

<div align="center">

### 🌟 Made with ❤️ by the MediWay Team

[![GitHub Stars](https://img.shields.io/github/stars/Y3S1-WE20/Medi.Way?style=social)](https://github.com/Y3S1-WE20/Medi.Way/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/Y3S1-WE20/Medi.Way?style=social)](https://github.com/Y3S1-WE20/Medi.Way/network/members)

**If you find this project helpful, please consider giving it a ⭐!**

</div>
