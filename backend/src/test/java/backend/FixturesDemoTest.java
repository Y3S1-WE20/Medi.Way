package backend;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.MedicalRecord;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.DoctorRepository;
import backend.repository.PatientRepository;
import backend.service.AppointmentService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ============================================================
 * MEMBER 2: FIXTURES (SETUP/TEARDOWN) DEMONSTRATION
 * ============================================================
 * 
 * This test class demonstrates TestNG fixture annotations:
 * 
 * 1. @BeforeSuite   - Runs ONCE before all tests in the suite
 * 2. @AfterSuite    - Runs ONCE after all tests in the suite
 * 3. @BeforeTest    - Runs before each <test> tag in testng.xml
 * 4. @AfterTest     - Runs after each <test> tag in testng.xml
 * 5. @BeforeClass   - Runs ONCE before the first test method in each class
 * 6. @AfterClass    - Runs ONCE after all test methods in each class
 * 7. @BeforeMethod  - Runs before EACH test method
 * 8. @AfterMethod   - Runs after EACH test method
 * 9. @BeforeGroups  - Runs before the first test in specified groups
 * 10. @AfterGroups  - Runs after the last test in specified groups
 * 
 * Run with: mvn test -Dtest=FixturesDemoTest
 * ============================================================
 */
public class FixturesDemoTest {
    
    // Class-level resources (initialized in @BeforeClass)
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    
    private AppointmentService appointmentService;
    
    // Test data
    private Patient testPatient;
    private Doctor testDoctor;
    private Appointment currentAppointment;
    
    // Counters for demonstration
    private static int suiteSetupCount = 0;
    private static int classSetupCount = 0;
    private int methodSetupCount = 0;
    
    // ============================================================
    // @BeforeSuite - Runs ONCE before ALL tests in the suite
    // ============================================================
    
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        suiteSetupCount++;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       @BeforeSuite - GLOBAL SUITE INITIALIZATION           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("→ Suite setup count: " + suiteSetupCount);
        System.out.println("→ This runs ONCE before any test in the entire suite");
        System.out.println("→ Use for: Global configuration, database connections, etc.");
        System.out.println("");
        
        // Example: Global configuration
        System.setProperty("test.environment", "UNIT_TEST");
        System.setProperty("test.log.level", "DEBUG");
        
        System.out.println("✓ Test environment property set: " + System.getProperty("test.environment"));
        System.out.println("✓ Log level property set: " + System.getProperty("test.log.level"));
    }
    
    // ============================================================
    // @AfterSuite - Runs ONCE after ALL tests in the suite
    // ============================================================
    
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       @AfterSuite - GLOBAL SUITE CLEANUP                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("→ This runs ONCE after all tests in the entire suite complete");
        System.out.println("→ Use for: Closing connections, generating final reports, etc.");
        System.out.println("");
        
        // Example: Cleanup global configuration
        System.clearProperty("test.environment");
        System.clearProperty("test.log.level");
        
        System.out.println("✓ Test properties cleared");
        System.out.println("✓ Total class setups: " + classSetupCount);
    }
    
    // ============================================================
    // @BeforeTest - Runs before each <test> in testng.xml
    // ============================================================
    
    @BeforeTest(alwaysRun = true)
    public void setUpTest() {
        System.out.println("\n┌────────────────────────────────────────────────────────────┐");
        System.out.println("│       @BeforeTest - TEST TAG INITIALIZATION                │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("→ This runs before each <test> tag in testng.xml");
        System.out.println("→ Use for: Test-level configuration, data setup");
        System.out.println("");
    }
    
    // ============================================================
    // @AfterTest - Runs after each <test> in testng.xml
    // ============================================================
    
    @AfterTest(alwaysRun = true)
    public void tearDownTest() {
        System.out.println("\n┌────────────────────────────────────────────────────────────┐");
        System.out.println("│       @AfterTest - TEST TAG CLEANUP                        │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("→ This runs after each <test> tag in testng.xml completes");
        System.out.println("→ Total method setups in this test: " + methodSetupCount);
        System.out.println("");
    }
    
    // ============================================================
    // @BeforeClass - Runs ONCE before first method in this class
    // ============================================================
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        classSetupCount++;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       MEMBER 2: FIXTURES DEMONSTRATION                     ║");
        System.out.println("║       Testing MEDI.WAY Healthcare Application              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n┌────────────────────────────────────────────────────────────┐");
        System.out.println("│       @BeforeClass - CLASS INITIALIZATION                  │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("→ Class setup count: " + classSetupCount);
        System.out.println("→ This runs ONCE before the first test method in this class");
        System.out.println("→ Use for: Service initialization, mock setup, etc.");
        System.out.println("");
        
        // Initialize Mockito
        MockitoAnnotations.openMocks(this);
        
        // Create service with mocked repositories
        appointmentService = new AppointmentService(appointmentRepository, doctorRepository, patientRepository);
        
        System.out.println("✓ Mockito initialized");
        System.out.println("✓ Mock repositories created");
        System.out.println("✓ AppointmentService instantiated");
    }
    
    // ============================================================
    // @AfterClass - Runs ONCE after last method in this class
    // ============================================================
    
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        System.out.println("\n┌────────────────────────────────────────────────────────────┐");
        System.out.println("│       @AfterClass - CLASS CLEANUP                          │");
        System.out.println("└────────────────────────────────────────────────────────────┘");
        System.out.println("→ This runs ONCE after all test methods in this class");
        System.out.println("→ Total methods executed: " + methodSetupCount);
        System.out.println("");
        
        // Cleanup class-level resources
        appointmentService = null;
        
        System.out.println("✓ Service reference cleared");
        System.out.println("✓ Class cleanup complete");
    }
    
    // ============================================================
    // @BeforeMethod - Runs before EACH test method
    // ============================================================
    
    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        methodSetupCount++;
        
        System.out.println("\n  ┌──────────────────────────────────────────────────────────┐");
        System.out.println("  │   @BeforeMethod - METHOD SETUP (#" + methodSetupCount + ")                      │");
        System.out.println("  └──────────────────────────────────────────────────────────┘");
        
        // Create fresh test data for each test
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setHealthId("HEALTH123456");
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        testPatient.setPhone("1234567890");
        testPatient.setAddress("123 Test Street");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Test");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setSpecialization("General Medicine");
        
        currentAppointment = null; // Reset for each test
        
        System.out.println("  → Fresh test patient created: " + testPatient.getFullName());
        System.out.println("  → Fresh test doctor created: " + testDoctor.getName());
    }
    
    // ============================================================
    // @AfterMethod - Runs after EACH test method
    // ============================================================
    
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {
        System.out.println("\n  ┌──────────────────────────────────────────────────────────┐");
        System.out.println("  │   @AfterMethod - METHOD CLEANUP                          │");
        System.out.println("  └──────────────────────────────────────────────────────────┘");
        
        // Clean up test data
        if (currentAppointment != null) {
            System.out.println("  → Cleaning up appointment: " + currentAppointment.getId());
        }
        
        testPatient = null;
        testDoctor = null;
        currentAppointment = null;
        
        System.out.println("  → Test data cleaned up");
        System.out.println("");
    }
    
    // ============================================================
    // @BeforeGroups / @AfterGroups - Group-level fixtures
    // ============================================================
    
    @BeforeGroups(groups = {"appointment-tests"}, alwaysRun = true)
    public void setUpAppointmentGroup() {
        System.out.println("\n  ▶ @BeforeGroups [appointment-tests] - Setting up appointment test group");
        
        // Setup common mocks for appointment tests
        List<Appointment> emptyList = new ArrayList<>();
        when(appointmentRepository.findByDoctorAndDate(any(), any())).thenReturn(emptyList);
    }
    
    @AfterGroups(groups = {"appointment-tests"}, alwaysRun = true)
    public void tearDownAppointmentGroup() {
        System.out.println("\n  ▶ @AfterGroups [appointment-tests] - Cleaning up appointment test group");
    }
    
    // ============================================================
    // TEST METHODS
    // ============================================================
    
    @Test(groups = {"fixtures", "unit"}, priority = 1,
          description = "Test that demonstrates @BeforeMethod provides fresh data")
    public void testFreshDataPerTest_FirstTest() {
        System.out.println("  ▶ TEST: Fresh Data Per Test (First Test)");
        
        // Modify the test patient
        testPatient.setFullName("Modified Name");
        
        Assert.assertEquals(testPatient.getFullName(), "Modified Name");
        System.out.println("  ✓ Modified patient name to: " + testPatient.getFullName());
        System.out.println("  → In next test, patient will be fresh again");
    }
    
    @Test(groups = {"fixtures", "unit"}, priority = 2,
          description = "Test that confirms data is reset by @BeforeMethod")
    public void testFreshDataPerTest_SecondTest() {
        System.out.println("  ▶ TEST: Fresh Data Per Test (Second Test)");
        
        // Patient should be fresh, not "Modified Name"
        Assert.assertEquals(testPatient.getFullName(), "Test Patient",
            "Patient should be fresh due to @BeforeMethod");
        
        System.out.println("  ✓ Patient name is fresh: " + testPatient.getFullName());
        System.out.println("  → This proves @BeforeMethod provides isolation");
    }
    
    @Test(groups = {"fixtures", "appointment-tests"}, priority = 3,
          description = "Test appointment creation with fixtures")
    public void testAppointmentCreation() {
        System.out.println("  ▶ TEST: Appointment Creation");
        
        currentAppointment = new Appointment();
        currentAppointment.setId(100L);
        currentAppointment.setPatient(testPatient);
        currentAppointment.setDoctor(testDoctor);
        currentAppointment.setDate(LocalDate.now().plusDays(1));
        currentAppointment.setTime(LocalTime.of(10, 0));
        currentAppointment.setStatus(Appointment.Status.PENDING);
        
        Assert.assertNotNull(currentAppointment);
        Assert.assertEquals(currentAppointment.getPatient().getFullName(), "Test Patient");
        Assert.assertEquals(currentAppointment.getDoctor().getName(), "Dr. Test");
        
        System.out.println("  ✓ Appointment created: ID=" + currentAppointment.getId());
        System.out.println("  ✓ Patient: " + currentAppointment.getPatient().getFullName());
        System.out.println("  ✓ Doctor: " + currentAppointment.getDoctor().getName());
    }
    
    @Test(groups = {"fixtures", "appointment-tests"}, priority = 4,
          description = "Test available slots with mocked repository")
    public void testAvailableSlots() {
        System.out.println("  ▶ TEST: Available Slots");
        
        // Setup mock behavior
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByDoctorAndDate(any(Doctor.class), any(LocalDate.class)))
            .thenReturn(new ArrayList<>());
        
        List<LocalTime> slots = appointmentService.availableSlots(1L, LocalDate.now().plusDays(1));
        
        Assert.assertNotNull(slots);
        Assert.assertFalse(slots.isEmpty(), "Should have available slots");
        Assert.assertEquals(slots.get(0), LocalTime.of(9, 0), "First slot should be 9:00");
        
        System.out.println("  ✓ Total available slots: " + slots.size());
        System.out.println("  ✓ First slot: " + slots.get(0));
        System.out.println("  ✓ Last slot: " + slots.get(slots.size() - 1));
    }
    
    @Test(groups = {"fixtures", "unit"}, priority = 5,
          description = "Test medical record with patient fixture")
    public void testMedicalRecordWithPatient() {
        System.out.println("  ▶ TEST: Medical Record With Patient");
        
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        record.setPatient(testPatient);
        record.setDoctor(testDoctor);
        record.setDiagnosis("Common Cold");
        record.setPrescriptions("Rest and fluids");
        
        Assert.assertNotNull(record.getPatient());
        Assert.assertEquals(record.getPatient().getHealthId(), "HEALTH123456");
        Assert.assertNotNull(record.getDoctor());
        
        System.out.println("  ✓ Medical record created for patient: " + record.getPatient().getFullName());
        System.out.println("  ✓ Diagnosis: " + record.getDiagnosis());
        System.out.println("  ✓ Created at: " + record.getCreatedAt());
    }
    
    @Test(groups = {"fixtures", "unit"}, priority = 6,
          description = "Test counter verification - demonstrates method count tracking")
    public void testMethodCountTracking() {
        System.out.println("  ▶ TEST: Method Count Tracking");
        
        // This should be the 6th method setup
        Assert.assertTrue(methodSetupCount >= 6, 
            "Method setup count should be at least 6 at this point");
        
        System.out.println("  ✓ Current @BeforeMethod execution count: " + methodSetupCount);
        System.out.println("  → This demonstrates that @BeforeMethod runs before EACH test");
    }
    
    @Test(groups = {"fixtures", "unit"}, priority = 7,
          description = "Test isolation - modifying doctor doesn't affect other tests")
    public void testIsolation_ModifyDoctor() {
        System.out.println("  ▶ TEST: Isolation - Modify Doctor");
        
        // Store original
        String originalName = testDoctor.getName();
        
        // Modify
        testDoctor.setName("Dr. Modified");
        testDoctor.setSpecialization("Modified Specialty");
        
        System.out.println("  → Original name: " + originalName);
        System.out.println("  → Modified name: " + testDoctor.getName());
        System.out.println("  → This modification won't affect other tests");
    }
    
    @Test(groups = {"fixtures", "unit"}, priority = 8,
          description = "Test isolation - verify doctor is fresh again")
    public void testIsolation_VerifyFreshDoctor() {
        System.out.println("  ▶ TEST: Isolation - Verify Fresh Doctor");
        
        Assert.assertEquals(testDoctor.getName(), "Dr. Test",
            "Doctor should be fresh after @BeforeMethod");
        Assert.assertEquals(testDoctor.getSpecialization(), "General Medicine",
            "Specialization should be fresh");
        
        System.out.println("  ✓ Doctor is fresh: " + testDoctor.getName());
        System.out.println("  ✓ Specialization is fresh: " + testDoctor.getSpecialization());
        System.out.println("  → @BeforeMethod successfully provides test isolation!");
    }
}
