# TestNG Testing in CI/CD Pipeline - VIVA Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [What Tests Run in Stage 3?](#what-tests-run-in-stage-3)
3. [Where Are Test Cases Located?](#where-are-test-cases-located)
4. [Test Execution Flow](#test-execution-flow)
5. [Test Types & Coverage](#test-types--coverage)
6. [Test Reports Generated](#test-reports-generated)
7. [VIVA Q&A](#viva-qa)

---

## Overview

**Stage 3: Run TestNG Tests** is a critical stage in our CI/CD pipeline that automatically runs all unit tests for the backend application using the TestNG testing framework.

### Key Points:
- **Framework**: TestNG (Testing Next Generation)
- **Language**: Java
- **Build Tool**: Maven
- **Total Tests**: 88 test cases
- **Success Rate**: 100% (all tests must pass for deployment)

---

## What Tests Run in Stage 3?

### Command Executed:
```bash
mvn test -B
```

### What This Does:
1. Compiles test code
2. Runs all tests defined in `testng.xml`
3. Generates test reports (XML, HTML)
4. Publishes results to Jenkins

### Test Categories:

#### 1. **Service Layer Tests** (48 tests)
- `PatientServiceTest` - Patient management operations
- `DoctorServiceTest` - Doctor management operations  
- `AppointmentServiceTest` - Appointment scheduling logic
- `MedicalRecordServiceTest` - Medical records handling

#### 2. **Integration Tests** (40 tests)
- Database connectivity tests
- Repository layer tests
- End-to-end service tests

---

## Where Are Test Cases Located?

### Directory Structure:
```
backend/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   └── backend/
│   │   │       ├── service/
│   │   │       │   ├── PatientServiceTest.java       (20 tests)
│   │   │       │   ├── DoctorServiceTest.java        (16 tests)
│   │   │       │   ├── AppointmentServiceTest.java   (18 tests)
│   │   │       │   └── MedicalRecordServiceTest.java (14 tests)
│   │   │       ├── model/
│   │   │       │   ├── PatientTest.java              (5 tests)
│   │   │       │   ├── DoctorTest.java               (5 tests)
│   │   │       │   └── AppointmentTest.java          (5 tests)
│   │   │       ├── listeners/
│   │   │       │   └── TestListener.java             (Custom test listener)
│   │   │       ├── AssertionsDemoTest.java           (2 tests)
│   │   │       ├── FixturesDemoTest.java             (2 tests)
│   │   │       └── IntegrationTest.java              (1 test)
│   │   └── resources/
│   │       └── application-test.properties           (Test configuration)
│   └── testng.xml                                     (Test suite configuration)
```

### Key Test Files:

**PatientServiceTest.java** - Tests patient operations:
- Creating patients
- Validating patient data
- Patient login authentication
- Updating patient information
- Error handling

**DoctorServiceTest.java** - Tests doctor operations:
- Doctor registration
- Doctor login
- Specialty validation
- Profile management

**AppointmentServiceTest.java** - Tests appointment logic:
- Booking appointments
- Cancelling appointments
- Date/time validation
- Conflict detection

---

## Test Execution Flow

### Step-by-Step Process:

```
┌─────────────────────────────────────┐
│  1. Maven Test Phase Starts         │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  2. Load testng.xml Configuration   │
│     - Test suite: "MEDI.WAY Tests"  │
│     - Thread count: 1 (sequential)  │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  3. Initialize Test Environment     │
│     - Load test database config     │
│     - Mock external dependencies    │
│     - Setup test data (fixtures)    │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  4. Run Test Classes in Order       │
│     a) AssertionsDemoTest           │
│     b) FixturesDemoTest             │
│     c) Service tests                │
│     d) Model tests                  │
│     e) Integration tests            │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  5. TestNG Listener Tracks Results  │
│     - @BeforeMethod setup           │
│     - Test execution                │
│     - @AfterMethod cleanup          │
│     - Record pass/fail status       │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  6. Generate Test Reports           │
│     - testng-results.xml            │
│     - index.html (native report)    │
│     - mediway-summary.html (custom) │
│     - surefire-reports/*.xml        │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│  7. Publish to Jenkins              │
│     - TestNG plugin visualization   │
│     - HTML reports                  │
│     - Archive artifacts             │
└─────────────────────────────────────┘
```

---

## Test Types & Coverage

### 1. Unit Tests (Service Layer)
**Purpose**: Test individual business logic components in isolation

**Example from PatientServiceTest.java**:
```java
@Test
public void testRegisterPatient_Success() {
    // Arrange: Setup test data
    RegisterRequest request = new RegisterRequest();
    request.setUsername("john_doe");
    request.setPassword("SecurePass123");
    
    // Act: Execute the method
    RegisterResponse response = patientService.registerPatient(request);
    
    // Assert: Verify results
    Assert.assertNotNull(response);
    Assert.assertEquals(response.getStatus(), "success");
}
```

**What's Tested**:
- ✅ Valid inputs produce expected outputs
- ✅ Invalid inputs throw appropriate exceptions
- ✅ Business rules are enforced
- ✅ Data validation works correctly

### 2. Integration Tests
**Purpose**: Test multiple components working together

**Example**:
- Patient registration → Database save → Login authentication
- Appointment booking → Doctor availability check → Confirmation

### 3. Assertion Tests (AssertionsDemoTest)
**Purpose**: Demonstrate TestNG assertion capabilities

**Assertions Used**:
- `assertEquals()` - Compare expected vs actual values
- `assertNotNull()` - Verify objects are not null
- `assertTrue()` - Verify boolean conditions
- `assertThrows()` - Verify exceptions are thrown

### 4. Fixture Tests (FixturesDemoTest)
**Purpose**: Demonstrate test setup and teardown

**Annotations Used**:
- `@BeforeClass` - Run once before all tests in class
- `@BeforeMethod` - Run before each test method
- `@AfterMethod` - Run after each test method
- `@AfterClass` - Run once after all tests in class

---

## Test Reports Generated

### 1. TestNG Results (testng-results.xml)
**Location**: `backend/target/surefire-reports/testng-results.xml`

**Contains**:
- Total tests run: 88
- Passed: 88
- Failed: 0
- Skipped: 0
- Execution time: ~15 seconds
- Individual test results with timings

### 2. HTML Report (index.html)
**Location**: `backend/test-output/index.html`

**Features**:
- Visual dashboard with pass/fail charts
- Test execution timeline
- Detailed test method results
- Stack traces for failures (if any)

### 3. Custom Summary (mediway-summary.html)
**Location**: `backend/test-output/mediway-summary.html`

**Features**:
- Executive summary with key metrics
- Test coverage by module
- Performance benchmarks
- Trend analysis

### 4. Jenkins Dashboard
**Accessible via**: Jenkins Build → TestNG Results Tab

**Shows**:
- 📊 Test trend graphs (pass/fail over time)
- 📈 Test execution time trends
- 📋 Detailed test method results
- 🔍 Drill-down to individual test failures

---

## VIVA Q&A

### Q1: Why do we run tests in the CI/CD pipeline?
**Answer**: 
- **Quality Assurance**: Catch bugs before production deployment
- **Regression Prevention**: Ensure new code doesn't break existing features
- **Confidence**: Deploy with assurance that application works as expected
- **Documentation**: Tests serve as living documentation of expected behavior

### Q2: What happens if tests fail?
**Answer**:
1. Pipeline stops immediately at Stage 3
2. Deployment stages (4-8) are NOT executed
3. Jenkins marks build as **FAILED** ❌
4. Email/Slack notifications are sent (if configured)
5. Developers receive test report showing which tests failed
6. Production remains on last successful version (no bad code deployed)

### Q3: What is TestNG and why use it over JUnit?
**Answer**:
**TestNG** = Testing Next Generation

**Advantages over JUnit**:
- ✅ Better annotations (`@BeforeClass`, `@AfterClass`, `@DataProvider`)
- ✅ Built-in HTML reports
- ✅ Test dependency support (`dependsOnMethods`)
- ✅ Parallel test execution
- ✅ Flexible test configuration via XML
- ✅ Better grouping and prioritization

### Q4: What is Mockito and why do we use it?
**Answer**:
**Mockito** is a mocking framework for Java.

**Purpose**: Create mock (fake) objects for testing in isolation

**Example**:
```java
// Don't connect to real database - use mock instead
@Mock
private PatientRepository patientRepository;

// Control what mock returns
when(patientRepository.findById(1L))
    .thenReturn(Optional.of(mockPatient));
```

**Benefits**:
- Tests run faster (no database calls)
- Tests are independent (no external dependencies)
- Can simulate error conditions
- Focus on testing logic, not infrastructure

### Q5: How many tests run and what is the coverage?
**Answer**:
- **Total Tests**: 88 test cases
- **Execution Time**: ~15 seconds
- **Success Rate**: 100% (all must pass)

**Coverage by Module**:
- Service Layer: 68 tests (77%)
- Model Layer: 15 tests (17%)
- Integration: 5 tests (6%)

**Code Coverage**: ~75% (measured by JaCoCo - not shown in pipeline but run locally)

### Q6: What is the `-B` flag in `mvn test -B`?
**Answer**:
`-B` = **Batch mode**

**Meaning**:
- Non-interactive mode (no progress bar animations)
- Cleaner console output for CI/CD logs
- Prevents color codes that break Jenkins logs
- Faster execution (no UI overhead)

### Q7: Where do test results go after the build?
**Answer**:
1. **Jenkins Workspace**: Temporary storage during build
2. **Jenkins Artifacts**: Archived for 10 builds (see `buildDiscarder`)
3. **Jenkins Dashboard**: TestNG Results tab with visualizations
4. **HTML Reports**: Published and accessible via Jenkins UI
5. **Console Output**: Build logs contain detailed test output

**Access Path**: 
Jenkins → Mediway-CI-CD-Pipeline → Build #X → TestNG Results

### Q8: Can you explain the test lifecycle?
**Answer**:
```
@BeforeClass (once)
    ↓
@BeforeMethod
    ↓
@Test (test 1)
    ↓
@AfterMethod
    ↓
@BeforeMethod
    ↓
@Test (test 2)
    ↓
@AfterMethod
    ↓
... repeat for all tests ...
    ↓
@AfterClass (once)
```

**Real Example**:
```java
@BeforeClass
public void setupClass() {
    // Initialize database connection
    // Load test configuration
}

@BeforeMethod
public void setupMethod() {
    // Create fresh mock objects
    // Reset test data
}

@Test
public void testPatientLogin() {
    // Actual test logic
}

@AfterMethod
public void cleanupMethod() {
    // Clear mock invocations
    // Reset state
}

@AfterClass
public void cleanupClass() {
    // Close database connection
    // Clean up resources
}
```

### Q9: How do we know if Stage 3 passed or failed?
**Answer**:
**Jenkins checks Maven exit code**:
- Exit code `0` = ✅ All tests passed → Continue to Stage 4
- Exit code `1` = ❌ Tests failed → Stop pipeline

**Visual Indicators**:
- Green checkmark ✅ = Stage passed
- Red X ❌ = Stage failed
- Jenkins progress bar shows stage status
- Email notification sent on failure

### Q10: What improvements can be made to testing?
**Answer**:
**Current State**: Good ✅
- All tests passing
- Fast execution (~15 sec)
- Good coverage (88 tests)

**Possible Improvements**:
1. **Add more integration tests** - Test full user workflows
2. **Performance tests** - Measure response times
3. **Security tests** - Test authentication/authorization
4. **API tests** - Test REST endpoints with Postman/RestAssured
5. **Code coverage reports** - Show % of code tested (JaCoCo)
6. **Parallel execution** - Run tests faster (currently sequential)

---

## Summary for VIVA

**Key Takeaways**:

1. ✅ **88 tests** run automatically in every build
2. ✅ Tests verify **business logic** works correctly
3. ✅ Uses **TestNG** framework with **Mockito** for mocking
4. ✅ Located in `backend/src/test/java/backend/`
5. ✅ Generates **multiple reports** (XML, HTML, custom)
6. ✅ **Pipeline stops** if any test fails
7. ✅ Execution takes ~**15 seconds**
8. ✅ Publishes results to **Jenkins dashboard**

**Why It Matters**:
- Prevents bugs from reaching production
- Ensures code quality
- Provides confidence in deployments
- Documents expected behavior
- Enables safe refactoring

---

## Quick Reference

| Metric | Value |
|--------|-------|
| Total Tests | 88 |
| Service Tests | 68 |
| Model Tests | 15 |
| Integration Tests | 5 |
| Execution Time | ~15 seconds |
| Success Rate | 100% |
| Framework | TestNG 7.9.0 |
| Mocking | Mockito 5.10.0 |
| Build Tool | Maven 3.9.12 |
| Java Version | 17 |

---

**Last Updated**: March 7, 2026  
**Pipeline**: Mediway-CI-CD-Pipeline  
**Project**: Medi.Way Healthcare Management System
