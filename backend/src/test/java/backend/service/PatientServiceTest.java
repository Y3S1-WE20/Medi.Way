package backend.service;

import backend.dto.LoginRequest;
import backend.dto.LoginResponse;
import backend.dto.RegisterRequest;
import backend.dto.RegisterResponse;
import backend.model.Patient;
import backend.repository.PatientRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PatientService
 */
public class PatientServiceTest {
    
    @Mock
    private PatientRepository patientRepository;
    
    private PatientService patientService;
    private AutoCloseable closeable;
    
    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        patientService = new PatientService(patientRepository);
    }
    
    @AfterMethod
    public void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test(groups = {"unit", "service"})
    public void testRegister_Success() {
        // Arrange
        RegisterRequest request = createValidRegisterRequest();
        
        when(patientRepository.existsByEmail(request.email)).thenReturn(false);
        when(patientRepository.existsByHealthId(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });
        
        // Act
        RegisterResponse response = patientService.register(request);
        
        // Assert
        Assert.assertNotNull(response);
        Assert.assertEquals(response.fullName, "John Doe");
        Assert.assertEquals(response.email, "john@example.com");
        Assert.assertNotNull(response.healthId);
        Assert.assertEquals(response.healthId.length(), 12);
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testRegister_DuplicateEmail_ThrowsException() {
        // Arrange
        RegisterRequest request = createValidRegisterRequest();
        when(patientRepository.existsByEmail(request.email)).thenReturn(true);
        
        // Act & Assert (exception expected)
        patientService.register(request);
    }
    
    @Test(groups = {"unit", "service"})
    public void testLogin_Success() {
        // Arrange
        Patient patient = createPatientWithCredentials();
        String rawPassword = "password123";
        
        when(patientRepository.findByEmail("john@example.com")).thenReturn(Optional.of(patient));
        
        LoginRequest request = new LoginRequest();
        request.email = "john@example.com";
        request.password = rawPassword;
        
        // Act
        LoginResponse response = patientService.login(request);
        
        // Assert
        Assert.assertNotNull(response);
        Assert.assertEquals(response.message, "Login successful");
        Assert.assertNotNull(response.healthId);
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testLogin_UserNotFound_ThrowsException() {
        // Arrange
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        LoginRequest request = new LoginRequest();
        request.email = "nonexistent@example.com";
        request.password = "anypassword";
        
        // Act & Assert (exception expected)
        patientService.login(request);
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testLogin_WrongPassword_ThrowsException() {
        // Arrange
        Patient patient = createPatientWithCredentials();
        when(patientRepository.findByEmail("john@example.com")).thenReturn(Optional.of(patient));
        
        LoginRequest request = new LoginRequest();
        request.email = "john@example.com";
        request.password = "wrongpassword"; // Wrong password
        
        // Act & Assert (exception expected)
        patientService.login(request);
    }
    
    @Test(groups = {"unit", "service"})
    public void testFindByHealthId_Found() {
        // Arrange
        Patient patient = new Patient();
        patient.setHealthId("HEALTH123456");
        patient.setFullName("John Doe");
        
        when(patientRepository.findByHealthId("HEALTH123456")).thenReturn(Optional.of(patient));
        
        // Act
        Optional<Patient> result = patientService.findByHealthId("HEALTH123456");
        
        // Assert
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get().getFullName(), "John Doe");
    }
    
    @Test(groups = {"unit", "service"})
    public void testFindByHealthId_NotFound() {
        // Arrange
        when(patientRepository.findByHealthId(anyString())).thenReturn(Optional.empty());
        
        // Act
        Optional<Patient> result = patientService.findByHealthId("NONEXISTENT");
        
        // Assert
        Assert.assertFalse(result.isPresent());
    }
    
    // Helper methods
    
    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.fullName = "John Doe";
        request.email = "john@example.com";
        request.password = "password123";
        request.phone = "1234567890";
        request.address = "123 Main St";
        request.dateOfBirth = LocalDate.of(1990, 1, 15);
        return request;
    }
    
    private Patient createPatientWithCredentials() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setHealthId("HEALTH123456");
        // Generate BCrypt hash for "password123" - use same encoder as service
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        patient.setPasswordHash(encoder.encode("password123"));
        return patient;
    }
}
