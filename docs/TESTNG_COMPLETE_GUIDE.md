# MEDI.WAY TestNG Testing Framework - Complete Guide

## 📋 Table of Contents

1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Team Member Responsibilities](#team-member-responsibilities)
4. [Feature 1: Assertions](#feature-1-assertions)
5. [Feature 2: Fixtures (Setup/Teardown)](#feature-2-fixtures-setupteardown)
6. [Feature 3: Test Reporting](#feature-3-test-reporting)
7. [Feature 4: CI/CD Pipeline](#feature-4-cicd-pipeline)
8. [Complete Workflow Demonstration](#complete-workflow-demonstration)
9. [Troubleshooting](#troubleshooting)

---

## Introduction

This document provides a comprehensive guide for implementing and demonstrating TestNG testing framework in the MEDI.WAY healthcare application. The guide covers everything from initial setup to successful test execution, including CI/CD integration with Jenkins, Docker, and AWS EC2.

### What is TestNG?

TestNG (Test Next Generation) is a testing framework inspired by JUnit and NUnit, designed to simplify a broad range of testing needs, from unit testing to integration testing.

**Key Features:**
- Powerful annotations (@Test, @BeforeMethod, @AfterClass, etc.)
- Flexible test configuration
- Support for data-driven testing
- Parallel test execution
- Detailed HTML/XML reporting
- Easy integration with CI/CD tools

---

## Project Overview

### MEDI.WAY Application Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      MEDI.WAY System                        │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐         ┌─────────────────────────────┐   │
│  │   Frontend  │  ◄───►  │         Backend API         │   │
│  │   (React)   │         │     (Spring Boot + Java)    │   │
│  └─────────────┘         └─────────────────────────────┘   │
│                                    │                        │
│                                    ▼                        │
│                          ┌─────────────────┐               │
│                          │     MySQL DB    │               │
│                          └─────────────────┘               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Backend Services Being Tested

| Service | Description | Test Coverage |
|---------|-------------|---------------|
| PatientService | Patient registration, login, health ID management | Unit + Integration |
| DoctorService | Doctor CRUD operations, authentication | Unit + Integration |
| AppointmentService | Appointment booking, scheduling, status management | Unit + Integration |
| MedicalRecordService | Medical record CRUD operations | Unit + Integration |

---

## Team Member Responsibilities

| Member | Feature | Demonstration |
|--------|---------|---------------|
| **Member 1** | Assertions | Various TestNG assertion types (assertEquals, assertTrue, assertNotNull, etc.) |
| **Member 2** | Fixtures (Setup/Teardown) | @BeforeMethod, @AfterMethod, @BeforeClass, @AfterClass annotations |
| **Member 3** | Test Reporting | TestNG HTML reports, custom reporters, Surefire integration |
| **Member 4** | CI/CD Pipeline | Jenkins → TestNG → Docker → DockerHub → AWS EC2 |

---

## Feature 1: Assertions

### 📁 Demo By: Member 1

### Overview

TestNG provides a rich set of assertion methods to verify test conditions. All assertions are in the `org.testng.Assert` class.

### Types of Assertions in MEDI.WAY Tests

#### 1. assertEquals - Verify Expected Values

```java
@Test
public void testPatientRegistration() {
    RegisterRequest request = new RegisterRequest();
    request.fullName = "John Doe";
    request.email = "john@example.com";
    request.password = "password123";
    request.phone = "1234567890";
    request.address = "123 Main St";
    
    RegisterResponse response = patientService.register(request);
    
    // assertEquals verifies that two values are equal
    Assert.assertEquals(response.fullName, "John Doe", 
        "Patient name should match the registered name");
    Assert.assertEquals(response.email, "john@example.com", 
        "Email should match the registered email");
}
```

#### 2. assertNotNull - Verify Non-Null Values

```java
@Test
public void testHealthIdGeneration() {
    RegisterResponse response = patientService.register(validRequest);
    
    // assertNotNull ensures a value is not null
    Assert.assertNotNull(response.healthId, 
        "Health ID should be generated for new patient");
    Assert.assertNotNull(response.id, 
        "Patient ID should be assigned");
}
```

#### 3. assertTrue/assertFalse - Verify Boolean Conditions

```java
@Test
public void testHealthIdFormat() {
    RegisterResponse response = patientService.register(validRequest);
    
    // assertTrue verifies a condition is true
    Assert.assertTrue(response.healthId.length() == 12, 
        "Health ID should be exactly 12 characters");
    Assert.assertTrue(response.healthId.matches("[A-Z0-9]+"), 
        "Health ID should only contain uppercase letters and numbers");
}

@Test
public void testDuplicateEmailPrevention() {
    patientService.register(validRequest);
    
    try {
        patientService.register(validRequest);
        Assert.fail("Should throw exception for duplicate email");
    } catch (IllegalArgumentException e) {
        // assertFalse verifies a condition is false
        Assert.assertFalse(e.getMessage().isEmpty(), 
            "Error message should not be empty");
    }
}
```

#### 4. assertThrows - Verify Exception Handling

```java
@Test
public void testInvalidLoginThrowsException() {
    LoginRequest request = new LoginRequest();
    request.email = "nonexistent@example.com";
    request.password = "wrongpassword";
    
    // assertThrows verifies that a specific exception is thrown
    Assert.assertThrows(IllegalArgumentException.class, () -> {
        patientService.login(request);
    });
}
```

#### 5. assertSame/assertNotSame - Verify Object References

```java
@Test
public void testDoctorRetrieval() {
    Doctor doctor1 = doctorService.get(1L);
    Doctor doctor2 = doctorService.get(1L);
    
    // Testing that different queries return data (not same instance with mock)
    Assert.assertNotSame(doctor1, doctor2, 
        "Different queries should return different object instances");
    Assert.assertEquals(doctor1.getId(), doctor2.getId(), 
        "But they should have the same ID");
}
```

### Complete Assertions Test Class

See: `backend/src/test/java/backend/AssertionsDemoTest.java`

### Assertion Best Practices

1. **Always provide descriptive messages** - Makes debugging easier
2. **Use specific assertions** - Don't always use assertTrue; use assertEquals for equality
3. **Assert one concept per test** - Keeps tests focused and maintainable
4. **Order assertions logically** - Fail fast on basic conditions

---

## Feature 2: Fixtures (Setup/Teardown)

### 📁 Demo By: Member 2

### Overview

TestNG fixtures are methods that run before or after tests to set up and clean up the test environment.

### Fixture Annotations Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                    TEST EXECUTION ORDER                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  @BeforeSuite      ─── Runs ONCE before all tests          │
│       │                                                     │
│  @BeforeTest       ─── Runs before each <test> tag         │
│       │                                                     │
│  @BeforeClass      ─── Runs ONCE before first test method  │
│       │                                                     │
│  @BeforeMethod     ─── Runs before EACH test method        │
│       │                                                     │
│  ┌─────────────┐                                           │
│  │   @Test     │   ─── The actual test method              │
│  └─────────────┘                                           │
│       │                                                     │
│  @AfterMethod      ─── Runs after EACH test method         │
│       │                                                     │
│  @AfterClass       ─── Runs ONCE after last test method    │
│       │                                                     │
│  @AfterTest        ─── Runs after each <test> tag          │
│       │                                                     │
│  @AfterSuite       ─── Runs ONCE after all tests           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Practical Examples for MEDI.WAY

#### @BeforeClass / @AfterClass - Class-Level Setup

```java
public class PatientServiceFixturesTest {
    
    private PatientService patientService;
    private PatientRepository mockRepository;
    
    @BeforeClass
    public void setUpClass() {
        System.out.println("========================================");
        System.out.println("INITIALIZING TEST SUITE FOR PATIENT SERVICE");
        System.out.println("========================================");
        
        // Initialize mock repository
        mockRepository = Mockito.mock(PatientRepository.class);
        
        // Create service with mock
        patientService = new PatientService(mockRepository);
        
        System.out.println("✓ Mock repository initialized");
        System.out.println("✓ Patient service created");
    }
    
    @AfterClass
    public void tearDownClass() {
        System.out.println("========================================");
        System.out.println("CLEANING UP PATIENT SERVICE TEST SUITE");
        System.out.println("========================================");
        
        // Cleanup resources
        patientService = null;
        mockRepository = null;
        
        System.out.println("✓ Resources cleaned up");
    }
}
```

#### @BeforeMethod / @AfterMethod - Method-Level Setup

```java
public class AppointmentServiceFixturesTest {
    
    private Patient testPatient;
    private Doctor testDoctor;
    private Appointment currentAppointment;
    
    @BeforeMethod
    public void setUpMethod() {
        System.out.println("\n--- Setting up test data ---");
        
        // Create fresh test patient
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setHealthId("HEALTH123456");
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        
        // Create fresh test doctor
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Test");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setSpecialization("General");
        
        System.out.println("✓ Test patient created: " + testPatient.getFullName());
        System.out.println("✓ Test doctor created: " + testDoctor.getName());
    }
    
    @AfterMethod
    public void tearDownMethod() {
        System.out.println("--- Cleaning up test data ---");
        
        // Reset test data
        currentAppointment = null;
        
        System.out.println("✓ Test data cleaned");
    }
    
    @Test
    public void testAppointmentCreation() {
        // Test uses fresh testPatient and testDoctor
        currentAppointment = new Appointment();
        currentAppointment.setPatient(testPatient);
        currentAppointment.setDoctor(testDoctor);
        currentAppointment.setDate(LocalDate.now().plusDays(1));
        currentAppointment.setTime(LocalTime.of(10, 0));
        
        Assert.assertNotNull(currentAppointment.getPatient());
        Assert.assertEquals(currentAppointment.getDoctor().getName(), "Dr. Test");
    }
}
```

#### @BeforeSuite / @AfterSuite - Suite-Level Setup

```java
public class GlobalTestSetup {
    
    private static Connection databaseConnection;
    
    @BeforeSuite
    public void globalSetUp() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   MEDI.WAY TEST SUITE STARTING       ║");
        System.out.println("╚══════════════════════════════════════╝");
        
        // Global setup - runs once for entire test suite
        // Example: Initialize test database, start embedded server
        initializeTestEnvironment();
    }
    
    @AfterSuite
    public void globalTearDown() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   MEDI.WAY TEST SUITE COMPLETED      ║");
        System.out.println("╚══════════════════════════════════════╝");
        
        // Global cleanup - runs once after all tests
        cleanupTestEnvironment();
    }
    
    private void initializeTestEnvironment() {
        System.out.println("→ Loading test configuration");
        System.out.println("→ Initializing mock services");
        System.out.println("→ Setting up test data fixtures");
    }
    
    private void cleanupTestEnvironment() {
        System.out.println("→ Closing connections");
        System.out.println("→ Cleaning up temporary files");
        System.out.println("→ Generating test summary");
    }
}
```

### Complete Fixtures Test Class

See: `backend/src/test/java/backend/FixturesDemoTest.java`

### Fixture Best Practices

1. **Use @BeforeClass for expensive setup** - Database connections, service initialization
2. **Use @BeforeMethod for test isolation** - Fresh data for each test
3. **Always clean up in @After methods** - Prevent test contamination
4. **Keep fixtures focused** - Don't over-initialize

---

## Feature 3: Test Reporting

### 📁 Demo By: Member 3

### Overview

TestNG provides built-in reporting capabilities and supports custom reporters for detailed test analysis.

### Default TestNG Reports

After running tests, TestNG generates reports in:
- `target/surefire-reports/` - Surefire plugin reports
- `test-output/` - TestNG's native reports

#### Report Types Generated

```
test-output/
├── index.html              ← Main HTML report (open in browser)
├── testng-results.xml      ← XML results for CI/CD
├── emailable-report.html   ← Email-friendly report
├── junitreports/           ← JUnit-compatible XML
│   └── TEST-*.xml
└── old/                    ← Previous test runs
```

### Configuring Test Reporting

#### 1. pom.xml Configuration

```xml
<build>
    <plugins>
        <!-- Maven Surefire Plugin for Test Execution -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>testng.xml</suiteXmlFile>
                </suiteXmlFiles>
                <reportsDirectory>${project.build.directory}/test-reports</reportsDirectory>
                <testFailureIgnore>false</testFailureIgnore>
                <!-- Generate both TestNG and JUnit reports -->
                <properties>
                    <property>
                        <name>usedefaultlisteners</name>
                        <value>true</value>
                    </property>
                    <property>
                        <name>reporter</name>
                        <value>org.testng.reporters.XMLReporter</value>
                    </property>
                </properties>
            </configuration>
        </plugin>
        
        <!-- Maven Site Plugin for Report Generation -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-site-plugin</artifactId>
            <version>4.0.0-M13</version>
        </plugin>
    </plugins>
</build>

<reporting>
    <plugins>
        <!-- Surefire Report Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.2.5</version>
        </plugin>
    </plugins>
</reporting>
```

### Custom Test Listener for Enhanced Reporting

```java
package backend.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MediWayTestListener implements ITestListener {
    
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    private long startTime;
    private PrintWriter logWriter;
    
    @Override
    public void onStart(ITestContext context) {
        startTime = System.currentTimeMillis();
        
        try {
            logWriter = new PrintWriter(new FileWriter("test-output/mediway-test-log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        log("╔══════════════════════════════════════════════════════════════╗");
        log("║            MEDI.WAY TEST EXECUTION STARTED                   ║");
        log("╠══════════════════════════════════════════════════════════════╣");
        log("║  Suite: " + padRight(context.getName(), 51) + " ║");
        log("║  Time:  " + padRight(getCurrentTime(), 51) + " ║");
        log("╚══════════════════════════════════════════════════════════════╝");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        log("\n▶ STARTING: " + result.getMethod().getMethodName());
        log("  Class: " + result.getTestClass().getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        log("✅ PASSED: " + result.getMethod().getMethodName() + " (" + duration + "ms)");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        long duration = result.getEndMillis() - result.getStartMillis();
        log("❌ FAILED: " + result.getMethod().getMethodName() + " (" + duration + "ms)");
        log("   Error: " + result.getThrowable().getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        log("⏭️ SKIPPED: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onFinish(ITestContext context) {
        long totalTime = System.currentTimeMillis() - startTime;
        int totalTests = passedTests + failedTests + skippedTests;
        double passRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        
        log("\n╔══════════════════════════════════════════════════════════════╗");
        log("║            MEDI.WAY TEST EXECUTION SUMMARY                   ║");
        log("╠══════════════════════════════════════════════════════════════╣");
        log("║  Total Tests:  " + padRight(String.valueOf(totalTests), 45) + " ║");
        log("║  ✅ Passed:    " + padRight(String.valueOf(passedTests), 45) + " ║");
        log("║  ❌ Failed:    " + padRight(String.valueOf(failedTests), 45) + " ║");
        log("║  ⏭️ Skipped:   " + padRight(String.valueOf(skippedTests), 45) + " ║");
        log("║  Pass Rate:    " + padRight(String.format("%.2f%%", passRate), 45) + " ║");
        log("║  Duration:     " + padRight(totalTime + "ms", 45) + " ║");
        log("╚══════════════════════════════════════════════════════════════╝");
        
        if (logWriter != null) {
            logWriter.close();
        }
    }
    
    private void log(String message) {
        System.out.println(message);
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String padRight(String s, int width) {
        return String.format("%-" + width + "s", s);
    }
}
```

### TestNG XML Configuration with Listeners

```xml
<!-- testng.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="MEDI.WAY Test Suite" verbose="2">
    
    <!-- Custom Listeners for Reporting -->
    <listeners>
        <listener class-name="backend.listeners.MediWayTestListener"/>
    </listeners>
    
    <!-- Test Groups -->
    <test name="Unit Tests">
        <groups>
            <run>
                <include name="unit"/>
            </run>
        </groups>
        <classes>
            <class name="backend.AssertionsDemoTest"/>
            <class name="backend.FixturesDemoTest"/>
        </classes>
    </test>
    
    <test name="Integration Tests">
        <groups>
            <run>
                <include name="integration"/>
            </run>
        </groups>
        <classes>
            <class name="backend.IntegrationTest"/>
        </classes>
    </test>
    
</suite>
```

### Running Tests and Viewing Reports

```bash
# Run tests with Maven
mvn clean test

# Generate site report
mvn surefire-report:report

# View reports
open test-output/index.html          # TestNG HTML Report
open target/site/surefire-report.html # Maven Surefire Report
```

### Sample Report Output

```
╔══════════════════════════════════════════════════════════════╗
║            MEDI.WAY TEST EXECUTION SUMMARY                   ║
╠══════════════════════════════════════════════════════════════╣
║  Total Tests:  24                                            ║
║  ✅ Passed:    22                                            ║
║  ❌ Failed:    1                                             ║
║  ⏭️ Skipped:   1                                             ║
║  Pass Rate:    91.67%                                        ║
║  Duration:     1543ms                                        ║
╚══════════════════════════════════════════════════════════════╝
```

---

## Feature 4: CI/CD Pipeline

### 📁 Demo By: Member 4

### Overview

This section covers the complete CI/CD pipeline integrating Jenkins, TestNG, Docker, DockerHub, and AWS EC2.

### Complete Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        MEDI.WAY CI/CD PIPELINE                              │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌─────────────┐
     │  Developer  │
     │  Commits    │
     └──────┬──────┘
            │
            ▼
┌─────────────────────────┐
│        GitHub           │
│     Repository          │
└───────────┬─────────────┘
            │ Webhook Trigger
            ▼
┌─────────────────────────┐
│        Jenkins          │
│    (CI/CD Server)       │
│                         │
│  ┌───────────────────┐  │
│  │ Stage 1: Checkout │  │
│  │   - Clone repo    │  │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 2: Build    │  │
│  │   - mvn compile   │  │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 3: Test     │  │
│  │ - mvn test        │  │
│  │ - TestNG Reports  │  │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 4: Package  │  │
│  │ - mvn package     │  │
│  │ - Create JAR      │  │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 5: Docker   │  │
│  │ - Build images    │  │
│  │ - Tag images      │  │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 6: Push     │  │
│  │ - Push to DockerHub│ │
│  └─────────┬─────────┘  │
│            │            │
│  ┌─────────▼─────────┐  │
│  │ Stage 7: Deploy   │  │
│  │ - SSH to EC2      │  │
│  │ - Docker Compose  │  │
│  └───────────────────┘  │
│                         │
└─────────────────────────┘
            │
            ▼
┌─────────────────────────┐
│      Docker Hub         │
│                         │
│  ┌─────────────────┐    │
│  │ mediway/backend │    │
│  └─────────────────┘    │
│  ┌─────────────────┐    │
│  │ mediway/frontend│    │
│  └─────────────────┘    │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│     AWS EC2 Instance    │
│    (Ubuntu Linux VM)    │
│                         │
│  ┌───────────────────┐  │
│  │  Docker Compose   │  │
│  │                   │  │
│  │ ┌───────────────┐ │  │
│  │ │   Backend     │ │  │
│  │ │   Container   │ │  │
│  │ └───────────────┘ │  │
│  │ ┌───────────────┐ │  │
│  │ │   Frontend    │ │  │
│  │ │   Container   │ │  │
│  │ └───────────────┘ │  │
│  │ ┌───────────────┐ │  │
│  │ │   MySQL       │ │  │
│  │ │   Container   │ │  │
│  │ └───────────────┘ │  │
│  └───────────────────┘  │
│                         │
└─────────────────────────┘
            │
            ▼
    ┌───────────────┐
    │   Users       │
    │   Access App  │
    │   via Browser │
    └───────────────┘
```

---

### Step 1: AWS EC2 Setup

#### 1.1 Launch EC2 Instance

```bash
# Use AWS Console or CLI
# Instance Type: t2.medium (or larger)
# AMI: Ubuntu Server 22.04 LTS
# Security Group Ports:
#   - 22 (SSH)
#   - 80 (HTTP)
#   - 443 (HTTPS)
#   - 8080 (Backend API)
#   - 3000 (Frontend Dev)
#   - 3306 (MySQL - internal only)
```

#### 1.2 Connect to EC2

```bash
# Connect via SSH
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

#### 1.3 Install Docker and Docker Compose

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version
```

---

### Step 2: Docker Configuration

#### 2.1 Backend Dockerfile

```dockerfile
# backend/Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cacheable layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2.2 Frontend Dockerfile

```dockerfile
# frontend/Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source and build
COPY . .
RUN npm run build

# Production image with nginx
FROM nginx:alpine

# Copy built assets
COPY --from=builder /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### 2.3 Frontend Nginx Configuration

```nginx
# frontend/nginx.conf
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    # React Router support
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API proxy
    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
    
    # Static assets caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 2.4 Docker Compose Configuration

```yaml
# docker-compose.yml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: mediway-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-rootpassword}
      MYSQL_DATABASE: ${MYSQL_DATABASE:-mediway}
      MYSQL_USER: ${MYSQL_USER:-mediway}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-mediway123}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    ports:
      - "3306:3306"
    networks:
      - mediway-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # Backend API
  backend:
    image: ${DOCKER_USERNAME:-mediway}/backend:${BUILD_NUMBER:-latest}
    container_name: mediway-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/mediway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER:-mediway}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD:-mediway123}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    ports:
      - "8080:8080"
    networks:
      - mediway-network
    depends_on:
      mysql:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  # Frontend
  frontend:
    image: ${DOCKER_USERNAME:-mediway}/frontend:${BUILD_NUMBER:-latest}
    container_name: mediway-frontend
    ports:
      - "80:80"
    networks:
      - mediway-network
    depends_on:
      - backend
    restart: unless-stopped

networks:
  mediway-network:
    driver: bridge

volumes:
  mysql_data:
```

---

### Step 3: Jenkins Setup

#### 3.1 Install Jenkins (on separate server or EC2)

```bash
# Install Java
sudo apt install -y openjdk-17-jdk

# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install -y jenkins

# Start Jenkins
sudo systemctl enable jenkins
sudo systemctl start jenkins

# Get initial password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

#### 3.2 Install Required Jenkins Plugins

Navigate to Jenkins → Manage Jenkins → Manage Plugins → Available:

- **Pipeline** - For pipeline jobs
- **Git** - Git integration
- **Docker Pipeline** - Docker integration
- **SSH Agent** - SSH credential management
- **TestNG Results Plugin** - TestNG report parsing
- **HTML Publisher** - Publish HTML reports
- **Credentials** - Store credentials securely

#### 3.3 Configure Jenkins Credentials

Navigate to Jenkins → Manage Jenkins → Manage Credentials:

1. **DockerHub Credentials**
   - Kind: Username with password
   - ID: `dockerhub-credentials`
   - Username: Your DockerHub username
   - Password: Your DockerHub password/token

2. **EC2 SSH Key**
   - Kind: SSH Username with private key
   - ID: `ec2-ssh-key`
   - Username: `ubuntu`
   - Private Key: Paste your .pem file content

3. **GitHub Credentials** (if private repo)
   - Kind: Username with password
   - ID: `github-credentials`
   - Username: Your GitHub username
   - Password: GitHub Personal Access Token

---

### Step 4: Jenkins Pipeline (Jenkinsfile)

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    environment {
        // Docker Hub credentials
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_USERNAME = 'your-dockerhub-username'
        
        // EC2 Configuration
        EC2_HOST = 'your-ec2-public-ip'
        EC2_USER = 'ubuntu'
        
        // Build info
        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
    }
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    stages {
        // ============================================
        // Stage 1: Checkout Source Code
        // ============================================
        stage('Checkout') {
            steps {
                echo '📥 Checking out source code from GitHub...'
                checkout scm
                
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                }
            }
        }
        
        // ============================================
        // Stage 2: Build Backend
        // ============================================
        stage('Build Backend') {
            steps {
                echo '🔨 Building backend application...'
                dir('backend') {
                    sh 'mvn clean compile -B'
                }
            }
        }
        
        // ============================================
        // Stage 3: Run TestNG Tests
        // ============================================
        stage('Run TestNG Tests') {
            steps {
                echo '🧪 Running TestNG tests...'
                dir('backend') {
                    sh 'mvn test -B'
                }
            }
            post {
                always {
                    // Publish TestNG Results
                    testNG reportFilenamePattern: '**/testng-results.xml'
                    
                    // Publish HTML Report
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/test-output',
                        reportFiles: 'index.html',
                        reportName: 'TestNG HTML Report'
                    ])
                    
                    // Archive test results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        // ============================================
        // Stage 4: Package Application
        // ============================================
        stage('Package') {
            steps {
                echo '📦 Packaging application...'
                dir('backend') {
                    sh 'mvn package -DskipTests -B'
                }
            }
        }
        
        // ============================================
        // Stage 5: Build Docker Images
        // ============================================
        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        echo '🐳 Building backend Docker image...'
                        dir('backend') {
                            sh """
                                docker build \
                                    -t ${DOCKER_USERNAME}/mediway-backend:${IMAGE_TAG} \
                                    -t ${DOCKER_USERNAME}/mediway-backend:latest \
                                    .
                            """
                        }
                    }
                }
                stage('Build Frontend Image') {
                    steps {
                        echo '🐳 Building frontend Docker image...'
                        dir('frontend') {
                            sh """
                                docker build \
                                    -t ${DOCKER_USERNAME}/mediway-frontend:${IMAGE_TAG} \
                                    -t ${DOCKER_USERNAME}/mediway-frontend:latest \
                                    .
                            """
                        }
                    }
                }
            }
        }
        
        // ============================================
        // Stage 6: Push to Docker Hub
        // ============================================
        stage('Push to Docker Hub') {
            steps {
                echo '📤 Pushing images to Docker Hub...'
                sh """
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    
                    docker push ${DOCKER_USERNAME}/mediway-backend:${IMAGE_TAG}
                    docker push ${DOCKER_USERNAME}/mediway-backend:latest
                    
                    docker push ${DOCKER_USERNAME}/mediway-frontend:${IMAGE_TAG}
                    docker push ${DOCKER_USERNAME}/mediway-frontend:latest
                    
                    docker logout
                """
            }
        }
        
        // ============================================
        // Stage 7: Deploy to AWS EC2
        // ============================================
        stage('Deploy to EC2') {
            steps {
                echo '🚀 Deploying to AWS EC2...'
                sshagent(['ec2-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                            cd /home/ubuntu/mediway
                            
                            # Pull latest images
                            docker-compose pull
                            
                            # Stop existing containers
                            docker-compose down
                            
                            # Start new containers
                            export BUILD_NUMBER=${IMAGE_TAG}
                            export DOCKER_USERNAME=${DOCKER_USERNAME}
                            docker-compose up -d
                            
                            # Cleanup old images
                            docker image prune -f
                            
                            # Show running containers
                            docker-compose ps
                        '
                    """
                }
            }
        }
        
        // ============================================
        // Stage 8: Health Check
        // ============================================
        stage('Health Check') {
            steps {
                echo '🏥 Running health checks...'
                script {
                    // Wait for services to start
                    sleep(time: 30, unit: 'SECONDS')
                    
                    // Check backend health
                    sh """
                        curl -f http://${EC2_HOST}:8080/actuator/health || exit 1
                    """
                    
                    // Check frontend
                    sh """
                        curl -f http://${EC2_HOST}/ || exit 1
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            echo "🌐 Application available at: http://${EC2_HOST}"
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        always {
            // Clean up workspace
            cleanWs()
        }
    }
}
```

---

### Step 5: GitHub Webhook Configuration

#### 5.1 Configure Webhook in GitHub

1. Go to your repository → Settings → Webhooks → Add webhook
2. Configure:
   - Payload URL: `http://your-jenkins-url:8080/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`
   - Active: ✓

#### 5.2 Configure Jenkins Job

1. Create new Pipeline job in Jenkins
2. Configure:
   - Build Triggers: ✓ GitHub hook trigger for GITScm polling
   - Pipeline: Pipeline script from SCM
   - SCM: Git
   - Repository URL: Your GitHub repo URL
   - Script Path: `Jenkinsfile`

---

### Step 6: AWS EC2 Deployment Setup

#### 6.1 Prepare EC2 for Deployment

```bash
# SSH into EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# Create application directory
mkdir -p /home/ubuntu/mediway
cd /home/ubuntu/mediway

# Create docker-compose.yml (copy content from Step 2.4)
nano docker-compose.yml

# Create environment file
cat > .env << EOF
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_DATABASE=mediway
MYSQL_USER=mediway
MYSQL_PASSWORD=your_secure_password
DOCKER_USERNAME=your-dockerhub-username
EOF

# Set permissions
chmod 600 .env
```

#### 6.2 Initial Manual Deployment Test

```bash
# Login to DockerHub
docker login

# Pull images
docker-compose pull

# Start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

---

### Complete Workflow Summary

```
┌────────────────────────────────────────────────────────────────────────┐
│                     COMPLETE CI/CD WORKFLOW                            │
└────────────────────────────────────────────────────────────────────────┘

   Developer                GitHub                  Jenkins
      │                        │                       │
      │  1. git push           │                       │
      ├───────────────────────►│                       │
      │                        │  2. Webhook trigger   │
      │                        ├──────────────────────►│
      │                        │                       │
      │                        │                       │  3. Checkout
      │                        │◄──────────────────────┤
      │                        │                       │
      │                        │                       │  4. Build
      │                        │                       │  (mvn compile)
      │                        │                       │
      │                        │                       │  5. Test
      │                        │                       │  (mvn test - TestNG)
      │                        │                       │
      │                        │                       │  6. Package
      │                        │                       │  (mvn package)
      │                        │                       │
      │                        │                       │
                                                       │
                               DockerHub               │  7. Build Docker Images
                                  │◄───────────────────┤  8. Push to DockerHub
                                  │                    │
                                  │                    │
                               AWS EC2                 │
                                  │◄───────────────────┤  9. Deploy
                                  │                    │     (docker-compose up)
                                  │                    │
                                  │                    │  10. Health Check
                               Users                   │
                                  │                    │
                                  │  Access App        │
                                  │  http://ec2-ip     │
```

---

## Complete Workflow Demonstration

### How Each Team Member Demonstrates Their Feature

#### Member 1: Assertions Demo

```bash
# Navigate to project directory
cd /path/to/MEDI.WAY/backend

# Run specific test class
mvn test -Dtest=AssertionsDemoTest

# Expected output shows various assertion types in action
```

**Presentation Points:**
1. Show assertEquals for value comparison
2. Demonstrate assertNotNull for null checks
3. Display assertTrue/assertFalse for boolean conditions
4. Show assertThrows for exception handling
5. Explain assertion messages for debugging

#### Member 2: Fixtures Demo

```bash
# Run fixtures test
mvn test -Dtest=FixturesDemoTest

# Observe console output showing execution order
```

**Presentation Points:**
1. Show @BeforeSuite initialization
2. Demonstrate @BeforeClass setup
3. Display @BeforeMethod before each test
4. Show @AfterMethod cleanup
5. Demonstrate @AfterClass and @AfterSuite

#### Member 3: Test Reporting Demo

```bash
# Run all tests
mvn clean test

# Generate site report
mvn surefire-report:report

# Open reports
open test-output/index.html
open target/site/surefire-report.html
```

**Presentation Points:**
1. Show TestNG HTML report structure
2. Demonstrate custom listener output
3. Display test statistics and pass rates
4. Show failed test details
5. Demonstrate CI integration with Jenkins reports

#### Member 4: CI/CD Pipeline Demo

```bash
# Trigger build by pushing code
git add .
git commit -m "Trigger CI/CD pipeline"
git push origin main

# Show Jenkins pipeline execution
# Navigate to Jenkins UI
```

**Presentation Points:**
1. Show GitHub webhook trigger
2. Demonstrate Jenkins pipeline stages
3. Display TestNG test execution in Jenkins
4. Show Docker image build and push
5. Demonstrate EC2 deployment
6. Verify running application

---

## Troubleshooting

### Common Issues and Solutions

#### TestNG Issues

| Issue | Solution |
|-------|----------|
| Tests not found | Ensure test classes are in `src/test/java` |
| TestNG annotations not recognized | Check TestNG dependency in pom.xml |
| Tests timing out | Increase timeout in @Test annotation |

#### Docker Issues

| Issue | Solution |
|-------|----------|
| Image build fails | Check Dockerfile syntax, ensure dependencies |
| Container won't start | Check docker logs, verify ports |
| Network issues | Use `docker network create` if needed |

#### Jenkins Issues

| Issue | Solution |
|-------|----------|
| Pipeline fails at checkout | Verify GitHub credentials |
| Docker push fails | Check DockerHub credentials |
| EC2 deployment fails | Verify SSH key and EC2 security groups |

#### AWS EC2 Issues

| Issue | Solution |
|-------|----------|
| Cannot connect | Check security group rules, key permissions |
| Services not accessible | Verify ports are open in security group |
| Out of memory | Upgrade instance type or increase swap |

---

## Quick Reference Commands

```bash
# Running Tests
mvn test                              # Run all tests
mvn test -Dtest=TestClass             # Run specific test class
mvn test -Dtest=TestClass#testMethod  # Run specific test method
mvn test -Dgroups="unit"              # Run tests in group

# Docker Commands
docker build -t image-name .          # Build image
docker-compose up -d                   # Start containers
docker-compose logs -f                 # View logs
docker-compose down                    # Stop containers
docker system prune -a                 # Clean up

# Jenkins
# Access at http://jenkins-ip:8080

# AWS EC2
ssh -i key.pem ubuntu@ec2-ip          # Connect to EC2
```

---

## Conclusion

This guide provides a complete workflow for demonstrating TestNG testing in the MEDI.WAY application, from initial setup to CI/CD deployment. Each team member has clearly defined responsibilities and demonstration points.

**Key Takeaways:**
1. **Assertions** - Verify test conditions and expected outcomes
2. **Fixtures** - Set up and clean up test environments
3. **Reporting** - Generate and analyze test results
4. **CI/CD** - Automate the entire testing and deployment pipeline

---

*Document Version: 1.0*  
*Last Updated: March 2026*  
*Project: MEDI.WAY Healthcare Application*
