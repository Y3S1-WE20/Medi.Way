package backend;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.MedicalRecord;
import backend.model.Patient;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration tests demonstrating end-to-end workflows in MEDI.WAY
 * 
 * Note: These are mock integration tests that simulate the workflow.
 * For production, these would connect to a test database.
 */
public class IntegrationTest {
    
    // Simulated database
    private List<Patient> patientDb;
    private List<Doctor> doctorDb;
    private List<Appointment> appointmentDb;
    private List<MedicalRecord> recordDb;
    
    private long patientIdCounter;
    private long doctorIdCounter;
    private long appointmentIdCounter;
    private long recordIdCounter;
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           INTEGRATION TESTS - MEDI.WAY                     ║");
        System.out.println("║           End-to-End Workflow Testing                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Reset simulated database
        patientDb = new ArrayList<>();
        doctorDb = new ArrayList<>();
        appointmentDb = new ArrayList<>();
        recordDb = new ArrayList<>();
        
        patientIdCounter = 0;
        doctorIdCounter = 0;
        appointmentIdCounter = 0;
        recordIdCounter = 0;
    }
    
    // ============================================================
    // Patient Registration to Appointment Workflow
    // ============================================================
    
    @Test(groups = {"integration"}, priority = 1,
          description = "Complete workflow: Patient registration → Doctor selection → Appointment booking")
    public void testPatientAppointmentWorkflow() {
        System.out.println("▶ Testing: Patient Registration to Appointment Workflow\n");
        
        // Step 1: Register Patient
        System.out.println("  Step 1: Registering new patient...");
        Patient patient = registerPatient("Alice Smith", "alice@email.com", "1234567890");
        Assert.assertNotNull(patient.getId(), "Patient should be assigned an ID");
        Assert.assertNotNull(patient.getHealthId(), "Patient should receive a Health ID");
        System.out.println("    ✓ Patient registered: " + patient.getFullName() + " (ID: " + patient.getHealthId() + ")");
        
        // Step 2: Add Doctor
        System.out.println("  Step 2: Adding doctor to system...");
        Doctor doctor = addDoctor("Dr. John Wilson", "wilson@hospital.com", "Cardiology");
        Assert.assertNotNull(doctor.getId(), "Doctor should be assigned an ID");
        System.out.println("    ✓ Doctor added: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        
        // Step 3: Book Appointment
        System.out.println("  Step 3: Booking appointment...");
        LocalDate appointmentDate = LocalDate.now().plusDays(3);
        LocalTime appointmentTime = LocalTime.of(10, 30);
        Appointment appointment = bookAppointment(patient, doctor, appointmentDate, appointmentTime);
        Assert.assertNotNull(appointment.getId(), "Appointment should be assigned an ID");
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.PENDING);
        System.out.println("    ✓ Appointment booked: " + appointmentDate + " at " + appointmentTime);
        
        // Step 4: Confirm Appointment
        System.out.println("  Step 4: Confirming appointment...");
        appointment.setStatus(Appointment.Status.CONFIRMED);
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.CONFIRMED);
        System.out.println("    ✓ Appointment confirmed");
        
        System.out.println("\n  ✅ Complete workflow successful!\n");
    }
    
    // ============================================================
    // Medical Record Creation Workflow
    // ============================================================
    
    @Test(groups = {"integration"}, priority = 2,
          description = "Complete workflow: Appointment → Medical Record Creation")
    public void testMedicalRecordWorkflow() {
        System.out.println("▶ Testing: Medical Record Creation Workflow\n");
        
        // Setup: Create patient and doctor
        Patient patient = registerPatient("Bob Johnson", "bob@email.com", "9876543210");
        Doctor doctor = addDoctor("Dr. Sarah Lee", "lee@hospital.com", "General Medicine");
        
        // Step 1: Create and confirm appointment
        System.out.println("  Step 1: Creating appointment for patient visit...");
        Appointment appointment = bookAppointment(patient, doctor, LocalDate.now(), LocalTime.of(14, 0));
        appointment.setStatus(Appointment.Status.CONFIRMED);
        System.out.println("    ✓ Appointment created and confirmed");
        
        // Step 2: Patient visits - Create medical record
        System.out.println("  Step 2: Recording medical visit...");
        MedicalRecord record = createMedicalRecord(
            patient, 
            doctor,
            "Seasonal Allergies",
            "Cetirizine 10mg once daily, Nasal spray as needed",
            "Patient presents with runny nose, sneezing. No fever."
        );
        Assert.assertNotNull(record.getId());
        Assert.assertEquals(record.getPatient().getId(), patient.getId());
        Assert.assertEquals(record.getDoctor().getId(), doctor.getId());
        System.out.println("    ✓ Medical record created");
        System.out.println("    - Diagnosis: " + record.getDiagnosis());
        System.out.println("    - Prescription: " + record.getPrescriptions());
        
        // Step 3: Verify record in patient history
        System.out.println("  Step 3: Verifying patient history...");
        List<MedicalRecord> patientHistory = getPatientRecords(patient);
        Assert.assertEquals(patientHistory.size(), 1);
        Assert.assertEquals(patientHistory.get(0).getDiagnosis(), "Seasonal Allergies");
        System.out.println("    ✓ Medical record found in patient history");
        
        System.out.println("\n  ✅ Medical record workflow successful!\n");
    }
    
    // ============================================================
    // Appointment Cancellation Workflow
    // ============================================================
    
    @Test(groups = {"integration"}, priority = 3,
          description = "Appointment cancellation and rebooking workflow")
    public void testAppointmentCancellationWorkflow() {
        System.out.println("▶ Testing: Appointment Cancellation Workflow\n");
        
        // Setup
        Patient patient = registerPatient("Carol White", "carol@email.com", "5555555555");
        Doctor doctor = addDoctor("Dr. Mike Brown", "brown@hospital.com", "Pediatrics");
        
        // Step 1: Book appointment
        System.out.println("  Step 1: Booking initial appointment...");
        LocalDate originalDate = LocalDate.now().plusDays(5);
        Appointment appointment = bookAppointment(patient, doctor, originalDate, LocalTime.of(9, 0));
        Long appointmentId = appointment.getId();
        System.out.println("    ✓ Appointment booked for " + originalDate);
        
        // Step 2: Patient cancels
        System.out.println("  Step 2: Patient cancelling appointment...");
        appointment.setStatus(Appointment.Status.CANCELLED);
        Assert.assertEquals(appointment.getStatus(), Appointment.Status.CANCELLED);
        System.out.println("    ✓ Appointment cancelled");
        
        // Step 3: Rebook for different date
        System.out.println("  Step 3: Rebooking appointment...");
        LocalDate newDate = LocalDate.now().plusDays(7);
        Appointment newAppointment = bookAppointment(patient, doctor, newDate, LocalTime.of(11, 0));
        Assert.assertNotEquals(newAppointment.getId(), appointmentId);
        Assert.assertEquals(newAppointment.getStatus(), Appointment.Status.PENDING);
        System.out.println("    ✓ New appointment booked for " + newDate);
        
        // Verify appointment counts
        List<Appointment> patientAppointments = getPatientAppointments(patient);
        Assert.assertEquals(patientAppointments.size(), 2);
        
        long activeCount = patientAppointments.stream()
            .filter(a -> a.getStatus() != Appointment.Status.CANCELLED)
            .count();
        Assert.assertEquals(activeCount, 1);
        System.out.println("    ✓ Patient has 1 active appointment, 1 cancelled");
        
        System.out.println("\n  ✅ Cancellation workflow successful!\n");
    }
    
    // ============================================================
    // Doctor Schedule Workflow
    // ============================================================
    
    @Test(groups = {"integration"}, priority = 4,
          description = "Doctor daily schedule management workflow")
    public void testDoctorScheduleWorkflow() {
        System.out.println("▶ Testing: Doctor Schedule Management Workflow\n");
        
        // Setup: Create doctor and multiple patients
        Doctor doctor = addDoctor("Dr. Emily Davis", "davis@hospital.com", "Dermatology");
        Patient patient1 = registerPatient("Patient One", "p1@email.com", "1111111111");
        Patient patient2 = registerPatient("Patient Two", "p2@email.com", "2222222222");
        Patient patient3 = registerPatient("Patient Three", "p3@email.com", "3333333333");
        
        LocalDate today = LocalDate.now();
        
        // Step 1: Book multiple appointments for doctor
        System.out.println("  Step 1: Booking multiple appointments...");
        bookAppointment(patient1, doctor, today, LocalTime.of(9, 0));
        bookAppointment(patient2, doctor, today, LocalTime.of(10, 0));
        bookAppointment(patient3, doctor, today, LocalTime.of(11, 0));
        System.out.println("    ✓ 3 appointments booked for Dr. " + doctor.getName());
        
        // Step 2: Get doctor's daily schedule
        System.out.println("  Step 2: Retrieving doctor's schedule...");
        List<Appointment> doctorSchedule = getDoctorAppointments(doctor, today);
        Assert.assertEquals(doctorSchedule.size(), 3);
        System.out.println("    ✓ Found " + doctorSchedule.size() + " appointments for today");
        
        // Step 3: Confirm all appointments
        System.out.println("  Step 3: Confirming all appointments...");
        for (Appointment apt : doctorSchedule) {
            apt.setStatus(Appointment.Status.CONFIRMED);
        }
        
        long confirmedCount = doctorSchedule.stream()
            .filter(a -> a.getStatus() == Appointment.Status.CONFIRMED)
            .count();
        Assert.assertEquals(confirmedCount, 3);
        System.out.println("    ✓ All 3 appointments confirmed");
        
        // Step 4: Check available slots
        System.out.println("  Step 4: Checking remaining availability...");
        List<LocalTime> availableSlots = getAvailableSlots(doctor, today);
        // 9:00-17:00 = 16 slots, 3 taken = 13 available
        Assert.assertEquals(availableSlots.size(), 13);
        System.out.println("    ✓ " + availableSlots.size() + " slots still available");
        
        System.out.println("\n  ✅ Doctor schedule workflow successful!\n");
    }
    
    // ============================================================
    // Multi-Doctor Comparison Workflow
    // ============================================================
    
    @Test(groups = {"integration"}, priority = 5,
          description = "Viewing and comparing multiple doctors")
    public void testMultiDoctorWorkflow() {
        System.out.println("▶ Testing: Multi-Doctor Comparison Workflow\n");
        
        // Step 1: Add multiple doctors with different specializations
        System.out.println("  Step 1: Adding doctors with various specializations...");
        addDoctor("Dr. Cardio", "cardio@hospital.com", "Cardiology");
        addDoctor("Dr. Neuro", "neuro@hospital.com", "Neurology");
        addDoctor("Dr. Ortho", "ortho@hospital.com", "Orthopedics");
        addDoctor("Dr. Pedia", "pedia@hospital.com", "Pediatrics");
        addDoctor("Dr. General", "general@hospital.com", "General Medicine");
        System.out.println("    ✓ Added 5 doctors");
        
        // Step 2: List all doctors
        System.out.println("  Step 2: Listing all doctors...");
        Assert.assertEquals(doctorDb.size(), 5);
        for (Doctor d : doctorDb) {
            System.out.println("    - " + d.getName() + " (" + d.getSpecialization() + ")");
        }
        
        // Step 3: Filter by specialization
        System.out.println("  Step 3: Filtering doctors by specialization...");
        List<Doctor> cardiologists = getDoctorsBySpecialization("Cardiology");
        Assert.assertEquals(cardiologists.size(), 1);
        Assert.assertEquals(cardiologists.get(0).getName(), "Dr. Cardio");
        System.out.println("    ✓ Found 1 cardiologist");
        
        // Step 4: Patient books with specific specialist
        System.out.println("  Step 4: Patient booking with specialist...");
        Patient patient = registerPatient("Heart Patient", "heart@email.com", "4444444444");
        Doctor cardiologist = cardiologists.get(0);
        Appointment appointment = bookAppointment(patient, cardiologist, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        Assert.assertEquals(appointment.getDoctor().getSpecialization(), "Cardiology");
        System.out.println("    ✓ Appointment booked with cardiologist");
        
        System.out.println("\n  ✅ Multi-doctor workflow successful!\n");
    }
    
    // ============================================================
    // Helper Methods - Simulating Service Layer
    // ============================================================
    
    private Patient registerPatient(String name, String email, String phone) {
        Patient patient = new Patient();
        patient.setId(++patientIdCounter);
        patient.setFullName(name);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setHealthId(generateHealthId());
        patientDb.add(patient);
        return patient;
    }
    
    private Doctor addDoctor(String name, String email, String specialization) {
        Doctor doctor = new Doctor();
        doctor.setId(++doctorIdCounter);
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setSpecialization(specialization);
        doctorDb.add(doctor);
        return doctor;
    }
    
    private Appointment bookAppointment(Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment();
        appointment.setId(++appointmentIdCounter);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus(Appointment.Status.PENDING);
        appointmentDb.add(appointment);
        return appointment;
    }
    
    private MedicalRecord createMedicalRecord(Patient patient, Doctor doctor, String diagnosis, 
                                               String prescriptions, String comments) {
        MedicalRecord record = new MedicalRecord();
        record.setId(++recordIdCounter);
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(diagnosis);
        record.setPrescriptions(prescriptions);
        record.setComments(comments);
        recordDb.add(record);
        return record;
    }
    
    private List<MedicalRecord> getPatientRecords(Patient patient) {
        return recordDb.stream()
            .filter(r -> r.getPatient().getId().equals(patient.getId()))
            .toList();
    }
    
    private List<Appointment> getPatientAppointments(Patient patient) {
        return appointmentDb.stream()
            .filter(a -> a.getPatient().getId().equals(patient.getId()))
            .toList();
    }
    
    private List<Appointment> getDoctorAppointments(Doctor doctor, LocalDate date) {
        return appointmentDb.stream()
            .filter(a -> a.getDoctor().getId().equals(doctor.getId()) && a.getDate().equals(date))
            .toList();
    }
    
    private List<LocalTime> getAvailableSlots(Doctor doctor, LocalDate date) {
        List<LocalTime> bookedSlots = getDoctorAppointments(doctor, date).stream()
            .map(Appointment::getTime)
            .toList();
        
        List<LocalTime> allSlots = new ArrayList<>();
        for (LocalTime t = LocalTime.of(9, 0); t.isBefore(LocalTime.of(17, 0)); t = t.plusMinutes(30)) {
            if (!bookedSlots.contains(t)) {
                allSlots.add(t);
            }
        }
        return allSlots;
    }
    
    private List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorDb.stream()
            .filter(d -> d.getSpecialization().equalsIgnoreCase(specialization))
            .toList();
    }
    
    private String generateHealthId() {
        return "HLTH" + String.format("%08d", patientIdCounter);
    }
}
