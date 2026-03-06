package backend.service;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.DoctorRepository;
import backend.repository.PatientRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService
 */
public class AppointmentServiceTest {
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    private AppointmentService appointmentService;
    private Patient testPatient;
    private Doctor testDoctor;
    
    @BeforeClass
    public void setUpClass() {
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentService(appointmentRepository, doctorRepository, patientRepository);
    }
    
    @BeforeMethod
    public void setUp() {
        reset(appointmentRepository, doctorRepository, patientRepository);
        
        // Setup test patient
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setHealthId("HEALTH123456");
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        
        // Setup test doctor
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Test");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setSpecialization("General");
    }
    
    @Test(groups = {"unit", "service"})
    public void testAvailableSlots_AllSlotsAvailable() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByDoctorAndDate(any(Doctor.class), any(LocalDate.class)))
            .thenReturn(new ArrayList<>());
        
        // Act
        List<LocalTime> slots = appointmentService.availableSlots(1L, LocalDate.now().plusDays(1));
        
        // Assert
        Assert.assertNotNull(slots);
        Assert.assertFalse(slots.isEmpty());
        // 9:00 to 17:00 with 30-min slots = 16 slots
        Assert.assertEquals(slots.size(), 16);
        Assert.assertEquals(slots.get(0), LocalTime.of(9, 0));
        Assert.assertEquals(slots.get(slots.size() - 1), LocalTime.of(16, 30));
    }
    
    @Test(groups = {"unit", "service"})
    public void testAvailableSlots_SomeSlotsBooked() {
        // Arrange
        Appointment bookedAppointment = new Appointment();
        bookedAppointment.setTime(LocalTime.of(10, 0));
        bookedAppointment.setStatus(Appointment.Status.CONFIRMED);
        
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByDoctorAndDate(any(Doctor.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(bookedAppointment));
        
        // Act
        List<LocalTime> slots = appointmentService.availableSlots(1L, LocalDate.now().plusDays(1));
        
        // Assert
        Assert.assertNotNull(slots);
        Assert.assertEquals(slots.size(), 15); // One slot taken
        Assert.assertFalse(slots.contains(LocalTime.of(10, 0))); // 10:00 should not be available
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testAvailableSlots_DoctorNotFound_ThrowsException() {
        // Arrange
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert (exception expected)
        appointmentService.availableSlots(999L, LocalDate.now().plusDays(1));
    }
    
    @Test(groups = {"unit", "service"})
    public void testBook_Success() {
        // Arrange
        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        LocalTime appointmentTime = LocalTime.of(10, 0);
        
        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.existsByDoctorAndDateAndTime(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });
        
        // Act
        Appointment result = appointmentService.book("HEALTH123456", 1L, appointmentDate, appointmentTime);
        
        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPatient().getHealthId(), "HEALTH123456");
        Assert.assertEquals(result.getDoctor().getName(), "Dr. Test");
        Assert.assertEquals(result.getDate(), appointmentDate);
        Assert.assertEquals(result.getTime(), appointmentTime);
        Assert.assertEquals(result.getStatus(), Appointment.Status.PENDING);
    }
    
    @Test(groups = {"unit", "service"}, expectedExceptions = IllegalArgumentException.class)
    public void testBook_SlotAlreadyBooked_ThrowsException() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.existsByDoctorAndDateAndTime(any(), any(), any())).thenReturn(true);
        
        // Act & Assert (exception expected)
        appointmentService.book("HEALTH123456", 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
    }
    
    @Test(groups = {"unit", "service"})
    public void testCancel_Success() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.PENDING);
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Appointment result = appointmentService.cancel(1L);
        
        // Assert
        Assert.assertEquals(result.getStatus(), Appointment.Status.CANCELLED);
    }
    
    @Test(groups = {"unit", "service"})
    public void testSetStatus_Confirmed() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.PENDING);
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Appointment result = appointmentService.setStatus(1L, Appointment.Status.CONFIRMED);
        
        // Assert
        Assert.assertEquals(result.getStatus(), Appointment.Status.CONFIRMED);
    }
    
    @Test(groups = {"unit", "service"})
    public void testListAll_ReturnsAllAppointments() {
        // Arrange
        List<Appointment> appointments = Arrays.asList(new Appointment(), new Appointment(), new Appointment());
        when(appointmentRepository.findAll()).thenReturn(appointments);
        
        // Act
        List<Appointment> result = appointmentService.listAll();
        
        // Assert
        Assert.assertEquals(result.size(), 3);
    }
    
    @Test(groups = {"unit", "service"})
    public void testListByStatus_FiltersByStatus() {
        // Arrange
        List<Appointment> pendingAppointments = Arrays.asList(new Appointment(), new Appointment());
        when(appointmentRepository.findByStatus(Appointment.Status.PENDING)).thenReturn(pendingAppointments);
        
        // Act
        List<Appointment> result = appointmentService.listByStatus(Appointment.Status.PENDING);
        
        // Assert
        Assert.assertEquals(result.size(), 2);
    }
}
