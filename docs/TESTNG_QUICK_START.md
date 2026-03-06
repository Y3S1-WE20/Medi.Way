# MEDI.WAY TestNG Quick Start Guide

## 🚀 Quick Commands

### Run All Tests
```bash
cd backend
mvn test
```

### Run Specific Test Classes
```bash
# Member 1: Assertions Demo
mvn test -Dtest=AssertionsDemoTest

# Member 2: Fixtures Demo
mvn test -Dtest=FixturesDemoTest

# Member 3: View Reports (after running tests)
open test-output/index.html

# All Service Tests
mvn test -Dtest=PatientServiceTest,DoctorServiceTest,AppointmentServiceTest
```

### Run Tests by Groups
```bash
# Unit tests only
mvn test -Dgroups=unit

# Integration tests only
mvn test -Dgroups=integration

# Assertions demos
mvn test -Dgroups=assertions

# Fixtures demos
mvn test -Dgroups=fixtures
```

### Generate Reports
```bash
# Run tests and generate reports
mvn clean test

# Generate Maven site report
mvn surefire-report:report

# View reports
open test-output/index.html                    # TestNG Report
open test-output/mediway-summary.html          # Custom Summary
open target/site/surefire-report.html          # Maven Report
```

## 📊 Report Locations

| Report | Location |
|--------|----------|
| TestNG HTML | `test-output/index.html` |
| Custom Summary | `test-output/mediway-summary.html` |
| Test Log | `test-output/mediway-test-log.txt` |
| Maven Surefire | `target/site/surefire-report.html` |
| XML Results | `test-output/testng-results.xml` |

## 🐳 Docker Commands

### Build and Run with Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild images
docker-compose build --no-cache
```

### Build Individual Images
```bash
# Backend
cd backend
docker build -t mediway-backend .

# Frontend
cd frontend
docker build -t mediway-frontend .
```

## 🔧 Troubleshooting

### Tests Not Found
Make sure test files are in `src/test/java/backend/`

### Compilation Errors
```bash
mvn clean compile test-compile
```

### Check for Errors
```bash
mvn test -X  # Debug mode
```

## 📁 Test File Structure

```
backend/src/test/java/backend/
├── AssertionsDemoTest.java      # Member 1
├── FixturesDemoTest.java        # Member 2
├── IntegrationTest.java         # Integration tests
├── listeners/
│   └── MediWayTestListener.java # Member 3
├── model/
│   └── ModelTest.java           # Model tests
└── service/
    ├── PatientServiceTest.java
    ├── DoctorServiceTest.java
    └── AppointmentServiceTest.java
```

## 🎯 Member Demonstrations

### Member 1: Assertions
- File: `AssertionsDemoTest.java`
- Features: assertEquals, assertNotNull, assertTrue, assertThrows, etc.
- Run: `mvn test -Dtest=AssertionsDemoTest`

### Member 2: Fixtures
- File: `FixturesDemoTest.java`
- Features: @BeforeSuite, @BeforeClass, @BeforeMethod, @AfterMethod, etc.
- Run: `mvn test -Dtest=FixturesDemoTest`

### Member 3: Reporting
- File: `listeners/MediWayTestListener.java`
- Features: Custom reports, HTML output, test statistics
- Run: `mvn test` then `open test-output/index.html`

### Member 4: CI/CD
- File: `Jenkinsfile` (root)
- Features: Jenkins pipeline, Docker, AWS EC2 deployment
- See: `docs/TESTNG_COMPLETE_GUIDE.md` for full setup
