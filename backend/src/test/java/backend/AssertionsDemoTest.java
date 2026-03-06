package backend;

import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.dto.RegisterResponse;
import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.repository.PatientRepository;
import backend.service.PatientService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * ============================================================
 * MEMBER 1: ASSERTIONS DEMONSTRATION
 * ============================================================
 * 
 * This test class demonstrates various TestNG assertion types:
 * 
 * 1. assertEquals    - Compare two values for equality
 * 2. assertNotEquals - Verify two values are different
 * 3. assertNotNull   - Verify value is not null
 * 4. assertNull      - Verify value is null
 * 5. assertTrue      - Verify condition is true
 * 6. assertFalse     - Verify condition is false
 * 7. assertThrows    - Verify exception is thrown
 * 8. assertSame      - Verify same object reference
 * 9. assertNotSame   - Verify different object references
 * 
 * Run with: mvn test -Dtest=AssertionsDemoTest
 * ============================================================
 */
public class AssertionsDemoTest {
    
    @Mock
    private PatientRepository patientRepository;
    
    private PatientService patientService;
    private RegisterRequest validRequest;
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       MEMBER 1: ASSERTIONS DEMONSTRATION                   ║");
        System.out.println("║       Testing MEDI.WAY Healthcare Application              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        patientService = new PatientService(patientRepository);
        
        // Create a valid registration request
        validRequest = new RegisterRequest();
        validRequest.fullName = "John Doe";
        validRequest.email = "john.doe@example.com";
        validRequest.password = "securePassword123";
        validRequest.phone = "1234567890";
        validRequest.address = "123 Healthcare Ave";
        validRequest.dateOfBirth = LocalDate.of(1990, 5, 15);
    }
    
    // ============================================================
    // 1. assertEquals - Verify expected values match
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertEquals for patient name verification")
    public void testAssertEquals_PatientName() {
        System.out.println("▶ Test: assertEquals - Patient Name Verification");
        
        Patient patient = new Patient();
        patient.setFullName("John Doe");
        
        // assertEquals verifies two values are equal
        Assert.assertEquals(patient.getFullName(), "John Doe", 
            "Patient name should match the expected value");
        
        System.out.println("  ✓ Patient name correctly set to 'John Doe'");
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertEquals with numbers")
    public void testAssertEquals_PatientId() {
        System.out.println("▶ Test: assertEquals - Patient ID Verification");
        
        Patient patient = new Patient();
        patient.setId(100L);
        
        // assertEquals works with any type
        Assert.assertEquals(patient.getId(), Long.valueOf(100L), 
            "Patient ID should be 100");
        
        System.out.println("  ✓ Patient ID correctly set to 100");
    }
    
    // ============================================================
    // 2. assertNotEquals - Verify values are different
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertNotEquals for unique health IDs")
    public void testAssertNotEquals_UniqueHealthIds() {
        System.out.println("▶ Test: assertNotEquals - Unique Health ID Generation");
        
        Patient patient1 = new Patient();
        patient1.setHealthId("HEALTH123456");
        
        Patient patient2 = new Patient();
        patient2.setHealthId("HEALTH789012");
        
        // assertNotEquals ensures values are different
        Assert.assertNotEquals(patient1.getHealthId(), patient2.getHealthId(), 
            "Each patient should have a unique Health ID");
        
        System.out.println("  ✓ Health IDs are unique: " + patient1.getHealthId() + " ≠ " + patient2.getHealthId());
    }
    
    // ============================================================
    // 3. assertNotNull - Verify value exists
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertNotNull for required fields")
    public void testAssertNotNull_RequiredFields() {
        System.out.println("▶ Test: assertNotNull - Required Field Verification");
        
        // Mock repository behavior
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByHealthId(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });
        
        RegisterResponse response = patientService.register(validRequest);
        
        // assertNotNull ensures object is not null
        Assert.assertNotNull(response, "Registration response should not be null");
        Assert.assertNotNull(response.id, "Patient ID should be generated");
        Assert.assertNotNull(response.healthId, "Health ID should be generated");
        Assert.assertNotNull(response.fullName, "Full name should be set");
        
        System.out.println("  ✓ All required fields are populated");
        System.out.println("    - ID: " + response.id);
        System.out.println("    - Health ID: " + response.healthId);
        System.out.println("    - Name: " + response.fullName);
    }
    
    // ============================================================
    // 4. assertNull - Verify value is null
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertNull for optional fields")
    public void testAssertNull_OptionalFields() {
        System.out.println("▶ Test: assertNull - Optional Field Verification");
        
        Patient patient = new Patient();
        patient.setFullName("John Doe");
        // Not setting optional fields
        
        // assertNull verifies value is null
        Assert.assertNull(patient.getGender(), "Gender should be null when not set");
        Assert.assertNull(patient.getPhone(), "Phone should be null when not set");
        
        System.out.println("  ✓ Optional fields are correctly null before setting");
    }
    
    // ============================================================
    // 5. assertTrue - Verify boolean condition is true
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertTrue for health ID format")
    public void testAssertTrue_HealthIdFormat() {
        System.out.println("▶ Test: assertTrue - Health ID Format Validation");
        
        String healthId = "ABCD12345678";
        
        // assertTrue verifies condition is true
        Assert.assertTrue(healthId.length() == 12, 
            "Health ID should be exactly 12 characters");
        Assert.assertTrue(healthId.matches("[A-Z0-9]+"), 
            "Health ID should contain only uppercase letters and numbers");
        Assert.assertTrue(Character.isLetter(healthId.charAt(0)), 
            "Health ID should start with a letter");
        
        System.out.println("  ✓ Health ID format is valid: " + healthId);
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertTrue for appointment time validation")
    public void testAssertTrue_ValidAppointmentTime() {
        System.out.println("▶ Test: assertTrue - Appointment Time Validation");
        
        LocalTime appointmentTime = LocalTime.of(10, 30);
        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closeTime = LocalTime.of(17, 0);
        
        // Verify appointment is within business hours
        Assert.assertTrue(
            !appointmentTime.isBefore(openTime) && appointmentTime.isBefore(closeTime),
            "Appointment should be within business hours (9:00 - 17:00)"
        );
        
        System.out.println("  ✓ Appointment time " + appointmentTime + " is within business hours");
    }
    
    // ============================================================
    // 6. assertFalse - Verify boolean condition is false
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertFalse for duplicate check")
    public void testAssertFalse_NoDuplicateEmail() {
        System.out.println("▶ Test: assertFalse - No Duplicate Email");
        
        String email = "new.patient@example.com";
        
        // Mock repository - email doesn't exist
        when(patientRepository.existsByEmail(email)).thenReturn(false);
        
        // assertFalse verifies condition is false
        Assert.assertFalse(patientRepository.existsByEmail(email), 
            "Email should not already exist for new registration");
        
        System.out.println("  ✓ Email '" + email + "' is available for registration");
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertFalse for cancelled appointment")
    public void testAssertFalse_CancelledAppointmentNotActive() {
        System.out.println("▶ Test: assertFalse - Cancelled Appointment Is Not Active");
        
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.Status.CANCELLED);
        
        boolean isActive = appointment.getStatus() == Appointment.Status.CONFIRMED || 
                          appointment.getStatus() == Appointment.Status.PENDING;
        
        // assertFalse to verify cancelled appointment is not active
        Assert.assertFalse(isActive, "Cancelled appointment should not be active");
        
        System.out.println("  ✓ Cancelled appointment correctly marked as inactive");
    }
    
    // ============================================================
    // 7. assertThrows - Verify exception handling
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertThrows for duplicate email")
    public void testAssertThrows_DuplicateEmailException() {
        System.out.println("▶ Test: assertThrows - Duplicate Email Exception");
        
        // Mock repository - email already exists
        when(patientRepository.existsByEmail(validRequest.email)).thenReturn(true);
        
        // assertThrows verifies exception is thrown
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            patientService.register(validRequest);
        });
        
        System.out.println("  ✓ IllegalArgumentException thrown for duplicate email");
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertThrows for invalid login")
    public void testAssertThrows_InvalidLoginCredentials() {
        System.out.println("▶ Test: assertThrows - Invalid Login Credentials");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = "nonexistent@example.com";
        loginRequest.password = "wrongpassword";
        
        // Mock repository - user not found
        when(patientRepository.findByEmail(loginRequest.email)).thenReturn(Optional.empty());
        
        // Assert that login with invalid credentials throws exception
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            patientService.login(loginRequest);
        });
        
        System.out.println("  ✓ IllegalArgumentException thrown for invalid credentials");
    }
    
    // ============================================================
    // 8. assertSame / assertNotSame - Verify object references
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertSame for object identity")
    public void testAssertSame_SameObjectReference() {
        System.out.println("▶ Test: assertSame - Same Object Reference");
        
        Patient patient = new Patient();
        patient.setFullName("John Doe");
        
        // Assigning to another variable (same reference)
        Patient samePatient = patient;
        
        // assertSame verifies same object reference
        Assert.assertSame(patient, samePatient, 
            "Both variables should reference the same object");
        
        System.out.println("  ✓ Both variables reference the same Patient object");
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertNotSame for different objects")
    public void testAssertNotSame_DifferentObjects() {
        System.out.println("▶ Test: assertNotSame - Different Object Instances");
        
        Patient patient1 = new Patient();
        patient1.setFullName("John Doe");
        patient1.setId(1L);
        
        Patient patient2 = new Patient();
        patient2.setFullName("John Doe");
        patient2.setId(1L);
        
        // assertNotSame verifies different object references
        Assert.assertNotSame(patient1, patient2, 
            "Should be different object instances even with same data");
        
        // But their data can be equal
        Assert.assertEquals(patient1.getFullName(), patient2.getFullName());
        Assert.assertEquals(patient1.getId(), patient2.getId());
        
        System.out.println("  ✓ Different objects with same data are not the same reference");
    }
    
    // ============================================================
    // 9. Additional Assertion Examples
    // ============================================================
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertEquals with collections")
    public void testAssertEquals_AppointmentStatus() {
        System.out.println("▶ Test: assertEquals - Appointment Status Enum");
        
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.Status.PENDING);
        
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.PENDING, 
            "New appointment should have PENDING status");
        
        // Change status
        appointment.setStatus(Appointment.Status.CONFIRMED);
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.CONFIRMED, 
            "Appointment status should be updated to CONFIRMED");
        
        System.out.println("  ✓ Appointment status transitions verified");
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate multiple assertions in one test")
    public void testMultipleAssertions_DoctorProfile() {
        System.out.println("▶ Test: Multiple Assertions - Complete Doctor Profile");
        
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Sarah Smith");
        doctor.setEmail("sarah.smith@mediway.com");
        doctor.setSpecialization("Cardiology");
        
        // Multiple assertions for complete object validation
        Assert.assertNotNull(doctor.getId(), "Doctor ID should not be null");
        Assert.assertEquals(doctor.getName(), "Dr. Sarah Smith", "Doctor name mismatch");
        Assert.assertTrue(doctor.getEmail().contains("@"), "Email should contain @");
        Assert.assertFalse(doctor.getSpecialization().isEmpty(), "Specialization should not be empty");
        Assert.assertEquals(doctor.getSpecialization(), "Cardiology", "Specialization mismatch");
        
        System.out.println("  ✓ All doctor profile fields validated:");
        System.out.println("    - ID: " + doctor.getId());
        System.out.println("    - Name: " + doctor.getName());
        System.out.println("    - Email: " + doctor.getEmail());
        System.out.println("    - Specialization: " + doctor.getSpecialization());
    }
    
    @Test(groups = {"assertions", "unit"}, description = "Demonstrate assertion with custom message")
    public void testAssertWithCustomMessage() {
        System.out.println("▶ Test: Assertions with Custom Error Messages");
        
        Patient patient = new Patient();
        patient.setDateOfBirth(LocalDate.of(2000, 1, 15));
        
        LocalDate today = LocalDate.now();
        int age = today.getYear() - patient.getDateOfBirth().getYear();
        
        // Using descriptive messages for debugging
        Assert.assertTrue(age >= 0, 
            String.format("Patient age calculation error: DOB=%s, calculated age=%d", 
                patient.getDateOfBirth(), age));
        
        Assert.assertTrue(age < 150, 
            String.format("Patient age unrealistic: %d years (DOB: %s)", 
                age, patient.getDateOfBirth()));
        
        System.out.println("  ✓ Patient age validated: " + age + " years old");
    }
}
