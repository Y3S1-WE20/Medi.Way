package backend.model;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * Unit tests for Model classes
 */
public class ModelTest {
    
    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private MedicalRecord medicalRecord;
    
    @BeforeMethod
    public void setUp() {
        // Initialize fresh objects for each test
        patient = new Patient();
        doctor = new Doctor();
        appointment = new Appointment();
        medicalRecord = new MedicalRecord();
    }
    
    // ============================================================
    // Patient Model Tests
    // ============================================================
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetId() {
        patient.setId(1L);
        Assert.assertEquals(patient.getId(), Long.valueOf(1L));
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetHealthId() {
        patient.setHealthId("HEALTH123456");
        Assert.assertEquals(patient.getHealthId(), "HEALTH123456");
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetFullName() {
        patient.setFullName("John Doe");
        Assert.assertEquals(patient.getFullName(), "John Doe");
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetEmail() {
        patient.setEmail("john.doe@example.com");
        Assert.assertEquals(patient.getEmail(), "john.doe@example.com");
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetPasswordHash() {
        String hash = "$2a$10$abc123...";
        patient.setPasswordHash(hash);
        Assert.assertEquals(patient.getPasswordHash(), hash);
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetPhone() {
        patient.setPhone("1234567890");
        Assert.assertEquals(patient.getPhone(), "1234567890");
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetAddress() {
        patient.setAddress("123 Main Street, City");
        Assert.assertEquals(patient.getAddress(), "123 Main Street, City");
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetDateOfBirth() {
        LocalDate dob = LocalDate.of(1990, 5, 15);
        patient.setDateOfBirth(dob);
        Assert.assertEquals(patient.getDateOfBirth(), dob);
    }
    
    @Test(groups = {"model"})
    public void testPatient_SetAndGetGender() {
        patient.setGender("MALE");
        Assert.assertEquals(patient.getGender(), "MALE");
    }
    
    // ============================================================
    // Doctor Model Tests
    // ============================================================
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetId() {
        doctor.setId(1L);
        Assert.assertEquals(doctor.getId(), Long.valueOf(1L));
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetName() {
        doctor.setName("Dr. Sarah Smith");
        Assert.assertEquals(doctor.getName(), "Dr. Sarah Smith");
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetEmail() {
        doctor.setEmail("sarah.smith@hospital.com");
        Assert.assertEquals(doctor.getEmail(), "sarah.smith@hospital.com");
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetSpecialization() {
        doctor.setSpecialization("Cardiology");
        Assert.assertEquals(doctor.getSpecialization(), "Cardiology");
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetPasswordHash() {
        String hash = "$2a$10$xyz789...";
        doctor.setPasswordHash(hash);
        Assert.assertEquals(doctor.getPasswordHash(), hash);
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetPhoto() {
        byte[] photoData = {0x01, 0x02, 0x03};
        doctor.setPhoto(photoData);
        Assert.assertEquals(doctor.getPhoto(), photoData);
    }
    
    @Test(groups = {"model"})
    public void testDoctor_SetAndGetPhotoContentType() {
        doctor.setPhotoContentType("image/jpeg");
        Assert.assertEquals(doctor.getPhotoContentType(), "image/jpeg");
    }
    
    // ============================================================
    // Appointment Model Tests
    // ============================================================
    
    @Test(groups = {"model"})
    public void testAppointment_SetAndGetId() {
        appointment.setId(1L);
        Assert.assertEquals(appointment.getId(), Long.valueOf(1L));
    }
    
    @Test(groups = {"model"})
    public void testAppointment_SetAndGetPatient() {
        patient.setFullName("Test Patient");
        appointment.setPatient(patient);
        Assert.assertEquals(appointment.getPatient().getFullName(), "Test Patient");
    }
    
    @Test(groups = {"model"})
    public void testAppointment_SetAndGetDoctor() {
        doctor.setName("Dr. Test");
        appointment.setDoctor(doctor);
        Assert.assertEquals(appointment.getDoctor().getName(), "Dr. Test");
    }
    
    @Test(groups = {"model"})
    public void testAppointment_SetAndGetDate() {
        LocalDate date = LocalDate.of(2026, 3, 15);
        appointment.setDate(date);
        Assert.assertEquals(appointment.getDate(), date);
    }
    
    @Test(groups = {"model"})
    public void testAppointment_SetAndGetTime() {
        LocalTime time = LocalTime.of(10, 30);
        appointment.setTime(time);
        Assert.assertEquals(appointment.getTime(), time);
    }
    
    @Test(groups = {"model"})
    public void testAppointment_DefaultStatus() {
        // Default status should be PENDING
        appointment.setStatus(Appointment.Status.PENDING);
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.PENDING);
    }
    
    @Test(groups = {"model"})
    public void testAppointment_AllStatuses() {
        // Test all status values
        for (Appointment.Status status : Appointment.Status.values()) {
            appointment.setStatus(status);
            Assert.assertEquals(appointment.getStatus(), status);
        }
    }
    
    @Test(groups = {"model"})
    public void testAppointment_StatusEnumValues() {
        Appointment.Status[] statuses = Appointment.Status.values();
        Assert.assertEquals(statuses.length, 4);
        Assert.assertEquals(Appointment.Status.valueOf("PENDING"), Appointment.Status.PENDING);
        Assert.assertEquals(Appointment.Status.valueOf("CONFIRMED"), Appointment.Status.CONFIRMED);
        Assert.assertEquals(Appointment.Status.valueOf("REJECTED"), Appointment.Status.REJECTED);
        Assert.assertEquals(Appointment.Status.valueOf("CANCELLED"), Appointment.Status.CANCELLED);
    }
    
    // ============================================================
    // MedicalRecord Model Tests
    // ============================================================
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetId() {
        medicalRecord.setId(1L);
        Assert.assertEquals(medicalRecord.getId(), Long.valueOf(1L));
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetPatient() {
        patient.setFullName("Test Patient");
        medicalRecord.setPatient(patient);
        Assert.assertEquals(medicalRecord.getPatient().getFullName(), "Test Patient");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetDoctor() {
        doctor.setName("Dr. Test");
        medicalRecord.setDoctor(doctor);
        Assert.assertEquals(medicalRecord.getDoctor().getName(), "Dr. Test");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetDiagnosis() {
        medicalRecord.setDiagnosis("Common Cold");
        Assert.assertEquals(medicalRecord.getDiagnosis(), "Common Cold");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetPrescriptions() {
        medicalRecord.setPrescriptions("Rest and fluids, Paracetamol 500mg");
        Assert.assertEquals(medicalRecord.getPrescriptions(), "Rest and fluids, Paracetamol 500mg");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetLabNotes() {
        medicalRecord.setLabNotes("Blood test results normal");
        Assert.assertEquals(medicalRecord.getLabNotes(), "Blood test results normal");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetComments() {
        medicalRecord.setComments("Patient reports mild symptoms for 3 days");
        Assert.assertEquals(medicalRecord.getComments(), "Patient reports mild symptoms for 3 days");
    }
    
    @Test(groups = {"model"})
    public void testMedicalRecord_SetAndGetCreatedAt() {
        OffsetDateTime createdAt = OffsetDateTime.now();
        medicalRecord.setCreatedAt(createdAt);
        Assert.assertEquals(medicalRecord.getCreatedAt(), createdAt);
    }
    
    // ============================================================
    // Integration-style Model Tests
    // ============================================================
    
    @Test(groups = {"model"})
    public void testCompleteAppointmentCreation() {
        // Create patient
        patient.setId(1L);
        patient.setHealthId("HEALTH123456");
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        
        // Create doctor
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        doctor.setSpecialization("General Medicine");
        
        // Create appointment
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setTime(LocalTime.of(10, 0));
        appointment.setStatus(Appointment.Status.PENDING);
        
        // Verify complete appointment
        Assert.assertNotNull(appointment.getId());
        Assert.assertNotNull(appointment.getPatient());
        Assert.assertNotNull(appointment.getDoctor());
        Assert.assertNotNull(appointment.getDate());
        Assert.assertNotNull(appointment.getTime());
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.PENDING);
        Assert.assertEquals(appointment.getPatient().getFullName(), "John Doe");
        Assert.assertEquals(appointment.getDoctor().getName(), "Dr. Smith");
    }
    
    @Test(groups = {"model"})
    public void testCompleteMedicalRecordCreation() {
        // Create patient
        patient.setId(1L);
        patient.setFullName("Jane Doe");
        
        // Create doctor
        doctor.setId(1L);
        doctor.setName("Dr. Johnson");
        doctor.setSpecialization("Internal Medicine");
        
        // Create medical record
        medicalRecord.setId(1L);
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setCreatedAt(OffsetDateTime.now());
        medicalRecord.setDiagnosis("Hypertension");
        medicalRecord.setPrescriptions("Lisinopril 10mg daily");
        medicalRecord.setComments("Blood pressure 150/90. Recommend lifestyle changes.");
        
        // Verify complete medical record
        Assert.assertNotNull(medicalRecord.getId());
        Assert.assertEquals(medicalRecord.getPatient().getFullName(), "Jane Doe");
        Assert.assertEquals(medicalRecord.getDoctor().getName(), "Dr. Johnson");
        Assert.assertEquals(medicalRecord.getDiagnosis(), "Hypertension");
        Assert.assertNotNull(medicalRecord.getPrescriptions());
        Assert.assertNotNull(medicalRecord.getComments());
    }
}
