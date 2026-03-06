package backend.service;

import backend.model.Doctor;
import backend.model.Appointment;
import backend.repository.DoctorRepository;
import backend.repository.AppointmentRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DoctorService
 */
public class DoctorServiceTest {
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    private DoctorService doctorService;
    
    @BeforeClass
    public void setUpClass() {
        MockitoAnnotations.openMocks(this);
        doctorService = new DoctorService(doctorRepository, appointmentRepository);
    }
    
    @BeforeMethod
    public void resetMocks() {
        reset(doctorRepository, appointmentRepository);
    }
    
    @Test(groups = {"unit", "service"})
    public void testCreate_Success() throws Exception {
        // Arrange
        when(doctorRepository.existsByEmail(anyString())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });
        
        // Act
        Doctor doctor = doctorService.create("Dr. Smith", "smith@hospital.com", "Cardiology", null);
        
        // Assert
        Assert.assertNotNull(doctor);
        Assert.assertEquals(doctor.getName(), "Dr. Smith");
        Assert.assertEquals(doctor.getEmail(), "smith@hospital.com");
        Assert.assertEquals(doctor.getSpecialization(), "Cardiology");
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testCreate_DuplicateEmail_ThrowsException() throws Exception {
        // Arrange
        when(doctorRepository.existsByEmail("existing@hospital.com")).thenReturn(true);
        
        // Act & Assert (exception expected)
        doctorService.create("Dr. Existing", "existing@hospital.com", "General", null);
    }
    
    @Test(groups = {"unit", "service"})
    public void testList_ReturnsAllDoctors() {
        // Arrange
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(createDoctor(1L, "Dr. Smith", "Cardiology"));
        doctors.add(createDoctor(2L, "Dr. Jones", "Neurology"));
        doctors.add(createDoctor(3L, "Dr. Brown", "Pediatrics"));
        
        when(doctorRepository.findAll()).thenReturn(doctors);
        
        // Act
        List<Doctor> result = doctorService.list();
        
        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get(0).getName(), "Dr. Smith");
    }
    
    @Test(groups = {"unit", "service"})
    public void testGet_Found() {
        // Arrange
        Doctor doctor = createDoctor(1L, "Dr. Smith", "Cardiology");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        
        // Act
        Doctor result = doctorService.get(1L);
        
        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getName(), "Dr. Smith");
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testGet_NotFound_ThrowsException() {
        // Arrange
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert (exception expected)
        doctorService.get(999L);
    }
    
    @Test(groups = {"unit", "service"})
    public void testUpdate_Success() throws Exception {
        // Arrange
        Doctor existing = createDoctor(1L, "Dr. Smith", "Cardiology");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Doctor updated = doctorService.update(1L, "Dr. Smith Jr.", null, "Cardiac Surgery", null);
        
        // Assert
        Assert.assertEquals(updated.getName(), "Dr. Smith Jr.");
        Assert.assertEquals(updated.getSpecialization(), "Cardiac Surgery");
    }
    
    @Test(groups = {"unit", "service"})
    public void testDelete_Success() {
        // Act
        doctorService.delete(1L);
        
        // Assert
        verify(doctorRepository, times(1)).deleteById(1L);
    }
    
    @Test(groups = {"unit", "service"})
    public void testAppointmentsByDoctor_ReturnsAppointments() {
        // Arrange
        Doctor doctor = createDoctor(1L, "Dr. Smith", "Cardiology");
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment());
        appointments.add(new Appointment());
        
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(appointments);
        
        // Act
        List<Appointment> result = doctorService.appointmentsByDoctor(1L);
        
        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testLogin_InvalidCredentials_ThrowsException() {
        // Arrange
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        // Act & Assert (exception expected)
        doctorService.login("invalid@email.com", "password");
    }
    
    // Helper methods
    
    private Doctor createDoctor(Long id, String name, String specialization) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setEmail(name.toLowerCase().replace(" ", ".").replace("dr.", "") + "@hospital.com");
        doctor.setSpecialization(specialization);
        return doctor;
    }
}
